// package com.example.tripchemistry.service;

// import static org.junit.jupiter.api.Assertions.assertAll;
// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.util.Arrays;

// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.ResponseEntity;

// import com.example.tripchemistry.DTO.TestResultDTO;
// import com.example.tripchemistry.model.Profile;
// import com.example.tripchemistry.model.TestAnswer;
// import com.example.tripchemistry.model.TestResult;
// import com.example.tripchemistry.repository.ProfileRepository;
// import com.example.tripchemistry.repository.CharacterRepository;
// import com.example.tripchemistry.types.CharacterId;
// import com.example.tripchemistry.types.TripTag;

// import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;

// @WebFluxTest
// @ExtendWith(MockitoExtension.class)
// public class TestDataServiceTest {

//     @MockBean
//     ProfileRepository mockProfileRepository;

//     @MockBean
//     CharacterRepository mockCharacterRepository;

//     @InjectMocks
//     TestDataService underTest;

//     @Test
//     void getTestAnswerById_shouldReturnNotFound_whenTestAnswerIsNull(){
//         // assertAll(null);
//         String id = "우동1234";

//         Mockito.when(mockProfileRepository.findById( id ))
//             .thenReturn(Mono.just(
//                 new Profile(
//                     "우동1234", 
//                     "우동"
//                 )
//             ));

//         ResponseEntity<TestAnswer> actual = underTest.getTestAnswerById(id).block();
//         Assertions.assertThat(actual).isEqualTo(ResponseEntity.notFound().build());            
//     }
// }
