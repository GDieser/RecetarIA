package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo7.recetaria.models.UnidadMedidaReceta;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private static final String NOMBRE_TABLA_UNIDAD = "Unidad";
    private final String NOMBRE_TABLA_PRODUCTO = "Producto";
    private final String NOMBRE_TABLA_RUBRO = "Rubro";
    private final String NOMBRE_TABLA_UNIDAD_RECETA = "UnidadReceta";
    private final String ID_PRODUCTO = "id_producto";
    private final String ID_RUBRO = "id_rubro";
    private final String NOMBRE = "nombre";
    private final SQLiteOpenHelper recetariaDbHelper;

    public ProductoDAO(Context context) {
        this.recetariaDbHelper = new RecetariaDbHelper(context);
    }

    public int obtenerIdPorNombreProducto(String nombre) {
        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        int idEncontrado = -1;

        String[] proyeccion = { "id_producto" };
        String seleccion = "nombre = ? COLLATE NOCASE";
        String[] seleccionArgs = { nombre };

        Cursor cursor;
        cursor = db.query(
                NOMBRE_TABLA_PRODUCTO,
                proyeccion,
                seleccion,
                seleccionArgs,
                null,
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                idEncontrado = cursor.getInt(0);
            }
            cursor.close();
        }
        return idEncontrado;
    }

    public int obtenerIdRubroPorNombre(String nombre){

        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        int idEncontrado = -1;

        String[] proyeccion = { "id_rubro" };
        String seleccion = "nombre = ? COLLATE NOCASE";
        String[] seleccionArgs = { nombre };
        Cursor cursor;
        cursor = db.query(
                NOMBRE_TABLA_RUBRO,
                proyeccion,
                seleccion,
                seleccionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                idEncontrado = cursor.getInt(0);
            }
            cursor.close();
        }
        return idEncontrado;
    }

    public List<String> obtenerListaPlanaRubros() {
        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        List<String> lista = new ArrayList<>();
        String[] proyeccion = { "nombre" };

        Cursor cursor = db.query(
                    NOMBRE_TABLA_RUBRO,
                    proyeccion,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    lista.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        return lista;
    }

    public List<String> obtenerListaNombresUnidades() {
        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        List<String> unidades = new ArrayList<>();

        String[] proyeccion = { "nombre" };

        Cursor cursor = db.query(
                    NOMBRE_TABLA_UNIDAD_RECETA,
                    proyeccion,
                    null,
                    null,
                    null,
                    null,
                    null
                    );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    unidades.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
        return unidades;
    }

    public int obtenerIdUnidad(String unidad) {
        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        int idEncontrado = -1;

        String[] proyeccion = { "id_unidad" };
        String seleccion = "nombre = ? COLLATE NOCASE";
        String[] seleccionArgs = { unidad };
        Cursor cursor;
        cursor = db.query(
                NOMBRE_TABLA_UNIDAD,
                proyeccion,
                seleccion,
                seleccionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                idEncontrado = cursor.getInt(0);
            }
            cursor.close();
        }
        return idEncontrado;
    }

    public UnidadMedidaReceta obtenerUnidadReceta(String nombreUnidad) {
        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        UnidadMedidaReceta unidad = new UnidadMedidaReceta();

        String[] proyeccion = { "id_unidad" };
        String seleccion = "nombre = ? COLLATE NOCASE";
        String[] seleccionArgs = { nombreUnidad };
        Cursor cursor;
        cursor = db.query(
                NOMBRE_TABLA_UNIDAD_RECETA,
                proyeccion,
                seleccion,
                seleccionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                unidad.setId_unidad(cursor.getInt(cursor.getColumnIndexOrThrow("id_unidad")));
                unidad.setNombre(nombreUnidad);
                unidad.setEsConvertible(cursor.getInt(cursor.getColumnIndexOrThrow("es_convertible"))==1);
            }
            cursor.close();
        }
        return unidad;
    }


    public long insertarNuevoProducto(String nombre, String rubro) {
        SQLiteDatabase db = this.recetariaDbHelper.getWritableDatabase();

        int idRubro = obtenerIdRubroPorNombre(rubro);

        ContentValues values = new ContentValues();
        values.put("nombre", nombre.toLowerCase().trim());
        values.put("id_rubro", idRubro);

        return db.insert("Producto", null, values);
    }
}
