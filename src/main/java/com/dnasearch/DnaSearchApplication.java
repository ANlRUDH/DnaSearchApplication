package com.dnasearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DnaSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(DnaSearchApplication.class, args);
    }
} 