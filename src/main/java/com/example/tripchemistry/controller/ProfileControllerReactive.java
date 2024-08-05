package com.example.tripchemistry.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.DTO.TestResultDTO;
import com.example.tripchemistry.model.TestAnswer;
import com.example.tripchemistry.service.TestDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000") 
@Slf4j
class ProfileControllerReactive {

    private final TestDataService testService;

    // /* 게스트 응답 저장 */
    // @PutMapping("/guest")
    // Mono<ResponseEntity<String>> submitGuestAnswer( @RequestBody GuestAnswerDTO guestAnswerDTO ){
    //     log.info("PUT /profile/guest\tguestAnswerDTO=" + guestAnswerDTO.toString() );
    //     return testService.submitGuestAnswer( guestAnswerDTO );
    // }    
    /* 테스트 응답 저장 */
    @PutMapping("/answer")
    Mono<ResponseEntity<ProfileDTO>> submitAnswer( @RequestParam("id") String id, @RequestBody TestAnswer testAnswer ){
        log.info("PUT /profile/answer?id=" + id );
        return testService.submitAnswer(id, testAnswer);
    }    

    /* 닉네임 수정 */
    @PutMapping("/setNickname")
    Mono<ResponseEntity<ProfileDTO>> setNickname( @RequestParam("id") String id, @RequestBody Map<String, String> body ){
        log.info(String.format("[setNickname]\tPUT /profile/setNickname?id=%s\tbody=%s", id, body.toString()) );
        return testService.setNickname(id, body.get("value"));
    }    

    /* 프로필 목록 검색 요청 */
    @GetMapping("/search")
    Mono<ResponseEntity<List<ProfileDTO.Info>>> searchId( @RequestParam("keyword") String keyword ){
        log.info("GET profile/search?keyword=" + keyword );
        String[] tokens = keyword.split("#");
        return testService.searchId( tokens[0], tokens.length > 1 ? tokens[1] : "");        
    }    

    /* 프로필 전체 요청 */
    @GetMapping()
    Mono<ResponseEntity<ProfileDTO>> getProfileById( @RequestParam("id") String id ){
        log.info("[ProfileController] GET profile/id=" + id);
        return testService.getProfileById( id );        
    }

    /* 프로필 기본 정보 요청 */
    @GetMapping("/info")
    Mono<ResponseEntity<ProfileDTO.Info>> getInfoById( @RequestParam("id") String id ){
        log.info("[ProfileController] GET profile/info/id=" + id);
        return testService.getInfoById( id );        
    }

    /* 테스트 결과 요청 */
    @GetMapping("/result")
    Mono<ResponseEntity<TestResultDTO>> getTestResultById( @RequestParam("id") String id ){
        log.info("[ProfileController] GET /profile/result?id"+id);
        return testService.getTestResultById( id );   
    }

    /* 테스트 응답 요청 */
    @GetMapping("/answer")
    Mono<ResponseEntity<ProfileDTO.TestAnswer>> getTestAnswerById( @RequestParam("id") String id ){
        log.info("[ProfileController] GET /profile/answer?id=" + id );
        return testService.getTestAnswerById( id );        
    }

    /* 샘플 테스트 결과 요청 */
    @GetMapping("/sample")
    Mono<ResponseEntity<List<ProfileDTO.Info>>> getSampleProfiles( ){
        log.info("[ProfileController] GET /profile/sample");
        return testService.getSampleProfiles()
            .map( it -> {
                log.info( "[ProfileController] getSampleProfiles\n\tResponse=" + it.toString() );
                return it;
            });        
    }

    /* 장소 결과 요청 */
    // @GetMapping("/{id}/cityGroup")
    // Mono<ResponseEntity<List<City>>> getCityGroupById(@PathVariable String id){
    //     log.info("[ProfileController] GET /profile/" + id + "/cityGroup");
    // return testService.getCityGroupById( id );
    //     
    // }


    // @GetMapping("/{id}/summary")
    // Optional<UserData> getTestResultSummaryById(@PathVariable String id){
    //     return this.UserDataRepository.findById(id);
    // }



    // @PostMapping("/{id}")
    // void setUserResponse(@PathVariable String id){
    // };
}
