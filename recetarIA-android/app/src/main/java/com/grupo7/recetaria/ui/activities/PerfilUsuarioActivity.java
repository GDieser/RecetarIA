package com.grupo7.recetaria.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.ui.fragments.ConfiguracionPerfilFragment;

public class PerfilUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorPerfil, new ConfiguracionPerfilFragment())
                    .commit();
        }
    }
}