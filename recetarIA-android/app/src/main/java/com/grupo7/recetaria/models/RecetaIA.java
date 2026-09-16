package com.grupo7.recetaria.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RecetaIA implements Parcelable {
    private String titulo;
    private String tiempo;
    private String dificultad;
    private int comensales;
    private List<IngredienteRecetaIA> ingredientes; // Lista de OBJETOS
    private List<String> instrucciones;
    public boolean isEsGuardada() {
        return esGuardada;
    }
    public void setEsGuardada(boolean esGuardada) {
        this.esGuardada = esGuardada;
    }
    private boolean esGuardada = false;
    public RecetaIA() {}

    protected RecetaIA(Parcel in) {
        titulo = in.readString();
        tiempo = in.readString();
        dificultad = in.readString();
        comensales = in.readInt();
        ingredientes = in.createTypedArrayList(IngredienteRecetaIA.CREATOR);
        instrucciones = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(tiempo);
        dest.writeString(dificultad);
        dest.writeInt(comensales);
        dest.writeTypedList(ingredientes);
        dest.writeStringList(instrucciones);
    }

    public static final Creator<RecetaIA> CREATOR = new Creator<RecetaIA>() {
        @Override
        public RecetaIA createFromParcel(Parcel in) { return new RecetaIA(in); }
        @Override
        public RecetaIA[] newArray(int size) { return new RecetaIA[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    // --- GETTERS Y SETTERS CORREGIDOS ---
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public int getComensales() { return comensales; }
    public void setComensales(int comensales) { this.comensales = comensales; }

    public List<IngredienteRecetaIA> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<IngredienteRecetaIA> ingredientes) { this.ingredientes = ingredientes; }

    public List<String> getInstrucciones() { return instrucciones; }
    public void setInstrucciones(List<String> instrucciones) { this.instrucciones = instrucciones; }
}