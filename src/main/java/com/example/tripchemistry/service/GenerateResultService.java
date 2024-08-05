package com.example.tripchemistry.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tripchemistry.model.TestAnswer;
import com.example.tripchemistry.model.TestResult;
import com.example.tripchemistry.repository.ProfileRepository;
import com.example.tripchemistry.types.ActivityTag;
import com.example.tripchemistry.types.CharacterId;
import com.example.tripchemistry.types.CityTag;
import com.example.tripchemistry.types.ExpectationTag;
import com.example.tripchemistry.types.TripTag;
import com.mongodb.client.result.UpdateResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
// @Configuration
@RequiredArgsConstructor
@Slf4j
public class GenerateResultService {

    private final ProfileRepository profileRepository;
//     private final CharacterRepository characterRepository;
//     private final CityRepository cityRepository;

    private Map<ExpectationTag, List<TripTag>> expectationToTripTagMap = Map.ofEntries(
            Map.entry(ExpectationTag.HEAL, Arrays.asList(TripTag.REST)),
            Map.entry(ExpectationTag.COMPACT, Arrays.asList(TripTag.PASSION)),
            Map.entry(ExpectationTag.FULLFILL, Arrays.asList(TripTag.PASSION)),
            Map.entry(ExpectationTag.MEMORY, Arrays.asList(TripTag.FRIENDSHIP)),
            Map.entry(ExpectationTag.RELAX, Arrays.asList(TripTag.REST, TripTag.REFRESH)),
            Map.entry(ExpectationTag.COMFORT, Arrays.asList(TripTag.REST)),
            Map.entry(ExpectationTag.ADVENTURE, Arrays.asList(TripTag.ADVENTURE)),
            Map.entry(ExpectationTag.NEW, Arrays.asList(TripTag.ADVENTURE, TripTag.PASSION)),
            Map.entry(ExpectationTag.DIGITAL_DETOX, Arrays.asList(TripTag.REFRESH, TripTag.REST)),
            Map.entry(ExpectationTag.REST, Arrays.asList(TripTag.REST)),
            Map.entry(ExpectationTag.VIEW, Arrays.asList(TripTag.ADVENTURE)),
            Map.entry(ExpectationTag.FRIENDSHIP, Arrays.asList(TripTag.FRIENDSHIP)));

    private Map<ActivityTag, List<TripTag>> activityToTripTagMap = Map.ofEntries(
            Map.entry(ActivityTag.PHOTO, Arrays.asList(TripTag.PHOTO)),
            Map.entry(ActivityTag.INSTA, Arrays.asList(TripTag.PHOTO, TripTag.INFLUENCER)),
            Map.entry(ActivityTag.NETWORK, Arrays.asList(TripTag.FRIENDSHIP, TripTag.ADVENTURE, TripTag.PASSION)),
            Map.entry(ActivityTag.EXTREME, Arrays.asList(TripTag.PHYSICAL)),
            Map.entry(ActivityTag.SWIM, Arrays.asList(TripTag.PHYSICAL)),
            Map.entry(ActivityTag.DRIVE, Arrays.asList(TripTag.ADVENTURE, TripTag.REFRESH)),
            Map.entry(ActivityTag.WALK, Arrays.asList(TripTag.REFRESH)),
            Map.entry(ActivityTag.THEMEPARK, Arrays.asList(TripTag.CULTURE)),
            Map.entry(ActivityTag.MARKET, Arrays.asList(TripTag.ADVENTURE)),
            Map.entry(ActivityTag.HOTEL, Arrays.asList(TripTag.REST)),
            Map.entry(ActivityTag.VLOG, Arrays.asList(TripTag.INFLUENCER)),
            Map.entry(ActivityTag.EAT, Arrays.asList(TripTag.EAT)),
            Map.entry(ActivityTag.BAR, Arrays.asList(TripTag.EAT)),
            Map.entry(ActivityTag.CAFE, Arrays.asList(TripTag.EAT, TripTag.COFFEE)),
            Map.entry(ActivityTag.SHOPPING, Arrays.asList()),
            Map.entry(ActivityTag.SHOW, Arrays.asList(TripTag.CULTURE)),
            Map.entry(ActivityTag.MUSEUM, Arrays.asList(TripTag.CULTURE)));

    private List<TripTag> tripTagFrequencyList = Stream
            .concat(expectationToTripTagMap.values().stream(), activityToTripTagMap.values().stream())
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    private Map<TripTag, Integer> tripTagTotalFrequencyMap = Map.ofEntries(
            Arrays.asList(TripTag.values())
                    .stream()
                    .map(it -> Map.entry(it, Collections.frequency(tripTagFrequencyList, it)))
                    .toArray(Map.Entry[]::new)
                    );

    /* 캐릭터 및 여행 성향 태그 계산 */
    @Transactional
    public Mono<TestResult> generateResult(String id, TestAnswer testAnswer) {
        log.info("[GenerateResultService] generateResultService");

        Mono<List<TripTag>> tripTaglist = generateTripTagList(testAnswer);
        Mono<CharacterId> characterResult = tripTaglist.flatMap(it -> generateCharacter(testAnswer, it));

        return Mono.zip(tripTaglist, characterResult)
                .map(
                        it -> new TestResult(it.getT1(), it.getT2(), new HashMap()));
    }

    /* 여행 성향 태그 계산 */
    @Transactional
    public Mono<List<TripTag>> generateTripTagList(TestAnswer testAnswer) {

        Stream<List<TripTag>> expectationTripTags = testAnswer.getHashtag().get("expectation")
                .stream()
                .map(index -> expectationToTripTagMap.get(ExpectationTag.values()[index]));

        Stream<List<TripTag>> activityTripTags = testAnswer.getHashtag().get("activity")
                .stream()
                .map(index -> activityToTripTagMap.get(ActivityTag.values()[index]));

        List<TripTag> tripTags = Stream.concat(expectationTripTags, activityTripTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Map<TripTag, Integer> tripTagFrequencyMap = Map.ofEntries(
                tripTagTotalFrequencyMap.entrySet().stream().map(
                        entry -> Map.entry(
                                entry.getKey(),
                                Collections.frequency(tripTags, entry.getKey())))
                        .toArray(Map.Entry[]::new));

        List<TripTag> tripTagList = tripTagFrequencyMap.entrySet().stream()
                .filter(
                        entry -> (entry.getValue() > 2)
                                || (tripTagTotalFrequencyMap.get(entry.getKey()) == 0)
                                || (entry.getValue() / tripTagTotalFrequencyMap.get(entry.getKey()) > 0.34))
                .map(entry -> entry.getKey())
                .toList();

        log.info(
                "[generateTripTagList] tripTagFrequencyMap=" + tripTagFrequencyMap.toString() + "\n" +
                        "[generateTripTagList] tripTagList=" + tripTagList.toString());

        // tripTagList.add(TripTag.DEFAULT);
        return Mono.just(tripTagList);
    }

    /* 캐릭터 계산 */
    @Transactional
    public Mono<CharacterId> generateCharacter(TestAnswer testAnswer, List<TripTag> tripTagList) {

        Map<CharacterId, Float> map = new HashMap<CharacterId, Float>();

        map.put(CharacterId.bee, (tripTagList.contains(TripTag.PASSION) ? 1 : 0f)
                + (testAnswer.getLeadership() > 2 ? 1 : 0)
                + (testAnswer.getSchedule().get("schedule") > 2 ? testAnswer.getSchedule().get("schedule") - 2 : 0));

        map.put(CharacterId.sloth, (tripTagList.contains(TripTag.REST) ? 1 : 0f)
                + (testAnswer.getLeadership() < 2 ? 1 : 0)
                + (testAnswer.getSchedule().get("schedule") <= 2 ? 3 - testAnswer.getSchedule().get("schedule") : 0));

        map.put(CharacterId.panda, (tripTagList.contains(TripTag.EAT) ? 1f : 0f) * (
                // + ((tripTagList.contains(TripTag.COFFEE) ? 1 : 0))
                // + (testAnswer.getRestaurant().get("dailyBudget") >= 12000 ? (testAnswer.getRestaurant().get("dailyBudget") - 12000) / 4000 : 0)
                + (testAnswer.getRestaurant().get("specialBudget") >= 40000 ? testAnswer.getRestaurant().get("specialBudget") / 20000 - 1 : 0)
                // + (testAnswer.getRestaurant().get("specialCount") / 2 )
                )
                );

        map.put(CharacterId.racoon, (tripTagList.contains(TripTag.CULTURE) ? 1 : 0f)
                + (testAnswer.getHashtag().get("activity").contains(ActivityTag.SHOPPING.getValue()) ? 1 : 0f )
                + (testAnswer.getHashtag().get("city").contains(CityTag.LOUD.getValue()) ? 1 : 0f )
                + (testAnswer.getHashtag().get("city").contains(CityTag.MODERN.getValue()) ? 1 : 0f )
                );
                // + (testAnswer.getCity().get("metropolis") > 3 ? testAnswer.getCity().get("metropolis") - 2 : 0));

        Comparator<Entry<CharacterId, Float>> comparator = new Comparator<Entry<CharacterId, Float>>() {
            @Override
            public int compare(Entry<CharacterId, Float> e1, Entry<CharacterId, Float> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        };

        return Mono.just(
            Collections.max( map.entrySet(), comparator).getKey()
        );
    }

    /* 여행지 계산 */
//     @Transactional
//     public Mono<Map<String, Float>> generateCityResult(TestAnswer testAnswer) {
//         return Mono.just(Arrays.asList("shiretoko", "biei", "tokyo", "kyoto"));
//         return(
//                 Map<String, Float>
//         )
//     }
    
    private Boolean assertWasAckowledged(UpdateResult updateResult) {
        assert updateResult.wasAcknowledged();
        return updateResult.wasAcknowledged();
    }

    private Mono<Boolean> setCharacter(String id, CharacterId character) {
        return profileRepository.updateById(id, "character", character)
                // profileRepository.findById(id).map(it->it.setCharacter(character))
                .map(this::assertWasAckowledged);
    }

    // @Transactional
    // public Mono<ResponseEntity<Profile>> generateResult_(String id) {
    // log.info("[GenerateResultService] generateResultService");

    // Mono<TestAnswer> testAnswer = profileRepository.findById(id).map(it ->
    // it.getTestAnswer());

    // /* Save tripTagList Intermediate to use it to generate cityGroup. */
    // Mono<List<TripTag>> tripTaglist = generateTripTagList(testAnswer.block());

    // Utils.logMono(tripTaglist, "generateResult: tripTaglist=");

    // /* Update TestResults to DB and get update results. */
    // Mono<Boolean> setTripTagListResult = tripTaglist.flatMap(it -> {
    // return profileRepository.updateById(id, "tripTagList", it);
    // }).map(this::assertWasAckowledged);

    // Mono<Boolean> setCharacterResult =
    // generateCharacter(testAnswer.block()).flatMap(characterId -> {
    // return profileRepository.updateById(id, "character", character);
    // }).map(this::assertWasAckowledged);

    // Mono<Boolean> setCityGroupResult =
    // generateCityGroup(tripTaglist).flatMap(cityGroup -> {
    // return profileRepository.updateById(id, "cityGroup", cityGroup);
    // }).map(this::assertWasAckowledged);

    // //
    // Utils.<Boolean>logMono(setCharacterResult.map(it->it.wasAcknowledged()),
    // // "generateResult: setCharacterResult: wasAcknowledged=");

    // /* Get user data only after all the updates are successfully completed. */
    // Mono<Profile> data = Mono.zip(setCharacterResult, setTripTagListResult,
    // setCityGroupResult)
    // .then(profileRepository.findById(id));

    // // Utils.<Character>logMono(data.map(it->it.getCharacter()),
    // // "generateResult: character(Get)=");

    // // Mono.zip(id, seteCharacterResult).map(it -> {

    // // // Mono<UserData> data = profileRepository.save(new UserData(it.getT1(),
    // // it.getT2(), it.getT3(), it.getT4()));

    // // log.info("generateResult: it.getT3()=", it.getT3());
    // // /* Set CharacterId */
    // // Mono<UserData> afterSetCharacterId =
    // // profileRepository.setCharacterById(it.getT1(), it.getT3())
    // // .flatMap(updateResult -> {
    // // if(updateResult.wasAcknowledged()){
    // // return profileRepository.findById(it.getT1());
    // // }
    // // else{
    // // throw new Error("setCharacterById: Update was not acknowledged");
    // // }
    // // });

    // // Utils.<UserData>logMono(afterSetCharacter, "generateResult:
    // // afterSetCharacter=");
    // // // reactiveMongoTemplate.update(UserData.class)
    // // // .matching(where("id").is(publisher.id))
    // // // Utils.<UserData>logMono(data, "generateResult: data=");

    // // return afterSetCharacter;

    // // }
    // // )
    // return data.map(it -> ResponseEntity.ok().body(it))
    // .defaultIfEmpty(ResponseEntity.badRequest().build());
    // }

    // @Transactional
    // public ResponseEntity<UserData> generateResult(UserData userData){

    // TestAnswer testAnswer = userData.getTestAnswer();

    // log.info("GenerateResultService: generateResult: userData(begin)=" +
    // userData.toString());

    // userData.setCharacter(
    // this.characterRepository.findById(generateCharacterId(testAnswer)).block()
    // );

    // log.info("GenerateResultService: generateResult: userData(begin)=" +
    // userData.toString());

    // userData.setTripTagList(generateTripTagList(testAnswer));
    // /* Intialize CityGroup */
    // userData.setCityGroup(new ArrayList<>());

    // log.info("GenerateResultService: generateResult: userData(end)=" +
    // userData.toString());
    // return profileRepository.save(userData)
    // .map(it-> ResponseEntity.ok().body(it))
    // .defaultIfEmpty(ResponseEntity.badRequest().build()).block();
    // }

    // public Mono<List<String>> getLeaderList( Flux<TestAnswer> testAnswerList ) {

    // Mono<Chemistry> data = Mono.just(new Chemistry( ));
    // return data.map(it-> ResponseEntity.ok().body(it))
    // .defaultIfEmpty(ResponseEntity.badRequest().build());
    // }

}
