package com.grupo7.recetaria.models;

public class Calendario {

    private int id_calendario;
    private int id_usuario;
    private int id_receta;
    private String tituloReceta;
    private String fecha;
    private int id_tipo_comida;
    private String nombre_tipo_comida;

    private int comensales;
    private String tiempo;
    private String dificultad;

    private boolean tieneStock;


    public Calendario() {
    }

    public Calendario(int id_calendario, int id_usuario, int id_receta, String tituloReceta, String fecha, int id_tipo_comida, String nombre_tipo_comida, int comensales, String tiempo, String dificultad) {
        this.id_calendario = id_calendario;
        this.id_usuario = id_usuario;
        this.id_receta = id_receta;
        this.tituloReceta = tituloReceta;
        this.fecha = fecha;
        this.id_tipo_comida = id_tipo_comida;
        this.nombre_tipo_comida = nombre_tipo_comida;
        this.comensales = comensales;
        this.tiempo = tiempo;
        this.dificultad = dificultad;
    }

    public int getId_calendario() {
        return id_calendario;
    }

    public void setId_calendario(int id_calendario) {
        this.id_calendario = id_calendario;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_receta() {
        return id_receta;
    }

    public void setId_receta(int id_receta) {
        this.id_receta = id_receta;
    }

    public String getTituloReceta() {
        return tituloReceta;
    }

    public void setTituloReceta(String tituloReceta) {
        this.tituloReceta = tituloReceta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getId_tipo_comida() {
        return id_tipo_comida;
    }

    public void setId_tipo_comida(int id_tipo_comida) {
        this.id_tipo_comida = id_tipo_comida;
    }

    public String getNombre_tipo_comida() {
        return nombre_tipo_comida;
    }

    public void setNombre_tipo_comida(String nombre_tipo_comida) {
        this.nombre_tipo_comida = nombre_tipo_comida;
    }
    public int getComensales() {
        return comensales;
    }

    public void setComensales(int comensales) {
        this.comensales = comensales;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public boolean isTieneStock() {
        return tieneStock;
    }

    public void setTieneStock(boolean tieneStock) {
        this.tieneStock = tieneStock;
    }

}
