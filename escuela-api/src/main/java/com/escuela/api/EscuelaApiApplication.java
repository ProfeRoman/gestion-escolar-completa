package com.escuela.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //ESTO: Activa el reloj interno para las tareas programadas
public class EscuelaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EscuelaApiApplication.class, args);
    }

}
