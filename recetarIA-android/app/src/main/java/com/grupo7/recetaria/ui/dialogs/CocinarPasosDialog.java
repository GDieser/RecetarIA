package com.grupo7.recetaria.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.grupo7.recetaria.databinding.DialogCocinarPasosBinding;
import com.grupo7.recetaria.models.IngredienteRecetaIA;
import com.grupo7.recetaria.models.RecetaIA;

import java.util.ArrayList;
import java.util.List;

public class CocinarPasosDialog extends DialogFragment {

    private DialogCocinarPasosBinding binding;
    private RecetaIA receta;
    private List<String> listaDePasos = new ArrayList<>();
    private int indiceActual = 0;

    public static CocinarPasosDialog newInstance(RecetaIA receta) {
        CocinarPasosDialog fragment = new CocinarPasosDialog();
        Bundle args = new Bundle();
        args.putParcelable("receta_obj", receta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCocinarPasosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            receta = getArguments().getParcelable("receta_obj");
            prepararListaDePasos();
            actualizarPaso();
        }

        binding.btnSiguiente.setOnClickListener(v -> {
            if (indiceActual < listaDePasos.size() - 1) {
                indiceActual++;
                actualizarPaso();
            } else {
                dismiss(); // Fin de la receta
            }
        });

        binding.btnAnterior.setOnClickListener(v -> {
            if (indiceActual > 0) {
                indiceActual--;
                actualizarPaso();
            }
        });

        binding.btnCerrarPasos.setOnClickListener(v -> dismiss());
    }

    private void prepararListaDePasos() {
        listaDePasos.clear();

        StringBuilder sb = new StringBuilder();
        if (receta.getIngredientes() != null) {
            for (IngredienteRecetaIA ing : receta.getIngredientes()) {
                sb.append("• ").append(ing.getNombre()).append("\n");
            }
        }
        listaDePasos.add(sb.toString());

        if (receta.getInstrucciones() != null) {
            listaDePasos.addAll(receta.getInstrucciones());
        }
    }

    private void actualizarPaso() {
        int total = listaDePasos.size();

        binding.tvContadorPasos.setText("PASO " + (indiceActual + 1) + " DE " + total);
        binding.tvContenidoPaso.setText(listaDePasos.get(indiceActual));

        int progreso = (int) (((float) (indiceActual + 1) / total) * 100);
        binding.progressCooking.setProgress(progreso, true);

        if (indiceActual == 0) {
            binding.tvTituloPaso.setText("Preparación de ingredientes");
            binding.btnAnterior.setVisibility(View.INVISIBLE);
        } else {
            binding.tvTituloPaso.setText("PASO #" + indiceActual);
            binding.btnAnterior.setVisibility(View.VISIBLE);
        }

        if (indiceActual == total - 1) {
            binding.btnSiguiente.setText("¡Terminar!");
        } else {
            binding.btnSiguiente.setText("Siguiente");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}