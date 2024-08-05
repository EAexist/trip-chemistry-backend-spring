package com.example.tripchemistry.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.tripchemistry.model.DatabaseSequence;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class DatabaseSequenceDALImpl implements DatabaseSequenceDAL{

    @Autowired
    private ReactiveMongoTemplate template;

    public Mono<DatabaseSequence> findAndModify(Query query, Update update, FindAndModifyOptions findAndModifyOptions, Class<DatabaseSequence> entityClass){
        return template.findAndModify(query, update, findAndModifyOptions, entityClass);   
    }
}
