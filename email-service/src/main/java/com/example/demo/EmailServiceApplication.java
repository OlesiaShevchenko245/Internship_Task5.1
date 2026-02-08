package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.model.EmailMessageDoc;

@EnableScheduling
@SpringBootApplication
public class EmailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailServiceApplication.class, args);
	}

	@Bean
	@Profile("!test")
	CommandLineRunner ensureEmailIndex(ElasticsearchOperations ops) {
		return args -> {
			var indexOps = ops.indexOps(EmailMessageDoc.class);

			if (!indexOps.exists()) {
				indexOps.create();
			}

			indexOps.putMapping(indexOps.createMapping());
		};
	}
}
