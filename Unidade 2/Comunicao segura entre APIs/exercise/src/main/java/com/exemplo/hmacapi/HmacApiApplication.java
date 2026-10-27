package com.exemplo.hmacapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HmacApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HmacApiApplication.class, args);
    }
}
