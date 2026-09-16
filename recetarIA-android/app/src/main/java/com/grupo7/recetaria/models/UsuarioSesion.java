package com.grupo7.recetaria.models;

import java.time.LocalDate;

public class UsuarioSesion {

    private int id_usuario;
    private String email;
    private String nombre;
    private String apellido;
    private String nacionalidad;
    private LocalDate fecha_nacimiento;
    private int adultos_familia;
    private int ninios_familia;
    private boolean es_premium;
    private int usos_ia = 0;
    private boolean es_verificado;

    public UsuarioSesion() { }
    public UsuarioSesion(int id_usuario, String email, String nombre, String apellido,
                         LocalDate fecha_nacimiento, int adultos_familia,
                         int ninios_familia, boolean es_premium, boolean es_verificado) {
        this.id_usuario = id_usuario;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha_nacimiento = fecha_nacimiento;
        this.adultos_familia = adultos_familia;
        this.ninios_familia = ninios_familia;
        this.es_premium = es_premium;
        this.es_verificado = es_verificado;
    }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getNacionalidad() {
        return nacionalidad;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    public int getUsos_ia() {
        return usos_ia;
    }

    public void setUsos_ia(int usos_ia) {
        this.usos_ia = usos_ia;
    }
    public LocalDate getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(LocalDate fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public int getAdultos_familia() { return adultos_familia; }
    public void setAdultos_familia(int adultos_familia) { this.adultos_familia = adultos_familia; }

    public int getNinios_familia() { return ninios_familia; }
    public void setNinios_familia(int ninios_familia) { this.ninios_familia = ninios_familia; }

    public boolean isEs_premium() { return es_premium; }
    public void setEs_premium(boolean es_premium) { this.es_premium = es_premium; }

    public boolean isEs_verificado() { return es_verificado; }
    public void setEs_verificado(boolean es_verificado) { this.es_verificado = es_verificado; }

    public boolean isPerfilCompleto() {
        boolean tieneNombre = nombre != null && !nombre.isBlank();
        boolean tieneApellido = apellido != null && !apellido.isBlank();
        boolean tieneFecha = fecha_nacimiento != null;
        boolean tieneIntegrantes = adultos_familia > 0;

        return tieneNombre && tieneApellido && tieneFecha && tieneIntegrantes;
    }
}