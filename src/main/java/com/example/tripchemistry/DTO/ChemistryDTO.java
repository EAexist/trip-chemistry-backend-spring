package com.example.tripchemistry.DTO;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.example.tripchemistry.model.Chemistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class ChemistryDTO {

    @Id
    private String id;

    private String title;
    private String titleCity;
    private Boolean isSample;
    // private LinkedHashMap<String, ProfileDTO> profiles;
    private List<ProfileDTO> profiles;
    private Map<String, Float> city;
    private Map<String, List<String>> memberLists;
    // private Map<String, List<String>> restaurant;
    // private List<String> leaderList;
    // private List<String> scheduleChemistryText;
    // private List<String> budgetChemistryText;

    public ChemistryDTO(Chemistry chemistry, List<ProfileDTO> profiles) {
        this.id = chemistry.getId();
        this.title = chemistry.getTitle();
        this.isSample = chemistry.getIsSample();
        this.profiles = profiles;
        this.city = chemistry.getCity();
        this.memberLists = chemistry.getIdLists();
        // Map.of(
        //     "relaxingMembers", chemistry.getIdLists().get("relaxingSchedule"),
        //     "busyMembers", chemistry.getIdLists().get("busySchedule")
        //     "lowDailyBudgetMembers", chemistry.getIdLists().get("lowDailyRestaurantBudget"),
        //     "highDailyBudgetMembers", chemistry.getIdLists().get("highDailyRestaurantBudget")
        // );
        // this.titleCity = chemistry.getTitleCity();
        // this.leaderList = chemistry.getLeaderList();
        // this.scheduleChemistryText = chemistry.getScheduleChemistryText();
        // this.budgetChemistryText = chemistry.getBudgetChemistryText();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String title;
        // private String titleCity;
        private String userId;
    }
}
