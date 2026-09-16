package com.grupo7.recetaria.ui.activities;

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
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.ui.viewmodels.AuthViewModel;

public class RecuperoActivity extends AppCompatActivity {

    Button btnRecupero;
    EditText tilEmail;
    ProgressBar progressBar;
    AuthViewModel authViewModel;
    private String emailParaVerificar;
    private String token;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupero);

        btnRecupero = findViewById(R.id.btnRecuperar);
        tilEmail =  findViewById(R.id.txtMailRecupero);
        progressBar = findViewById(R.id.recuperoProgress);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getAuthResult().observe(this, result -> {
           gestionarAuthViewModel(result);
        });


        btnRecupero.setOnClickListener(v -> {
            String email = tilEmail.getText().toString().trim();
            if(email.isBlank()) {
                mostrarToast(v, "Debe ingresar un email...");
                return;
            }
            emailParaVerificar = email;
            authViewModel.recuperarContrasenia(email);
            ocultarTeclado();
        });
    }
    private void gestionarAuthViewModel(AuthResult resultado) {
        switch (resultado.getStatus()) {
            case LOADING:
                gestionarVisualizacionBotones(true);
                break;

            case SUCCESS:
                gestionarVisualizacionBotones(false);
                Intent intent = new Intent(this, RecuperarContraseniaActivity.class);
                intent.putExtra("email", emailParaVerificar);
                startActivity(intent);
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
        progressBar.setVisibility(estaCargando ? View.VISIBLE : View.INVISIBLE);
        btnRecupero.setEnabled(!estaCargando);
        btnRecupero.setText(estaCargando ? "" : getString(R.string.recuperar_pass));
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