package com.grupo7.recetaria.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;

public class CocinarFragment extends Fragment {

    private CocinarViewModel viewModel;

    public static CocinarFragment newInstance() {
        return new CocinarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cocinar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CocinarViewModel.class);

        viewModel.getEstadoNavegacion().observe(getViewLifecycleOwner(), estado -> {
            switch (estado) {
                case FORMULARIO:
                    replaceChild(new FormularioIAFragment());
                    break;
                case ESPERA:
                    replaceChild(new EsperaFragment());
                    break;
                case SUGERENCIAS:
                    replaceChild(new SugerenciasFragment());
                    break;
            }
        });
    }

    private void replaceChild(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.contenedor_hijos, fragment)
                .commit();
    }
}