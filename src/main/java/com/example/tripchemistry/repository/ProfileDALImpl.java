package com.example.tripchemistry.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.answer.CityChemistry;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProfileDALImpl implements ProfileDAL {

    @Autowired
    private ReactiveMongoTemplate template;
    // public Flux<Profile> findById( String id ){
    // Aggregation aggregation = Aggregation.newAggregation(
    // Aggregation.match( Criteria.where("id").in( idList ) ),
    // Aggregation.project("id")
    // .and("testResult").as("testResult")
    // );

    // return(
    // template.aggregate( aggregation, Profile.class, Profile.class )
    // );
    // };

    public Mono<Profile> findByAuthProviderId(String id) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("authProviderId").is(id)));

        return (template.aggregate(aggregation, Profile.class, Profile.class).next());
    };

    public Flux<Profile> findAllById(List<String> idList) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("id").in(idList)),
                Aggregation.project("id")
                        .and("testResult").as("testResult"));

        return (template.aggregate(aggregation, Profile.class, Profile.class));
    };

    public Flux<Profile> findAllBySearchIdMatch(String nicknameKeyword, String discriminatorKeyword) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("nickname").regex(String.format("^%s", nicknameKeyword))),
                Aggregation.match(Criteria.where("discriminator").regex(String.format("^%s", discriminatorKeyword)))
        // Aggregation.project()
        // .and("id").as("id")
        // .and("nickname").as("nickname")
        // .and("discriminator").as("discriminator")
        // .and("testResult.character_id").as("character_id")
        );
        return (template.aggregate(aggregation, Profile.class, Profile.class).map(it -> {
            log.info(it.toString());
            return it;
        }));
    };

    public Flux<Profile> findAllSample() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("isSample").is(true)));

        return (template.aggregate(aggregation, Profile.class, Profile.class));
    };

    public <T> Mono<UpdateResult> updateById(String id, String key, T target) {
        return template.update(Profile.class)
                .matching(where("id").is(id))
                .apply(new Update().set(key, target)).first();
    }

    public Flux<String> findLeaderAll(List<String> idList) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("id").in(idList)),
                Aggregation.project("id")
                        .and("testAnswer.leadership").as("leadership"),
                Aggregation.group("leadership")
                        .push("id").as("leaderList"),
                Aggregation.sort(Sort.Direction.DESC, "leadership"),
                Aggregation.limit(1),
                Aggregation.unwind("leaderList"));
        return (template.aggregate(aggregation, Profile.class, BasicDBObject.class)
                .map(it -> {
                    log.info("[ProfileDALImpl.findLeaderAll] returns " + it.getString("leaderList"));
                    return (it.getString("leaderList")

                );
                }));
    };

    public Flux<Profile> findAllTestResultWithNicknameById(List<String> idList) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("id").in(idList)),
                Aggregation.match(Criteria.where("testResult").exists(true)),
                Aggregation.project("id")
                        // .and("nickname").as("nickname")
                        .and("testAnswer").as("testAnswer"));

        return (template.aggregate(aggregation, Profile.class, Profile.class));

    };

//     public Mono<Map<String, Float>> getCityChemistry(List<String> idList) {
//         log.info("[getCityChemistry] idList=" + String.join(",", idList.toArray(new String[idList.size()])));

//         Aggregation aggregation = Aggregation.newAggregation(
//                 Aggregation.match(Criteria.where("id").in(idList)),
//                 Aggregation.replaceRoot("testAnswer.city"),
//                 Aggregation.group()
//                         .avg("metropolis").as("metropolis")
//                         .avg("history").as("history")
//                         .avg("nature").as("nature")
//                         .avg("small").as("small"),
//                 Aggregation.project("metropolis", "history", "nature", "small"));

//         return (template.aggregate(aggregation, Profile.class, Map.class).next().map(it -> {
//             return (((Map<String, Float>) it).entrySet().stream()
//                     .filter(entry -> entry.getValue() != null)
//                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
//         }));

//     };

    // public Mono<CityResponse> getCityChemistry( List<String> idList ){

    // return( );
    // }

    // public <T> Mono<UpdateResult> getById(String id, String key, T target){
    // return template.update(UserData.class)
    // .matching(where("id").is(id))
    // .apply(new Update().set(key, target)).first();
    // }

    // public Mono<Character> getCharacterId(String id){
    // Criteria criteria = new Criteria("id");
    // criteria.is(id);
    // Query query = new Query(criteria);
    // query.fields().include("character");
    // Mono<UserData> userData = template.findOne(query, UserData.class);
    // Util.logMono(userData, "UserDataDALImpl: getCharacterId: userData=");
    // return userData.map(it->it.getCharacter());
    // }

    // public Mono<UpdateResult> setCharacterByIdReactive(Mono<String> id,
    // Mono<Character> character){

    // Mono<UpdateResult> result = template.update(UserData.class)
    // .matching(where("id").is(id))
    // .apply(new Update().set("characterId", character)).first();
    // // .apply(new Update().set("name", "NameIsUpdated")).first();

    // Util.logMono(result.map(it->it.wasAcknowledged()), "setCharacterById:
    // wasAcknowledged=");

    // return result;
    // }
}
