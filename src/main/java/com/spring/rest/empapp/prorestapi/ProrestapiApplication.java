package com.spring.rest.empapp.prorestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProrestapiApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProrestapiApplication.class, args);
    }

}
