package com.hoppin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HoppinApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoppinApplication.class, args);
    }

}
