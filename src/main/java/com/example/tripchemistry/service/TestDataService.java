package com.example.tripchemistry.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.DTO.TestResultDTO;
import com.example.tripchemistry.model.Chemistry;
import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.TestAnswer;
import com.example.tripchemistry.model.TestResult;
import com.example.tripchemistry.repository.ChemistryRepository;
import com.example.tripchemistry.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestDataService {

    private final ProfileRepository profileRepository;
    private final ChemistryRepository chemistryRepository;

    private final GenerateResultService generateResultService;
    private final ChemistryService chemistryService;
    private final DTOService dtoService;

    // @Bean
    public Chemistry getChemistry(List<String> idList) {
        return new Chemistry();
    }

    /* 게스트 응답 저장 */
    // @Transactional
    // public Mono<ResponseEntity<String>> submitGuestAnswer(GuestAnswerDTO
    // guestAnswerDTO) {

    // Profile profile = new Profile(
    // "id",
    // guestAnswerDTO.getNickname(),
    // AuthProvider.GUEST);

    // Mono<Profile> profileMono = profileRepository.save(profile);

    // return profileMono.then(
    // this.submitAnswer("id", guestAnswerDTO.getTestAnswer())).then(
    // chemistryService.joinChemistryHelper("id", guestAnswerDTO.getChemistryId()));
    // }

    /* 샘플 프로필 리스트 요청 */
    @Transactional
    public Mono<ResponseEntity<List<ProfileDTO.Info>>> getSampleProfiles() {

        return profileRepository.findAllSample()
                .map(ProfileDTO.Info::new)
                .collectList()
                .map(it -> ResponseEntity.ok(it))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* 사용자 본인 닉네임 수정 */
    @Transactional
    public Mono<ResponseEntity<ProfileDTO>> setNickname(String id, String newNickname) {

        log.info(String.format("[setNickname]\tid=%s\tnewNickname=%s", id, newNickname));

        Mono<Profile> profile = profileRepository.findById(id)
                .map(it -> {
                    it.setNickname(newNickname);
                    log.info(String.format("[setNickname]\tupdated Rrofile=%s", it.toString()));
                    return it;
                })
                .flatMap(profileRepository::save)
                .map(it -> {
                    log.info(String.format("[setNickname]\tsaved Profile=%s", it.toString()));
                    return it;
                });

        return profile.flatMap(this::profileToDTO)
                .map(it -> {
                    log.info(String.format("[setNickname]\tprofileDTO=%s", it.toString()));
                    return it;
                })
                .map(it -> ResponseEntity.ok().body(it))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* 사용자 본인 테스트 응답 제출 (저장) 
     * 1. 응답 저장
     * 2. 사용자가 참여 중인 모든 케미스트리의 결과 업데이트     * 
    */
    @Transactional
    public Mono<ResponseEntity<ProfileDTO>> submitAnswer(String id, TestAnswer testAnswer) {

        log.info(String.format("[submitAnswer] testAnswer=%s", testAnswer.toString()));

        Mono<TestResult> testResult = generateResultService.generateResult(id, testAnswer);

        /* 1. 응답 저장 */
        Mono<Profile> profile = profileRepository.findById(id)
                .zipWith(testResult)
                .map(it -> {
                    it.getT1().setTestAnswer(testAnswer);
                    it.getT1().setTestResult(it.getT2());
                    return it.getT1();
                })
                .flatMap(profileRepository::save);

        Flux<String> chemistryIdFlux = profile.map(Profile::getChemistryIdList).flatMapIterable(it -> it);

        /* 2. 사용자가 참여 중인 모든 케미스트리의 결과 업데이트 */
        chemistryIdFlux.flatMap(chemistryRepository::findById)
            .map(it -> {
                log.info(String.format("[submitAnswer] (before update)chemistry=%s", it.toString()));
                return it;
            })
            .flatMap(chemistryService::generateChemistry)
            .map(it -> {
                log.info(String.format("[submitAnswer] (after update)chemistry=%s", it.toString()));
                return it;
            })
            .flatMap(chemistryRepository::save)
            .subscribe();

        return profile.zipWith(
                dtoService.getTestResultDTO(profile))
                .map(it -> new ProfileDTO(it.getT1(), it.getT2()))
                .map(it -> ResponseEntity.ok().body(it))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* Id 로 사용자 검색 요청 */
    @Transactional
    public Mono<ResponseEntity<List<ProfileDTO.Info>>> searchId( String nicknameKeyword, String discriminatorKeyword) {

        // String searchToken = Utils.tokenizeToLetter(keyword);

        return profileRepository.findAllBySearchIdMatch(nicknameKeyword, discriminatorKeyword)
                .map(ProfileDTO.Info::new)
                .collectList()
                .map(it -> new ResponseEntity<List<ProfileDTO.Info>>(it, HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    
    /****** 사용자 프로필 요청 ******/

    /* 프로필 전체 */
    @Transactional
    public Mono<ResponseEntity<ProfileDTO>> getProfileById(String id) {

        Mono<Profile> profile = profileRepository.findById(id);
        // .map( it -> {
        // log.info("[TestDataService.getProfileById] profile=" + it.toString() );
        // return it;
        // });

        Mono<TestResultDTO> testResultDTO = dtoService.getTestResultDTO(profile);
        // .map( it -> {
        // log.info("[TestDataService.getProfileById] testResultDTO=" + it.toString() );
        // return it;
        // });

        return profile.zipWith(
                // getTestResult(profile).map(TestResultDTO::getTestResult)
                testResultDTO)
                .map(it -> new ProfileDTO(it.getT1(), it.getT2()))
                // .map( it -> {
                // log.info("[TestDataService.getProfileById] new ProfileDTO returns " +
                // it.toString() );
                // return it;
                // })
                .map(it -> ResponseEntity.ok().body(it))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* 프로필 기본 정보 */
    @Transactional
    public Mono<ResponseEntity<ProfileDTO.Info>> getInfoById(String id) {

        return profileRepository.findById(id)
                .map(ProfileDTO.Info::new)
                .map(it -> new ResponseEntity<ProfileDTO.Info>(it, HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* 테스트 응답 */
    @Transactional
    public Mono<ResponseEntity<ProfileDTO.TestAnswer>> getTestAnswerById(String id) {

        Mono<Profile> profile = profileRepository.findById(id);

        Mono<ProfileDTO.TestAnswer> testAnswerDTO = profile.map(ProfileDTO.TestAnswer::new);

        return testAnswerDTO.map(it -> new ResponseEntity<ProfileDTO.TestAnswer>(it, HttpStatus.OK));
    }

    /* 테스트 결과 */
    @Transactional
    public Mono<ResponseEntity<TestResultDTO>> getTestResultById(String id) {

        Mono<Profile> profile = profileRepository.findById(id);

        return dtoService.getTestResultDTO(profile)
                .map(it -> ResponseEntity.ok().body(it))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* Profile model -> dto 변환 */
    @Transactional
    public Mono<ProfileDTO> profileToDTO(Profile profile) {

        log.info(String.format("[profileToDTO]\tprofile=%s", profile.toString()));

        Mono<TestResultDTO> testResultDTO = dtoService.getTestResultDTO(Mono.just(profile));
        // .map( it -> {
        // log.info("[TestDataService.getProfileById] testResultDTO=" + it.toString() );
        // return it;
        // });

        return (testResultDTO
                .map(it -> new ProfileDTO(profile, it)));
    }

    /* Depreacated: Blocking */
    // @Transactional
    // public ResponseEntity<Result> getTestResultById(String id){
    // TestResult result = null;
    // HttpStatus status;
    // if(this.profileRepository.existsById(id)){
    // // log.info("TestService - getTestResultById - !exists!");

    // CharacterId characterId =
    // this.profileRepository.findById(id).get().getCharacter();
    // // log.info("TestService - getTestResultById - characterId=" +
    // character);

    // // CharacterId characterId =
    // this.characterRepository.findById(characterId).get();
    // // log.info("TestService - getTestResultById - character=" +
    // character.toString());

    // List<TripTag> tripTag = Arrays.asList(TripTag.자연경관, TripTag.하이킹);

    // result = new TestResult(character, tripTag, "cityGroupTitle Sample");
    // log.info("TestService - getTestResultById - result=" + result.toString());

    // status = HttpStatus.OK;
    // }
    // else{
    // status = HttpStatus.NOT_FOUND;
    // }
    // return new ResponseEntity<Result>(result, status);
    // // return this.profileRepository.findById(id).get();
    // }

    /* 여행지 결과 요청 */
    // @Transactional
    // public Mono<ResponseEntity<List<City>>> getCityGroupById(String id) {

    // return profileRepository.findById(id)
    // .map(user -> user.getTestResult() .getCityGroup());
    // // .flatMapMany(Flux::fromIterable)
    // // .map(it -> cityRepository.findById(it))
    // // .collectList()
    // // .map(it -> new ResponseEntity<List<City>>(it, HttpStatus.OK))
    // // .defaultIfEmpty(ResponseEntity.notFound().build());
    // }

    /* Depreacated: Blocking */
    /* 여행지 결과 요청 */
    // @Transactional
    // public ResponseEntity<CityGroupList> getCityGroupById(String id){
    // CityGroupList result = null;
    // HttpStatus status;
    // if(this.profileRepository.existsById(id)){
    // log.info("TestService - getCityGroupById - !exists! id= "+id);

    // List<City> CityGroup = this.profileRepository.findById(id).get()
    // .getCityGroup()
    // .stream()
    // .map(e->e.getCity())
    // .toList();
    // // log.info("TestService - getCityGroupById - CityGroup= " + CityGroup);

    // List<NationId> nationList = new ArrayList<NationId>(
    // CityGroup.stream().map(city -> city.getNationId()).toList()
    // );
    // // log.info("TestService - getCityGroupById - nationList= " +
    // nationList.toString());

    // result = new CityGroupList(CityGroup, nationList);
    // log.info("TestService - getCityGroupById - result= " + result.toString());

    // status = HttpStatus.OK;
    // }
    // else{
    // status = HttpStatus.CREATED;
    // }
    // return new ResponseEntity<CityGroupList>(result, status);
    // }
}
