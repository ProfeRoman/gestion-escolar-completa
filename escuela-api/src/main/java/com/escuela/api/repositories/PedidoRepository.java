package com.escuela.api.repositories;

import com.escuela.api.models.Pedido;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Este método les va a servir para ver solo los pedidos de hoy
    List<Pedido> findByFechaAndEntregadoFalse(LocalDate fecha);

    // Esto le dice a Spring: "Buscame en la BD todos los que tengan entregado = false"
    List<Pedido> findByEntregadoFalse();

}
