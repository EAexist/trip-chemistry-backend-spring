package com.example.tripchemistry.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.tripchemistry.model.Chemistry;

/* Autowire(주입) repository to class*/
public interface ChemistryRepository extends ReactiveCrudRepository<Chemistry, String> {}; /* Repository(ReactiveCrudRepository / JpaRepository) <Object Type, Id Type>. Spring Data"s Repository is an abstract interface for various DBs. */