// package com.example.tripchemistry.repository;

// import static org.junit.jupiter.api.Assertions.assertAll;
// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.junit.jupiter.SpringExtension;

// import com.example.tripchemistry.model.TestAnswer;
// import com.example.tripchemistry.model.TestResult;
// import com.example.tripchemistry.model.answer.CityChemistry;

// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;

// import com.example.tripchemistry.model.Profile;

// // https://medium.com/@BPandey/writing-unit-test-in-reactive-spring-boot-application-32b8878e2f57

// @DataMongoTest
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// public class ProfileRepositoryTest {

//     @Autowired
//     private ProfileRepository profileRepository;

//     private Profile profile1 = new Profile(
//         "profile1", 
//         "profile1", 
//         true,
//         // null,
//         // null
//         new TestAnswer(
//             1,
//             1,
//             5000,
//             0,
//             1,
//             2
//         ),		
//         new TestResult()
//     );			
//     private Profile profile2 = new Profile(
//         "profile2", 
//         "profile2", 
//         true,
//         // null,
//         // null
//         new TestAnswer(
//             1,
//             1,
//             5000,
//             1,
//             2,
//             3
//         ),		
//         new TestResult()
//     );		

//     @BeforeEach
//     public void setUp(){
//         profileRepository.deleteAll();
//         profileRepository.saveAll( Flux.just( profile1, profile2 ) );
//     }

//     // @Test
//     // public void findById_shouldReturnProfile_whenProfileExists(){
//     //     profileRepository.deleteAll();
//     //     profileRepository.saveAll( Flux.just( profile1, profile2 ) );
//     //     Mono<CityChemistry> cityChemistryMono = profileRepository.getCityChemistry(
//     //         List.of( "profile1", "profile2" )
//     //     );

//     //     StepVerifier
//     //         .create( cityChemistryMono )
//     //         .assertNext( cityChemistry -> {
//     //             assertAll(
//     //                 () -> Assertions.assertThat( cityChemistry.getMetropolis() ).isEqualTo( 1.5f ),
//     //                 () -> Assertions.assertThat( cityChemistry.getHistory() ).isEqualTo( 2.5f ),
//     //                 () -> Assertions.assertThat( cityChemistry.getNature() ).isEqualTo( 3.5f )                
//     //             );
//     //         })
//     //         .verifyComplete();
//     // }

//     @Test
//     public void getCityChemistry_shouldReturnAverage_whenProfilesExist(){
//         // profileRepository.deleteAll();
//         // profileRepository.saveAll( Flux.just( profile1, profile2 ) );
//         Mono<CityChemistry> cityChemistryMono = profileRepository.getCityChemistry(
//             List.of( "profile1", "profile2" )
//         );

//         StepVerifier
//             .create( cityChemistryMono )
//             .assertNext( cityChemistry -> {
//                 assertAll(
//                     () -> Assertions.assertThat( cityChemistry.getMetropolis() ).isEqualTo( 1.5f ),
//                     () -> Assertions.assertThat( cityChemistry.getHistory() ).isEqualTo( 2.5f ),
//                     () -> Assertions.assertThat( cityChemistry.getNature() ).isEqualTo( 3.5f )                
//                 );
//             })
//             .verifyComplete();
//     }
// }
