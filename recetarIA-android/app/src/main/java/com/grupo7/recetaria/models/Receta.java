package com.grupo7.recetaria.models;

import java.util.List;

public class Receta {

    private int id_receta;
    private int id_usuario;
    private String titulo;
    private String dificultad;
    private int comensales;
    private String tiempo;
    private String lista_pasos;

    private int es_favorito;
    private int id_tipo_comida;
    private String nombre_tipo_comida;

    private String fecha_creacion;

    // Var para ui
    private List<IngredienteReceta> ingredientes_cargados;
    private List<PasoReceta> pasos_estructurados;

    public Receta() { }

    public Receta(int id_receta, int id_usuario, int es_favorito,String titulo, String dificultad,
                  int comensales, String tiempo, String lista_pasos, int id_tipo_comida, String fecha_creacion) {
        this.id_receta = id_receta;
        this.id_usuario = id_usuario;
        this.es_favorito = es_favorito;
        this.titulo = titulo;
        this.dificultad = dificultad;
        this.comensales = comensales;
        this.tiempo = tiempo;
        this.lista_pasos = lista_pasos;
        this.id_tipo_comida = id_tipo_comida;
        this.fecha_creacion = fecha_creacion;
    }

    public int getId_receta() { return id_receta; }
    public void setId_receta(int id_receta) { this.id_receta = id_receta; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getEs_favorito() {
        return es_favorito;
    }

    public void setEs_favorito(int es_favorito) {
        this.es_favorito = es_favorito;
    }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public int getComensales() { return comensales; }
    public void setComensales(int comensales) { this.comensales = comensales; }

    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public String getLista_pasos() { return lista_pasos; }
    public void setLista_pasos(String lista_pasos) { this.lista_pasos = lista_pasos; }

    public String getFecha_creacion() { return fecha_creacion; }
    public void setFecha_creacion(String fecha_creacion) { this.fecha_creacion = fecha_creacion; }

    public List<IngredienteReceta> getIngredientes_cargados() { return ingredientes_cargados; }
    public void setIngredientes_cargados(List<IngredienteReceta> ingredientes_cargados) { this.ingredientes_cargados = ingredientes_cargados; }

    public List<PasoReceta> getPasos_estructurados() { return pasos_estructurados; }
    public void setPasos_estructurados(List<PasoReceta> pasos_estructurados) { this.pasos_estructurados = pasos_estructurados; }


    public int getId_tipo_comida() { return id_tipo_comida; }
    public void setId_tipo_comida(int id_tipo_comida) { this.id_tipo_comida = id_tipo_comida; }
    public String getTipoComida() {
        return nombre_tipo_comida;
    }
    public void setTipoComida(String nombre_tipo_comida){
        this.nombre_tipo_comida = nombre_tipo_comida;
    }
}
