package com.example.ketari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point for the Ketari Recruitment and Job Portal Platform.
 */
@SpringBootApplication
@EnableScheduling
public class KetariApplication {

    public static void main(String[] args) {
        SpringApplication.run(KetariApplication.class, args);
    }
}
