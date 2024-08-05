package com.example.tripchemistry.DTO;

import java.util.ArrayList;
import java.util.List;

import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.types.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
// import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

/* 사용자 */
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
// @NoArgsConstructor
// @RequiredArgsConstructor
/* ! User는 H2 DB 의 예약어임. 
 * @https://www.inflearn.com/questions/546219/user-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%83%9D%EC%84%B1%EC%9D%B4-%EC%95%88%EB%90%A9%EB%8B%88%EB%8B%A4
*/
public class ProfileDTO  {

    private String id;  
    private String nickname;
    private String discriminator;  

    /* Auth */
    private AuthProvider authProvider;
    private String kakaoAccessToken; 
	private String authProviderNickname;

    /* Test */
    private TestAnswerDTO testAnswer;   
    private com.example.tripchemistry.DTO.TestResultDTO.TestResult testResult;  
    
    /* Chemistry */
    private List<String> chemistryIdList = new ArrayList<String>();
    
    public ProfileDTO ( Profile profile, TestResultDTO testResultDTO ){
        this.id = profile.getId();
        this.nickname = profile.getNickname();
        this.discriminator = profile.getDiscriminator();  
        this.authProvider = profile.getAuthProvider();
        this.kakaoAccessToken = profile.getKakaoAccessToken(); 
        this.authProviderNickname = profile.getAuthProviderNickname();

        this.testAnswer = profile.getTestAnswer() == null ? null : new TestAnswerDTO( profile.getTestAnswer() );  
        this.testResult = testResultDTO.getTestResult();

        this.chemistryIdList = profile.getChemistryIdList();
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    @AllArgsConstructor
    public static class Info extends TestResultDTO {
        private String id;  
        private String nickname;
        private String discriminator;  

        public Info ( Profile profile ){
            super();
            this.id = profile.getId();
            this.nickname = profile.getNickname();
            this.discriminator = profile.getDiscriminator();  
            this.testResult = ( profile.getTestResult() == null) ? null : new TestResultDTO.TestResult(
                profile.getTestResult().getCharacter_id()              
            );
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Id {
        private String id;  

        public Id ( Profile profile ){
            this.id = profile.getId();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestAnswer {
        private TestAnswerDTO testAnswer;  

        public TestAnswer ( Profile profile ){
            this.testAnswer = profile.getTestAnswer() == null ? null : new TestAnswerDTO( profile.getTestAnswer() );
        }
    }
}
