package com.example.tripchemistry.repository;

import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.types.CharacterId;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;

public interface CharacterDAL {

    public Mono<UpdateResult> updateUserDataList(Profile userData, CharacterId characterId);
    // public Mono<UpdateResult> setCharacterById(String id, CharacterId character);   
    
}
