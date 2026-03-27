package com.escuela.api.repositories;

import com.escuela.api.models.Alumno;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // Spring Data JPA crea la consulta mágica: SELECT * FROM alumno WHERE nombre LIKE %...%
    // Este nombre de método es clave: Spring traduce esto a SQL automáticamente
    List<Alumno> findByNombreContainingIgnoreCase(String nombre);
}
