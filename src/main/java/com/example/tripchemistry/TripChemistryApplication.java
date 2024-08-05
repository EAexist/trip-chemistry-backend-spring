package com.example.tripchemistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@EnableReactiveMongoRepositories
@EnableAutoConfiguration
@ComponentScan
public class TripChemistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripChemistryApplication.class, args);
	}

	/* MongoDB */
	// @Value("${spring.data.mongodb.uri}")
	// private ConnectionString connectionString;

	// @Override
	// protected String getDatabaseName() {
	// 	return "cluster-main";
	// }

	// @Bean
    // public MongoClient mongoClient() {
    //     MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
    //     return MongoClients.create(mongoClientSettings);
    // }
}
