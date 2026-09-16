package com.grupo7.recetaria.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;


public class IngredienteRecetaIA implements Parcelable {
    private String nombre;
    private String nombreIngrediente;
    private double cantidad;
    private String unidad;
    private Boolean esAdquirido = false;
    private String rubro = "";

    public IngredienteRecetaIA() {
    }

    public IngredienteRecetaIA(String nombre, String nombreIngrediente, double cantidad, String unidad, Boolean esAdquirido) {
        this.nombre = nombre;
        this.nombreIngrediente = nombreIngrediente;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.esAdquirido = esAdquirido;
    }

    protected IngredienteRecetaIA(Parcel in) {
        nombre = in.readString();
        nombreIngrediente = in.readString();
        cantidad = in.readDouble();
        unidad = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            esAdquirido = in.readBoolean();
        }
        rubro = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(nombreIngrediente);
        dest.writeDouble(cantidad);
        dest.writeString(unidad);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(esAdquirido);
        }
        dest.writeString(rubro);
    }

    public static final Creator<IngredienteRecetaIA> CREATOR = new Creator<IngredienteRecetaIA>() {
        @Override
        public IngredienteRecetaIA createFromParcel(Parcel in) { return new IngredienteRecetaIA(in); }
        @Override
        public IngredienteRecetaIA[] newArray(int size) { return new IngredienteRecetaIA[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public double getCantidad() { return cantidad; }
    public String getUnidad() { return unidad; }
    public String getNombreIngrediente() {
        return nombreIngrediente;
    }
    public String getRubro() {
        return rubro;
    }
    public void setRubro(String rubro) {
        this.rubro = rubro;
    }
    public Boolean isEsAdquirido() {
        return esAdquirido;
    }

    public void setEsAdquirido(Boolean esAdquirido) {
        this.esAdquirido = esAdquirido;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setNombreIngrediente(String nombreIngrediente) {
        this.nombreIngrediente = nombreIngrediente;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}