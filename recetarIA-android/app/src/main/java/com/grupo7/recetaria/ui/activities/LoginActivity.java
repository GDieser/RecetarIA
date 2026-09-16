package com.grupo7.recetaria.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.ui.viewmodels.AuthViewModel;


public class LoginActivity extends AppCompatActivity {

    TextView inputEmail;
    TextView inputPass;
    MaterialButton btnGoogle;
    View btnRegistro;
    Button btnLogin;
    ProgressBar progressBar;
    View btnOlvidoPass;
    private String emailVerificar;
    private AuthViewModel authViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Se genera solo con el .json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                }
        );

        inputEmail = findViewById(R.id.loginMail);
        inputPass = findViewById(R.id.loginPass);
        btnLogin = findViewById(R.id.btnIngresar);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnRegistro = findViewById(R.id.btnRegistro);
        progressBar = findViewById(R.id.loginProgress);
        btnOlvidoPass = findViewById(R.id.btnOlvidoPass);

        // Cargamos el servicio
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        // Observamos la variable del tipo LiveData
        authViewModel.getAuthResult().observe(this, result -> {
            gestionarAuthViewModel(result); //IDE pide reemplazar por LAMDA pero dejo asi para
            // mayor claridad
        });

        btnLogin.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String pass = inputPass.getText().toString().trim();

            if(!email.isBlank() && !pass.isBlank()){
                ocultarTeclado();
                this.emailVerificar = email;
                authViewModel.loginConEmail(email, pass);
            } else {
                mostrarToast(v, "Debes ingresar un email y un password para continuar...");
            }
        });

        btnOlvidoPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecuperoActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnGoogle.setOnClickListener(v ->{
            ocultarTeclado();
            mGoogleSignInClient.signOut();
            mGoogleSignInClient.revokeAccess();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        btnRegistro.setOnClickListener(v ->{
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authViewModel.loginConGoogle(account);
        } catch (ApiException e) {
            Log.e("GoogleAuth", "Error code=" + e.getStatusCode());
        }
    }

    private void gestionarVisualizacionBotones(boolean estaCargando){
        progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
        btnLogin.setText(estaCargando ? "" : getString(R.string.btn_ingresar));
        boolean habilitar = !estaCargando;

        btnLogin.setEnabled(habilitar);
        btnOlvidoPass.setEnabled(habilitar);
        btnGoogle.setEnabled(habilitar);
        btnRegistro.setEnabled(habilitar);
    }

    private void gestionarAuthViewModel(AuthResult resultado){
        switch (resultado.getStatus()) {
            case LOADING:
                gestionarVisualizacionBotones(true);
                break;
            case SUCCESS:
                if(!resultado.isVerificado())
                {
                    Intent intent = new Intent(this, VerificarCorreoActivity.class);
                    intent.putExtra("email_registro", emailVerificar);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
                else
                {Intent intent;

                    if (!SessionManager.getInstance(getApplication()).usuarioLogueado().isPerfilCompleto()) {

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