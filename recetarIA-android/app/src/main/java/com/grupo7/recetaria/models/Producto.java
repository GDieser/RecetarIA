package com.grupo7.recetaria.models;

public class Producto {

    private int id_producto;
    private int id_rubro;
    private String nombre;

    public Producto() { }

    public Producto(int id_producto, int id_rubro, String nombre) {
        this.id_producto = id_producto;
        this.id_rubro = id_rubro;
        this.nombre = nombre;
    }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public int getId_rubro() { return id_rubro; }
    public void setId_rubro(int id_rubro) { this.id_rubro = id_rubro; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
