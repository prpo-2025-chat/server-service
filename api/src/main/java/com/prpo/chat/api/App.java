package com.prpo.chat.api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.prpo.chat")
@EnableMongoRepositories(basePackages = "com.prpo.chat.service.repository")
public class App implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  public static void main(final String[] args) {
    SpringApplication.run(App.class, args);
  }

  @Override
  public void run(String... args) {
    logger.info("Logging all environment variables at startup:");
    Map<String, String> env = System.getenv();
    env.forEach((key, value) -> logger.info("{}={}", key, value));
  }

}
