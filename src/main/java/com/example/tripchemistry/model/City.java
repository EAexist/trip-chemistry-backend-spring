package com.example.tripchemistry.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.example.tripchemistry.types.NationId;
import com.example.tripchemistry.types.TripTag;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="city")
public class City {

    @Id
    private String id;

    private NationId nationId;
    private String name;
    private String body;
    private List<TripTag> tripTagList; 
    // @OneToMany(mappedBy = "city") /* Replace ManyToMany by OneToManys and intermediate entity */
    // private List<UserDataToCity> userDataListCityGroup = new ArrayList<>();   

}
