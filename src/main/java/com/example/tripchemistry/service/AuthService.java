// https://velog.io/@ads0070/%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8-API%EB%A1%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%9D%B8%EC%A6%9D%ED%95%98%EA%B8%B0
// https://velog.io/@leejinagood/%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0

package com.example.tripchemistry.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.tripchemistry.DTO.LoginResultDTO;
import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.auth.KakaoToken;
import com.example.tripchemistry.repository.ProfileRepository;
import com.example.tripchemistry.types.AuthProvider;
/* Gson */
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    /* Repository */
    private final ProfileRepository profileRepository;

    /* Service */
    private final TestDataService testDataService;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final ChemistryService chemistryService;
    private final ResponseService responseService;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    // @Transactional
    public Mono<ResponseEntity<ProfileDTO>> guestSignIn() {
        log.info(String.format("[guestSignIn]"));

        /* Generate New Id */
        // String id = "0";
        // try {
        //     id = sequenceGeneratorService.generateSequence("profile") + "";
        // } catch (InterruptedException | ExecutionException e) {
        //     log.info(String.format("Error:{}", e.getMessage()));
        // }
        Mono<String> profileIdMono = sequenceGeneratorService.generateId("profile");

        return profileIdMono.flatMap( id -> profileRepository.save( new Profile(id, AuthProvider.GUEST) ))
                .flatMap(it -> chemistryService.createSampleChemistry(it.getId()))
                .flatMap(testDataService::profileToDTO)
                .flatMap( it-> responseService.createResponseEntity(it, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Mono<ResponseEntity<ProfileDTO>> guestLogin(String id) {
        log.info(String.format("[guestLogin]"));

        return profileRepository.findById(id)
                .filter(profile -> profile.getAuthProvider() == AuthProvider.GUEST)
                .flatMap(testDataService::profileToDTO)
                .flatMap( it-> responseService.createResponseEntity(it, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Mono<KakaoToken> getKakaoAccessToken(String code) {

        log.info(String.format("[AuthService.getKakaoAccessToken] code=%s", code));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", CLIENT_ID);
        formData.add("grant_type", GRANT_TYPE);
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("redirect_uri", REDIRECT_URI);

        String url = TOKEN_URI + "?";
        for (String key : formData.keySet()) {
            url += ("&" + key + "=" + formData.get(key));
        }
        url = url.replace("[", "").replace("]", "");
        System.out.println("[AuthService.getKakaoAccessToken] " + url);

        WebClient webClient = WebClient.builder()
                .baseUrl(TOKEN_URI)
                .build();

        try {
            Mono<KakaoToken> token = webClient.post()
                    .body(BodyInserters.fromFormData(formData))
                    .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                    .retrieve()
                    .bodyToMono(KakaoToken.class);

            return token;

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(e);
        }
    }

    public Mono<ResponseEntity<LoginResultDTO>> kakaoLogin(String accessToken, String id) {

        log.info(String.format("[AuthService.kakaoLogin] accessToken=%s", accessToken));

        WebClient webClient = WebClient.builder()
                .baseUrl(USER_INFO_URI)
                .build();

        try {
            Mono<JsonObject> result = webClient
                    .post()
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(JsonParser::parseString)
                    .map(JsonElement::getAsJsonObject);

            Mono<String> authProviderId = result.map(it -> it.get("id").getAsString());
            Mono<String> authProviderNickname = result
                    .map(it -> it.get("properties").getAsJsonObject().get("nickname").getAsString());

            /* Repository.save should update not insert when the document exists. */
            return authProviderId.flatMap(profileRepository::findByAuthProviderId)
                    /* 이전에 카카오 로그인한 적이 잇는 경우 */
                    .map(it -> {
                        it.setKakaoAccessToken(accessToken);
                        return it;
                    })
                    .flatMap(profileRepository::save)
                    .flatMap(testDataService::profileToDTO)
                    .map(it -> new LoginResultDTO(false, it))
                    .map(it -> ResponseEntity.ok().body(it))
                    /* switchIfEmpty : 카카오 로그인이 처음인 경우 */
                    .switchIfEmpty(
                            /* 게스트 프로필을 카카오 프로필로 이전하는 경우 */
                            Mono.zip(profileRepository.findById(id), authProviderId, authProviderNickname)
                                    .map(it -> {
                                        log.info(
                                                "[kakaoLogin] Profile with Id found. Profile with authProviderId Not Found");
                                        it.getT1().setAuthProvider(AuthProvider.KAKAO);
                                        it.getT1().setAuthProviderId(it.getT2());
                                        it.getT1().setAuthProviderNickname(it.getT3());
                                        it.getT1().setKakaoAccessToken(accessToken);
                                        return (it.getT1());
                                    })
                                    .flatMap(profileRepository::save)
                                    .flatMap(testDataService::profileToDTO)
                                    .map(it -> new LoginResultDTO(false, it))
                                    /* 게스트 프로필 없이 새 카카오 프로필을 생성하는 경우 */
                                    .switchIfEmpty(
                                            Mono.zip(sequenceGeneratorService.generateId("profile"), authProviderId, authProviderNickname)
                                                    .map(it ->new Profile(
                                                            it.getT1(),
                                                                it.getT2(),
                                                                it.getT3(),
                                                                AuthProvider.KAKAO,
                                                                accessToken,
                                                                it.getT3())
                                                    )
                                                    .flatMap(profileRepository::save)
                                                    .flatMap(it -> chemistryService.createSampleChemistry(it.getId()))
                                                    .flatMap(testDataService::profileToDTO)
                                                    .map(it -> new LoginResultDTO(true, it)))
                                    .map(it -> ResponseEntity
                                            .created(URI.create("/profile?id=" + it.getProfile().getId()))
                                            .body(it)));

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

    public Mono<ResponseEntity<ProfileDTO>> kakaoLoginByAccesssToken(String accessToken) {

        log.info(String.format("[AuthService.kakaoLogin] accessToken=%s", accessToken));

        WebClient webClient = WebClient.builder()
                .baseUrl(USER_INFO_URI)
                .build();

        try {
            Mono<JsonObject> result = webClient
                    .post()
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(JsonParser::parseString)
                    .map(JsonElement::getAsJsonObject);

            Mono<String> authProviderId = result.map(it -> it.get("id").getAsString());

            return authProviderId.flatMap(profileRepository::findByAuthProviderId)
                    .flatMap(testDataService::profileToDTO)
                    .map(it -> ResponseEntity.ok().body(it))
                    .defaultIfEmpty(ResponseEntity.notFound().build());

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

    @Transactional
    public Mono<ResponseEntity<String>> kakaoLogout(String id) {
        log.info("[AuthService.kakaoLogout]");

        Mono<Profile> profileMono = profileRepository.findById(id);

        Mono<String> accessToken = profileMono.map(Profile::getKakaoAccessToken);
        String LOGOUT_URI = "https://kapi.kakao.com/v1/user/logout";

        try {
            Mono<JsonObject> result = accessToken.flatMap(it -> {
                log.info(String.format("[AuthService.kakaoLogout] accessToekn=%s", it));

                return WebClient.builder()
                        .baseUrl(LOGOUT_URI)
                        .build()
                        .post()
                        .header("Content-type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .header("Authorization", "Bearer " + it)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(JsonParser::parseString)
                        .map(JsonElement::getAsJsonObject);
            });

            return result.map(it -> it.get("id").getAsString())
                    .map(it -> {
                        log.info(String.format("[AuthService.kakaoLogout] loggedOutId = %s", it));
                        return it;
                    })
                    .then(profileMono)
                    .map(it -> {
                        it.setKakaoAccessToken(null);
                        return it;
                    })
                    .flatMap(profileRepository::save)
                    .map(it -> ResponseEntity.ok().body(it.getId()))
                    .defaultIfEmpty(ResponseEntity.notFound().build());

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(e);
        }
    }
}
