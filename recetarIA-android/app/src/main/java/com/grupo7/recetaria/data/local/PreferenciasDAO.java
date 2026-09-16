package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo7.recetaria.models.PreferenciaAlimentaria;

import java.util.ArrayList;
import java.util.List;

public class PreferenciasDAO {

    private final SQLiteOpenHelper dbHelper;
    public  PreferenciasDAO(Context context){
        dbHelper = new RecetariaDbHelper(context);
    }

    public List<PreferenciaAlimentaria> obtenerPreferenciasAlimentarias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<PreferenciaAlimentaria> preferencias = new ArrayList<>();

        Cursor cursor = db.query("PreferenciaAlimentaria", null, null, null, null, null, null);
        int nombre_idx = cursor.getColumnIndexOrThrow("nombre");
        int id_idx = cursor.getColumnIndexOrThrow("id_preferencia");

        while (cursor.moveToNext()) {
            PreferenciaAlimentaria pref = new PreferenciaAlimentaria();
            pref.setNombre(cursor.getString(nombre_idx));
            pref.setId_preferencia(cursor.getInt(id_idx));
            preferencias.add(pref);
        }

        cursor.close();
        return preferencias;
    }

    public List<PreferenciaAlimentaria> obtenerPreferenciasPorUsuario(int idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PreferenciaAlimentaria> preferencias = new ArrayList<>();

        String query = "SELECT p.id_preferencia, p.nombre " +
                "FROM PreferenciaAlimentaria p " +
                "INNER JOIN PreferenciaAlimentariaUsuario up ON p.id_preferencia = up.id_preferencia " +
                "WHERE up.id_usuario = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndexOrThrow("id_preferencia");
            int nombreIdx = cursor.getColumnIndexOrThrow("nombre");

            do {
                PreferenciaAlimentaria pref = new PreferenciaAlimentaria();
                pref.setId_preferencia(cursor.getInt(idIdx));
                pref.setNombre(cursor.getString(nombreIdx));
                preferencias.add(pref);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return preferencias;
    }

    public void asignarPreferencias(int idUsuario, List<Integer> prefIds) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete("PreferenciaAlimentariaUsuario", "id_usuario = ?", new String[]{String.valueOf(idUsuario)});

            for (Integer prefId : prefIds) {
                ContentValues values = new ContentValues();
                values.put("id_usuario", idUsuario);
                values.put("id_preferencia", prefId);

                db.insert("PreferenciaAlimentariaUsuario", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
