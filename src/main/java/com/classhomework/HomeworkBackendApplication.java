package com.classhomework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.classhomework.repository")
public class HomeworkBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeworkBackendApplication.class, args);
    }
}