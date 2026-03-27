package com.escuela.api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alumnos") // Así se va a llamar la tabla en MySQL
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private boolean seQuedaAComer;
    private String telefonoPadre; // Formato: 549341XXXXXXX (Código país + característica + número)

    public String getTelefonoPadre() {
        return telefonoPadre;
    }

    public void setTelefonoPadre(String telefonoPadre) {
        this.telefonoPadre = telefonoPadre;
    }

    // Constructor vacío (Obligatorio para JPA)
    public Alumno() {
    }

    // Getters y Setters (Para que Spring pueda leer y escribir los datos)
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

    public boolean isSeQuedaAComer() {
        return seQuedaAComer;
    }

    public void setSeQuedaAComer(boolean seQuedaAComer) {
        this.seQuedaAComer = seQuedaAComer;
    }
}
