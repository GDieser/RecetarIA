package com.grupo7.recetaria.models;

public class IngredienteReceta {

    private int id_ingrediente;
    private int id_receta;
    private int id_producto;
    private int id_unidad;
    private double cantidad;
    private String nombre_producto;
    private String nombre_unidad;
    public IngredienteReceta() { }
    public IngredienteReceta(int id_ingrediente, int id_receta, int id_producto,
                             int id_unidad, double cantidad) {
        this.id_ingrediente = id_ingrediente;
        this.id_receta = id_receta;
        this.id_producto = id_producto;
        this.id_unidad = id_unidad;
        this.cantidad = cantidad;
    }
    public int getId_ingrediente() { return id_ingrediente; }
    public void setId_ingrediente(int id_ingrediente) { this.id_ingrediente = id_ingrediente; }
    public int getId_receta() { return id_receta; }
    public void setId_receta(int id_receta) { this.id_receta = id_receta; }
    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }
    public int getId_unidad() { return id_unidad; }
    public void setId_unidad(int id_unidad) { this.id_unidad = id_unidad; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public String getNombre_producto() { return nombre_producto; }
    public void setNombre_producto(String nombre_producto) { this.nombre_producto = nombre_producto; }
    public String getNombre_unidad() { return nombre_unidad; }
    public void setNombre_unidad(String nombre_unidad) { this.nombre_unidad = nombre_unidad; }
}
