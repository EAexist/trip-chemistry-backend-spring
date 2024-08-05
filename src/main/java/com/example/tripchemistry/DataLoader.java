package com.example.tripchemistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.tripchemistry.DTO.ChemistryDTO;
import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.model.Chemistry;
import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.TestAnswer;
import com.example.tripchemistry.repository.ChemistryRepository;
import com.example.tripchemistry.repository.CityRepository;
import com.example.tripchemistry.repository.ProfileRepository;
import com.example.tripchemistry.service.ChemistryService;
import com.example.tripchemistry.service.GenerateResultService;
import com.example.tripchemistry.service.TestDataService;
import com.example.tripchemistry.types.ActivityTag;
import com.example.tripchemistry.types.AuthProvider;
import com.example.tripchemistry.types.CityTag;
import com.example.tripchemistry.types.ExpectationTag;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

// @Component
@AllArgsConstructor
@Slf4j
public class DataLoader {

        public final ProfileRepository profileRepository;
        public final ChemistryRepository chemistryRepository;
        public final CityRepository cityRepository;

        private final TestDataService testDataService;
        private final ChemistryService chemistryService;
        private final GenerateResultService generateResultService;

        /*
         * Application 실행 시 자동 코드 실행 방법:
         * CommandLineRunner / ApplicationRunner: repository bean을 autowire함. Repository
         * bean을 Mock 객체로 대체하기 어려움.
         * 
         * @Component, @PostConstruct: @Component Annotation 을 주석처리해서 데이터 추가 bean을 비활성화
         * 할 수 있음.
         */

        @PostConstruct
        private void loadData() {

                log.info("[DataLoader] loadData");

                /* City */
                // cityRepository.saveAll(List.of(
                // new City("osaka", NationId.jp, "오사카", "현대적 대도시와 오랜 역사의 흔적이 공존하는 곳.",
                // Arrays.asList(TripTag.식도락, TripTag.대도시, TripTag.테마파크)),
                // new City("kyoto", NationId.jp, "교토", "전통 가옥과 사찰이 많은 천년의 고도",
                // Arrays.asList(TripTag.식도락, TripTag.고즈넉한)),
                // new City("biei", NationId.jp, "비에이", "아름다운 들판이 펼쳐진 시골 마을",
                // Arrays.asList(TripTag.자연, TripTag.트레킹, TripTag.사진, TripTag.식도락)),
                // new City("shiretoko", NationId.jp, "시레토코", "야생동물들이 살아가는 자연 그대로의 트레킹 명소",
                // Arrays.asList(TripTag.트레킹, TripTag.자연)),
                // new City("tokyo", NationId.jp, "도쿄", "스카이라인이 매력적인 일본의 수도",
                // Arrays.asList(TripTag.식도락, TripTag.대도시, TripTag.테마파크))))
                // .subscribe();

                /* Profile */
                // profileRepository.deleteAll().subscribe();
                List<Profile> profiles = Arrays.asList(
                                new Profile(
                                                "minji",
                                                "민지",
                                                AuthProvider.GUEST,
                                                true,
                                                // null,
                                                // null
                                                new TestAnswer(
                                                                Map.of(
                                                                                "expectation",
                                                                                Arrays.asList(
                                                                                                ExpectationTag.COMPACT
                                                                                                                .getValue(),
                                                                                                ExpectationTag.FULLFILL
                                                                                                                .getValue(),
                                                                                                ExpectationTag.ADVENTURE
                                                                                                                .getValue(),
                                                                                                ExpectationTag.NEW
                                                                                                                .getValue()),
                                                                                "activity",
                                                                                Arrays.asList(
                                                                                                ActivityTag.PHOTO
                                                                                                                .getValue(),
                                                                                                ActivityTag.INSTA
                                                                                                                .getValue(),
                                                                                                ActivityTag.CAFE.getValue(),
                                                                                                ActivityTag.VLOG.getValue()),
                                                                                "city", Arrays.asList(
                                                                                                CityTag.QUIET
                                                                                                                .getValue(),
                                                                                                CityTag.HISTORY
                                                                                                                .getValue(),
                                                                                                CityTag.HIDDEN
                                                                                                                .getValue(),
                                                                                                CityTag.NATURE
                                                                                                                .getValue())),
                                                                3,
                                                                Map.of(
                                                                                "schedule", 4,
                                                                                "startTime", 8,
                                                                                "endTime", 20,
                                                                                "nightPlan", 2),
                                                                Map.of(
                                                                                "dailyBudget", 8000,
                                                                                "specialBudget", 20000,
                                                                                "specialCount", 1,
                                                                                "price", 1,
                                                                                "taste", 2,
                                                                                "uniqueness", 1,
                                                                                "popularity", 0)
                                                // Map.of(
                                                // "metropolis", 5,
                                                // "history", 4,
                                                // "nature", 2,
                                                // "small", 4)
                                                )),
                                new Profile(
                                                "danielle",
                                                "다니엘",
                                                AuthProvider.GUEST,
                                                true,
                                                // null,
                                                // null
                                                new TestAnswer(
                                                                Map.of(
                                                                                "expectation", Arrays.asList(
                                                                                                ExpectationTag.HEAL
                                                                                                                .getValue(),
                                                                                                ExpectationTag.RELAX
                                                                                                                .getValue(),
                                                                                                ExpectationTag.COMFORT
                                                                                                                .getValue(),
                                                                                                ExpectationTag.DIGITAL_DETOX
                                                                                                                .getValue(),
                                                                                                ExpectationTag.RELAX
                                                                                                                .getValue()),
                                                                                "activity", Arrays.asList(
                                                                                                ActivityTag.WALK.getValue(),
                                                                                                ActivityTag.HOTEL
                                                                                                                .getValue()),
                                                                                "city", Arrays.asList(
                                                                                                CityTag.QUIET
                                                                                                                .getValue(),
                                                                                                CityTag.HISTORY
                                                                                                                .getValue(),
                                                                                                CityTag.HIDDEN
                                                                                                                .getValue(),
                                                                                                CityTag.NATURE
                                                                                                                .getValue())),
                                                                1,
                                                                Map.of(
                                                                                "schedule", 1,
                                                                                "startTime", 10,
                                                                                "endTime", 18,
                                                                                "nightPlan", 0),
                                                                Map.of(
                                                                                "dailyBudget", 12000,
                                                                                "specialBudget", 60000,
                                                                                "specialCount", 2,
                                                                                "price", 0,
                                                                                "taste", 2,
                                                                                "uniqueness", 0,
                                                                                "popularity", 1)
                                                // Map.of(
                                                // "metropolis", 2,
                                                // "history", 4,
                                                // "nature", 5,
                                                // "small", 5)
                                                )),
                                new Profile(
                                                "hanni",
                                                "하니",
                                                AuthProvider.GUEST,
                                                true,
                                                // null,
                                                // null
                                                new TestAnswer(
                                                                Map.of(
                                                                                "expectation", Arrays.asList(
                                                                                                ExpectationTag.HEAL
                                                                                                                .getValue(),
                                                                                                ExpectationTag.FULLFILL
                                                                                                                .getValue(),
                                                                                                ExpectationTag.MEMORY
                                                                                                                .getValue(),
                                                                                                ExpectationTag.ADVENTURE
                                                                                                                .getValue(),
                                                                                                ExpectationTag.NEW
                                                                                                                .getValue()),
                                                                                "activity", Arrays.asList(
                                                                                                ActivityTag.PHOTO
                                                                                                                .getValue(),
                                                                                                ActivityTag.INSTA
                                                                                                                .getValue(),
                                                                                                ActivityTag.NETWORK
                                                                                                                .getValue(),
                                                                                                ActivityTag.BAR.getValue(),
                                                                                                ActivityTag.EAT.getValue(),
                                                                                                ActivityTag.CAFE.getValue()),
                                                                                "city", Arrays.asList(
                                                                                                CityTag.MODERN
                                                                                                                .getValue(),
                                                                                                CityTag.FAMOUS
                                                                                                                .getValue(),
                                                                                                CityTag.ACTIVE
                                                                                                                .getValue(),
                                                                                                CityTag.INTERNATIONAL
                                                                                                                .getValue())),
                                                                1,
                                                                Map.of(
                                                                                "schedule", 0,
                                                                                "startTime", 12,
                                                                                "endTime", 18,
                                                                                "nightPlan", 1),
                                                                Map.of(
                                                                                "dailyBudget", 20000,
                                                                                "specialBudget", 100000,
                                                                                "specialCount", 4,
                                                                                "price", 0,
                                                                                "taste", 0,
                                                                                "uniqueness", 2,
                                                                                "popularity", 1)
                                                // Map.of(
                                                // "metropolis", 5,
                                                // "history", 4,
                                                // "nature", 2,
                                                // "small", 3)
                                                )),
                                new Profile(
                                                "haerin",
                                                "해린",
                                                AuthProvider.GUEST,
                                                true,
                                                // null,
                                                // null
                                                new TestAnswer(
                                                                Map.of(
                                                                                "expectation", Arrays.asList(
                                                                                                ExpectationTag.HEAL
                                                                                                                .getValue(),
                                                                                                ExpectationTag.MEMORY
                                                                                                                .getValue(),
                                                                                                ExpectationTag.COMFORT
                                                                                                                .getValue(),
                                                                                                ExpectationTag.REST
                                                                                                                .getValue(),
                                                                                                ExpectationTag.VIEW
                                                                                                                .getValue(),
                                                                                                ExpectationTag.FRIENDSHIP
                                                                                                                .getValue()),
                                                                                "activity", Arrays.asList(
                                                                                                ActivityTag.PHOTO
                                                                                                                .getValue(),
                                                                                                ActivityTag.INSTA
                                                                                                                .getValue(),
                                                                                                ActivityTag.NETWORK
                                                                                                                .getValue(),
                                                                                                ActivityTag.DRIVE
                                                                                                                .getValue(),
                                                                                                ActivityTag.THEMEPARK
                                                                                                                .getValue(),
                                                                                                ActivityTag.CAFE.getValue(),
                                                                                                ActivityTag.BAR.getValue(),
                                                                                                ActivityTag.VLOG.getValue(),
                                                                                                ActivityTag.SHOPPING
                                                                                                                .getValue(),
                                                                                                ActivityTag.SHOW.getValue()),
                                                                                "city", Arrays.asList(
                                                                                                CityTag.MODERN
                                                                                                                .getValue(),
                                                                                                CityTag.LOUD
                                                                                                                .getValue(),
                                                                                                CityTag.FAMOUS
                                                                                                                .getValue(),
                                                                                                CityTag.ACTIVE
                                                                                                                .getValue(),
                                                                                                CityTag.INTERNATIONAL
                                                                                                                .getValue())),
                                                                2,
                                                                Map.of(
                                                                                "schedule", 2,
                                                                                "startTime", 9,
                                                                                "endTime", 16,
                                                                                "nightPlan", 0),
                                                                Map.of(
                                                                                "dailyBudget", 12000,
                                                                                "specialBudget", 60000,
                                                                                "specialCount", 1,
                                                                                "price", 1,
                                                                                "taste", 1,
                                                                                "uniqueness", 0,
                                                                                "popularity", 2)
                                                // Map.of(
                                                // "metropolis", 5,
                                                // "history", 3,
                                                // "nature", 1,
                                                // "small", 2)
                                                )));
             

                // for (Profile profile : profiles) {
                //         Mono<Profile> profileMono = profileRepository.save(profile);
                //         profileMono.map(it -> {
                //                 chemistryService.joinChemistry(it.getId(), "sample");
                //                 testDataService.submitAnswer(it.getId(), it.getTestAnswer());
                //                 return it;
                //         }).subscribe();
                // }

                Flux<ResponseEntity<ProfileDTO>> profiles_ = Flux.fromIterable(profiles).flatMap(profileRepository::save)
                .flatMap((profile) -> {
                        return testDataService.submitAnswer(profile.getId(), profile.getTestAnswer());
                });

                /* Chemistry */
                // chemistryRepository.deleteAll().subscribe();              

                List<String> sampleIdList = List.of("minji", "danielle", "hanni", "haerin");

                profiles_.then(
                chemistryService.generateChemistry(
                        chemistryService.addProfileList(
                                                new Chemistry(
                                                                "sample",
                                                                new ChemistryDTO.CreateDTO(
                                                                                "나의 첫 번째 여행",
                                                                                ""),
                                                                true), sampleIdList))
                                .flatMap(chemistryRepository::save)
                ).subscribe();

                // chemistryService.generateChemistry(
                //                                 new Chemistry(
                //                                                 "sample",
                //                                                 new ChemistryDTO.CreateDTO(
                //                                                                 "나의 첫 번째 여행",
                //                                                                 ""),
                //                                                 true))
                //                 .flatMap(chemistryRepository::save).subscribe();

        }
}