package com.grupo7.recetaria.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.databinding.FragmentFormularioIaBinding;
import com.grupo7.recetaria.models.TipoComida;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;

import java.util.List;


public class FormularioIAFragment extends Fragment {

    private CocinarViewModel viewModel;
    private FragmentFormularioIaBinding binding;

    public FormularioIAFragment() {

    }

    public static FormularioIAFragment newInstance() {
        return new FormularioIAFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFormularioIaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.viewModel = new ViewModelProvider(requireParentFragment()).get(CocinarViewModel.class);
        UsuarioSesion usuario = SessionManager.getInstance(requireContext()).usuarioLogueado();
        binding.etAdultosIA.setText(String.valueOf(usuario.getAdultos_familia()));
        binding.etNiniosIA.setText(String.valueOf(usuario.getNinios_familia()));

        viewModel.getTiposComida().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                mostrarChipsComida(lista);
            }
        });

        viewModel.cargarTiposComida();

        binding.btnGenerarReceta.setOnClickListener(v -> {
            UsuarioSesion us = SessionManager.getInstance(getParentFragment().getContext()).usuarioLogueado();

            if(!us.isEs_premium() && us.getUsos_ia() >= 5){
                mostrarDialogoPremium();
                return;
            }

            String momento = getChipText(binding.chipGroupMomento);
            String tiempo = getChipText(binding.chipGroupTiempo);
            String dificultad = getChipText(binding.chipGroupDificultad);

            int adultos = Integer.parseInt(binding.etAdultosIA.getText().toString().isEmpty() ? "0" : binding.etAdultosIA.getText().toString());
            int ninios = Integer.parseInt(binding.etNiniosIA.getText().toString().isEmpty() ? "0" : binding.etNiniosIA.getText().toString());
            String contexto = binding.etContextoIA.getText().toString();

            boolean soloAlacena = binding.switchAlacena.isChecked();
            boolean usarPrefs = binding.switchPreferencias.isChecked();
            boolean usarRestr = binding.switchRestricciones.isChecked();

            int checkedId = binding.chipGroupMomento.getCheckedChipId();

            if (checkedId != View.NO_ID) {
                Chip chipSeleccionado = binding.chipGroupMomento.findViewById(checkedId);
                int idTipo = ((Number) chipSeleccionado.getTag()).intValue();
                viewModel.setTipoComidaSeleccionadoId(idTipo);


                viewModel.solicitarSugerenciasIA(momento, adultos, ninios, tiempo, dificultad, soloAlacena, usarPrefs, usarRestr, contexto);

            } else {
                Toast.makeText(getContext(), "Por favor, elegí un momento del día", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void mostrarDialogoPremium() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("¡Límite alcanzado!")
                .setMessage("Lo siento, ya alcanzaste la cantidad máxima de usos gratuitos. Para seguir disfrutando de todo el potencial de la APP puedes suscribirte a continuación.")
                .setPositiveButton("Suscribirse", (dialog, which) -> {
                    viewModel.hacerPremium();
                    Toast.makeText(getContext(), "Simulando suscripción...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private String getChipText(com.google.android.material.chip.ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id != View.NO_ID) {
            com.google.android.material.chip.Chip chip = group.findViewById(id);
            return chip.getText().toString();
        }
        return "Indistinto";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void mostrarChipsComida(List<TipoComida> tipos) {
        binding.chipGroupMomento.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (TipoComida tipo : tipos) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip_tipo, binding.chipGroupMomento, false);

            chip.setText(tipo.getNombre());
            chip.setTag(tipo.getId());

            if (tipo.getNombre().equalsIgnoreCase("Almuerzo")) {
                chip.setChecked(true);
            }

            binding.chipGroupMomento.addView(chip);
        }
    }

    @Override //para actualziar datos si se modifican en perfil
    public void onResume() {
        super.onResume();
        UsuarioSesion usuarioActual = SessionManager.getInstance(requireContext()).usuarioLogueado();

        if (usuarioActual != null && binding != null) {
            binding.etAdultosIA.setText(String.valueOf(usuarioActual.getAdultos_familia()));
            binding.etNiniosIA.setText(String.valueOf(usuarioActual.getNinios_familia()));
        }
    }

}