package com.example.tripchemistry.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.example.tripchemistry.DTO.TestAnswerDTO;
import com.example.tripchemistry.types.ActivityTag;
import com.example.tripchemistry.types.ExpectationTag;
import com.mongodb.lang.Nullable;


/* Test 응답 */
/* ! User는 H2 DB 의 예약어임. 
 * @https://www.inflearn.com/questions/546219/user-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%83%9D%EC%84%B1%EC%9D%B4-%EC%95%88%EB%90%A9%EB%8B%88%EB%8B%A4
*/
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TestAnswer {

    /* Test Answers */   
    private Map<String, List<Integer>> hashtag = new HashMap<String, List<Integer>>();

    @Nullable
    private int leadership;
    private Map<String, Integer> schedule = new HashMap<>();
    private Map<String, Integer> restaurant = new HashMap<>();
    // private Map<String, Integer> city;

    // public TestAnswer(
    //     Map<String, List<Integer>> hashtag,
    //     int leadership,
    //     Map<String, Integer> schedule,
    //     Map<String, Integer> restaurant,
    //     Map<String, Integer> city
    // ){
    //     this.hashtag = hashtag;
    //     this.leadership = leadership;
    //     this.schedule = schedule;
    //     this.restaurant = restaurant;
    //     this.city = city;
    // }
    
    public TestAnswer( TestAnswerDTO testAnswerDTO ){        
        
        // this.expectation = testAnswerDTO.getExpectation().stream().map(
        //     value -> ExpectationTag.values()[value]
        // ).toList();

        // this.activity =  testAnswerDTO.getActivity().stream().map(
        //     value -> ActivityTag.values()[value]
        // ).toList();

        this.hashtag = testAnswerDTO.getHashtag();
        this.schedule = testAnswerDTO.getSchedule();
        this.restaurant = testAnswerDTO.getRestaurant();
        this.leadership = testAnswerDTO.getLeadership();
        // this.city = testAnswerDTO.getCity();
    }

    
    
    // private LeadershipTestAnswer leadership = new LeadershipTestAnswer();
    // private ScheduleTestAnswer schedule = new ScheduleTestAnswer();

    /* Budget */
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "budget_id")
    // private BudgetTestAnswer budget = new BudgetTestAnswer();

    // private int foodBudget;
    // private int foodSpecialBudget;
    // private int accomodateBudget;
    // private int accomodateSpecialBudget;

    /* City */
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "city_id")
    // private CityTestAnswer city = new CityTestAnswer();

    // private int metropolis; 
    // private int history;
    // private int nature;

    /* Activity */
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "activity_id")
    // private ActivityResponse activity;
    // private int food;
    // private int walk;
    // private int shopping;
    // private int museum;
    // private int themePark;
}