package com.grupo7.recetaria.models;

public class PasoReceta {

    private int numero_paso;
    private String descripcion;
    private int tiempo_temporizador_minutos;

    public PasoReceta() { }

    public PasoReceta(int numero_paso, String descripcion, int tiempo_temporizador_minutos) {
        this.numero_paso = numero_paso;
        this.descripcion = descripcion;
        this.tiempo_temporizador_minutos = tiempo_temporizador_minutos;
    }

    public int getNumero_paso() { return numero_paso; }
    public void setNumero_paso(int numero_paso) { this.numero_paso = numero_paso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getTiempo_temporizador_minutos() { return tiempo_temporizador_minutos; }
    public void setTiempo_temporizador_minutos(int tiempo_temporizador_minutos) { this.tiempo_temporizador_minutos = tiempo_temporizador_minutos; }


    //para saber si hay que mostrar el temp
    public boolean tieneTemporizador() {
        return tiempo_temporizador_minutos > 0;
    }
}
