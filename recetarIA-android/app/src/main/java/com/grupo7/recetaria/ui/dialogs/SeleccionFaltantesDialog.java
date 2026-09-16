package com.grupo7.recetaria.ui.dialogs;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.grupo7.recetaria.R;
import com.grupo7.recetaria.databinding.DialogSeleccionFaltantesBinding;
import com.grupo7.recetaria.models.IngredienteRecetaIA;
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;

import java.util.ArrayList;
import java.util.List;

public class SeleccionFaltantesDialog extends DialogFragment {

    private DialogSeleccionFaltantesBinding binding;
    private static final String ARG_INGREDIENTES = "ingredientes";
    private List<CheckBox> listaCheckBoxes = new ArrayList<>();
    private CocinarViewModel vm;

    public static SeleccionFaltantesDialog newInstance(ArrayList<IngredienteRecetaIA> ingredientes) {
        SeleccionFaltantesDialog fragment = new SeleccionFaltantesDialog();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_INGREDIENTES, ingredientes);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSeleccionFaltantesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(getParentFragment()).get(CocinarViewModel.class);
        ArrayList<IngredienteRecetaIA> lista = getArguments().getParcelableArrayList(ARG_INGREDIENTES);

        for (IngredienteRecetaIA ing : lista) {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(ing.getNombre());
            cb.setTag(ing);
            cb.setTextColor(getResources().getColor(R.color.gris_plomo));
            if (ing.isEsAdquirido()){
                cb.setChecked(true);
                cb.setEnabled(false);
                cb.setTextColor(getResources().getColor(R.color.gris_claro));
            }

            binding.containerChecks.addView(cb);
            listaCheckBoxes.add(cb);
        }

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnConfirmar.setOnClickListener(v -> {
            ArrayList<IngredienteRecetaIA> ingredientesLista = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                CheckBox cb = (CheckBox) binding.containerChecks.getChildAt(i);
                if (cb.isChecked() && !lista.get(i).isEsAdquirido()) {
                    lista.get(i).setEsAdquirido(true);
                    ingredientesLista.add(lista.get(i));
                }
            }
            if (ingredientesLista.isEmpty()) {
                Toast.makeText(getContext(), "No seleccionaste nada", Toast.LENGTH_SHORT).show();
            } else {
                vm.agregarAlCarrito(ingredientesLista);
                Toast.makeText(getContext(), ingredientesLista.size() + " items agregados", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void agregarCheckBox(String nombre) {
        CheckBox cb = new CheckBox(getContext());
        cb.setText(nombre);
        cb.setTextSize(16);
        cb.setPadding(10, 20, 10, 20);
        cb.setChecked(true);

        binding.containerChecks.addView(cb);
        listaCheckBoxes.add(cb);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Esto hace que el diálogo ocupe el 90% del ancho de la pantalla
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}