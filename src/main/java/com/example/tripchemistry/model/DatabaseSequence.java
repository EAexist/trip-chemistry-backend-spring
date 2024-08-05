package com.example.tripchemistry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

// https://www.baeldung.com/spring-boot-mongodb-auto-generated-field
/* 다른 DB Collection 에 auto-increment 되는 고유 id를 부여하기 위한 collection. */
@Document(collection = "database_sequences")
@Data
public class DatabaseSequence {

    @Id
    private String id;

    private long seq;
} 