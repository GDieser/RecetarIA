package com.grupo7.recetaria.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.data.local.RecetariaDbHelper;
import com.grupo7.recetaria.models.AuthResult;
import com.grupo7.recetaria.models.PreferenciaAlimentaria;
import com.grupo7.recetaria.models.RestriccionAlimentaria;
import com.grupo7.recetaria.ui.viewmodels.AuthViewModel;
import com.grupo7.recetaria.ui.viewmodels.OnboardingViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    // 1. Declarar variables a nivel de clase
    private Button btnAceptarPerfil;
    private Button btnPreferencias;
    private Button btnRestricciones;

    private EditText editTextNombre;
    private EditText editTextApellido;
    private EditText editTextEdad;
    private EditText editTextNinios;
    private EditText editTextAdultos;

    private RecetariaDbHelper dbHelper;
    private String[] opcionesPreferencias;
    private boolean[] seleccionadosPreferencias;
    private int[] idsPreferencias;

    private ArrayList<PreferenciaAlimentaria> listaPref;
    private ArrayList<RestriccionAlimentaria> listaRest;

    private String[] opcionesRestricciones;
    private boolean[] seleccionadosRestricciones;
    private int[] idsRestricciones;
    private AutoCompleteTextView autoComplete;

    private AuthViewModel authViewModel;
    private LocalDate fechaSeleccionada;
    private OnboardingViewModel onboardingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        String[] paises = getResources().getStringArray(R.array.paises_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                paises
        );
        autoComplete = findViewById(R.id.autoCompleteNacionalidad);
        autoComplete.setAdapter(adapter);

        onboardingViewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        onboardingViewModel.obtenerPreferencias();
        onboardingViewModel.obtenerRestricciones();

        btnAceptarPerfil = findViewById(R.id.btnAceptarPerfil);
        btnPreferencias = findViewById(R.id.btnPreferenciasAlimentarias);
        btnRestricciones = findViewById(R.id.btnRestriccionesAlimentarias);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextEdad = findViewById(R.id.editTextEdad);
        editTextNinios = findViewById(R.id.editTextNiños);
        editTextAdultos = findViewById(R.id.editTextAdultos);
        dbHelper = new RecetariaDbHelper(this);

        Listeners();
        configurarObservador();

        onboardingViewModel.getPreferenciasAlimentarias().observe(this, result ->{
             this.listaPref = (ArrayList<PreferenciaAlimentaria>) result;
             llenarListaPref(this.listaPref);
        });

        onboardingViewModel.getRestriccionesAlimentarias().observe(this, result -> {
            this.listaRest = (ArrayList<RestriccionAlimentaria>) result;
            llenarListaRestr(this.listaRest);
        });
    }

    private void llenarListaPref(ArrayList<PreferenciaAlimentaria> listaPref) {
        int tamano = listaPref.size();
        opcionesPreferencias = new String[tamano];
        seleccionadosPreferencias = new boolean[tamano];
        idsPreferencias = new int[tamano];

        for (int i = 0; i < tamano; i++) {
            opcionesPreferencias[i] = listaPref.get(i).getNombre();
            idsPreferencias[i] = listaPref.get(i).getId_preferencia();
            seleccionadosPreferencias[i] = false;
        }
    }


    private void llenarListaRestr(ArrayList<RestriccionAlimentaria> listaRestr) {
        int tamano = listaRestr.size();
        opcionesRestricciones = new String[tamano];
        seleccionadosRestricciones = new boolean[tamano];
        idsRestricciones = new int[tamano];

        for (int i = 0; i < tamano; i++) {
            opcionesRestricciones[i] = listaRestr.get(i).getNombre();
            idsRestricciones[i] = listaRestr.get(i).getId_restriccion();
            seleccionadosRestricciones[i] = false;
        }
    }

    private void configurarObservador() {
        authViewModel.getAuthResult().observe(this, resultado -> {
            if (resultado == null) return;

            switch (resultado.getStatus()) {
                case LOADING:
                    setButtonsEnabled(false);
                    break;
                case SUCCESS:
                    setButtonsEnabled(true);
                    Toast.makeText(this, "¡Perfil guardado!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    break;
                case ERROR:
                    setButtonsEnabled(true);
                    String msgError = resultado.getErrorMessage() != null ? resultado.getErrorMessage() : "Error al guardar";
                    Toast.makeText(this, msgError, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnAceptarPerfil.setEnabled(enabled);
        btnPreferencias.setEnabled(enabled);
        btnRestricciones.setEnabled(enabled);
        float alpha = enabled ? 1.0f : 0.5f;
        btnAceptarPerfil.setAlpha(alpha);
        btnPreferencias.setAlpha(alpha);
        btnRestricciones.setAlpha(alpha);
    }

    private List<Integer> obtenerIdsSeleccionados(boolean[] seleccionados, int[] ids) {
        List<Integer> seleccionadosList = new ArrayList<>();
        if (seleccionados == null || ids == null) return seleccionadosList;

        for (int i = 0; i < seleccionados.length; i++) {
            if (seleccionados[i]) {
                seleccionadosList.add(ids[i]);
            }
        }
        return seleccionadosList;
    }
    private void Listeners() {

        editTextEdad.setOnClickListener(v -> mostrarCalendario());

        btnPreferencias.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Seleccionar Preferencias")
                    .setMultiChoiceItems(opcionesPreferencias, seleccionadosPreferencias, (dialog, which, isChecked) -> {
                        seleccionadosPreferencias[which] = isChecked;
                    })
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        Toast.makeText(this, "Preferencias actualizadas", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


        btnRestricciones.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Seleccionar Restricciones")
                    .setMultiChoiceItems(opcionesRestricciones, seleccionadosRestricciones, (dialog, which, isChecked) -> {
                        seleccionadosRestricciones[which] = isChecked;
                    })
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        Toast.makeText(this, "Restricciones actualizadas", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnAceptarPerfil.setOnClickListener(v -> {
            if (validarCampos()) {
                List<Integer> prefElegidas = obtenerIdsSeleccionados(seleccionadosPreferencias, idsPreferencias);
                List<Integer> restElegidas = obtenerIdsSeleccionados(seleccionadosRestricciones, idsRestricciones);

                authViewModel.guardarPerfilCompleto(
                        editTextNombre.getText().toString().trim(),
                        editTextApellido.getText().toString().trim(),
                        fechaSeleccionada,
                        Integer.parseInt(editTextAdultos.getText().toString()),
                        Integer.parseInt(editTextNinios.getText().toString()),
                        autoComplete.getText().toString().trim(),
                        prefElegidas,
                        restElegidas  
                );
            } else {
                Toast.makeText(this, "Por favor, completa los campos marcados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarCalendario() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
            editTextEdad.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            editTextEdad.setError(null);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validarCampos() {
        String nombre = editTextNombre.getText().toString().trim();
        String apellido = editTextApellido.getText().toString().trim();
        String niniosStr = editTextNinios.getText().toString().trim();
        String adultosStr = editTextAdultos.getText().toString().trim();

        if (nombre.isEmpty()) {
            editTextNombre.setError("El nombre es obligatorio");
            return false;
        }
        if (apellido.isEmpty()) {
            editTextApellido.setError("El apellido es obligatorio");
            return false;
        }

        if (fechaSeleccionada == null) {
            editTextEdad.setError("Selecciona tu fecha de nacimiento");
            return false;
        }

        if (niniosStr.isEmpty()) {
            editTextNinios.setError("Campo obligatorio (puedes poner 0)");
            return false;
        }
        try {
            int ninios = Integer.parseInt(niniosStr);
            if (ninios < 0 || ninios > 50) {
                editTextNinios.setError("Cantidad no válida");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextNinios.setError("Número demasiado grande");
            return false;
        }


        if (adultosStr.isEmpty()) {
            editTextAdultos.setError("Campo obligatorio");
            return false;
        }
        try {
            int adultos = Integer.parseInt(adultosStr);
            if (adultos < 1 || adultos > 50) {
                editTextAdultos.setError("Debe haber al menos 1 adulto");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextAdultos.setError("Número demasiado grande");
            return false;
        }

        return true;
    }
}