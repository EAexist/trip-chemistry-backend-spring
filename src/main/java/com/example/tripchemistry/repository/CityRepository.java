package com.example.tripchemistry.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.tripchemistry.model.City;

/* Autowire(주입) repository to class*/
public interface CityRepository extends ReactiveCrudRepository<City, String> {}; /* Repository(ReactiveCrudRepository / JpaRepository) <Object Type, Id Type>. Spring Data"s Repository is an abstract interface for various DBs. */