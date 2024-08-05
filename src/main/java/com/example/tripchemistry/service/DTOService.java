package com.example.tripchemistry.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tripchemistry.DTO.ChemistryDTO;
import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.DTO.TestResultDTO;
import com.example.tripchemistry.model.Chemistry;
import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.TestResult;
import com.example.tripchemistry.repository.ProfileRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class DTOService {

    private final ProfileRepository profileRepository;
    // private final CharacterRepository characterRepository;

    @Transactional
    public Mono<ProfileDTO> profileToDTO(Profile profile) {

        log.info(String.format("[profileToDTO]\tprofile=%s", profile.toString()));

        Mono<TestResultDTO> testResultDTO = this.getTestResultDTO(Mono.just(profile));
        // .map( it -> {
        // log.info("[TestDataService.getProfileById] testResultDTO=" + it.toString() );
        // return it;
        // });

        return (testResultDTO
                .map(it -> new ProfileDTO(profile, it)));
    }

    public Mono<TestResultDTO> getTestResultDTO(Mono<Profile> profile) {

        log.info("[TestDataService.getTestResultDTO]");

        return profile.map(Optional::of)
                .map(it -> it.map(Profile::getTestResult))
                // .map( it -> {
                // log.info("[TestDataService.getTestResultDTO] Profile::getTestResult returns "
                // + it.toString() );
                // return it;
                // })
                .filter(Optional::isPresent)
                .map(Optional::get)
                // .map( it -> {
                // log.info("[TestDataService.getTestResultDTO] Optional::get returns " +
                // it.toString() );
                // return it;
                // })
                .map(this::testResultToDTO)
                .defaultIfEmpty(new TestResultDTO());
        // .map( it -> {
        // log.info("[TestDataService.getTestResultDTO] returns " + it.toString() );
        // return it;
        // });
    }

    public TestResultDTO getTestResultDTO(Profile profile) {

        log.info("[TestDataService.getTestResultDTO]");
        TestResult testResult = Optional.of(profile).get().getTestResult();
        return testResultToDTO(testResult);
    }

    private TestResultDTO testResultToDTO(TestResult testResult) {

        return new TestResultDTO(new TestResultDTO.TestResult(testResult.getTripTagList(), testResult.getCharacter_id(),
                testResult.getCityGroup()));
    }

    private class ProfileDTOComparator implements Comparator<ProfileDTO> {
        @Override    
        public int compare(ProfileDTO profile1, ProfileDTO profile2) {  
            boolean isProfile1UnAnswered = profile1.getTestAnswer() == null;
            boolean isProfile2UnAnswered = profile2.getTestAnswer() == null;
            if(!(isProfile1UnAnswered ^ isProfile2UnAnswered))
                return profile1.getNickname().compareTo(profile2.getNickname());
            else if(isProfile1UnAnswered)
                return 1;
            else
                return -1;
        }
    }

    public Mono<ChemistryDTO> chemistryToDTO(Chemistry chemistry) {
        
        log.info( String.format("[chemistryToDTO]\tprofileIdList=%s", chemistry.getProfileIdList().toString()));

        Flux<Profile> profileFlux = profileRepository.findAllById(chemistry.getProfileIdList());

        Mono<List<ProfileDTO>> profileDTOMapMono = profileFlux
                .flatMap(this::profileToDTO)
                .collectList()
                .map(
                    it -> {
                        it.sort(new ProfileDTOComparator());
                        return it;
                    }
                );
                /* https://www.baeldung.com/java-list-to-map */
                // .map(it -> it.stream()
                //     .sorted(new ProfileDTOComparator()) // Sort Profiles. 1. Answered profile comes first. 2. Alphabetical
                //     .collect(null)
                // ).map(it -> {
                //     log.info( String.format("[chemistryToDTO]\tprofileDTOMap KeySet=%s", it.toString()));
                //     return it;
                // });

        // Mono<LinkedHashMap<String, ProfileDTO>> profileDTOMapMono = profileFlux
        //         .flatMap(this::profileToDTO)
        //         .collectList()
        //         /* https://www.baeldung.com/java-list-to-map */
        //         .map(it -> it.stream()
        //             .sorted(new ProfileDTOComparator()) // Sort Profiles. 1. Answered profile comes first. 2. Alphabetical
        //             .collect(Collectors.toMap(ProfileDTO::getId, Function.identity(), (x, y) -> y, LinkedHashMap::new))
        //         ).map(it -> {
        //             log.info( String.format("[chemistryToDTO]\tprofileDTOMap KeySet=%s", it.keySet().toString()));
        //             return it;
        //         });

        return profileDTOMapMono
                .map(it -> new ChemistryDTO(chemistry, it));
    }
}
