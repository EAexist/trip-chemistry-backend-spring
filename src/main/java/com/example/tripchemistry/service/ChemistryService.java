package com.example.tripchemistry.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tripchemistry.DTO.ChemistryDTO;
import com.example.tripchemistry.DTO.ProfileDTO;
import com.example.tripchemistry.model.Chemistry;
import com.example.tripchemistry.model.Profile;
import com.example.tripchemistry.model.TestAnswer;
import com.example.tripchemistry.model.answer.CityChemistry;
import com.example.tripchemistry.repository.ChemistryRepository;
import com.example.tripchemistry.repository.ProfileRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

@Service
@AllArgsConstructor
@Slf4j
public class ChemistryService {

	/* Repository */
	private final ChemistryRepository chemistryRepository;
	private final ProfileRepository profileRepository;

	/* Service */
	private final DTOService dtoService;
	private final SequenceGeneratorService sequenceGeneratorService;
	private final ResponseService responseService;

	/* 케미스트리 생성 */
	@Transactional
	public Mono<ResponseEntity<ChemistryDTO>> createChemistry(ChemistryDTO.CreateDTO createDTO) {
		log.info(String.format("[createChemistry]\tcreateDTO=%s", createDTO.toString()));

		return this.createChemistryHelper(createDTO)
				.flatMap(dtoService::chemistryToDTO)
				.flatMap(responseService::createResponseEntity);
	}

	/* 샘플 케미스트리 생성 */
	@Transactional
	public Mono<Profile> createSampleChemistry(String userId) {
		log.info(String.format("[createSampleChemistry]\tuserId=%s", userId.toString()));

		/* Generate New Id */
		// Mono<Chemistry> chemistryMono = chemistryRepository.findById("sample").map(
		// chemistry -> new Chemistry( newChemistryId, chemistry ))
		// .flatMap(chemistryRepository::save);

		/* Create New Sample Chemistry */
		Mono<Chemistry> chemistryMono = sequenceGeneratorService.generateId("chemistry")
				.zipWith(chemistryRepository.findById("sample"))
				.flatMap(
						it -> chemistryRepository.save(new Chemistry(it.getT1(), it.getT2())));

		// Mono<Chemistry> chemistryMono = chemistryRepository.save( new Chemistry(id,
		// createDTO) );
		// Mono<String> chemistryIdMono =
		// Mono.just(id).zipWith(chemistryRepository.findById("sample"))
		// .map(it -> new Chemistry(it.getT1(), it.getT2()))
		// .flatMap(chemistryRepository::save)
		// .map(Chemistry::getId);

		/* Join */
		return chemistryMono.map(Chemistry::getId)
				.flatMap(chemistryId -> this.joinChemistryHelper(userId, chemistryId))
				.map(it -> {
					log.info(String.format("[createSampleChemistry]\tnew Chemistry=%s", it.toString()));
					return it;
				})
				.flatMap(it -> profileRepository.findById(userId));

		// return chemistryMono.flatMap(it -> profileRepository.findById(userId));
		// return chemistryMono.flatMap(chemistry -> this.joinChemistryHelper(userId,
		// chemistry.getId()));
	}

	@Transactional
	public Mono<Chemistry> createChemistryHelper(ChemistryDTO.CreateDTO createDTO) {
		log.info(String.format("[createChemistryHelper]\tcreateDTO=%s", createDTO.toString()));

		/* Generate New Id */
		Mono<String> chemistryIdMono = sequenceGeneratorService.generateId("chemistry");

		Mono<Chemistry> chemistryMono = chemistryIdMono.flatMap(
				id -> chemistryRepository.save(new Chemistry(id, createDTO)));

		return chemistryMono.flatMap(chemistry -> this.joinChemistryHelper(createDTO.getUserId(), chemistry.getId()));
	}

	/* 케미스트리 참여 */
	@Transactional
	public Mono<ResponseEntity<ChemistryDTO>> joinChemistry(String userId, String chemistryId) {
		log.info(String.format("[joinChemistry]\tuserId=%s\\tchemistryId=%s", userId, chemistryId));

		return this.joinChemistryHelper(userId, chemistryId)
				.flatMap(dtoService::chemistryToDTO)
				.flatMap(responseService::createResponseEntity);
	}

	@Transactional
	public Mono<Chemistry> joinChemistryHelper(String userId, String chemistryId) {
		log.info(String.format("[joinChemistryHelper]\tuserId=%s\\tchemistryId=%s", userId, chemistryId));

		/* 기존 케미스트리 */
		Mono<Chemistry> chemistryMono = chemistryRepository.findById(chemistryId);

		/* 기존 프로필 */
		Mono<Profile> profileMono = profileRepository.findById(userId);

		/*
		 * 프로필의 기존 chemistryIdList 에 chemistryId 가 없는 경우 chemistryId 가 추가된 프로필. 이미 추가되어
		 * 있을 경우 Empty Mono.
		 */
		Mono<Profile> updatedProfileMono = profileMono
				/* Publish only if the user hasn't joned the chemistry before. */
				.filterWhen(profile -> Mono.just(!profile.getChemistryIdList().contains(chemistryId)))
				/* Add chemistryId to the user profile's chemistry id list. */
				.map(profile -> {
					List<String> chemistryIdList = profile.getChemistryIdList();
					chemistryIdList.add(0, chemistryId);
					profile.setChemistryIdList(chemistryIdList);
					return profile;
				})
				/* Save the new profile. This saves to repository and updates to MongoDB. */
				.flatMap(profileRepository::save)
				.map(profile -> {
					log.info(String.format("[joinChemistry]\tnew Profile=%s", profile));
					return profile;
				});

		/*
		 * 케미스트리의 기존 profileIdList 에 profileId 가 없는 경우 profileId가 추가된 케미스트리. 이미 추가되어 있을
		 * 경우 Empty Mono.
		 */
		Mono<Chemistry> updatedChemistryMono =
				// updatedProfileMono.zipWith(chemistryMono, (a, b) -> b)
				chemistryMono
						.map(it -> {
							log.info(String.format("[joinChemistry]\tchemistry=%s", it.toString()));
							return it;
						})
						.filterWhen(chemistry -> Mono.just(!chemistry.getProfileIdList().contains(userId)))
						/* Add userId to chemistry's profile id list. */
						.map(chemistry -> this.addProfileList(chemistry, List.of(userId)));

		Mono<Chemistry> resultMono = updatedProfileMono
				.map(Optional::of)
				.map(it -> it.map(Profile::getTestAnswer))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(it -> {
					log.info(String.format("[joinChemistry]\tprofile=%s", it.toString()));
					return it;
				})
				.zipWith(updatedChemistryMono, (a, b) -> b)
				/* 1. 사용자가 새롭게 케미스트리에 참여했으며, 2. 사용자가 테스트를 완료한 경우 케미스트리를 새롭게 계산 */
				.flatMap(this::generateChemistry)
				/* 이외의 경우 계산 없이 사용자만 추가된 케미스트리 사용 */
				.switchIfEmpty(updatedChemistryMono)
				/* 케미스트리가 업데이트된 경우 Save. This saves to repository and updates to MongoDB. */
				.flatMap(chemistryRepository::save)
				/* 이미 참여한 사용자일 경우 기존 케미스트리 값으로 응답. */
				.switchIfEmpty(chemistryMono);

		return resultMono;
	}

	/* 케미스트리 GET */
	@Transactional
	public Mono<ResponseEntity<ChemistryDTO>> getChemistryById(String id) {
		Mono<ChemistryDTO> chemistryDTOMono = chemistryRepository.findById(id)
				.flatMap(dtoService::chemistryToDTO);

		return chemistryDTOMono
				.map(it -> ResponseEntity.ok().body(it))
				.defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	/* 케미스트리 계산 */
	// @Transactional
	// public Mono<Chemistry> generateChemistry_(String id) {
	// 	log.info(String.format("[generateChemistry] id=%s", id));

	// 	Mono<Chemistry> chemistryMono = chemistryRepository.findById(id);

	// 	Mono<List<String>> idList = chemistryMono.map(Chemistry::getProfileIdList);
	// 	Flux<Profile> profiles = idList.flatMapMany(profileRepository::findAllTestResultWithNicknameById);
	// 	Mono<Map<String, Float>> city = idList.flatMap(profileRepository::getCityChemistry);
	// 	Mono<Map<String, List<String>>> schedule = getScheduleChemistry(profiles);
	// 	// Mono<List<String>> leaderList =
	// 	// idList.flatMapMany(profileRepository::findLeaderAll).collectList();
	// 	// Mono<List<String>> budgetChemistryText = getBudgetChemistry(profiles);

	// 	return idList.filter(it -> (it.size() > 1))
	// 			.then(chemistryMono)
	// 			.then(
	// 				Mono.zip(
	// 					chemistryMono,
	// 					city,
	// 					schedule
	// 				// scheduleChemistryText, budgetChemistryText
	// 				))
	// 			.map(it -> {
	// 				it.getT1().setCity(it.getT2());
	// 				it.getT1().setSchedule(it.getT3());
	// 				// it.getT1().setScheduleChemistryText(it.getT4());
	// 				// it.getT1().setBudgetChemistryText(it.getT5());
	// 				return it.getT1();
	// 			})
	// 			.flatMap(chemistryRepository::save)
	// 			.switchIfEmpty(chemistryMono);

	// 	// if (chemistry.getProfileIdList().size() > 1) {
	// 	// log.info("[generateChemistry] chemistry has more than one profile.");
	// 	// Mono.zip(leaderList, cityChemistry, scheduleChemistryText,
	// 	// budgetChemistryText)
	// 	// .subscribe(it -> {
	// 	// chemistry.setLeaderList(it.getT1());
	// 	// chemistry.setCityChemistry(it.getT2());
	// 	// chemistry.setScheduleChemistryText(it.getT3());
	// 	// chemistry.setBudgetChemistryText(it.getT4());
	// 	// });
	// 	// }

	// 	// return chemistry;
	// }

	@Transactional
	public Mono<Chemistry> generateChemistry(Chemistry chemistry) {
		log.info(String.format("[generateChemistry]\t(before update)\tchemistry=%s", chemistry.toString()));

		List<String> idList = chemistry.getProfileIdList();
		Flux<Profile> profiles = profileRepository.findAllTestResultWithNicknameById(idList).map(it ->{
			log.info(String.format("[generateChemistry]\tprofiles=%s", it.toString()));
			return it;
		});
		// Mono<List<String>> leaderList =
		// profileRepository.findLeaderAll(idList).collectList();
		// Mono<Map<String, Float>> city = profileRepository.getCityChemistry(idList);
		Mono<Map<String, List<String>>> schedule = getScheduleChemistry(profiles);
		Mono<Map<String, List<String>>> restaurant = getRestaurantChemistry(profiles);
		// Mono<List<String>> budgetChemistryText = getBudgetChemistry(profiles);

		return profiles
				.collectList()
				.map(it -> { log.info(String.format("[getScheduleChemistry]\tprofiles=%s", it.toString())); return it; })
				.filter(it -> it.size() > 1)
				.flatMap(it ->
					Mono.zip(
						// city, 
						schedule,
						restaurant
					)
				)
				.map(it -> {
					// chemistry.setCity(it.getT1());
					chemistry.setIdLists(Map.of(
						"busy", it.getT1().get("busy"),
						"relaxing", it.getT1().get("relaxing"),
						"lowDailyRestaurantBudget", it.getT2().get("lowDailyRestaurantBudget"),
						"highDailyRestaurantBudget", it.getT2().get("highDailyRestaurantBudget")
					));
					// chemistry.setSchedule(it.getT3());
					// chemistry.setBudgetChemistryText(it.getT4());
					log.info(String.format("[generateChemistry]\t(after update)\tchemistry=%s",
							chemistry.toString()));
					return chemistry;
				})
				.defaultIfEmpty(chemistry);
		// .flatMap(chemistryRepository::save)
		// .subscribe();
		// }
		// log.info(String.format("[generateChemistry](returns)\tchemistry=%s",
		// chemistry.toString()));

		// return chemistry;

		// if (chemistry.getProfileIdList().size() > 1) {
		// log.info("[generateChemistry] chemistry has more than one profile.");
		// Mono.zip(leaderList, cityChemistry, scheduleChemistryText,
		// budgetChemistryText)
		// .subscribe(it -> {
		// chemistry.setLeaderList(it.getT1());
		// chemistry.setCityChemistry(it.getT2());
		// chemistry.setScheduleChemistryText(it.getT3());
		// chemistry.setBudgetChemistryText(it.getT4());
		// });
		// }

		// return chemistry;
	}

	// @Transactional
	// /* 케미스트리 계산 */
	// public Mono<ResponseEntity<Chemistry>> getChemistry(String id, List<String>
	// idList) {

	// Flux<Profile> profiles =
	// profileRepository.findAllTestResultWithNicknameById(idList);
	// Mono<List<String>> leaderList =
	// profileRepository.findLeaderAll(idList).collectList();
	// Mono<CityChemistry> cityChemistry =
	// profileRepository.getCityChemistry(idList);
	// Mono<List<String>> scheduleChemistryText = getScheduleChemistry(profiles);
	// Mono<List<String>> budgetChemistryText = getBudgetChemistry(profiles);

	// Mono<Chemistry> data = Mono.zip(leaderList, cityChemistry,
	// scheduleChemistryText, budgetChemistryText)
	// .map(it -> new Chemistry(id, idList, it.getT1(), it.getT2(), it.getT3(),
	// it.getT4()));

	// return data.map(it -> ResponseEntity.ok().body(it))
	// .defaultIfEmpty(ResponseEntity.badRequest().build());
	// }

	private Mono<Map<String, List<String>>> getScheduleChemistry(Flux<Profile> profiles) {

		int timeConflictThresholdhours = 4;
		
		Flux<String> idList = profiles.map(Profile::getId);

		Flux<Integer> activeHoursFlux = profiles
			.map(Profile::getTestAnswer)
			.map(
				it ->
				it.getSchedule().get("endTime") - it.getSchedule().get("startTime")
			);

		Mono<Boolean> hasConflictMono = activeHoursFlux
				.collectList()
				.map(it -> (Collections.max(it) - Collections.min(it)) > timeConflictThresholdhours);

		Mono<Double> averageHoursMono = activeHoursFlux
				.collectList()
				.map(it ->{ log.info(String.format("[getScheduleChemistry]\tactiveHoursFlux=%s", it.toString())); return it; })
				.map(it -> it.stream().mapToInt(v -> v).average().getAsDouble());

		Flux<Tuple3<String, Integer, Double>> activeHoursProfileFlux = Flux.zip(idList, activeHoursFlux, averageHoursMono.cache().repeat());

		Flux<String> relaxingMemberIds = activeHoursProfileFlux
				.filter(it -> it.getT2() < it.getT3())
				.map(it -> it.getT1());

		Flux<String> busyMemberIds = activeHoursProfileFlux
				.filter(it -> it.getT2() > it.getT3())
				.map(it -> it.getT1());

		return(
			hasConflictMono.
			then(relaxingMemberIds.collectList())
			.zipWith(busyMemberIds.collectList())
				.map( it ->					
					Map.of(
						"relaxing", it.getT1(),
						"busy", it.getT2()
					)
				)
		);
	}

		private Mono<Map<String, List<String>>> getRestaurantChemistry(Flux<Profile> profiles) {

			int dailyBudgetConflictThreshold = 4000;	

			Flux<String> idList = profiles.map(Profile::getId);

			Flux<Integer> dailyRestaurantBudgetAnswerFlex = profiles
			.map(Profile::getTestAnswer)
			.map(
				it ->
				it.getRestaurant().get("dailyBudget")
			);

	
			Mono<Boolean> hasConflictMono = dailyRestaurantBudgetAnswerFlex
					.collectList()
					.map(it -> (Collections.max(it) - Collections.min(it)) > dailyBudgetConflictThreshold);
	
			Mono<Double> averageBudgetMono = dailyRestaurantBudgetAnswerFlex
					.collectList()
					.map(it -> it.stream().mapToInt(v -> v).average().getAsDouble());
					
		Flux<Tuple3<String, Integer, Double>> dailyRestaurantBudgetAnswerProfileFlex = Flux.zip(idList, dailyRestaurantBudgetAnswerFlex, averageBudgetMono.cache().repeat());
	
			Flux<String> lowDailyBudgetMemberIds = dailyRestaurantBudgetAnswerProfileFlex
					.filter(it -> it.getT2() < it.getT3())
					.map(it -> it.getT1());
	
	
			Flux<String> highDailyBudgetMemberIds = dailyRestaurantBudgetAnswerProfileFlex
					.filter(it -> it.getT2() > it.getT3())
					.map(it -> it.getT1());
	
			return(
				hasConflictMono.
				then(lowDailyBudgetMemberIds.collectList())
				.zipWith(highDailyBudgetMemberIds.collectList())
					.map( it ->					
						Map.of(
							"lowDailyRestaurantBudget", it.getT1(),
							"highDailyRestaurantBudget", it.getT2()
						)
					)
			);

		// Mono<Tuple2<List<String>, List<String>>> nicknameTuple = hasConflictMono
		// 		.then(profiles.collectList())
		// 		.zipWith(averageHoursMono)
		// 		.map(
		// 				it -> it.getT1().stream().filter(profile -> profile.getTestAnswer().getSchedule())

		// 		)

		// 		.map(it -> Tuples.of(
		// 				it.stream()
		// 						.filter(profile -> profile.getTestAnswer()
		// 								.getSchedule().get("schedule") < 3)
		// 						.map(Profile::getNickname)
		// 						.toList(),
		// 				it.stream()
		// 						.filter(profile -> profile.getTestAnswer()
		// 								.getSchedule().get("schedule") > 3)
		// 						.map(Profile::getNickname)
		// 						.toList())

		// 		);

		// Mono<String> summary = scheduleAnswerListMono.map(it -> {
		// 	boolean isAvgHigh = it.stream().mapToInt(v -> v).average().getAsDouble() > 3;
		// 	return (isAvgHigh
		// 			? "여행 일정은 알찬게 좋을 것 같아요."
		// 			: "여행 일정은 여유로운게 좋을 것 같아요.");
		// });

		// Mono<List<String>> detail = nicknameTuple
		// 		.map(it -> Arrays.asList(
		// 				String.join(
		// 						", ",
		// 						it.getT2().stream()
		// 								.map(nickname -> String.format(
		// 										"%%%s%% 님", nickname))
		// 								.toList())
		// 						// Collections.nCopies(it.getT2(), "/h 님"))
		// 						+ "은 "
		// 						+ (it.getT1().size() > 1
		// 								? "다른 친구들이 "
		// 								: String.format("%%%s%% 님이 ",
		// 										it.getT1().get(0)))
		// 						+ "지치지 않도록 체력과 여유를 생각하면서 일정에 대한 욕심을 덜어보세요.",
		// 				String.join(
		// 						", ",
		// 						it.getT1().stream()
		// 								.map(nickname -> String.format(
		// 										"%%%s%% 님", nickname))
		// 								.toList())
		// 						+ "은 "
		// 						+ (it.getT2().size() > 1
		// 								? "다른 친구들이 "
		// 								: String.format("%%%s%% 님이 ",
		// 										it.getT2().get(0)))
		// 						+ "아쉬워하지 않도록 새로운 계획을 적극적으로 받아들이려고 노력해보세요. 카페에 모여서 여행에서 하고 싶은 것들에 대해 듣는 시간을 가져보는 건 어떨까요?"

		// 		))
		// 		.defaultIfEmpty(Arrays
		// 				.asList("여행 일정에 대한 모두의 생각이 비슷해요. 마음이 잘 맞으니 편한 마음으로 일정을 게획하면 되겠어요."));

		// return summary.zipWith(detail)
		// 		.map(it -> {
		// 			List<String> r = new ArrayList<String>();
		// 			r.add(it.getT1());
		// 			r.addAll(it.getT2());
		// 			return r;
		// 		});
	}

	private Mono<List<String>> getScheduleChemistryLegacy(Flux<Profile> profiles) {

		Mono<List<Integer>> scheduleAnswerListMono = profiles.map(Profile::getTestAnswer)
				.map(TestAnswer::getSchedule)
				.map(it -> it.get("schedule"))
				.collectList();

		Mono<Boolean> hasConflictMono = scheduleAnswerListMono.map(it -> {
			int max = Collections.max(it);
			int min = Collections.min(it);
			return (max > 3) && (min < 3);
		})
				.filter(hasConflict -> hasConflict);

		Mono<Tuple2<List<String>, List<String>>> nicknameTuple = hasConflictMono
				.zipWith(profiles.collectList())
				.map(Tuple2::getT2)
				.map(it -> Tuples.of(
						it.stream()
								.filter(profile -> profile.getTestAnswer()
										.getSchedule().get("schedule") < 3)
								.map(Profile::getNickname)
								.toList(),
						it.stream()
								.filter(profile -> profile.getTestAnswer()
										.getSchedule().get("schedule") > 3)
								.map(Profile::getNickname)
								.toList()));

		Mono<String> summary = scheduleAnswerListMono.map(it -> {
			boolean isAvgHigh = it.stream().mapToInt(v -> v).average().getAsDouble() > 3;
			return (isAvgHigh
					? "여행 일정은 알찬게 좋을 것 같아요."
					: "여행 일정은 여유로운게 좋을 것 같아요.");
		});

		Mono<List<String>> detail = nicknameTuple
				.map(it -> Arrays.asList(
						String.join(
								", ",
								it.getT2().stream()
										.map(nickname -> String.format(
												"%%%s%% 님", nickname))
										.toList())
								// Collections.nCopies(it.getT2(), "/h 님"))
								+ "은 "
								+ (it.getT1().size() > 1
										? "다른 친구들이 "
										: String.format("%%%s%% 님이 ",
												it.getT1().get(0)))
								+ "지치지 않도록 체력과 여유를 생각하면서 일정에 대한 욕심을 덜어보세요.",
						String.join(
								", ",
								it.getT1().stream()
										.map(nickname -> String.format(
												"%%%s%% 님", nickname))
										.toList())
								+ "은 "
								+ (it.getT2().size() > 1
										? "다른 친구들이 "
										: String.format("%%%s%% 님이 ",
												it.getT2().get(0)))
								+ "아쉬워하지 않도록 새로운 계획을 적극적으로 받아들이려고 노력해보세요. 카페에 모여서 여행에서 하고 싶은 것들에 대해 듣는 시간을 가져보는 건 어떨까요?"

				))
				.defaultIfEmpty(Arrays
						.asList("여행 일정에 대한 모두의 생각이 비슷해요. 마음이 잘 맞으니 편한 마음으로 일정을 게획하면 되겠어요."));

		return summary.zipWith(detail)
				.map(it -> {
					List<String> r = new ArrayList<String>();
					r.add(it.getT1());
					r.addAll(it.getT2());
					return r;
				});
	}

	// private Mono<List<String>> getDailyBudgetChemistry(Flux<Profile> profiles)
	// {

	// Mono<List<Integer>> budgetAnswerListMono =
	// profiles.map(Profile::getTestAnswer)
	// .map(TestAnswer::getFood)
	// .collectList();

	// Mono<Integer> medianMono = budgetAnswerListMono.map(it -> {
	// Collections.sort(it);
	// return ((it.size() % 2 == 0)
	// ? (it.get((it.size() - 1) / 2) + it.get(it.size() / 2)) / 2
	// : it.get(it.size() / 2));
	// });

	// Mono<Double> averageMono = budgetAnswerListMono
	// .map(it -> it.stream().distinct().mapToInt(v -> v).average().getAsDouble());

	// Mono<Boolean> hasConflictMono = budgetAnswerListMono.map(it -> {
	// int min = Collections.min(it);
	// int max = Collections.max(it);
	// return (max - min) > 10000;
	// })
	// .filter(hasConflict -> hasConflict);

	// Mono<Tuple2<List<String>, List<String>>> nicknameTuple = hasConflictMono
	// .zipWith(averageMono)
	// .map(Tuple2::getT2)
	// .zipWith(profiles.collectList())
	// .map(it -> Tuples.of(
	// it.getT2().stream()
	// .filter(profile -> profile.getTestAnswer()
	// .getFood() < it.getT1())
	// .map(Profile::getNickname)
	// .toList(),
	// it.getT2().stream()
	// .filter(profile -> profile.getTestAnswer()
	// .getFood() > it.getT1())
	// .map(Profile::getNickname)
	// .toList()));

	// Mono<String> summary = medianMono
	// .map(median -> String.format("여행 중 식사 한끼에는 평균적으로 %d원 정도 쓰는게 적당할 것 같아요.",
	// median));

	// Mono<List<String>> detail = nicknameTuple
	// .map(it -> Arrays.asList(
	// String.join(
	// ", ",
	// it.getT1().stream()
	// .map(nickname -> String.format(
	// "%%%s%% 님", nickname))
	// .toList())
	// // Collections.nCopies(it.getT2(), "/h 님"))
	// + "은 "
	// + (it.getT2().size() > 1
	// ? "다른 친구들이 "
	// : String.format("%%%s%% 님이 ",
	// it.getT2().get(0)))
	// + "여행에서 즐기고 싶은 음식과 레스토랑에 대해 이야기를 들어보세요. 돈을 조금 더 쓰더라도 여행 중 몇 끼는 특별한 식사를
	// 함께해봐요.",
	// String.join(
	// ", ",
	// it.getT2().stream()
	// .map(nickname -> String.format(
	// "%%%s%% 님", nickname))
	// .toList())
	// + "은 여행에서 맛있는 걸 많이 즐기고 싶은 마음이 크겠지만 "
	// + (it.getT1().size() > 1
	// ? "다른 친구들에게는 "
	// : String.format("%%%s%% 님에게는 ",
	// it.getT1().get(0)))
	// + "부담스러울 수 있을 것 같아요. 꼭 먹고 싶은 음식들을 함께 선별해보는 건 어떨까요? 합리적인 가격대에서 즐길 수 있는 식당을 새로
	// 찾아보는 것도 좋아요."

	// ))
	// .defaultIfEmpty(Arrays.asList("식비에 대한 모두의 생각이 비슷해요. 마음이 잘 맞으니 편한 마음으로 예산을 짜면
	// 되겠어요."));

	// return summary.zipWith(detail)
	// .map(it -> {
	// List<String> r = new ArrayList<String>();
	// r.add(it.getT1());
	// r.addAll(it.getT2());
	// return r;
	// });
	// }

	// private Mono<List<String>> getBudgetChemistryLegacy(Flux<Profile>
	// profiles) {

	// Mono<List<Integer>> budgetAnswerListMono =
	// profiles.map(Profile::getTestAnswer)
	// .map(TestAnswer::getFood)
	// .collectList();

	// Mono<Integer> medianMono = budgetAnswerListMono.map(it -> {
	// Collections.sort(it);
	// return ((it.size() % 2 == 0)
	// ? (it.get((it.size() - 1) / 2) + it.get(it.size() / 2)) / 2
	// : it.get(it.size() / 2));
	// });

	// Mono<Double> averageMono = budgetAnswerListMono
	// .map(it -> it.stream().distinct().mapToInt(v -> v).average().getAsDouble());

	// Mono<Boolean> hasConflictMono = budgetAnswerListMono.map(it -> {
	// int min = Collections.min(it);
	// int max = Collections.max(it);
	// return (max - min) > 10000;
	// })
	// .filter(hasConflict -> hasConflict);

	// Mono<Tuple2<List<String>, List<String>>> nicknameTuple = hasConflictMono
	// .zipWith(averageMono)
	// .map(Tuple2::getT2)
	// .zipWith(profiles.collectList())
	// .map(it -> Tuples.of(
	// it.getT2().stream()
	// .filter(profile -> profile.getTestAnswer()
	// .getFood() < it.getT1())
	// .map(Profile::getNickname)
	// .toList(),
	// it.getT2().stream()
	// .filter(profile -> profile.getTestAnswer()
	// .getFood() > it.getT1())
	// .map(Profile::getNickname)
	// .toList()));

	// Mono<String> summary = medianMono
	// .map(median -> String.format("여행 중 식사 한끼에는 평균적으로 %d원 정도 쓰는게 적당할 것 같아요.",
	// median));

	// Mono<List<String>> detail = nicknameTuple
	// .map(it -> Arrays.asList(
	// String.join(
	// ", ",
	// it.getT1().stream()
	// .map(nickname -> String.format(
	// "%%%s%% 님", nickname))
	// .toList())
	// // Collections.nCopies(it.getT2(), "/h 님"))
	// + "은 "
	// + (it.getT2().size() > 1
	// ? "다른 친구들이 "
	// : String.format("%%%s%% 님이 ",
	// it.getT2().get(0)))
	// + "여행에서 즐기고 싶은 음식과 레스토랑에 대해 이야기를 들어보세요. 돈을 조금 더 쓰더라도 여행 중 몇 끼는 특별한 식사를
	// 함께해봐요.",
	// String.join(
	// ", ",
	// it.getT2().stream()
	// .map(nickname -> String.format(
	// "%%%s%% 님", nickname))
	// .toList())
	// + "은 여행에서 맛있는 걸 많이 즐기고 싶은 마음이 크겠지만 "
	// + (it.getT1().size() > 1
	// ? "다른 친구들에게는 "
	// : String.format("%%%s%% 님에게는 ",
	// it.getT1().get(0)))
	// + "부담스러울 수 있을 것 같아요. 꼭 먹고 싶은 음식들을 함께 선별해보는 건 어떨까요? 합리적인 가격대에서 즐길 수 있는 식당을 새로
	// 찾아보는 것도 좋아요."

	// ))
	// .defaultIfEmpty(Arrays.asList("식비에 대한 모두의 생각이 비슷해요. 마음이 잘 맞으니 편한 마음으로 예산을 짜면
	// 되겠어요."));

	// return summary.zipWith(detail)
	// .map(it -> {
	// List<String> r = new ArrayList<String>();
	// r.add(it.getT1());
	// r.addAll(it.getT2());
	// return r;
	// });
	// }

	/* Helper */
	public Chemistry addProfileList(Chemistry chemistry, List<String> idList) {
		List<String> profileIdList = chemistry.getProfileIdList();
		profileIdList.addAll(idList);
		chemistry.setProfileIdList(profileIdList);
		log.info(String.format("[addProfileList]\tnew Chemistry=%s", chemistry.toString()));
		return chemistry;
	}
}
