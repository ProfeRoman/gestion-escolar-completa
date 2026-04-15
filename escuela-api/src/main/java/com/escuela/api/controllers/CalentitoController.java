package com.escuela.api.controllers;

import com.escuela.api.models.Pedido;
import com.escuela.api.controllers.PedidoDTO; // Asegurate de tener este DTO creado
import com.escuela.api.models.Alumno;
import com.escuela.api.repositories.AlumnoRepository;
import com.escuela.api.repositories.PedidoRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Ventana 1: 6:00 a 8:00 (Ajustado para que puedas probar ahora que son las 6:30)
        if (ahora.isAfter(LocalTime.of(6, 0)) && ahora.isBefore(LocalTime.of(8, 0))) {
            return "PRIMER_RECREO";
        }

        // Ventana 2: 8:40 a 9:30
        if (ahora.isAfter(LocalTime.of(8, 40)) && ahora.isBefore(LocalTime.of(23, 30))) {
            return "SEGUNDO_RECREO";
        }

        return "CERRADO";
    }

    @PostMapping("/pedir")
    public ResponseEntity<String> registrarPedido(@RequestBody PedidoDTO datos) {
        String recreo = determinarRecreo();

        // Si está cerrado, devolvemos un mensaje de texto pero con estatus OK (200)
        // para que el JavaScript no falle por seguridad (CORS/Fetch)
        if (recreo.equals("CERRADO")) {
            return ResponseEntity.ok("HORARIO_CERRADO");
        }

        try {
            Pedido nuevoPedido = new Pedido();
            Alumno alumno = alumnoRepository.findById(datos.getAlumnoId()).get();
            nuevoPedido.setAlumno(alumno);
            nuevoPedido.setMetodoPago(datos.getMetodoPago());
            nuevoPedido.setRecreo(recreo);
            nuevoPedido.setFecha(LocalDate.now());
            nuevoPedido.setEntregado(false);

            // Si no es efectivo, ya está pagado
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