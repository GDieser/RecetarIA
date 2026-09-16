package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo7.recetaria.models.IngredienteRecetaIA;
import com.grupo7.recetaria.models.Receta;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.models.TipoComida;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RecetaDAO {

    private final SQLiteOpenHelper dbHelper;

    public RecetaDAO(Context context) {
        this.dbHelper = new RecetariaDbHelper(context);
    }

    public List<Receta> obtenerFavoritasPorUsuario(int idUsuario) {
        return obtenerRecetasFiltradas(idUsuario, -1);
    }

    public List<Receta> obtenerRecetasPorUsuarioYTipo(int idUsuario, int idTipoComida) {
        return obtenerRecetasFiltradas(idUsuario, idTipoComida);
    }

    private List<Receta> obtenerRecetasFiltradas(int idUsuario, int idTipoComida) {
        List<Receta> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT R.*, T.nombre AS nombre_tipo_comida " +
                "FROM Receta R " +
                "LEFT JOIN TipoComida T ON R.id_tipo_comida = T.id_tipo_comida " +
                "WHERE R.id_usuario = ? AND R.es_favorito = 1 ";


        String[] args = {String.valueOf(idUsuario)};

        if (idTipoComida != -1) {
            query += "AND R.id_tipo_comida = ? ";
            args = new String[]{String.valueOf(idUsuario), String.valueOf(idTipoComida)};
        }

        query += "ORDER BY R.fecha_creacion DESC";

        Cursor cursor = db.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {
                Receta receta = new Receta();
                receta.setId_receta(cursor.getInt(cursor.getColumnIndexOrThrow("id_receta")));
                receta.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                receta.setEs_favorito(cursor.getInt(cursor.getColumnIndexOrThrow("es_favorito")));
                receta.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
                receta.setId_tipo_comida(cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_comida")));
                receta.setTipoComida(cursor.getString(cursor.getColumnIndexOrThrow("nombre_tipo_comida")));
                receta.setDificultad(cursor.getString(cursor.getColumnIndexOrThrow("dificultad")));
                receta.setComensales(cursor.getInt(cursor.getColumnIndexOrThrow("comensales")));
                receta.setTiempo(cursor.getString(cursor.getColumnIndexOrThrow("tiempo")));
                receta.setLista_pasos(cursor.getString(cursor.getColumnIndexOrThrow("lista_pasos")));
                receta.setFecha_creacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion")));
                lista.add(receta);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public RecetaIA obtenerRecetaIAPorId(int idReceta) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        RecetaIA recetaIA = null;

        Cursor cursor = db.query("Receta", null, "id_receta = ?", new String[]{String.valueOf(idReceta)}, null, null, null);
        if (cursor.moveToFirst()) {
            recetaIA = new RecetaIA();
            recetaIA.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
            recetaIA.setDificultad(cursor.getString(cursor.getColumnIndexOrThrow("dificultad")));
            recetaIA.setComensales(cursor.getInt(cursor.getColumnIndexOrThrow("comensales")));
            recetaIA.setTiempo(cursor.getString(cursor.getColumnIndexOrThrow("tiempo")));
            String pasos = cursor.getString(cursor.getColumnIndexOrThrow("lista_pasos"));
            if (pasos != null && !pasos.isEmpty()) {
                recetaIA.setInstrucciones(Arrays.asList(pasos.split("\n")));
            }

            List<IngredienteRecetaIA> ingredientes = new ArrayList<>();
            Cursor cIng = db.query("IngredienteReceta", null, "id_receta = ?", new String[]{String.valueOf(idReceta)}, null, null, null);
            while (cIng.moveToNext()) {
                IngredienteRecetaIA ing = new IngredienteRecetaIA();
                ing.setNombre(cIng.getString(cIng.getColumnIndexOrThrow("nombre_completo")));
                ing.setNombreIngrediente(cIng.getString(cIng.getColumnIndexOrThrow("nombre_ingrediente")));
                ing.setCantidad(cIng.getDouble(cIng.getColumnIndexOrThrow("cantidad")));
                ing.setUnidad(cIng.getString(cIng.getColumnIndexOrThrow("unidad")));
                ingredientes.add(ing);
            }
            cIng.close();
            recetaIA.setIngredientes(ingredientes);
        }
        cursor.close();
        db.close();
        return recetaIA;
    }

    public boolean eliminarFavorito(int idUsuario, int idReceta) {
        Calendar cal = Calendar.getInstance();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hoy ;
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        hoy = String.format("%d-%02d-%02d", year, month + 1, day);

        // Verificar si está agendada a futuro
        Cursor c = db.rawQuery("SELECT 1 FROM CalendarioRecetas WHERE id_receta = ? AND fecha >= ?",
                new String[]{String.valueOf(idReceta), hoy});

        if (c.moveToFirst()) {
            c.close();
            return false;
        }
        c.close();

        db.delete("Receta", "id_receta = ? AND id_usuario = ?", new String[]{String.valueOf(idReceta), String.valueOf(idUsuario)});
        db.close();
        return true;

    }

    public long guardarRecetaComoFavorita(RecetaIA recetaIA, int idUsuario, int tipoComida) {

        long idReceta = -1;
        //Consultamos la base si ya existe la receta.
        idReceta = obtenerIdRecetaPorTitulo(recetaIA.getTitulo(), idUsuario);
        if(idReceta != -1){
            return idReceta; // Devolvemos el ID existente si ya está guardada
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //La receta NO existe, la guarda en favoritos y retorna el id receta.
        db.beginTransaction();
        try {
            ContentValues valores = new ContentValues();
            valores.put("id_usuario", idUsuario);
            valores.put("titulo", recetaIA.getTitulo());
            valores.put("dificultad", recetaIA.getDificultad());
            valores.put("comensales", recetaIA.getComensales());
            valores.put("id_tipo_comida", tipoComida);
            valores.put("fecha_creacion", String.valueOf(System.currentTimeMillis()));
            valores.put("tiempo", recetaIA.getTiempo());
            valores.put("lista_pasos", String.join("\n", recetaIA.getInstrucciones()));
            valores.put("es_favorito", 1);

            idReceta = db.insert("Receta", null, valores);

            if (idReceta != -1 && recetaIA.getIngredientes() != null) {
                for (IngredienteRecetaIA ing : recetaIA.getIngredientes()) {
                    ContentValues vIng = new ContentValues();
                    vIng.put("id_receta", idReceta);
                    vIng.put("nombre_completo", ing.getNombre());
                    vIng.put("nombre_ingrediente", ing.getNombreIngrediente());
                    vIng.put("cantidad", ing.getCantidad());
                    vIng.put("unidad", ing.getUnidad());

                    db.insert("IngredienteReceta", null, vIng);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return idReceta;
    }

    public int obtenerIdRecetaPorTitulo(String titulo, int idUsuario) {
        //Para verificar si existe o no la receta
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int id = -1;
        Cursor cursor = db.query("Receta", new String[]{"id_receta"}, "titulo = ? AND id_usuario = ?",
                new String[]{titulo, String.valueOf(idUsuario)}, null, null, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id_receta"));
        }
        cursor.close();
        db.close();
        return id;
    }
    public List<TipoComida> obtenerTiposComidaFavoritosPorUsuario(int idUsuario) {
        List<TipoComida> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT DISTINCT T.id_tipo_comida, T.nombre " +
                "FROM TipoComida T " +
                "JOIN Receta R ON T.id_tipo_comida = R.id_tipo_comida " +
                "WHERE R.id_usuario = ? AND R.es_favorito = 1";
        String[] args = {String.valueOf(idUsuario)};
        Cursor cursor = db.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            do {
                TipoComida tipo = new TipoComida();
                tipo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_comida")));
                tipo.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                lista.add(tipo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;

    }

}
