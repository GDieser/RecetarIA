package com.grupo7.recetaria.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;

import com.grupo7.recetaria.adapter.TabAdapter;
import com.grupo7.recetaria.data.local.CalendarioDAO;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //TextView txtCerrarSesion;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // al iniciar la app, borra registros del calendarios con fechas anteriores a hoy.
        limpiarCalendarioAntiguo();

        //txtCerrarSesion = findViewById(R.id.txtCerrarSesion);

        /*
        txtCerrarSesion.setOnClickListener(v -> {
            SessionManager.getInstance(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
         */

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        TabAdapter tabAdapter = new TabAdapter(this);
        viewPager.setAdapter(tabAdapter);

        final String[] titulos = new String[]{"Recetas", "Calendario", "Cocinar", "Alacena", "Compras"};
        final int[] iconos = new int[]{R.drawable.ic_recetas, R.drawable.ic_calendario, R.drawable.ic_boton_central_bigote, R.drawable.ic_alacena, R.drawable.ic_lista};

        int orientation = getResources().getConfiguration().orientation;
        boolean isLandscape = (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setIcon(iconos[position]);

            if (isLandscape) {
                tab.setText(titulos[position]);
            } else {
                tab.setText(null);
            }
        }).attach();

        TabLayout.Tab tabCocinar = tabLayout.getTabAt(2);
        if (tabCocinar != null) {
            tabCocinar.setCustomView(R.layout.layout_tab_cocinar);
        }

        int pantallaAabir = getIntent().getIntExtra("PANTALLA_INICIAL", 0);
        if (pantallaAabir > 0) {
            viewPager.setCurrentItem(pantallaAabir, false);
        }

        com.google.android.material.floatingactionbutton.FloatingActionButton fabPerfil = findViewById(R.id.fabPerfilUsuario);
        fabPerfil.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
            startActivity(intent);
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    View custom = tab.getCustomView();
                    if (custom != null) {
                        ImageView img = custom.findViewById(R.id.icon_custom);
                        img.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.primario));
                        img.setScaleX(1.1f);
                        img.setScaleY(1.1f);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    View custom = tab.getCustomView();
                    if (custom != null) {
                        ImageView img = custom.findViewById(R.id.icon_custom);
                        img.clearColorFilter();
                        img.setScaleX(1.0f);
                        img.setScaleY(1.0f);
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void limpiarCalendarioAntiguo() {
        CalendarioDAO calendarioDAO = new CalendarioDAO(this);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String hoy = String.format("%d-%02d-%02d", year, month + 1, day);
        calendarioDAO.eliminarRegistrosAnteriores(hoy);
    }
}