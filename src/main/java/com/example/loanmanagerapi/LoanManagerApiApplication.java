package com.example.loanmanagerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.loanmanagerapi.feign")
@EnableScheduling
public class LoanManagerApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanManagerApiApplication.class, args);
    }
}
