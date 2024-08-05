package com.example.tripchemistry.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class ResponseService {

    /* 케미스트리 참여 */
    @Transactional
    public <T> Mono<ResponseEntity<T>> createResponseEntity( T body, HttpStatus httpStatus ) {
        return Mono.just(body)
                .map(it -> ResponseEntity.ok().body(it))
                .defaultIfEmpty( new ResponseEntity(httpStatus));    
    }
    
    public <T> Mono<ResponseEntity<T>> createResponseEntity( T body ) {
        return this.createResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

}
