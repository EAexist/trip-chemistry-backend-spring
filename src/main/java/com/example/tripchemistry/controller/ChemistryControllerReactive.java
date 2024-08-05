// https://velog.io/@ads0070/%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8-API%EB%A1%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%9D%B8%EC%A6%9D%ED%95%98%EA%B8%B0
// https://velog.io/@leejinagood/%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
package com.example.tripchemistry.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tripchemistry.DTO.ChemistryDTO;
import com.example.tripchemistry.model.Chemistry;
import com.example.tripchemistry.service.ChemistryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.time.Duration;

@RestController
@RequestMapping("/chemistry")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000")
@Slf4j
class ChemistryControllerReactive {

    private final ChemistryService chemistryService;

    /* 케미스트리 저장 */
    @PostMapping("/create")
    Mono<ResponseEntity<ChemistryDTO>> createChemistry( @RequestBody ChemistryDTO.CreateDTO createDTO ){
        log.info( String.format("[createChemistry] POST /chemistry?\tcreateDTO=%s", createDTO.toString()));
        return chemistryService.createChemistry( createDTO );
    }

    /* 케미스트리 참여 */
    @PutMapping("/join")
    Mono<ResponseEntity<ChemistryDTO>> joinChemistry( @RequestBody Map<String, String> body ){
        log.info( String.format("PUT /chemistry?\tbody=%s", body.toString()));
        return chemistryService.joinChemistry( body.get("userId"), body.get("chemistryId") );
    }

    /* 케미스트리 결과 요청 */
    @GetMapping()
    Mono<ResponseEntity<ChemistryDTO>> getChemistryById( @RequestParam("id") String id ){
        log.info("GET /chemistry?id=" + id );
        return chemistryService.getChemistryById( id );
    }
    // /* 케미스트리 결과 요청 */
    // @GetMapping()
    // Mono<ResponseEntity<Chemistry>> getChemistryByIds( @RequestParam("idList") List<String> idList ){
    //     log.info("[ProfileController] GET /chemistry?idList=" + idList );
    //     return chemistryService.getChemistry( idList );
    // }
}
