package com.escuela.api.controllers;

import com.escuela.api.models.Alumno;
import com.escuela.api.repositories.AlumnoRepository;
import com.escuela.api.services.TareaProgramadaService;
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
    private TareaProgramadaService tareaProgramadaService;
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

    // 3. REGISTRO DE COMEDOR (Con Doble Validación de Seguridad y Cupos)
    @PutMapping("/{id}/comedor")
    public ResponseEntity<?> actualizarComedor(@PathVariable Long id,
            @RequestParam boolean seQueda,
            @RequestParam(required = false) String turnoElegido,
            @RequestParam(required = false) String pinIngresado, // Nuevo: para la Web
            @RequestParam(required = false) String nroCelular) { // Nuevo: para el Bot

        Alumno alumno = alumnoRepository.findById(id).orElse(null);
        if (alumno == null) {
            return ResponseEntity.notFound().build();
        }

        // --- INICIO DE VALIDACIÓN DE SEGURIDAD ---
        if (nroCelular != null && !nroCelular.isEmpty()) {
            if (alumno.getTelefonoAlumno() == null) {
                alumno.setTelefonoAlumno(nroCelular);
                alumnoRepository.save(alumno);
            } else if (!alumno.getTelefonoAlumno().equals(nroCelular)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Error: Este DNI ya está vinculado a otro celular.");
            }
        } else {
            if (alumno.getPassword() == null) {
                if (pinIngresado == null || pinIngresado.isEmpty()) {
                    return ResponseEntity.badRequest().body("Debes elegir un PIN de seguridad.");
                }
                alumno.setPassword(pinIngresado);
                alumnoRepository.save(alumno);
            } else if (!alumno.getPassword().equals(pinIngresado)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("PIN incorrecto.");
            }
        }
        // --- FIN DE VALIDACIÓN DE SEGURIDAD ---

        // Mantenemos tu restricción horaria
        LocalTime ahora = LocalTime.now();
        if (ahora.isAfter(LocalTime.of(9, 0))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("El registro para el comedor cierra a las 9:00 AM.");
        }

        if (seQueda) {
            // --- NUEVA VALIDACIÓN DE CUPOS PARA LA COCINERA ---
            if (turnoElegido != null && !turnoElegido.equals("No")) {
                Long anotadosEnTurno = alumnoRepository.countByTurnoComedor(turnoElegido);
                if (anotadosEnTurno >= 50) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("El turno de las " + turnoElegido + " ya está lleno (cupo de 50 alcanzado).");
                }
            }

            // Mantenemos la restricción de taller
            if ("13:00".equals(alumno.getHoraInicioTaller()) && "12:40".equals(turnoElegido)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No podés elegir 12:40 porque tenés taller a las 13:00 hs.");
            }
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

    @PutMapping("/reset-comedor")
    public ResponseEntity<String> resetearComedor() {
        tareaProgramadaService.resetDiarioComedor(); 
        return ResponseEntity.ok("Comedor reseteado correctamente");
    }
}