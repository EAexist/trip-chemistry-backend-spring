package com.example.tripchemistry.service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tripchemistry.model.DatabaseSequence;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/* Auto-increment 되는 고유 id 부여를 위한 서비스 */
// https://www.baeldung.com/spring-boot-mongodb-auto-generated-field
// https://jkoder.com/reactive-mongodb-auto-increment-id-using-spring-webflux/
@Service
@AllArgsConstructor
@Slf4j
public class SequenceGeneratorService {

    private final ReactiveMongoOperations mongoOperations;

    @Transactional
    public Mono<Long> generateSequence( String seqName ) {
        log.info(String.format("[generateSequence]\tseqName=%s", seqName ));

        return mongoOperations.findAndModify(
                new Query( Criteria.where("_id").is(seqName) ),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class).map(DatabaseSequence::getSeq);
    }

    public Mono<String> generateId( String seqName ) {

        return generateSequence(seqName).map(it -> it + "");
            // .doOnError(null);
        /* Generate New Id */
        // String id = "-1";
        // try {
        //     id = generateSequence(seqName) + "";
        // } catch (InterruptedException | ExecutionException e) {
        //     log.info(String.format("Error:{}", e.getMessage()));
        // }
        // log.info(String.format("[generateId]\tseqName=%s\tid=%s", seqName, id ));
        // return(id);

    }
}
