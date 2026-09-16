package com.grupo7.recetaria.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.ui.viewmodels.AuthViewModel;

import java.util.Objects;

public class RecuperarContraseniaActivity extends AppCompatActivity {

    private TextInputEditText txtCodigo, txtPass, txtPass2;
    private TextView informacionUsuario;
    private AuthViewModel authViewModel;
    private Button btnCambiarPass;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasenia);

        txtPass = findViewById(R.id.txtPass2);
        txtCodigo = findViewById(R.id.txtToken);
        txtPass2 = findViewById(R.id.txtPass);
        informacionUsuario = findViewById(R.id.tvEmailAMostrar);
        btnCambiarPass = findViewById(R.id.btnCambiarPass);
        progressBar = findViewById(R.id.cambioProgress);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        String email = getIntent().getStringExtra("email");

        // TODO: En caso de enviar por mail estas 3 lineas deberian volar
        String token = authViewModel.obtenerTokenPorEmail(email);
        String msjUsuario = email + " - Token verificacion: " + token;
        informacionUsuario.setText(msjUsuario);

        authViewModel.getAuthResult().observe(this, resultado -> {
            gestionarAuthViewModel(resultado);
        });

        btnCambiarPass.setOnClickListener(v -> {
            String pass = Objects.requireNonNull(txtPass.getText()).toString().trim();
            String pass2 = Objects.requireNonNull(txtPass2.getText()).toString().trim();
            String tokenEnviar = Objects.requireNonNull(txtCodigo.getText()).toString().trim();

            if (tokenEnviar.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                mostrarToast(v, "Por favor, completa todos los campos");
                return;
            }

            if (!esPasswordValida(pass)) {
                mostrarToast(v, "La clave debe tener al menos 8 caracteres, numeros y letras");
                return;
            }

            if (!pass.equals(pass2)) {
                mostrarToast(v, "Las contraseñas no coinciden");
                return;
            }
            authViewModel.cambiarPass(email, tokenEnviar, pass);
        });
    }

    private void gestionarAuthViewModel(AuthResult resultado) {
        switch (resultado.getStatus()) {
            case LOADING:
                gestionarVisualizacionBotones(true);
                break;

            case SUCCESS:
                gestionarVisualizacionBotones(false);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                break;

            case ERROR:
                gestionarVisualizacionBotones(false);
                mostrarToast(findViewById(R.id.main), resultado.getErrorMessage());
                break;

            default:
                gestionarVisualizacionBotones(false);
                break;
        }
    }

    private void gestionarVisualizacionBotones(boolean estaCargando) {
        progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
        btnCambiarPass.setEnabled(!estaCargando);
        btnCambiarPass.setText(estaCargando ? "" : getString(R.string.cambiar_contrasenia));
    }
    private boolean esPasswordValida(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";
        return password.matches(pattern);
    }

    private void mostrarToast(View v, String msj){
        Snackbar snackbar = Snackbar.make(v, msj, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.primario));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

}