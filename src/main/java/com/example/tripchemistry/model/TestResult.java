package com.example.tripchemistry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.tripchemistry.types.CharacterId;
import com.example.tripchemistry.types.TripTag;

import lombok.AllArgsConstructor;
import lombok.Data;
// import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;


/* 결과 전송 */
// @Document(collection="testResult")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResult {

    // @Id 
    // private String id;

    /* Trip Character */
    private List<TripTag> tripTagList = new ArrayList<TripTag>();

    private CharacterId character_id;

    private Map<String, Float> city;
}