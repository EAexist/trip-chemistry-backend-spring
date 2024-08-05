package com.example.tripchemistry.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.tripchemistry.DTO.ChemistryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="chemistry")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class Chemistry {
    
    @Transient
    public static final String SEQUENCE_NAME = "chemistry";

    @Id
    private String id = "0";
    private String title = "";
    private Boolean isSample = false;
    // private String titleCity = "";
    private List<String> profileIdList = new ArrayList<String>();
    private Map<String, Float> city = new HashMap<String, Float>();
    private Map<String, List<String>> idLists = new HashMap<String, List<String>>();
    // private Map<String, List<String>> restaurant = new HashMap<String, List<String>>();
    // private List<String> budgetChemistryText = new ArrayList<String>();

    public Chemistry( List<String> profileIdList ){
        this.profileIdList = profileIdList;
    }

    public Chemistry( String id, ChemistryDTO.CreateDTO createDTO ){
        this.id = id;
        this.title = createDTO.getTitle();
    }

    public Chemistry( String id, ChemistryDTO.CreateDTO createDTO, Boolean isSample ){
        this.id = id;
        this.title = createDTO.getTitle();
        this.isSample = isSample;
    }

    public Chemistry( String id, Chemistry chemistry ){
        this.id = id;
        this.isSample = chemistry.getIsSample();
        this.title = chemistry.getTitle();
        this.profileIdList = chemistry.getProfileIdList();
        this.city = chemistry.getCity();     
        this.idLists = chemistry.getIdLists();     
        // this.scheduleChemistryText = chemistry.getScheduleChemistryText();
        // this.budgetChemistryText = chemistry.getBudgetChemistryText();
    }
}
