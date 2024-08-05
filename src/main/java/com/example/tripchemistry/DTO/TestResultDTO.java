package com.example.tripchemistry.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.lang.Nullable;

import com.example.tripchemistry.types.CharacterId;
import com.example.tripchemistry.types.TripTag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/* 결과 전송 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResultDTO {

    @Nullable
    TestResult testResult;

    @Data
    @NoArgsConstructor
    public static class TestResult {
    
        private List<Integer> tripTagList = new ArrayList<Integer>();
    
        private CharacterId characterId;
    
        private Map<String, Float> city;
    
        public TestResult( 
            List<TripTag> tripTagList,
            CharacterId characterId,
            Map<String, Float> city
        ){
            this.tripTagList = tripTagList.stream()
                .map(
                    tag -> tag.getValue()
                )
                .toList();
            this.characterId = characterId;
            this.city = city;
        }

        public TestResult( CharacterId characterId ){
            this.characterId = characterId;
        }
    }
}