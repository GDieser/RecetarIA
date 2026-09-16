package com.grupo7.recetaria.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.adapter.RecetasAdapter;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.RecetaDAO;
import com.grupo7.recetaria.models.Receta;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.models.TipoComida;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;

import java.util.List;


public class RecetasFragment extends Fragment implements RecetasAdapter.OnItemClickListener {

    private RecyclerView rvMisRecetas;
    private ChipGroup chipGroup;
    private RecetasAdapter adapter;
    private RecetaDAO recetaDAO;
    private UsuarioSesion usuarioSesion;


    public RecetasFragment() {
        // Required empty public constructor
    }

    public static RecetasFragment newInstance() {
        RecetasFragment fragment = new RecetasFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recetaDAO = new RecetaDAO(getContext());
        usuarioSesion = SessionManager.getInstance(getContext()).usuarioLogueado();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recetas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMisRecetas = view.findViewById(R.id.rvMisRecetas);
        chipGroup = view.findViewById(R.id.chip_group);

        rvMisRecetas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecetasAdapter(this);
        rvMisRecetas.setAdapter(adapter);

        configurarChips();
    }


    @Override
    public void onResume() {
        super.onResume();
        configurarChips();
        cargarRecetasFavoritas(-1);//cargo todas las recetas
    }

    private void configurarChips() {//Para filtrar las recetas, obtengo los tipos de comida y
        // creo los chips.
        if (usuarioSesion == null) return;

        chipGroup.removeAllViews();

        Chip chipTodas = new Chip(getContext(), null, com.google.android.material.R.attr.chipStyle);
        chipTodas.setText("Todas");
        chipTodas.setId(View.generateViewId());
        chipTodas.setCheckable(true);
        chipTodas.setChecked(true);
        chipTodas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) cargarRecetasFavoritas(-1);//cargo todas las recetas
        });
        chipGroup.addView(chipTodas);


        List<TipoComida> tipos = recetaDAO.obtenerTiposComidaFavoritosPorUsuario(usuarioSesion.getId_usuario());
        for (TipoComida tipo : tipos) {
            Chip chip = new Chip(getContext(), null, com.google.android.material.R.attr.chipStyle);
            chip.setText(tipo.getNombre());
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    cargarRecetasFavoritas(tipo.getId());
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void cargarRecetasFavoritas(int idTipoComida) {
        if (usuarioSesion != null) {
            List<Receta> favoritas;
            if (idTipoComida == -1) {
                favoritas = recetaDAO.obtenerFavoritasPorUsuario(usuarioSesion.getId_usuario());
            } else {
                favoritas = recetaDAO.obtenerRecetasPorUsuarioYTipo(usuarioSesion.getId_usuario(), idTipoComida);
            }
            adapter.setListaRecetas(favoritas);
        }
    }

    @Override
    public void onRecetaClick(Receta receta) {
        RecetaIA recetaIA = recetaDAO.obtenerRecetaIAPorId(receta.getId_receta());
        if (recetaIA != null) {
            DetalleRecetaFragment fragment = DetalleRecetaFragment.newInstance(recetaIA, true, true,true);
            fragment.show(getChildFragmentManager(), "DetalleReceta");
        } else {
            Toast.makeText(getContext(), "Error al cargar el detalle", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onToggleFavorito(Receta receta) {
        if (usuarioSesion == null) return;
        mostrarAlerta("¿Eliminar de favoritos?","¿Estás seguro de que deseas eliminar \"" + receta.getTitulo() + "\" de tus favoritos?",receta);
    }



    public void mostrarAlerta(String titulo, String mensaje,Receta receta) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alerta, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        TextView tvTitulo = dialogView.findViewById(R.id.tvDialogTitulo);
        TextView tvMensaje = dialogView.findViewById(R.id.tvDialogMensaje);
        MaterialButton btnAceptar = dialogView.findViewById(R.id.btnDialogAceptar);
        MaterialButton btnExtra = dialogView.findViewById(R.id.btnExtra);


        tvTitulo.setText(titulo);
        tvMensaje.setText(mensaje);
        btnExtra.setText("Cancelar");
        btnAceptar.setText("Eliminar");


        btnExtra.setOnClickListener(v -> dialog.dismiss());
        btnAceptar.setOnClickListener(v -> {
            boolean eliminado = recetaDAO.eliminarFavorito(usuarioSesion.getId_usuario(), receta.getId_receta());
                    if (eliminado) {
                        Toast.makeText(getContext(), "Receta eliminada de favoritos", Toast.LENGTH_SHORT).show();
                        configurarChips();//generamos los chips si ya no hay mas ej: cena.
                        cargarRecetasFavoritas(-1);//cargo todas las recetas
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar la receta, esta planificada en calendario", Toast.LENGTH_SHORT).show();
                    }
        });

        dialog.show();
    }


}
