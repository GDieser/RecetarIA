package com.grupo7.recetaria.models;

public class PreferenciaAlimentaria {
    private int id_preferencia;
    private String nombre;

    public PreferenciaAlimentaria() {
    }

    public int getId_preferencia() {
        return id_preferencia;
    }

    public void setId_preferencia(int id_preferencia) {
        this.id_preferencia = id_preferencia;
    }

    @Override
    public String toString() {
        return "PreferenciaAlimentaria{" +
                "id_restriccion=" + id_preferencia +
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
