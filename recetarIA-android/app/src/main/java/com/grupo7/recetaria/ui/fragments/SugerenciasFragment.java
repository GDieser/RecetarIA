package com.grupo7.recetaria.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.grupo7.recetaria.databinding.FragmentSugerenciasBinding;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;

import java.util.ArrayList;

import com.grupo7.recetaria.adapter.SugerenciasAdapter;

public class SugerenciasFragment extends Fragment {

    private FragmentSugerenciasBinding binding;
    private CocinarViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSugerenciasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(CocinarViewModel.class);

        SugerenciasAdapter adapter = new SugerenciasAdapter(new ArrayList<>(), receta -> {
            abrirDetalleReceta(receta);
        });
        binding.rvSugerencias.setAdapter(adapter);
        binding.rvSugerencias.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getRecetasSugeridas().observe(getViewLifecycleOwner(), recetas -> {
            if (recetas != null) {
                adapter.updateData(recetas);
            }
        });

        binding.btnVolverConsultar.setOnClickListener(v -> {
            viewModel.setEstado(CocinarViewModel.CocinarEstado.FORMULARIO);
        });
    }

    private void abrirDetalleReceta(RecetaIA receta) {
        DetalleRecetaFragment detalleFragment = DetalleRecetaFragment.newInstance(receta);
        detalleFragment.show(getParentFragmentManager(), "DetalleRecetaModal");
    }
}