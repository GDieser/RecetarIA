package com.grupo7.recetaria.models;

public class RestriccionAlimentaria {
    private int id_restriccion;
    private String nombre;

    public RestriccionAlimentaria() {
    }

    public int getId_restriccion() {
        return id_restriccion;
    }

    public void setId_restriccion(int id_restriccion) {
        this.id_restriccion = id_restriccion;
    }

    @Override
    public String toString() {
        return "RestriccioóAlimentaria{" +
                "id_restriccion=" + id_restriccion +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }





}
