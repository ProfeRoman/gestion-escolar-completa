package com.escuela.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private boolean pideCalentito;
    @Column(name = "telefono_padre")
    private String telefonoPadre;
    @Column(name = "hora_inicio_taller")
    private String horaInicioTaller;
    @Column(name = "turno_comedor")
    private String turnoComedor;
    private Double nota;

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Alumno() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefonoPadre() {
        return telefonoPadre;
    }

    public void setTelefonoPadre(String telefonoPadre) {
        this.telefonoPadre = telefonoPadre;
    }

    public String getHoraInicioTaller() {
        return horaInicioTaller;
    }

    public void setHoraInicioTaller(String horaInicioTaller) {
        this.horaInicioTaller = horaInicioTaller;
    }

    public String getTurnoComedor() {
        return turnoComedor;
    }

    public void setTurnoComedor(String turnoComedor) {
        this.turnoComedor = turnoComedor;
    }
}
