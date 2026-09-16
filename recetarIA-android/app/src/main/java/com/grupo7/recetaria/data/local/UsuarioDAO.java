package com.grupo7.recetaria.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.grupo7.recetaria.models.UsuarioSesion;

import java.time.LocalDate;

public class UsuarioDAO {

    private final SQLiteOpenHelper dbHelper;

    public UsuarioDAO(Context context) {
        this.dbHelper = new RecetariaDbHelper(context);
    }

    public String registrarUsuario(String email, String pass) {
        String token = generarTokenDeSeisDigitos();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("email", email);
        valores.put("pass", hashearPassword(pass));
        valores.put("codigo_validacion", token);
        valores.put("es_validado", 0);

        long resultado = db.insert("Usuario", null, valores);
        if(resultado != -1){
            return token;
        };
        return "";
    }

    public UsuarioSesion login(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String passHasheada = hashearPassword(password);

        String selection = "email = ? AND pass = ?";
        String[] selectionArgs = {email, passHasheada};

        Cursor cursor = db.query("Usuario", null, selection, selectionArgs, null, null, null);
        UsuarioSesion sesion = mapearUsuario(cursor);

        cursor.close();
        return sesion;
    }

    // --- MÉTODOS PARA GOOGLE OAUTH ---

    public UsuarioSesion obtenerUsuarioPorEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Usuario", null, "email = ?", new String[]{email}, null, null, null);

        UsuarioSesion usuario = mapearUsuario(cursor);

        if (cursor != null) cursor.close();
        return usuario;
    }

    public long insertarUsuarioGoogle(GoogleSignInAccount account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("email", account.getEmail());
        valores.put("nombre", account.getGivenName());
        valores.put("apellido", account.getFamilyName());
        valores.put("es_validado", 1); // Google ya validó el mail
        valores.put("es_premium", 0);

        long id = db.insert("Usuario", null, valores);
        return id;
    }


    public int verificarCorreo(String emailRecibido, String codigoIngresado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("es_validado", 1);

        String clausulaWhere = "email = ? AND codigo_validacion = ?";
        String[] argumentosWhere = {emailRecibido, codigoIngresado};

        int filas = db.update("Usuario", valores, clausulaWhere, argumentosWhere);
        return filas;
    }

    private UsuarioSesion mapearUsuario(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            UsuarioSesion u = new UsuarioSesion();
            u.setId_usuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
            u.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            u.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            u.setApellido(cursor.getString(cursor.getColumnIndexOrThrow("apellido")));
            u.setUsos_ia(cursor.getInt(cursor.getColumnIndexOrThrow("usos_ia")));
            String fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento"));
            if (fechaStr != null && !fechaStr.isEmpty()) {
                u.setFecha_nacimiento(LocalDate.parse(fechaStr));
            }
            u.setNacionalidad(cursor.getString(cursor.getColumnIndexOrThrow("nacionalidad")));
            u.setAdultos_familia(cursor.getInt(cursor.getColumnIndexOrThrow("adultos_familia")));
            u.setNinios_familia(cursor.getInt(cursor.getColumnIndexOrThrow("ninios_familia")));
            u.setEs_verificado(cursor.getInt(cursor.getColumnIndexOrThrow("es_validado")) == 1);
            u.setEs_premium(cursor.getInt(cursor.getColumnIndexOrThrow("es_premium")) == 1);

            return u;
        }
        return null;
    }

    private String hashearPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private String generarTokenDeSeisDigitos() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public void actualizarPerfil(UsuarioSesion usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("nombre", usuario.getNombre());
        valores.put("apellido", usuario.getApellido());

        if (usuario.getFecha_nacimiento() != null) {
            valores.put("fecha_nacimiento", usuario.getFecha_nacimiento().toString());
        }

        valores.put("adultos_familia", usuario.getAdultos_familia());
        valores.put("ninios_familia", usuario.getNinios_familia());
        valores.put("nacionalidad", usuario.getNacionalidad());

        String selection = "id_usuario = ?";
        String[] selectionArgs = { String.valueOf(usuario.getId_usuario()) };

        db.update("Usuario", valores, selection, selectionArgs);
    }

    public String otenerTokenPorEmail(String emailRecibido) {
        String token ="";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Usuario", new String[]{"codigo_validacion"}, "email = ?", new String[]{emailRecibido}, null, null, null);

        if(cursor.moveToFirst()){
            token = cursor.getString(cursor.getColumnIndexOrThrow("codigo_validacion"));
        }

        if (cursor != null) cursor.close();
        return token;
    }

    public void sumarUsoIA(int idUsuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "UPDATE Usuario SET usos_ia = usos_ia + 1 WHERE id_usuario = ?";
            db.execSQL(sql, new Object[]{idUsuario});

            Log.d("DB_UPDATE", "Uso de IA incrementado para el ID: " + idUsuario);

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al incrementar uso de IA: " + e.getMessage());
        }
    }

    public void suscribirPremium(int idUsuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "UPDATE Usuario SET es_premium = 1 WHERE id_usuario = ?";
            db.execSQL(sql, new Object[]{idUsuario});

            Log.d("DB_UPDATE", "Usuario " + idUsuario + " pasado a Premium.");

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al suscribir usuario " + idUsuario + ": " + e.getMessage());
        }
    }

    public String actualizarToken(String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String token = generarTokenDeSeisDigitos();
        String rpta = "";
        try {
            String sql = "UPDATE Usuario SET codigo_validacion = ? WHERE email = ?";
            db.execSQL(sql, new Object[]{token, email});
            rpta = token;
            Log.d("DB_UPDATE", "Usuario " + email + " con token actualizado.");
            return rpta;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al generar el token al usuario " + email + ": " + e.getMessage());
            return rpta;
        }
    }

    public long actualizarPass(String email, String token, String pass1) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String passHasheada = hashearPassword(pass1);

        try {
            ContentValues values = new ContentValues();
            values.put("pass", passHasheada);
            values.put("codigo_validacion", (String) null);

            String where = "email = ? AND codigo_validacion = ?";
            String[] args = {email, token};

            int filasAfectadas = db.update("Usuario", values, where, args);

            if (filasAfectadas > 0) {
                Log.d("DB_UPDATE", "Contraseña actualizada para: " + email);
                return 1;
            } else {
                Log.w("DB_UPDATE", "No se pudo actualizar: Token o email inválidos.");
                return -1;
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al cambiar contraseña de " + email + ": " + e.getMessage());
            return -1;
        }
    }
}