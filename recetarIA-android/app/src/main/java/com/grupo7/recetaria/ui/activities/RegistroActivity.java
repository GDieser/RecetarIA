package com.grupo7.recetaria.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.ui.viewmodels.AuthViewModel;

public class RegistroActivity extends AppCompatActivity {

    private EditText inputMail;
    private EditText inputPass;
    private EditText inputPass2;
    private Button btnRegistro;
    private AuthViewModel authViewModel;
    private ProgressBar progressBar;
    private String emailParaVerificar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inputMail = findViewById(R.id.txtRegistroMail);
        inputPass = findViewById(R.id.txtRegistroPass);
        inputPass2 = findViewById(R.id.txtRegistroPass2);
        btnRegistro = findViewById(R.id.btnRegistro);
        progressBar = findViewById(R.id.registerProgress);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getAuthResult().observe(this, resultado -> {
            gestionarAuthViewModel(resultado);
        });

        btnRegistro.setOnClickListener(v -> {
            String email = inputMail.getText().toString().trim();
            String pass = inputPass.getText().toString().trim();
            String pass2 = inputPass2.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                mostrarToast(v, "Por favor, completa todos los campos");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mostrarToast(v, "El formato del correo no es valido");
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

            this.emailParaVerificar = email;
            authViewModel.registrar(email, pass);
        });
    }

    private void gestionarAuthViewModel(AuthResult resultado) {
        switch (resultado.getStatus()) {
            case LOADING:
                gestionarVisualizacionBotones(true);
                break;

            case SUCCESS:
                if(!resultado.isVerificado())
                {
                    Intent intent = new Intent(this, VerificarCorreoActivity.class);
                    intent.putExtra("email_registro", emailParaVerificar);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
                else
                {Intent intent;

                    if (!SessionManager.getInstance(this).usuarioLogueado().isPerfilCompleto()) {

                        intent = new Intent(this, OnBoardingActivity.class);
                    } else {

                        intent = new Intent(this, MainActivity.class);
                    }
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
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
        btnRegistro.setEnabled(!estaCargando);
        btnRegistro.setText(estaCargando ? "" : getString(R.string.btn_registro_registro));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
    private boolean esPasswordValida(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";
        return password.matches(pattern);
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void mostrarToast(View v, String msj){
        Snackbar snackbar = Snackbar.make(v, msj, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.primario));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }
}