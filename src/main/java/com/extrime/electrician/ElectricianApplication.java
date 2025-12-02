package com.extrime.electrician;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElectricianApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectricianApplication.class, args);
        System.out.println("Открыть сайт http://localhost:8081");
    }
}
