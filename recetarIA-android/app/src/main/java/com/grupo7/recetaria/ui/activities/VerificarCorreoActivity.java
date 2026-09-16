package com.grupo7.recetaria.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.viewmodels.AuthViewModel;

import java.util.Objects;

public class VerificarCorreoActivity extends AppCompatActivity {

    TextView email;
    View btnRegistrarNuevamente;
    TextInputEditText codigo;
    Button bntVerificar;
    TextView btnReenviarCodigo;
    ProgressBar progressBar;
    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_correo);

        email = findViewById(R.id.tvEmailAMostrar);
        btnRegistrarNuevamente = findViewById(R.id.btnCambiarCuenta);
        codigo = findViewById(R.id.txtCodigo);
        bntVerificar = findViewById(R.id.btnVerificar);
        btnReenviarCodigo = findViewById(R.id.btnReenviarCodigo);
        progressBar = findViewById(R.id.recuperoProgress);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        String emailRecibido = getIntent().getStringExtra("email_registro");
        String token = authViewModel.obtenerTokenPorEmail(emailRecibido);

        email.setText(emailRecibido + " - token: " + token);


        authViewModel.getAuthResult().observe(this, this::gestionarAuthViewModel);

        btnRegistrarNuevamente.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            SessionManager.getInstance(this).logout();
        });

        bntVerificar.setOnClickListener(v -> {
            String codigoIngresado = Objects.requireNonNull(codigo.getText()).toString().trim();
            if (codigoIngresado.length() == 6) {
                authViewModel.verificarCodigo(emailRecibido, codigoIngresado);
            } else {
                mostrarToast(v, "El código debe tener 6 dígitos");
            }
        });

        btnReenviarCodigo.setOnClickListener(v -> {
            authViewModel.reenviarCodigoVerificacion(emailRecibido);
            mostrarToast(v, "Reenviando código a " + emailRecibido);
        });
    }

    private void gestionarVisualizacionBotones(boolean estaCargando) {
        progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
        bntVerificar.setText(estaCargando ? "" : getString(R.string.verificar_email_btn));

        boolean habilitar = !estaCargando;
        bntVerificar.setEnabled(habilitar);
        btnReenviarCodigo.setEnabled(habilitar);
        btnRegistrarNuevamente.setEnabled(habilitar);
        codigo.setEnabled(habilitar);
    }

    private void gestionarAuthViewModel(AuthResult resultado) {
        switch (resultado.getStatus()) {
            case LOADING:
                gestionarVisualizacionBotones(true);
                break;

            case SUCCESS:
                Intent intent = new Intent(this, OnBoardingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
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

    private void mostrarToast(View v, String msj) {
        Snackbar snackbar = Snackbar.make(v, msj, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.primario));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }
}