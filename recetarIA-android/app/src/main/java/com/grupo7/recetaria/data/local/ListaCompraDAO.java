package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grupo7.recetaria.models.ItemListaCompra;

import java.util.ArrayList;
import java.util.List;

public class ListaCompraDAO {

    private final SQLiteOpenHelper dbHelper;

    public ListaCompraDAO(Context context) {
        this.dbHelper = new RecetariaDbHelper(context);
    }

    // traigo listas
    public List<ItemListaCompra> obtenerListaPorUsuario(int idUsuario) {
        List<ItemListaCompra> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT l.*, p.nombre AS nombre_producto, u.nombre AS nombre_unidad, r.nombre AS nombre_rubro " +
                "FROM ProductoListaCompra l " +
                "INNER JOIN Producto p ON l.id_producto = p.id_producto " +
                "LEFT JOIN Rubro r ON p.id_rubro = r.id_rubro " +
                "LEFT JOIN Unidad u ON l.id_unidad = u.id_unidad " +
                "WHERE l.id_usuario = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                ItemListaCompra item = new ItemListaCompra();
                item.setId_producto_lista(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto_lista")));
                item.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                item.setId_producto(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")));

                int idxUnidad = cursor.getColumnIndex("id_unidad");
                if (idxUnidad != -1 && !cursor.isNull(idxUnidad)) {
                    item.setId_unidad(cursor.getInt(idxUnidad));
                }

                item.setCantidad(cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad")));
                item.setEs_adquirido(cursor.getInt(cursor.getColumnIndexOrThrow("es_adquirido")) == 1);

                item.setNombre_producto(cursor.getString(cursor.getColumnIndexOrThrow("nombre_producto")));

                int idxNombreUnidad = cursor.getColumnIndex("nombre_unidad");
                if (idxNombreUnidad != -1 && !cursor.isNull(idxNombreUnidad)) {
                    item.setNombre_unidad(cursor.getString(idxNombreUnidad));
                }

                int idxNombreRubro = cursor.getColumnIndex("nombre_rubro");
                if (idxNombreRubro != -1 && !cursor.isNull(idxNombreRubro)) {
                    item.setNombre_rubro(cursor.getString(idxNombreRubro));
                }

                lista.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // verifico listas
    public ItemListaCompra obtenerItemPorNombre(int idUsuario, String nombreProducto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ItemListaCompra item = null;

        String query = "SELECT l.*, p.nombre AS nombre_producto, u.nombre AS nombre_unidad " +
                "FROM ProductoListaCompra l " +
                "INNER JOIN Producto p ON l.id_producto = p.id_producto " +
                "LEFT JOIN Unidad u ON l.id_unidad = u.id_unidad " +
                "WHERE l.id_usuario = ? AND p.nombre COLLATE NOCASE = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario), nombreProducto});

        if (cursor.moveToFirst()) {
            item = new ItemListaCompra();
            item.setId_producto_lista(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto_lista")));
            item.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
            item.setId_producto(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")));
            item.setCantidad(cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad")));
            item.setEs_adquirido(cursor.getInt(cursor.getColumnIndexOrThrow("es_adquirido")) == 1);
            item.setNombre_producto(cursor.getString(cursor.getColumnIndexOrThrow("nombre_producto")));

            int idxNombreUnidad = cursor.getColumnIndex("nombre_unidad");
            if (idxNombreUnidad != -1 && !cursor.isNull(idxNombreUnidad)) {
                item.setNombre_unidad(cursor.getString(idxNombreUnidad));
            }
        }
        cursor.close();
        db.close();
        return item;
    }

    // agrego producto a lsta
    public boolean agregarProductoALista(int idUsuario, String nombreProducto, String nombreRubro, String nombreUnidad, double cantidad, int esAdquirido) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean exito = false;
        db.beginTransaction();
        try {
            int idRubro = obtenerIdPorNombre(db, "Rubro", "id_rubro", nombreRubro);
            int idUnidad = obtenerIdPorNombre(db, "Unidad", "id_unidad", nombreUnidad);

            int idProducto = -1;
            Cursor cursorProd = db.rawQuery("SELECT id_producto FROM Producto WHERE nombre COLLATE NOCASE = ?", new String[]{nombreProducto});
            if (cursorProd.moveToFirst()) idProducto = cursorProd.getInt(0);
            cursorProd.close();

            if (idProducto == -1) {
                ContentValues valoresProd = new ContentValues();
                valoresProd.put("nombre", nombreProducto);
                valoresProd.put("id_rubro", idRubro);
                idProducto = (int) db.insert("Producto", null, valoresProd);
            }

            ContentValues insertValues = new ContentValues();
            insertValues.put("id_usuario", idUsuario);
            insertValues.put("id_producto", idProducto);
            insertValues.put("id_unidad", idUnidad);
            insertValues.put("cantidad", cantidad);
            insertValues.put("es_adquirido", esAdquirido);

            db.insert("ProductoListaCompra", null, insertValues);

            db.setTransactionSuccessful();
            exito = true;
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            db.endTransaction();
            db.close();
        }
        return exito;
    }

    public boolean agregarProductoALista(ItemListaCompra item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean exito = false;
        db.beginTransaction();
        try {
            int idRubro = obtenerIdPorNombre(db, "Rubro", "id_rubro", item.getNombre_rubro());
            int idUnidad = item.getId_unidad();

            int idProducto = -1;
            Cursor cursorProd = db.rawQuery("SELECT id_producto FROM Producto WHERE nombre COLLATE NOCASE = ?", new String[]{item.getNombre_producto()});
            if (cursorProd.moveToFirst()) idProducto = cursorProd.getInt(0);
            cursorProd.close();

            if (idProducto == -1) {
                ContentValues valoresProd = new ContentValues();
                valoresProd.put("nombre", item.getNombre_producto());
                valoresProd.put("id_rubro", idRubro);
                idProducto = (int) db.insert("Producto", null, valoresProd);
            }

            ContentValues insertValues = new ContentValues();
            insertValues.put("id_usuario", item.getId_usuario());
            insertValues.put("id_producto", idProducto);
            insertValues.put("id_unidad", idUnidad);
            insertValues.put("cantidad", item.getCantidad());
            insertValues.put("es_adquirido", 0);

            db.insert("ProductoListaCompra", null, insertValues);

            db.setTransactionSuccessful();
            exito = true;
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            db.endTransaction();
            db.close();
        }
        return exito;
    }

    // actualizo carrito
    public boolean alternarEstadoCompra(int idProductoLista, boolean enCarrito) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("es_adquirido", enCarrito ? 1 : 0);
        int filas = db.update("ProductoListaCompra", valores, "id_producto_lista = ?", new String[]{String.valueOf(idProductoLista)});
        db.close();
        return filas > 0;
    }

    // cant y uni
    public boolean actualizarCantidadYUnidad(int idProductoLista, double nuevaCantidad, String nombreNuevaUnidad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int idUnidad = obtenerIdPorNombre(db, "Unidad", "id_unidad", nombreNuevaUnidad);

        ContentValues valores = new ContentValues();
        valores.put("cantidad", nuevaCantidad);
        if (idUnidad != -1) valores.put("id_unidad", idUnidad);

        int filas = db.update("ProductoListaCompra", valores, "id_producto_lista = ?", new String[]{String.valueOf(idProductoLista)});
        db.close();
        return filas > 0;
    }

    // eliminar item
    public boolean eliminarItemLista(int idProductoLista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("ProductoListaCompra", "id_producto_lista = ?", new String[]{String.valueOf(idProductoLista)});
        db.close();
        return filas > 0;
    }

    // elimina toda la lista
    public void limpiarComprados(int idUsuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("ProductoListaCompra", "id_usuario = ? AND es_adquirido = 1", new String[]{String.valueOf(idUsuario)});
        db.close();
    }

    // para id
    private int obtenerIdPorNombre(SQLiteDatabase db, String tabla, String columnaId, String nombre) {
        if (nombre == null || nombre.isEmpty()) return -1;
        int id = -1;
        Cursor cursor = db.rawQuery("SELECT " + columnaId + " FROM " + tabla + " WHERE nombre = ?", new String[]{nombre});
        if (cursor.moveToFirst()) id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    public ItemListaCompra obtenerItemPorProducto(int idUsuario, int idProducto) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        ItemListaCompra item = null;

        String query = "SELECT l.*, p.nombre AS nombre_producto, u.nombre AS nombre_unidad " +
                "FROM ProductoListaCompra l " +
                "INNER JOIN Producto p ON l.id_producto = p.id_producto " +
                "LEFT JOIN Unidad u ON l.id_unidad = u.id_unidad " +
                "WHERE l.id_usuario = ? AND l.id_producto = ?";

        String[] args = { String.valueOf(idUsuario), String.valueOf(idProducto) };
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, args);

            if (cursor != null && cursor.moveToFirst()) {
                item = new ItemListaCompra();

                item.setId_producto_lista(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto_lista")));
                item.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                item.setId_producto(cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")));
                item.setCantidad(cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad")));

                item.setEs_adquirido(cursor.getInt(cursor.getColumnIndexOrThrow("es_adquirido")) == 1);

                item.setNombre_producto(cursor.getString(cursor.getColumnIndexOrThrow("nombre_producto")));

                int idxUnidad = cursor.getColumnIndex("nombre_unidad");
                if (idxUnidad != -1 && !cursor.isNull(idxUnidad)) {
                    item.setNombre_unidad(cursor.getString(idxUnidad));
                }
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener item por ID: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return item;
    }

    public void actualizarCantidad(int idProductoLista, double nuevaCantidad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);

        String selection = "id_producto_lista = ?";
        String[] selectionArgs = { String.valueOf(idProductoLista) };

        int filasAfectadas = db.update(
                "ProductoListaCompra",
                values,
                selection,
                selectionArgs
        );

        if (filasAfectadas > 0) {
            Log.d("DB_DEBUG", "Cantidad actualizada con éxito para el ID: " + idProductoLista);
        } else {
            Log.e("DB_DEBUG", "No se pudo actualizar la cantidad. ID no encontrado.");
        }
    }

    //modificar
    public boolean editarProductoCompleto(int idProductoLista, int idUsuario, String nuevoNombre, String nuevoRubro, String nuevaUnidad, double nuevaCantidad) {
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

            ContentValues updateLista = new ContentValues();
            updateLista.put("id_producto", idProducto);
            updateLista.put("id_unidad", idUnidad);
            updateLista.put("cantidad", nuevaCantidad);

            db.update("ProductoListaCompra", updateLista, "id_producto_lista = ?", new String[]{String.valueOf(idProductoLista)});

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
