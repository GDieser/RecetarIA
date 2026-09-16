package com.grupo7.recetaria.models;

import java.time.LocalDate;

public class Usuario {

    private int id_usuario;
    private String email;
    private String pass;
    private String nombre;
    private String apellido;
    private String google_token;
    private boolean es_validado;
    private LocalDate fecha_nacimiento;
    private int codigo_validacion;
    private int adultos_familia;
    private int ninios_familia;
    private boolean es_premium;

    public Usuario() { }

    public Usuario(int id_usuario, String email, String pass, String nombre,
                   String apellido, String google_token, boolean es_validado,
                   LocalDate fecha_nacimiento, int codigo_validacion,
                   int adultos_familia, int ninios_familia, boolean es_premium) {
        this.id_usuario = id_usuario;
        this.email = email;
        this.pass = pass;
        this.nombre = nombre;
        this.apellido = apellido;
        this.google_token = google_token;
        this.es_validado = es_validado;
        this.fecha_nacimiento = fecha_nacimiento;
        this.codigo_validacion = codigo_validacion;
        this.adultos_familia = adultos_familia;
        this.ninios_familia = ninios_familia;
        this.es_premium = es_premium;
    }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getGoogle_token() { return google_token; }
    public void setGoogle_token(String google_token) { this.google_token = google_token; }

    public boolean isEs_validado() { return es_validado; }
    public void setEs_validado(boolean es_validado) { this.es_validado = es_validado; }

    public LocalDate getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(LocalDate fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public int getCodigo_validacion() { return codigo_validacion; }
    public void setCodigo_validacion(int codigo_validacion) { this.codigo_validacion = codigo_validacion; }

    public int getAdultos_familia() { return adultos_familia; }
    public void setAdultos_familia(int adultos_familia) { this.adultos_familia = adultos_familia; }

    public int getNinios_familia() { return ninios_familia; }
    public void setNinios_familia(int ninios_familia) { this.ninios_familia = ninios_familia; }

    public boolean isEs_premium() { return es_premium; }
    public void setEs_premium(boolean es_premium) { this.es_premium = es_premium; }
}
