package com.myshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AiPrintShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiPrintShopApplication.class, args);
    }
}


