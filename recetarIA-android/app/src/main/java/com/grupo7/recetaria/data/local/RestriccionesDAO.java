package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo7.recetaria.models.PreferenciaAlimentaria;
import com.grupo7.recetaria.models.RestriccionAlimentaria;

import java.util.ArrayList;
import java.util.List;

public class RestriccionesDAO {

    private final SQLiteOpenHelper dbHelper;
    public  RestriccionesDAO(Context context){
        dbHelper = new RecetariaDbHelper(context);
    }

    public List<RestriccionAlimentaria> obtenerRestriccionesAlimentarias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<RestriccionAlimentaria> restricciones = new ArrayList<>();

        Cursor cursor = db.query("RestriccionAlimentaria", null, null, null, null, null, null);
        int nombre_idx = cursor.getColumnIndexOrThrow("nombre");
        int id_idx = cursor.getColumnIndexOrThrow("id_restriccion");

        while (cursor.moveToNext()) {
            RestriccionAlimentaria rest = new RestriccionAlimentaria();
            rest.setNombre(cursor.getString(nombre_idx));
            rest.setId_restriccion(cursor.getInt(id_idx));
            restricciones.add(rest);
        }

        cursor.close();
        return restricciones;
    }

    public void asignarRestricciones(int idUsuario, List<Integer> restIds) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete("RestriccionAlimentariaUsuario", "id_usuario = ?", new String[]{String.valueOf(idUsuario)});

            for (Integer restId : restIds) {
                ContentValues values = new ContentValues();
                values.put("id_usuario", idUsuario);
                values.put("id_restriccion", restId);

                db.insert("RestriccionAlimentariaUsuario", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<RestriccionAlimentaria> obtenerRestriccionesPorUsuario(int idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<RestriccionAlimentaria> restricciones = new ArrayList<>();

        String query = "SELECT p.id_restriccion, p.nombre " +
                "FROM RestriccionAlimentaria p " +
                "INNER JOIN RestriccionAlimentariaUsuario up ON p.id_restriccion = up.id_restriccion " +
                "WHERE up.id_usuario = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndexOrThrow("id_restriccion");
            int nombreIdx = cursor.getColumnIndexOrThrow("nombre");

            do {
                RestriccionAlimentaria rest = new RestriccionAlimentaria();
                rest.setId_restriccion(cursor.getInt(idIdx));
                rest.setNombre(cursor.getString(nombreIdx));
                restricciones.add(rest);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return restricciones;
    }
}
