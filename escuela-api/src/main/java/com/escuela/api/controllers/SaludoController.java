package com.escuela.api.controllers;

import com.escuela.api.models.Alumno;
import com.escuela.api.repositories.AlumnoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST,RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/saludo")
public class SaludoController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @GetMapping
    public List<Alumno> saludar() {
        return alumnoRepository.findAll();
    }

    // Nuevo método para RECIBIR datos del formulario
    @PostMapping
    public Alumno guardarAlumno(@RequestBody Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    // Método para borrar por ID
    @DeleteMapping("/{id}")
    public void borrarAlumno(@PathVariable Long id) {
        System.out.println("SÍ! ME LLEGÓ EL PEDIDO PARA BORRAR EL ID: " + id);
        alumnoRepository.deleteById(id);
    }

    // Método para cambiar el estado del comedor (UPDATE)
    @PutMapping("/{id}/comedor")
public Alumno cambiarComedor(@PathVariable Long id) {
    // 1. Buscamos el alumno en la base de datos
    Alumno alumno = alumnoRepository.findById(id).orElse(null);
    
    if (alumno != null) {
        // 2. Invertimos el estado (si era true, pasa a false)
        alumno.setSeQuedaAComer(!alumno.isSeQuedaAComer());
        
        // 3. ¡ESTA ES LA CLAVE! Guardamos el cambio para que Hibernate haga el UPDATE
        return alumnoRepository.save(alumno);
    }
    return null;
}

    // Método para EDITAR un alumno completo
    @PutMapping("/{id}")
    public Alumno actualizarAlumno(@PathVariable Long id, @RequestBody Alumno alumnoActualizado) {
        return alumnoRepository.findById(id)
                .map(alumno -> {
                    // Seteamos los nuevos datos que vienen del frontend
                    alumno.setNombre(alumnoActualizado.getNombre());
                    alumno.setApellido(alumnoActualizado.getApellido());
                    alumno.setDni(alumnoActualizado.getDni());
                    // Guardamos y devolvemos el alumno editado
                    return alumnoRepository.save(alumno);
                })
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con id " + id));
    }

    // Buscador de alumnos por nombre
    @GetMapping("/buscar")
    public List<Alumno> buscarAlumnos(@RequestParam String nombre) {
        return alumnoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Nuevo: El bot consulta el total de alumnos y cuántos comen
    @GetMapping("/stats")
    public String obtenerEstadisticas() {
        long total = alumnoRepository.count();
        long comen = alumnoRepository.findAll().stream()
                .filter(a -> a.isSeQuedaAComer())
                .count();

        return "Actualmente hay " + total + " alumnos registrados y hoy se quedan a comer " + comen + " chicos.";
    }
}
