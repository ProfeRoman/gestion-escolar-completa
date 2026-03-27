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

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
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
        // 1. Buscamos al alumno por ID
        Alumno alumno = alumnoRepository.findById(id).orElseThrow();

        // 2. Invertimos el valor booleano (si es true -> false / si es false -> true)
        alumno.setSeQuedaAComer(!alumno.isSeQuedaAComer());

        // 3. Guardamos los cambios y devolvemos el alumno actualizado
        System.out.println("Cambiando estado de comedor para: " + alumno.getNombre());
        return alumnoRepository.save(alumno);
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
}
