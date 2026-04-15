package com.escuela.api.controllers;

import com.escuela.api.models.Alumno;
import com.escuela.api.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alumnos")
@CrossOrigin(origins = "*")
public class AlumnoController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    // 1. LISTAR TODOS (Para el Profe)
    @GetMapping
    public List<Alumno> listar() {
        return alumnoRepository.findAll();
    }

    // 2. BUSCAR POR DNI (Para el Padre)
    @GetMapping("/buscar")
    public List<Alumno> buscarPorDni(@RequestParam String dni) {
        return alumnoRepository.findByDni(dni);
    }

    // 3. REGISTRO DE COMEDOR (Recuperando la elección del alumno)
    @PutMapping("/{id}/comedor")
    public ResponseEntity<?> actualizarComedor(@PathVariable Long id,
            @RequestParam boolean seQueda,
            @RequestParam(required = false) String turnoElegido) { // Agregamos esto

        // Mantenemos tu restricción horaria (acá podés poner las 9:00 AM)
        LocalTime ahora = LocalTime.now();
        if (ahora.isAfter(LocalTime.of(23, 0))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("El registro para el comedor cierra a las 9:00 AM.");
        }

        Alumno alumno = alumnoRepository.findById(id).orElse(null);
        if (alumno == null) {
            return ResponseEntity.notFound().build();
        }

        if (seQueda) {
            // RESTRICCIÓN DE LA 275: Si tiene taller a las 13:00, no puede elegir 12:40
            if ("13:00".equals(alumno.getHoraInicioTaller()) && "12:40".equals(turnoElegido)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No podés elegir 12:40 porque tenés taller a las 13:00 hs.");
            }

            // Guardamos el turno que el alumno eligió en el Front
            alumno.setTurnoComedor(turnoElegido);
        } else {
            alumno.setTurnoComedor("No");
        }

        return ResponseEntity.ok(alumnoRepository.save(alumno));
    }

    // 4. NUEVO: ACTUALIZAR NOTA (ABM Profe)
    @PutMapping("/{id}/nota")
    public ResponseEntity<Alumno> actualizarNota(@PathVariable Long id, @RequestParam Double valor) {
        return alumnoRepository.findById(id).map(a -> {
            a.setNota(valor);
            return ResponseEntity.ok(alumnoRepository.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. NUEVO: ROTACIÓN DE TALLER (ABM Profe)
    @PutMapping("/{id}/taller")
    public ResponseEntity<Alumno> actualizarTaller(@PathVariable Long id, @RequestParam String nuevoHorario) {
        return alumnoRepository.findById(id).map(a -> {
            a.setHoraInicioTaller(nuevoHorario);
            return ResponseEntity.ok(alumnoRepository.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comedor/resumen")
    public Map<String, Long> getResumenComedor() {
        List<Object[]> resultados = alumnoRepository.obtenerConteosComedor();
        Map<String, Long> resumen = new HashMap<>();
        for (Object[] fila : resultados) {
            resumen.put((String) fila[0], (Long) fila[1]);
        }
        return resumen;
    }
}
