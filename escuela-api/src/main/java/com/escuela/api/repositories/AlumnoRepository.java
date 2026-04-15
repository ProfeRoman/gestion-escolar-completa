package com.escuela.api.repositories; 

import com.escuela.api.models.Alumno; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // 1. Buscar coincidencia exacta de DNI
    List<Alumno> findByDni(String dni);

    // 2. Buscar por nombre (ignora mayúsculas y busca partes del nombre)
    List<Alumno> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT a.turnoComedor, COUNT(a) FROM Alumno a WHERE a.turnoComedor != 'No' GROUP BY a.turnoComedor")
List<Object[]> obtenerConteosComedor();
}