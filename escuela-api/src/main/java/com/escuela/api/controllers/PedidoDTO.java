package com.escuela.api.controllers;

public class PedidoDTO {

    private Long alumnoId;
    private String metodoPago;

    public PedidoDTO(Long alumnoId, String metodoPago) {
        this.alumnoId = alumnoId;
        this.metodoPago = metodoPago;
    }

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

}
