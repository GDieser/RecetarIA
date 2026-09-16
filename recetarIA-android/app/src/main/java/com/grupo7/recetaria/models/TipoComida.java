package com.grupo7.recetaria.models;

public class TipoComida {

    private int id;
    private String nombre;


    public TipoComida(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public TipoComida() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
