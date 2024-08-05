package com.example.tripchemistry.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Data;
// import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

/* trip-chemistry */
import com.example.tripchemistry.types.AuthProvider;

/* 기본정보, 테스트 답변, 테스트 결과, 참여한 여행 목록을 포함한 사용자 프로필. */
/* ! User는 H2 DB 의 예약어임. 
 * @https://www.inflearn.com/questions/546219/user-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%83%9D%EC%84%B1%EC%9D%B4-%EC%95%88%EB%90%A9%EB%8B%88%EB%8B%A4
*/
@Document(collection="profile")
@Data
@NoArgsConstructor
public class Profile {
    
    @Transient
    public static final String SEQUENCE_NAME = "profile";

    @Id
    private String id = "0";
    private Boolean isSample = false;
    
    /* Basic Info */
	private String nickname;
    private String discriminator = "1";

    /* Auth */
    private AuthProvider authProvider;
    private String authProviderId; 
    private String kakaoAccessToken; 
	private String authProviderNickname;

    /* Test Answer */
    @Nullable
    private TestAnswer testAnswer = null;   
    
    /* Test Result */
    @Nullable
    private TestResult testResult = null;   

    /* Chemistry */
    private List<String> chemistryIdList = new ArrayList<String>();
    // private List<TripTag> tripTagList = new ArrayList<TripTag>();
    

    /* Constructors */
    public Profile( String id, String authProviderId, String nickname, AuthProvider authProvider, String kakaoAccessToken, String authProviderNickname ){
        this.id = id;
        this.authProviderId = authProviderId;
        this.nickname = nickname;
        this.authProvider = authProvider;
        this.kakaoAccessToken = kakaoAccessToken; 
        this.authProviderNickname = authProviderNickname; 
    }

    public Profile( String id, AuthProvider authProvider ){
        this.id = id;
        this.authProvider = authProvider;
    }

    public Profile(String id, TestResult testResult ){
        this.id = id;
        this.testResult = testResult;
    }

    public Profile(String id, String nickname, AuthProvider authProvider ){
        this.id = id;
        this.nickname = nickname;
        this.authProvider = authProvider;
    }

    public Profile(String id, String nickname, boolean isSample, TestAnswer testAnswer ){
        this.id = id;
        this.isSample = isSample;
        this.nickname = nickname;
        this.testAnswer = testAnswer;  
        // this.testResult = testResult;  
    }

}
