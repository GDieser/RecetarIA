package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo7.recetaria.models.Calendario;

import java.util.ArrayList;
import java.util.List;

public class CalendarioDAO {

    private final SQLiteOpenHelper dbHelper;

    public CalendarioDAO(Context context) {
        this.dbHelper = new RecetariaDbHelper(context);
    }

    public List<Calendario> obtenerRecetasPorUsuarioYfecha(int idUsuario, String fecha) {
        return obtenerRecetasFiltradas(idUsuario, fecha);
    }

    private List<Calendario> obtenerRecetasFiltradas(int idUsuario, String fecha) {
        List<Calendario> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT CR.*, R.titulo AS titulo_receta, R.dificultad, R.comensales, R.tiempo, TC.nombre AS nombre_tipo_comida " +
                "FROM CalendarioRecetas CR " +
                "LEFT JOIN Receta R ON CR.id_receta = R.id_receta " +
                "LEFT JOIN TipoComida TC ON CR.id_tipo_comida = TC.id_tipo_comida " +
                "WHERE CR.id_usuario = ? AND CR.fecha = ? " +
                "ORDER BY CR.id_calendario DESC";
        String[] args = {String.valueOf(idUsuario),fecha};

        Cursor cursor = db.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            do {
                Calendario calendario = new Calendario();
                calendario.setId_calendario(cursor.getInt(cursor.getColumnIndexOrThrow("id_calendario")));
                calendario.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                calendario.setId_receta(cursor.getInt(cursor.getColumnIndexOrThrow("id_receta")));
               calendario.setTituloReceta(cursor.getString(cursor.getColumnIndexOrThrow("titulo_receta")));
                calendario.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                calendario.setId_tipo_comida(cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_comida")));
               calendario.setComensales(cursor.getInt(cursor.getColumnIndexOrThrow("comensales")));
               calendario.setTiempo(cursor.getString(cursor.getColumnIndexOrThrow("tiempo")));
               calendario.setDificultad(cursor.getString(cursor.getColumnIndexOrThrow("dificultad")));
               calendario.setNombre_tipo_comida(cursor.getString(cursor.getColumnIndexOrThrow("nombre_tipo_comida")));
                lista.add(calendario);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public void eliminarRecetaPlanificada(int idCalendario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("CalendarioRecetas", "id_calendario = ?", new String[]{String.valueOf(idCalendario)});
        db.close();
    }

    public void eliminarRegistrosAnteriores(String fechaHoy) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("CalendarioRecetas", "fecha < ?", new String[]{fechaHoy});
        db.close();
    }

    public boolean verificarStockReceta(int idUsuario, int idReceta) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT nombre_ingrediente, cantidad, unidad FROM IngredienteReceta WHERE id_receta = ?";
        Cursor cursorIng = db.rawQuery(query, new String[]{String.valueOf(idReceta)});

        boolean hayTodo = true;

        if (cursorIng.moveToFirst()) {
            do {
                String nombre = cursorIng.getString(0);
                double cantNecesaria = cursorIng.getDouble(1);
                String unidadReceta = cursorIng.getString(2);

                String sqlStock = "SELECT pa.cantidad, u.nombre, pa.es_infaltable FROM ProductoAlacena pa " +
                        "JOIN Producto p ON pa.id_producto = p.id_producto " +
                        "LEFT JOIN Unidad u ON pa.id_unidad = u.id_unidad " +
                        "WHERE pa.id_usuario = ? AND p.nombre LIKE ?";

                Cursor cursorStock = db.rawQuery(sqlStock, new String[]{String.valueOf(idUsuario), "%" + nombre + "%"});

                if (cursorStock.moveToFirst()) {
                    double cantDisponible = cursorStock.getDouble(0);
                    String unidadAlacena = cursorStock.getString(1);
                    int esInfaltable = cursorStock.getInt(2);

                    // aca aplicamos el conver
                    if (esInfaltable == 0) {
                        double cantNecesariaConvertida = com.grupo7.recetaria.ui.otro.Conversor.convertir(
                                cantNecesaria,
                                unidadReceta,
                                unidadAlacena
                        );

                        if (cantDisponible < cantNecesariaConvertida) {
                            hayTodo = false;
                        }
                    }
                } else {
                    hayTodo = false; // No tiene el ingrediente
                }
                cursorStock.close();

                if (!hayTodo) break;
            } while (cursorIng.moveToNext());
        }
        cursorIng.close();
        return hayTodo;
    }

    /*
    public boolean verificarStockReceta(int idUsuario, int idReceta) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Buscamos los ingredientes de la receta
        String query = "SELECT nombre_ingrediente, cantidad FROM IngredienteReceta WHERE id_receta = ?";
        Cursor cursorIng = db.rawQuery(query, new String[]{String.valueOf(idReceta)});

        boolean hayTodo = true;

        if (cursorIng.moveToFirst()) {
            do {
                String nombre = cursorIng.getString(0);
                double cantNecesaria = cursorIng.getDouble(1);

                // Buscamos si el usuario tiene ese producto en su alacena
                String sqlStock = "SELECT pa.cantidad FROM ProductoAlacena pa " +
                        "JOIN Producto p ON pa.id_producto = p.id_producto " +
                        "WHERE pa.id_usuario = ? AND p.nombre LIKE ?";

                Cursor cursorStock = db.rawQuery(sqlStock, new String[]{String.valueOf(idUsuario), "%" + nombre + "%"});

                if (cursorStock.moveToFirst()) {
                    double cantDisponible = cursorStock.getDouble(0);
                    if (cantDisponible < cantNecesaria) {
                        hayTodo = false;
                    }
                } else {
                    hayTodo = false; // No tiene el ingrediente
                }
                cursorStock.close();

                if (!hayTodo) break;
            } while (cursorIng.moveToNext());
        }
        cursorIng.close();
        return hayTodo;
    }

     */

    public void guardarRecetaPlanificada(Calendario recetaACalendario) {
           SQLiteDatabase db = dbHelper.getWritableDatabase();

           db.beginTransaction();
           try {
               ContentValues valores = new ContentValues();
               valores.put("id_usuario", recetaACalendario.getId_usuario());
               valores.put("id_receta", recetaACalendario.getId_receta());
               valores.put("fecha", recetaACalendario.getFecha());
               valores.put("id_tipo_comida", recetaACalendario.getId_tipo_comida());
               db.insert("CalendarioRecetas", null, valores);
               db.setTransactionSuccessful();// Guardo correctamente
           }
           finally {
               db.endTransaction();
               db.close();
           }
           
    }

    public void cambiarFechaReceta(int idCalendario, String nuevaFecha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fecha", nuevaFecha);
        db.update("CalendarioRecetas", values, "id_calendario = ?", new String[]{String.valueOf(idCalendario)});
        db.close();
    }

    public long agendarReceta(long idReceta, int idUsuario, String fechaProgramada, int idTipoComida) {

        if(verificarStockReceta(idUsuario, (int) idReceta))
        {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("id_usuario", idUsuario);
        values.put("id_receta", idReceta);
        values.put("fecha", fechaProgramada);
        values.put("id_tipo_comida", idTipoComida);

        long idInsertado = db.insert("CalendarioRecetas", null, values);

        db.close();
        return idInsertado;}
        else{
            return -1;
        }
    }
}
