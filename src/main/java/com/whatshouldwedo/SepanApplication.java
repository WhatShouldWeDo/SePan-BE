package com.whatshouldwedo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SepanApplication {
    public static void main(String[] args) {
        SpringApplication.run(SepanApplication.class, args);
    }
}
