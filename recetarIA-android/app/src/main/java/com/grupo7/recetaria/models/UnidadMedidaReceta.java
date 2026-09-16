package com.grupo7.recetaria.models;

public class UnidadMedidaReceta {

    private int id_unidad;
    private String nombre;
    private boolean esConvertible;

    public UnidadMedidaReceta() {
    }

    // Getters y setters
    public int getId_unidad() {
        return id_unidad;
    }
    public void setId_unidad(int id_unidad) {
        this.id_unidad = id_unidad;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public boolean isEsConvertible() {
        return esConvertible;
    }
    public void setEsConvertible(boolean esConvertible) {
        this.esConvertible = esConvertible;
    }
}
