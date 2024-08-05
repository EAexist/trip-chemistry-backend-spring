package com.example.tripchemistry.repository;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.tripchemistry.model.DatabaseSequence;

import reactor.core.publisher.Mono;

public interface DatabaseSequenceDAL {

    public Mono<DatabaseSequence> findAndModify( 
        Query query, 
        Update update, 
        FindAndModifyOptions findAndModifyOptions, 
        Class<DatabaseSequence> entityClass 
    );
    
}
