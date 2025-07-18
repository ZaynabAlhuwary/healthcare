package com.healthcare.healthcare_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HealthcareSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthcareSystemApplication.class, args);
    }
}