package com.grupo7.recetaria.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.PreferenciasDAO;
import com.grupo7.recetaria.data.local.RestriccionesDAO;
import com.grupo7.recetaria.data.local.UsuarioDAO;
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.models.UsuarioSesion;

import java.time.LocalDate;
import java.util.List;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private UsuarioDAO usuarioDAO;
    private PreferenciasDAO preferenciasDAO;
    private RestriccionesDAO restriccionesDAO;
    private MutableLiveData<String> _token = new MutableLiveData<>();
    public MutableLiveData<AuthResult> getAuthResult() {
        return authResult;
    }


    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.usuarioDAO = new UsuarioDAO(application.getApplicationContext());
        this.preferenciasDAO = new PreferenciasDAO(application.getApplicationContext());
        this.restriccionesDAO = new RestriccionesDAO(application.getApplicationContext());
    }


    public void loginConEmail(String email, String password) {
        authResult.setValue(AuthResult.loading());
        UsuarioSesion usuarioSesion = usuarioDAO.login(email, password);
        if (usuarioSesion!=null){
            SessionManager.getInstance(getApplication()).crearSesion(usuarioSesion);
            authResult.setValue(AuthResult.success(usuarioSesion.isEs_verificado()));

        } else {
            authResult.setValue(AuthResult.error("Email o contraseñas incorrectos, intente nuevamente"));
        }
    }

    public void loginConGoogle(GoogleSignInAccount account) {
        authResult.setValue(AuthResult.loading());

        String email = account.getEmail();
        UsuarioSesion usuario = usuarioDAO.obtenerUsuarioPorEmail(email);

        if (usuario == null) {
            usuarioDAO.insertarUsuarioGoogle(account);
            usuario = usuarioDAO.obtenerUsuarioPorEmail(email);
        }
        SessionManager.getInstance(getApplication()).crearSesion(usuario);
        authResult.setValue(AuthResult.success(true));
    }

    public void registrar(String email, String pass) {
        authResult.setValue(AuthResult.loading());
        String token = usuarioDAO.registrarUsuario(email, pass);
        _token.postValue(token);
        if (!token.isEmpty()){
            authResult.setValue(AuthResult.success(false));
        } else {
            authResult.setValue(AuthResult.error("Email ya existe. Puede recuperar contraseña si no se acuerda"));
        }
    }

    public String recuperarContrasenia(String email) {
        authResult.setValue(AuthResult.loading());
        String token = usuarioDAO.actualizarToken(email);
        authResult.setValue(AuthResult.success(false));
        return token;
    }

    public void verificarCodigo(String emailRecibido, String codigoIngresado) {
        authResult.setValue(AuthResult.loading());
        int filas_actualizadas = usuarioDAO.verificarCorreo(emailRecibido, codigoIngresado);
        if(filas_actualizadas == 1){
            authResult.setValue(AuthResult.success(true));
            UsuarioSesion usuario = usuarioDAO.obtenerUsuarioPorEmail(emailRecibido);
            SessionManager.getInstance(getApplication()).crearSesion(usuario);
        } else {
            authResult.setValue(AuthResult.error("No se pudo verificar el correo, intente nuevamente"));
        }
    }

    public void reenviarCodigoVerificacion(String emailRecibido) {
        // TODO: METODO para restablecer CONTRASEÑA.

    }

    public void guardarPerfilCompleto(String nombre, String apellido, LocalDate fechaSeleccionada, int cantAdultos, int cantNinios, String nacionalidad, List<Integer> pref, List<Integer> rest) {
        authResult.postValue(AuthResult.loading());
        new Thread(() -> {
            try {
                UsuarioSesion usuario = SessionManager.getInstance(this.getApplication()).usuarioLogueado();

                if (usuario != null) {
                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setFecha_nacimiento(fechaSeleccionada);
                    usuario.setAdultos_familia(cantAdultos);
                    usuario.setNinios_familia(cantNinios);
                    usuario.setNacionalidad(nacionalidad);

                    usuarioDAO.actualizarPerfil(usuario);
                    int idUsuario = usuario.getId_usuario();
                    this.preferenciasDAO.asignarPreferencias(idUsuario, pref) ;
                    this.restriccionesDAO.asignarRestricciones(idUsuario, rest);

                    SessionManager.getInstance(this.getApplication()).crearSesion(usuario);

                    authResult.postValue(AuthResult.success(true));
                } else {
                    authResult.postValue(AuthResult.error("No se encontró una sesión activa"));
                }
            } catch (Exception e) {
                authResult.postValue(AuthResult.error("Error al guardar: " + e.getMessage()));
            }
        }).start();
    }

    public String obtenerTokenPorEmail(String emailRecibido) {
        return usuarioDAO.otenerTokenPorEmail(emailRecibido);
    }

    public void cambiarPass(String email, String token, String pass) {
        authResult.postValue(AuthResult.loading());
        long resultado = usuarioDAO.actualizarPass(email, token, pass);
        if(resultado == -1){
            authResult.postValue(AuthResult.error("Error al validar credenciales"));
            return;
        }
        authResult.postValue(AuthResult.success(true));
    }
}
