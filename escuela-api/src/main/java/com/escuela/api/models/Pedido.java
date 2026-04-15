package com.escuela.api.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos_calentitos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usamos el objeto Alumno para que Hibernate traiga nombre y apellido automáticamente.
    @ManyToOne
    @JoinColumn(name = "alumno_id") 
    private Alumno alumno; 

    private String recreo;     // PRIMER_RECREO o SEGUNDO_RECREO
    private String metodoPago; // EFECTIVO, TRANSFERENCIA, QR
    private boolean pagado;    // True si es Transf/QR
    private boolean entregado; // Para que los de 6to marquen cuando lo dan
    private LocalDate fecha;   // Para filtrar los pedidos del día

    // --- CONSTRUCTORES ---

    public Pedido() {
        this.entregado = false; // Por defecto no está entregado
        this.fecha = LocalDate.now(); // Se setea la fecha actual al crear el pedido
    }

    public Pedido(Long id, Alumno alumno, String recreo, String metodoPago, boolean pagado, boolean entregado, LocalDate fecha) {
        this.id = id;
        this.alumno = alumno;
        this.recreo = recreo;
        this.metodoPago = metodoPago;
        this.pagado = pagado;
        this.entregado = entregado;
        this.fecha = fecha;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getRecreo() {
        return recreo;
    }

    public void setRecreo(String recreo) {
        this.recreo = recreo;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}