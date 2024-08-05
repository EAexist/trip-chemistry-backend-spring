package com.example.tripchemistry.repository;

import java.util.List;
import java.util.Map;

import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.answer.CityChemistry;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileDAL {
    public Mono<Profile> findByAuthProviderId( String id );

    // public Flux<Profile> findAllById( List<String> idList );

    public Flux<Profile> findAllBySearchIdMatch( String nicknameKeyword, String discriminatorKeyword );

    public Flux<Profile> findAllSample();

    public <T> Mono<UpdateResult> updateById(String id, String key, T target);
    
    public Flux<String> findLeaderAll( List<String> idList );
    
    public Flux<Profile> findAllTestResultWithNicknameById( List<String> idList );

    // public Mono<Map<String, Float>> getCityChemistry( List<String> idList );
    // public Mono<CityChemistry> getCityChemistry( List<String> idList );

    
    
    // public Mono<CityResponse> getCityChemistry( List<String> idList );

    // public <T> Mono<UpdateResult> setById(String id, String key, T target);
    // public Mono<Character> getCharacterId(String id);

    // public Mono<UpdateResult> setCharacterById(String id, CharacterId character);   
    
}
