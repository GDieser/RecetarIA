package com.grupo7.recetaria.auth;

import android.content.Context;
import android.content.SharedPreferences;
import com.grupo7.recetaria.models.UsuarioSesion;

import java.time.LocalDate;

public class SessionManager {

    private static final String PREF_NAME = "RecetariaSession";
    private static SessionManager sessionManager;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        this.pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }
    public static synchronized SessionManager getInstance(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager;
    }
    public void crearSesion(UsuarioSesion usuario) {
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("id_usuario", usuario.getId_usuario());
        editor.putString("email", usuario.getEmail());
        editor.putString("nombre", usuario.getNombre());
        editor.putString("nacionalidad", usuario.getNacionalidad());
        editor.putString("apellido", usuario.getApellido());
        if (usuario.getFecha_nacimiento() != null) {
            editor.putString("fecha_nacimiento", usuario.getFecha_nacimiento().toString());
        }
        editor.putInt("adultos_familia", usuario.getAdultos_familia());
        editor.putInt("ninios_familia", usuario.getNinios_familia());
        editor.putBoolean("es_verificado", usuario.isEs_verificado());
        editor.putInt("usos_ia", usuario.getUsos_ia());
        editor.putBoolean("es_premium", usuario.isEs_premium());

        editor.apply();
    }
    public UsuarioSesion usuarioLogueado() {
        int id_usuario = pref.getInt("id_usuario", -1);

        if (id_usuario == -1) {
            return null;
        }
        UsuarioSesion usuario = new UsuarioSesion();
        usuario.setId_usuario(id_usuario);
        usuario.setEmail(pref.getString("email", ""));
        usuario.setNombre(pref.getString("nombre", ""));
        usuario.setNacionalidad(pref.getString("nacionalidad", ""));
        usuario.setApellido(pref.getString("apellido", ""));
        String fechaStr = pref.getString("fecha_nacimiento", null);
        if (fechaStr != null && !fechaStr.isEmpty()) {
            usuario.setFecha_nacimiento(LocalDate.parse(fechaStr));
        }
        usuario.setAdultos_familia(pref.getInt("adultos_familia", 0));
        usuario.setNinios_familia(pref.getInt("ninios_familia", 0));
        usuario.setUsos_ia(pref.getInt("usos_ia", 0));
        usuario.setEs_verificado(pref.getBoolean("es_verificado",false));
        usuario.setEs_premium(pref.getBoolean("es_premium", false));
        return usuario;
    }
    public boolean isLoggedIn() {
        return pref.getBoolean("isLoggedIn", false);
    }
    public void logout() {
        editor.clear();
        editor.apply();
    }
}