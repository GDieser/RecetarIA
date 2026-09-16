package com.grupo7.recetaria.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.activities.MainActivity;

import com.grupo7.recetaria.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OCULTAR BARRAS SUPERIOR E INFERIOR PARA QUE SPLASH SCREEN OCUPE TODA LA PANTALLA
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Oculta barra inferior
                | View.SYSTEM_UI_FLAG_FULLSCREEN    // Oculta barra superior
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // Evita que vuelvan al tocar
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UsuarioSesion usuario = SessionManager.getInstance(getApplication()).usuarioLogueado();

                if (usuario != null) {
                    if (!usuario.isEs_verificado()) {
                        irA(VerificarCorreoActivity.class);
                    } else if (usuario.isPerfilCompleto()) {
                        irA(MainActivity.class);
                    } else {
                        irA(OnBoardingActivity.class);
                    }
                    return;
                }
                irA(LoginActivity.class);
            }
        }, 4000); // Reducido a 2s para mejor UX
    }

    private void irA(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }


}