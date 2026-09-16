package com.grupo7.recetaria.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.PreferenciasDAO;
import com.grupo7.recetaria.data.local.RestriccionesDAO;
import com.grupo7.recetaria.data.local.UsuarioDAO;
import com.grupo7.recetaria.models.PreferenciaAlimentaria;
import com.grupo7.recetaria.models.RestriccionAlimentaria;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.activities.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionPerfilFragment extends Fragment {

    private UsuarioDAO usuarioDAO;
    private PreferenciasDAO preferenciasDAO;
    private RestriccionesDAO restriccionesDAO;
    private UsuarioSesion usuarioActual;
    private TextInputEditText etNombre, etApellido, etAdultos, etNinios;
    private TextView txtEmail;
    private String[] opcionesRestricciones;
    private boolean[] seleccionadosRestricciones;
    private List<RestriccionAlimentaria> listaRestriccionesGlobal;
    private boolean[] tempSeleccionadosRestricciones;

    private String[] opcionesPreferencias;
    private boolean[] seleccionadosPreferencias;
    private List<PreferenciaAlimentaria> listaPreferenciasGlobal;
    private boolean[] tempSeleccionadosPreferencias;

    public ConfiguracionPerfilFragment() {}

    public static ConfiguracionPerfilFragment newInstance() {
        return new ConfiguracionPerfilFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuracion_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usuarioDAO = new UsuarioDAO(requireContext());
        preferenciasDAO = new PreferenciasDAO(requireContext());
        restriccionesDAO = new RestriccionesDAO(requireContext());
        usuarioActual = SessionManager.getInstance(requireContext()).usuarioLogueado();

        if (usuarioActual == null) {
            Toast.makeText(getContext(), "Error al cargar perfil", Toast.LENGTH_SHORT).show();
            return;
        }

        txtEmail = view.findViewById(R.id.txtEmailPerfil);
        etNombre = view.findViewById(R.id.etNombrePerfil);
        etApellido = view.findViewById(R.id.etApellidoPerfil);
        etAdultos = view.findViewById(R.id.etAdultosPerfil);
        etNinios = view.findViewById(R.id.etNiniosPerfil);

        Button btnRestricciones = view.findViewById(R.id.btnRestriccionesAlimentarias);
        Button btnPreferencias = view.findViewById(R.id.btnPreferenciasAlimentarias);
        AppCompatButton btnGuardar = view.findViewById(R.id.btnAceptarPerfil);

        android.widget.ImageButton btnVolver = view.findViewById(R.id.btnVolverPerfil);
        if(btnVolver != null) {
            btnVolver.setOnClickListener(v -> requireActivity().finish());
        }

        txtEmail.setText(usuarioActual.getEmail());
        etNombre.setText(usuarioActual.getNombre());
        etApellido.setText(usuarioActual.getApellido());
        etAdultos.setText(String.valueOf(usuarioActual.getAdultos_familia()));
        etNinios.setText(String.valueOf(usuarioActual.getNinios_familia()));

        cargarOpcionesRestricciones();
        cargarOpcionesPreferencias();

        btnRestricciones.setOnClickListener(v -> {
            tempSeleccionadosRestricciones = seleccionadosRestricciones.clone();

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Mis Restricciones Médicas")
                    .setMultiChoiceItems(opcionesRestricciones, seleccionadosRestricciones, (dialog, which, isChecked) -> {
                        seleccionadosRestricciones[which] = isChecked;
                    })
                    .setPositiveButton("Aceptar", null)
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        seleccionadosRestricciones = tempSeleccionadosRestricciones;
                    })
                    .show();
        });

        btnPreferencias.setOnClickListener(v -> {
            tempSeleccionadosPreferencias = seleccionadosPreferencias.clone();

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Mis Preferencias Alimentarias")
                    .setMultiChoiceItems(opcionesPreferencias, seleccionadosPreferencias, (dialog, which, isChecked) -> {
                        seleccionadosPreferencias[which] = isChecked;
                    })
                    .setPositiveButton("Aceptar", null)
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        seleccionadosPreferencias = tempSeleccionadosPreferencias;
                    })
                    .show();
        });

        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesionPerfil);
        btnCerrarSesion.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro de que querés salir de tu cuenta?")
                    .setPositiveButton("Salir", (dialog, which) -> {
                        SessionManager.getInstance(requireContext()).logout();

                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnGuardar.setOnClickListener(v -> guardarPerfil());
    }

    private void cargarOpcionesRestricciones() {
        listaRestriccionesGlobal = restriccionesDAO.obtenerRestriccionesAlimentarias();
        opcionesRestricciones = new String[listaRestriccionesGlobal.size()];
        seleccionadosRestricciones = new boolean[listaRestriccionesGlobal.size()];

        List<RestriccionAlimentaria> misRestricciones = restriccionesDAO.obtenerRestriccionesPorUsuario(usuarioActual.getId_usuario());
        List<Integer> misRestriccionesIds = new ArrayList<>();
        for (RestriccionAlimentaria r : misRestricciones) {
            misRestriccionesIds.add(r.getId_restriccion());
        }

        for (int i = 0; i < listaRestriccionesGlobal.size(); i++) {
            RestriccionAlimentaria rest = listaRestriccionesGlobal.get(i);
            opcionesRestricciones[i] = rest.getNombre();
            seleccionadosRestricciones[i] = misRestriccionesIds.contains(rest.getId_restriccion());
        }
    }

    private void cargarOpcionesPreferencias() {
        listaPreferenciasGlobal = preferenciasDAO.obtenerPreferenciasAlimentarias();
        opcionesPreferencias = new String[listaPreferenciasGlobal.size()];
        seleccionadosPreferencias = new boolean[listaPreferenciasGlobal.size()];

        List<PreferenciaAlimentaria> misPreferencias = preferenciasDAO.obtenerPreferenciasPorUsuario(usuarioActual.getId_usuario());
        List<Integer> misPreferenciasIds = new ArrayList<>();
        for (PreferenciaAlimentaria p : misPreferencias) {
            misPreferenciasIds.add(p.getId_preferencia());
        }

        for (int i = 0; i < listaPreferenciasGlobal.size(); i++) {
            PreferenciaAlimentaria pref = listaPreferenciasGlobal.get(i);
            opcionesPreferencias[i] = pref.getNombre();
            seleccionadosPreferencias[i] = misPreferenciasIds.contains(pref.getId_preferencia());
        }
    }

    private void guardarPerfil() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String adultosStr = etAdultos.getText().toString().trim();
        String niniosStr = etNinios.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || adultosStr.isEmpty() || niniosStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completá todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setApellido(apellido);
        usuarioActual.setAdultos_familia(Integer.parseInt(adultosStr));
        usuarioActual.setNinios_familia(Integer.parseInt(niniosStr));

        usuarioDAO.actualizarPerfil(usuarioActual);

        List<Integer> idsRestriccionesMarcadas = new ArrayList<>();
        for (int i = 0; i < seleccionadosRestricciones.length; i++) {
            if (seleccionadosRestricciones[i]) {
                idsRestriccionesMarcadas.add(listaRestriccionesGlobal.get(i).getId_restriccion());
            }
        }
        restriccionesDAO.asignarRestricciones(usuarioActual.getId_usuario(), idsRestriccionesMarcadas);

        List<Integer> idsPreferenciasMarcadas = new ArrayList<>();
        for (int i = 0; i < seleccionadosPreferencias.length; i++) {
            if (seleccionadosPreferencias[i]) {
                idsPreferenciasMarcadas.add(listaPreferenciasGlobal.get(i).getId_preferencia());
            }
        }
        preferenciasDAO.asignarPreferencias(usuarioActual.getId_usuario(), idsPreferenciasMarcadas);

        SessionManager.getInstance(requireContext()).crearSesion(usuarioActual);

        Toast.makeText(getContext(), "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show();
    }
}


