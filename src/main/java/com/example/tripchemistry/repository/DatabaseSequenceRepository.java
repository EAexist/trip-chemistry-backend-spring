package com.example.tripchemistry.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;

import com.example.tripchemistry.model.DatabaseSequence;

@Component
public interface DatabaseSequenceRepository extends ReactiveMongoRepository<DatabaseSequence, String>, DatabaseSequenceDAL{}; 