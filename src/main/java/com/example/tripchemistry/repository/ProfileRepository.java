package com.example.tripchemistry.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.example.tripchemistry.model.Profile;

/* Autowire(주입) repository to class*/
public interface ProfileRepository extends ReactiveMongoRepository<Profile, String>, ProfileDAL{};

/* Repository(ReactiveCrudRepository / JpaRepository) <Object Type, Id Type>. Spring Data"s Repository is an abstract interface for various DBs. */