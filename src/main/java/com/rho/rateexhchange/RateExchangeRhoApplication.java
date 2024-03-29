package com.rho.rateexhchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RateExchangeRhoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateExchangeRhoApplication.class, args);
    }

}
