package com.example.tripchemistry.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.types.CharacterId;
import com.mongodb.client.result.UpdateResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
public class CharacterDALImpl implements CharacterDAL{

    @Autowired
    private ReactiveMongoTemplate template;

    public Mono<UpdateResult> updateUserDataList(Profile userData, CharacterId characterId){
        return template.update(CharacterId.class)
        .matching(where("id").is(characterId))
        .apply(new Update().push("userDataList", userData))
        .first();
    }    
}
