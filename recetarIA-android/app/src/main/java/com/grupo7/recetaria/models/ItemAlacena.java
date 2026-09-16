package com.grupo7.recetaria.models;

public class ItemAlacena {

    private int id_producto_alacena;
    private int id_usuario;
    private int id_producto;
    private int id_unidad;
    private double cantidad;
    private boolean es_adquirido;
    private int es_infaltable;


    // Variables para ui
    private String nombre_producto;
    private String nombre_unidad;
    private String nombre_rubro;

    public ItemAlacena() { }

    public ItemAlacena(int id_producto_alacena, int id_usuario, int id_producto,
                       int id_unidad, double cantidad, boolean es_adquirido, int es_infaltable) {
        this.id_producto_alacena = id_producto_alacena;
        this.id_usuario = id_usuario;
        this.id_producto = id_producto;
        this.id_unidad = id_unidad;
        this.cantidad = cantidad;
        this.es_adquirido = es_adquirido;
        this.es_infaltable = es_infaltable;
    }

    public int getId_producto_alacena() { return id_producto_alacena; }
    public void setId_producto_alacena(int id_producto_alacena) { this.id_producto_alacena = id_producto_alacena; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public int getId_unidad() { return id_unidad; }
    public void setId_unidad(int id_unidad) { this.id_unidad = id_unidad; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public boolean isEs_adquirido() { return es_adquirido; }
    public void setEs_adquirido(boolean es_adquirido) { this.es_adquirido = es_adquirido; }

    public int getEs_infaltable() {return es_infaltable; }
    public void setEs_infaltable(int es_infaltable) { this.es_infaltable = es_infaltable; }

    public String getNombre_producto() { return nombre_producto; }
    public void setNombre_producto(String nombre_producto) { this.nombre_producto = nombre_producto; }

    public String getNombre_unidad() { return nombre_unidad; }
    public void setNombre_unidad(String nombre_unidad) { this.nombre_unidad = nombre_unidad; }

    public String getNombre_rubro() { return nombre_rubro; }
    public void setNombre_rubro(String nombre_rubro) { this.nombre_rubro = nombre_rubro; }
}
