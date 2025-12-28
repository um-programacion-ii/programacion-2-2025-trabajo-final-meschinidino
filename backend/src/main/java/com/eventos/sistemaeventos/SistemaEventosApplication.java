package com.eventos.sistemaeventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaEventosApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaEventosApplication.class, args);
    }

}
