package com.escuela.api.controllers;

import com.escuela.api.models.Pedido;
import com.escuela.api.controllers.PedidoDTO; // Asegurate de tener este DTO creado
import com.escuela.api.models.Alumno;
import com.escuela.api.repositories.AlumnoRepository;
import com.escuela.api.repositories.PedidoRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/calentitos")
public class CalentitoController {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;

    // Función que valida las ventanas de recreo
    public String determinarRecreo() {
        LocalTime ahora = LocalTime.now();

        // --- TURNO MAÑANA ---
        // Ventana 1: 6:00 a 8:00
        if (ahora.isAfter(LocalTime.of(6, 0)) && ahora.isBefore(LocalTime.of(8, 0))) {
            return "PRIMER_RECREO_MAÑANA";
        }

        // Ventana 2: 8:40 a 9:30
        if (ahora.isAfter(LocalTime.of(8, 40)) && ahora.isBefore(LocalTime.of(9, 30))) {
            return "SEGUNDO_RECREO_MAÑANA";
        }

        // --- TURNO TARDE ---
        // Ventana 3: 12:00 a 13:00
        if (ahora.isAfter(LocalTime.of(12, 0)) && ahora.isBefore(LocalTime.of(13, 15))) {
            return "PRIMER_RECREO_TARDE";
        }

        // Ventana 4: 13:45 a 14:45
        if (ahora.isAfter(LocalTime.of(13, 45)) && ahora.isBefore(LocalTime.of(14, 45))) {
            return "SEGUNDO_RECREO_TARDE";
        }

        return "CERRADO";
    }

    @PostMapping("/pedir")
    public ResponseEntity<String> registrarPedido(
            @RequestBody PedidoDTO datos,
            @RequestParam String pinIngresado) {

        try {
            // 1. BUSCAR ALUMNO Y VALIDAR PIN (ESTO VA PRIMERO SIEMPRE)
            Optional<Alumno> alumnoOpt = alumnoRepository.findById(datos.getAlumnoId());
            if (!alumnoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
            }

            Alumno alumno = alumnoOpt.get();

            // LÓGICA DE SEGURIDAD
            if (alumno.getPassword() == null || alumno.getPassword().isEmpty()) {
                alumno.setPassword(pinIngresado);
                alumnoRepository.save(alumno);
            } else if (!alumno.getPassword().equals(pinIngresado)) {
                // SI EL PIN ESTÁ MAL, SALTA ACÁ Y DEVUELVE 401
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("PIN_INCORRECTO");
            }

            // 2. RECIÉN SI EL PIN ESTÁ BIEN, MIRAMOS EL RELOJ
            String recreo = determinarRecreo();
            if (recreo.equals("CERRADO")) {
                return ResponseEntity.ok("HORARIO_CERRADO");
            }

            // 3. SI PASÓ LAS DOS TRABAS, CREAMOS EL PEDIDO
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setAlumno(alumno);
            nuevoPedido.setMetodoPago(datos.getMetodoPago());
            nuevoPedido.setRecreo(recreo);
            nuevoPedido.setFecha(LocalDate.now());
            nuevoPedido.setEntregado(false);
            nuevoPedido.setPagado(!datos.getMetodoPago().equalsIgnoreCase("EFECTIVO"));

            pedidoRepository.save(nuevoPedido);

            return ResponseEntity.ok("¡Pedido anotado para el " + recreo + "!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar: " + e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public List<Pedido> obtenerPendientes() {
        return pedidoRepository.findByEntregadoFalse();
    }

    @PutMapping("/entregar/{id}")
    public ResponseEntity<String> marcarEntregado(@PathVariable Long id) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEntregado(true);
            pedidoRepository.save(pedido);
            return ResponseEntity.ok("¡Tostado entregado con éxito!");
        }).orElse(ResponseEntity.notFound().build());
    }
}
