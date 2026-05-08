package com.escuela.api.repositories;

import com.escuela.api.models.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // 1. Buscar coincidencia exacta de DNI
    List<Alumno> findByDni(String dni);

    // 2. Buscar por nombre
    List<Alumno> findByNombreContainingIgnoreCase(String nombre);

    // 3. Obtener conteos para el resumen del panel
    @Query("SELECT a.turnoComedor, COUNT(a) FROM Alumno a WHERE a.turnoComedor != 'No' AND a.turnoComedor IS NOT NULL GROUP BY a.turnoComedor")
    List<Object[]> obtenerConteosComedor();

    // --- NUEVO MÉTODO PARA LA COCINERA ---
    // Este método nos dirá cuántos alumnos hay anotados en un turno específico (ej: "11:20")
    Long countByTurnoComedor(String turnoComedor);

    // 4. RESET MASIVO PROFESIONAL
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Alumno a SET a.turnoComedor = 'No' WHERE a.id > 0")
    void resetearComedorMasivo();
}
