package com.escuela.api.services;

import com.escuela.api.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TareaProgramadaService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    // ELIMINAMOS EL @PostConstruct QUE ESTABA ACÁ
    // MANTENEMOS SOLO ESTE:
    // Se ejecuta a las 00:00 solo si la PC queda prendida
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Argentina/Buenos_Aires")
    public void resetDiarioComedor() {
        alumnoRepository.resetearComedorMasivo();
        System.out.println("✅ RESET NOCTURNO: Comedor limpio para el nuevo día.");
    }
}
