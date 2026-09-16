package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grupo7.recetaria.models.ItemAlacena;

import java.util.ArrayList;
import java.util.List;

public class AlacenaDAO {

    private final SQLiteOpenHelper dbHelper;

    public AlacenaDAO(Context context) {
        this.dbHelper = new RecetariaDbHelper(context);
    }

    // lee
    // modifica

    public boolean actualizarCantidad(int idProductoAlacena, double nuevaCantidad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("cantidad", nuevaCantidad);

        int filasAfectadas = db.update("ProductoAlacena", valores, "id_producto_alacena = ?",
                new String[]{String.valueOf(idProductoAlacena)});
        db.close();
        return filasAfectadas > 0;
    }
    // infaltables

    public boolean alternarInfaltable(int idProductoAlacena, int esInfaltable) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("es_infaltable", esInfaltable);

        int filasAfectadas = db.update("ProductoAlacena", valores, "id_producto_alacena = ?",
                new String[]{String.valueOf(idProductoAlacena)});
        db.close();
        return filasAfectadas > 0;
    }
    // elimina

    public boolean eliminarProductoAlacena(int idProductoAlacena) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("ProductoAlacena", "id_producto_alacena = ?",
                new String[]{String.valueOf(idProductoAlacena)});
        db.close();
        return filasAfectadas > 0;
    }
    public List<ItemAlacena> obtenerAlacenaPorUsuario(int idUsuario) {
        List<ItemAlacena> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String query = "SELECT a.*, p.nombre AS nombre_producto, u.nombre AS nombre_unidad, r.nombre AS nombre_rubro " +
                "FROM ProductoAlacena a " +
                "INNER JOIN Producto p ON a.id_producto = p.id_producto " +
                "LEFT JOIN Rubro r ON p.id_rubro = r.id_rubro " +
                "LEFT JOIN Unidad u ON a.id_unidad = u.id_unidad " +
                "WHERE a.id_usuario = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                ItemAlacena item = new ItemAlacena();
                item.setId_producto_alacena(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto_alacena")));
                item.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                item.setId_producto(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")));

                int idxUnidad = cursor.getColumnIndexOrThrow("id_unidad");
                if (!cursor.isNull(idxUnidad)) {
                    item.setId_unidad(cursor.getInt(idxUnidad));
                }

                item.setCantidad(cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad")));
                item.setEs_infaltable(cursor.getInt(cursor.getColumnIndexOrThrow("es_infaltable")));

                item.setNombre_producto(cursor.getString(cursor.getColumnIndexOrThrow("nombre_producto")));
                item.setNombre_unidad(cursor.getString(cursor.getColumnIndexOrThrow("nombre_unidad")));

                int idxRubro = cursor.getColumnIndex("nombre_rubro");
                if (idxRubro != -1 && !cursor.isNull(idxRubro)) {
                    item.setNombre_rubro(cursor.getString(idxRubro));
                }

                lista.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public List<ItemAlacena> obtenerAlacenaPorUsuarioPrompt(int idUsuario) {
        List<ItemAlacena> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String query = "SELECT a.*, p.nombre AS nombre_producto, u.nombre AS nombre_unidad, r.nombre AS nombre_rubro, a.cantidad " +
                "FROM ProductoAlacena a " +
                "INNER JOIN Producto p ON a.id_producto = p.id_producto " +
                "LEFT JOIN Rubro r ON p.id_rubro = r.id_rubro " +
                "LEFT JOIN Unidad u ON a.id_unidad = u.id_unidad " +
                "WHERE a.id_usuario = ?" +
                "AND a.cantidad > 0";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                ItemAlacena item = new ItemAlacena();
                item.setId_producto_alacena(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto_alacena")));
                item.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                item.setId_producto(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")));

                int idxUnidad = cursor.getColumnIndexOrThrow("id_unidad");
                if (!cursor.isNull(idxUnidad)) {
                    item.setId_unidad(cursor.getInt(idxUnidad));
                }

                item.setCantidad(cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad")));
                item.setEs_infaltable(cursor.getInt(cursor.getColumnIndexOrThrow("es_infaltable")));

                item.setNombre_producto(cursor.getString(cursor.getColumnIndexOrThrow("nombre_producto")));
                item.setNombre_unidad(cursor.getString(cursor.getColumnIndexOrThrow("nombre_unidad")));

                int idxRubro = cursor.getColumnIndex("nombre_rubro");
                if (idxRubro != -1 && !cursor.isNull(idxRubro)) {
                    item.setNombre_rubro(cursor.getString(idxRubro));
                }

                lista.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }
    // trae los rubros
    public List<String> obtenerNombresRubros() {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM Rubro ORDER BY nombre ASC", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // trae unidades para los desp
    public List<String> obtenerNombresUnidades() {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM Unidad ORDER BY nombre ASC", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // agrega
    public boolean agregarProductoAAlacena(int idUsuario, String nombreProducto, String nombreRubro, String nombreUnidad, double cantidad, int esInfaltable) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean exito = false;

        db.beginTransaction();
        try {
            int idRubro = obtenerIdPorNombre(db, "Rubro", "id_rubro", nombreRubro);
            int idUnidad = obtenerIdPorNombre(db, "Unidad", "id_unidad", nombreUnidad);

            int idProducto = -1;
            Cursor cursorProd = db.rawQuery("SELECT id_producto FROM Producto WHERE nombre COLLATE NOCASE = ?", new String[]{nombreProducto});
            if (cursorProd.moveToFirst()) {
                idProducto = cursorProd.getInt(0); // Ya existe
            }
            cursorProd.close();

            if (idProducto == -1) {
                ContentValues valoresProd = new ContentValues();
                valoresProd.put("nombre", nombreProducto);
                valoresProd.put("id_rubro", idRubro);
                idProducto = (int) db.insert("Producto", null, valoresProd);
            }

            int idProductoAlacena = -1;
            double cantidadActual = 0;
            Cursor cursorAlacena = db.rawQuery("SELECT id_producto_alacena, cantidad FROM ProductoAlacena WHERE id_usuario = ? AND id_producto = ?",
                    new String[]{String.valueOf(idUsuario), String.valueOf(idProducto)});

            if (cursorAlacena.moveToFirst()) {
                idProductoAlacena = cursorAlacena.getInt(0);
                cantidadActual = cursorAlacena.getDouble(1);
            }
            cursorAlacena.close();

            if (idProductoAlacena != -1) {
                String unidadActual = null;
                Cursor cursorUnidad = db.rawQuery(
                        "SELECT u.nombre FROM ProductoAlacena a " +
                                "INNER JOIN Unidad u ON a.id_unidad = u.id_unidad " +
                                "WHERE a.id_producto_alacena = ?",
                        new String[]{String.valueOf(idProductoAlacena)});

                if (cursorUnidad.moveToFirst()) {
                    unidadActual = cursorUnidad.getString(0);
                }
                cursorUnidad.close();

                double cantidadConvertida;
                if (unidadActual != null) {
                    cantidadConvertida = com.grupo7.recetaria.ui.otro.Conversor.convertir(cantidad, nombreUnidad, unidadActual);
                } else {
                    cantidadConvertida = cantidad;
                }
                ContentValues updateValues = new ContentValues();
                updateValues.put("cantidad", cantidadActual + cantidadConvertida);

                if (esInfaltable == 1) updateValues.put("es_infaltable", 1);

                db.update("ProductoAlacena", updateValues, "id_producto_alacena = ?", new String[]{String.valueOf(idProductoAlacena)});
            } else {
                ContentValues insertValues = new ContentValues();
                insertValues.put("id_usuario", idUsuario);
                insertValues.put("id_producto", idProducto);
                insertValues.put("id_unidad", idUnidad);
                insertValues.put("cantidad", cantidad);
                insertValues.put("es_infaltable", esInfaltable);
                db.insert("ProductoAlacena", null, insertValues);
            }

            db.setTransactionSuccessful();
            exito = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return exito;
    }

    // busca id
    private int obtenerIdPorNombre(SQLiteDatabase db, String tabla, String columnaId, String nombre) {
        if (nombre == null || nombre.isEmpty()) return -1;
        int id = -1;
        Cursor cursor = db.rawQuery("SELECT " + columnaId + " FROM " + tabla + " WHERE nombre = ?", new String[]{nombre});
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    //actualizar uni
    public boolean actualizarCantidadYUnidad(int idProductoAlacena, double nuevaCantidad, String nombreNuevaUnidad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int idUnidad = obtenerIdPorNombre(db, "Unidad", "id_unidad", nombreNuevaUnidad);

        ContentValues valores = new ContentValues();
        valores.put("cantidad", nuevaCantidad);

        if (idUnidad != -1) {
            valores.put("id_unidad", idUnidad);
        }

        int filasAfectadas = db.update("ProductoAlacena", valores, "id_producto_alacena = ?",
                new String[]{String.valueOf(idProductoAlacena)});
        db.close();
        return filasAfectadas > 0;
    }

    public String obtenerUnidadPreferida(int idUsuario, int idProducto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nombreUnidad = null;

        String query = "SELECT u.nombre FROM ProductoAlacena a " +
                "INNER JOIN Unidad u ON a.id_unidad = u.id_unidad " +
                "WHERE a.id_usuario = ? AND a.id_producto = ?";

        String[] args = { String.valueOf(idUsuario), String.valueOf(idProducto) };

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, args);

            if (cursor != null && cursor.moveToFirst()) {
                nombreUnidad = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener unidad preferida: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return nombreUnidad;
    }

    public void restarStock(int idUsuario, int idProducto, double cantidadARestar, String unidadReceta) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String query = "SELECT id_producto_alacena, cantidad FROM ProductoAlacena " +
                "WHERE id_usuario = ? AND id_producto = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario), String.valueOf(idProducto)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idAlacena = cursor.getInt(0);
                double cantidadActual = cursor.getDouble(1);

                if (cantidadActual > 0) {
                    double nuevaCantidad = cantidadActual - cantidadARestar;
                    if (nuevaCantidad <= 0) {
                        db.delete("ProductoAlacena", "id_producto_alacena = ?", new String[]{String.valueOf(idAlacena)});
                    } else {
                        ContentValues values = new ContentValues();
                        values.put("cantidad", nuevaCantidad);
                        db.update("ProductoAlacena", values, "id_producto_alacena = ?", new String[]{String.valueOf(idAlacena)});
                    }
                }
            }
            cursor.close();
        }
    }

    //modifica
    public boolean editarProductoCompleto(int idProductoAlacena, int idUsuario, String nuevoNombre, String nuevoRubro, String nuevaUnidad, double nuevaCantidad, int esInfaltable) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean exito = false;

        db.beginTransaction();
        try {
            int idRubro = obtenerIdPorNombre(db, "Rubro", "id_rubro", nuevoRubro);
            int idUnidad = obtenerIdPorNombre(db, "Unidad", "id_unidad", nuevaUnidad);

            int idProducto = -1;
            Cursor cursorProd = db.rawQuery("SELECT id_producto FROM Producto WHERE nombre COLLATE NOCASE = ?", new String[]{nuevoNombre});
            if (cursorProd.moveToFirst()) {
                idProducto = cursorProd.getInt(0);
            }
            cursorProd.close();

            if (idProducto == -1) {
                ContentValues valoresProd = new ContentValues();
                valoresProd.put("nombre", nuevoNombre);
                valoresProd.put("id_rubro", idRubro);
                idProducto = (int) db.insert("Producto", null, valoresProd);
            } else {
                ContentValues updateProd = new ContentValues();
                updateProd.put("id_rubro", idRubro);
                db.update("Producto", updateProd, "id_producto = ?", new String[]{String.valueOf(idProducto)});
            }

            ContentValues updateAlacena = new ContentValues();
            updateAlacena.put("id_producto", idProducto);
            updateAlacena.put("id_unidad", idUnidad);
            updateAlacena.put("cantidad", nuevaCantidad);
            updateAlacena.put("es_infaltable", esInfaltable);

            db.update("ProductoAlacena", updateAlacena, "id_producto_alacena = ?", new String[]{String.valueOf(idProductoAlacena)});

            db.setTransactionSuccessful();
            exito = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return exito;
    }


}
