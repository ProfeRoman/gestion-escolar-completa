package com.escuela.api.controllers;

import com.escuela.api.models.Pedido;
import com.escuela.api.controllers.PedidoDTO; 
import com.escuela.api.models.Alumno;
import com.escuela.api.repositories.AlumnoRepository;
import com.escuela.api.repositories.PedidoRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

    public String determinarRecreo() {
        LocalTime ahora = LocalTime.now();

        if (ahora.isAfter(LocalTime.of(6, 0)) && ahora.isBefore(LocalTime.of(8, 0))) {
            return "PRIMER_RECREO_MAÑANA";
        }
        if (ahora.isAfter(LocalTime.of(8, 40)) && ahora.isBefore(LocalTime.of(9, 30))) {
            return "SEGUNDO_RECREO_MAÑANA";
        }
        if (ahora.isAfter(LocalTime.of(12, 0)) && ahora.isBefore(LocalTime.of(13, 15))) {
            return "PRIMER_RECREO_TARDE";
        }
        // Ventana 4 amplia para que puedas probar ahora
        if (ahora.isAfter(LocalTime.of(13, 45)) && ahora.isBefore(LocalTime.of(14, 45))) {
            return "SEGUNDO_RECREO_TARDE";
        }

        return "CERRADO";
    }

    @PostMapping("/pedir")
    public ResponseEntity<String> registrarPedido(
            @RequestBody PedidoDTO datos, // <--- EL NOMBRE ES 'datos'
            @RequestParam String pinIngresado) {

        try {
            Optional<Alumno> alumnoOpt = alumnoRepository.findById(datos.getAlumnoId());
            if (!alumnoOpt.isPresent()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");

            Alumno alumno = alumnoOpt.get();
            if (alumno.getPassword() != null && !alumno.getPassword().equals(pinIngresado)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("PIN_INCORRECTO");
            }

            String recreo = determinarRecreo();
            if (recreo.equals("CERRADO")) return ResponseEntity.ok("HORARIO_CERRADO");

            LocalDate hoy = LocalDate.now();

            // A. VALIDAR LÍMITE GENERAL (Stock de la escuela)
            long totalPedidosRecreo = pedidoRepository.countByFechaAndRecreo(hoy, recreo);
            if (totalPedidosRecreo >= 6) {
                return ResponseEntity.ok("CUPO_LLENO");
            }

            // B. VALIDAR LÍMITE POR ALUMNO (Tu regla: hasta 6 por persona)
            long pedidosDelAlumno = pedidoRepository.countByAlumnoIdAndFechaAndRecreo(datos.getAlumnoId(), hoy, recreo);
            
            if (pedidosDelAlumno >= 6) {
                return ResponseEntity.ok("YA_TIENES_UN_PEDIDO"); 
            }
            
            
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setAlumno(alumno);
            nuevoPedido.setMetodoPago(datos.getMetodoPago());
            nuevoPedido.setRecreo(recreo);
            nuevoPedido.setFecha(hoy);
            nuevoPedido.setEntregado(false);
            nuevoPedido.setPagado(!datos.getMetodoPago().equalsIgnoreCase("EFECTIVO"));

            pedidoRepository.save(nuevoPedido);
            return ResponseEntity.ok("¡Pedido #" + (pedidosDelAlumno + 1) + " anotado para el " + recreo + "!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
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

    @GetMapping("/hoy/{alumnoId}")
    public ResponseEntity<List<Pedido>> obtenerPedidosHoy(@PathVariable Long alumnoId) {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        List<Pedido> pedidos = pedidoRepository.findByAlumnoIdAndFecha(alumnoId, hoy);
        return ResponseEntity.ok(pedidos);
    }
}