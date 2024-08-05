// https://velog.io/@ads0070/%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8-API%EB%A1%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%9D%B8%EC%A6%9D%ED%95%98%EA%B8%B0
// https://velog.io/@leejinagood/%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
package com.example.tripchemistry.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tripchemistry.DTO.LoginResultDTO;
import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.model.auth.KakaoToken;
import com.example.tripchemistry.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000")
@Slf4j
class AuthControllerReactive {

    private final AuthService authService;

    /* 카카오 인증 */
    // @GetMapping("/kakao")
    // Mono<ResponseEntity<AuthResultDTO>> kakaoAuth( @RequestParam("code") String
    // code ) {
    // log.info("[AuthController.kakaoAuth] GET /auth/kakao/login?code=" + code );
    // Mono<String> accessToken = Mono.just( code ).flatMap(
    // authService::getKakaoAccessToken );
    // Mono<ResponseEntity<AuthResultDTO>> response = accessToken.flatMap(
    // authService::getKakaoUserInfo );

    // return response;
    // }

    /* 게스트 생성 */
    @PostMapping("/guest/signIn")
    Mono<ResponseEntity<ProfileDTO>> guestSignIn() {
        log.info(String.format("POST /profile/guest"));
        return authService.guestSignIn();
    }

    /* 게스트 로그인 */
    @GetMapping("/guest/login")
    Mono<ResponseEntity<LoginResultDTO>> guestLogin(@RequestParam("id") String id) {
        log.info(String.format("GET /profile/guest"));
        return authService.guestLogin( id );
    }

    /* 카카오 로그인 */
    @PostMapping("/kakao/login")
    Mono<ResponseEntity<LoginResultDTO>> kakaoLogin(@RequestBody Map<String, String> body) {
        log.info(String.format("[AuthController.kakaoLogin] GET /auth/kakao/login?\tcode=%s\tid=%s", body.get("code"), body.get("id") ));
        // Mono<ResponseEntity<LoginResultDTO>> response = authService.kakaoLogin(
        // body.get("code") );
        Mono<KakaoToken> token = authService.getKakaoAccessToken(body.get("code"));
        Mono<ResponseEntity<LoginResultDTO>> response = token.map(KakaoToken::getAccess_token)
                .flatMap(it ->
                    authService.kakaoLogin(it, body.get("id"))
                );
        return response.map(it -> {
            log.info(String.format("[AuthController.kakaoLogin] returns loginResultDTO=%s",
                    ((LoginResultDTO) it.getBody()).toString()));
            return it;
        });
    }

    /* 카카오 로그인 ( accessToken ) */
    @PostMapping("/kakao/login/ByAccessToken")
    Mono<ResponseEntity<ProfileDTO>> kakaoLoginByAccessToken(@RequestBody Map<String, String> body) {
        log.info(String.format("[AuthController.kakaoLogin] GET /auth/kakao/login?\taccessToken=%s", body.get("accessToken") ));
        
        Mono<ResponseEntity<ProfileDTO>> response = authService.kakaoLoginByAccesssToken(body.get("accessToken"));

        return response.map(it -> {
            log.info(String.format("[AuthController.kakaoLogin] returns loginResultDTO=%s",
                    ((ProfileDTO) it.getBody()).toString()));
            return it;
        });
    }

    /* 테스트 응답 저장 */
    @GetMapping("/kakao/logout")
    Mono<ResponseEntity<String>> kakaoLogout(@RequestParam("id") String id) {
        log.info("[AuthController.kakaoLogout] GET /auth/kakao/logout?id=" + id);
        return authService.kakaoLogout(id);
    }
}
