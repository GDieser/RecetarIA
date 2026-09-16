package com.grupo7.recetaria.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grupo7.recetaria.models.TipoComida;

import java.util.ArrayList;
import java.util.List;

public class TipoComidaDao {

    private RecetariaDbHelper recetariaDbHelper;


    public TipoComidaDao(Context context) {
        this.recetariaDbHelper = new RecetariaDbHelper(context);
    }

    public List<TipoComida> obtenerTiposComida() {
        SQLiteDatabase db = recetariaDbHelper.getReadableDatabase();
        List<TipoComida> lista = new ArrayList<>();
        Cursor cursor = db.query("TipoComida", null, null, null, null, null, "nombre ASC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_comida"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                lista.add(new TipoComida(id, nombre));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

}
