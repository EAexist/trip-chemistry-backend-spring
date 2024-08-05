package com.example.tripchemistry.DTO;

import java.util.List;
import java.util.Map;

import com.mongodb.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/* 결과 전송 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestAnswerDTO {
    private Map<String, List<Integer>> hashtag;
    
    @Nullable
    private int leadership;
    private Map<String, Integer> schedule;
    private Map<String, Integer> restaurant; 
    // private Map<String, Integer> city;

    public TestAnswerDTO( com.example.tripchemistry.model.TestAnswer testAnswer ){        
        
        // this.expectation = testAnswer.getExpectation().stream().map(
        //     tag -> tag.getValue()
        // ).toList();

        // this.activity =  testAnswer.getActivity().stream().map(
        //     tag -> tag.getValue()
        // ).toList();

        // this.hashtag = testAnswer.getHashtag().stream().map(
        //     tag -> tag.getValue()
        // ).toList(); 

        this.hashtag = testAnswer.getHashtag();
        this.leadership = testAnswer.getLeadership();
        this.schedule = testAnswer.getSchedule(); 
        this.restaurant = testAnswer.getRestaurant(); 
        // this.city = testAnswer.getCity();
    }
}