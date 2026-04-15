package com.escuela.api.controllers;

import com.escuela.api.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class BotController {

    @Autowired
    private AlumnoRepository repository;

    @GetMapping("/preguntar")
    public String responder(@RequestParam String msg) {
        String pregunta = msg.toLowerCase();

        // Lógica para el comedor
        if (pregunta.contains("comen") || pregunta.contains("cuantos")) {
            // Filtramos directamente en la lista los que NO tienen "No"
            long cantidad = repository.findAll().stream()
                    .filter(a -> a.getTurnoComedor() != null && !a.getTurnoComedor().equalsIgnoreCase("No"))
                    .count();

            return "Hoy se quedan a comer " + cantidad + " alumnos. ¿Querés que te pase la lista?";
        }

        // Saludo inicial que vimos en tu captura
        if (pregunta.contains("hola")) {
            return "¡Hola Román! Soy el asistente de la Escuela. ¿En qué puedo ayudarte con los alumnos?";
        }

        return "Todavía no entiendo esa pregunta, pero puedo decirte cuántos alumnos comen hoy.";
    }
}
