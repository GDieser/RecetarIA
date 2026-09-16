package com.grupo7.recetaria.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.RecetaDAO;
import com.grupo7.recetaria.databinding.FragmentDetalleRecetaBinding;
import com.grupo7.recetaria.models.IngredienteRecetaIA;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.dialogs.CocinarPasosDialog;
import com.grupo7.recetaria.ui.dialogs.SeleccionFaltantesDialog; // Asegurá que la ruta sea correcta
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;

import java.util.ArrayList;
import java.util.Calendar;

public class DetalleRecetaFragment extends BottomSheetDialogFragment {

    private static final String ARG_RECETA = "objeto_receta";
    private static final String ARG_OCULTAR_COMPRAR = "ocultar_comprar";
    private static final String ARG_OCULTAR_GUARDAR = "ocultar_guardar";
    private static final String ARG_OCULTAR_AGENDAR = "ocultar_agendar";
    private FragmentDetalleRecetaBinding binding;
    private RecetaDAO recetaFavorita;

    private UsuarioSesion usuarioSesion;


    public static DetalleRecetaFragment newInstance (RecetaIA receta){
        return newInstance(receta,false,false,false);
    }

    public static DetalleRecetaFragment newInstance(RecetaIA receta,boolean ocultarGuardar,boolean ocultarComprar,boolean ocultarAgendar) {
        final DetalleRecetaFragment fragment = new DetalleRecetaFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_RECETA, receta);
        args.putBoolean(ARG_OCULTAR_GUARDAR, ocultarGuardar);
        args.putBoolean(ARG_OCULTAR_COMPRAR, ocultarComprar);
        args.putBoolean(ARG_OCULTAR_AGENDAR, ocultarAgendar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        android.app.Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);

                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);

                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleRecetaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CocinarViewModel cocinarViewModel = new ViewModelProvider(getParentFragment()).get(CocinarViewModel.class);

        recetaFavorita = new RecetaDAO(getContext());
        usuarioSesion = SessionManager.getInstance(getContext()).usuarioLogueado();

        if (getArguments() != null) {
            RecetaIA receta = getArguments().getParcelable(ARG_RECETA);
            boolean ocultarGuardar = getArguments().getBoolean(ARG_OCULTAR_GUARDAR, false);
            boolean ocultarComprar = getArguments().getBoolean(ARG_OCULTAR_COMPRAR, false);
            boolean ocultarAgendar = getArguments().getBoolean(ARG_OCULTAR_AGENDAR, false);
            if (receta != null) {
                inyectarDatos(receta);

                if (ocultarGuardar){
                    binding.btnGuardar.setVisibility((View.GONE));
                }
                if (ocultarComprar){
                    binding.btnListaCompras.setVisibility((View.GONE));
                }
                if(ocultarAgendar){
                    binding.btnProgramar.setVisibility((View.GONE));
                }

                // Listener para abrir el selector de ingredientes faltantes
                binding.btnListaCompras.setOnClickListener(v -> {
                    if (receta.getIngredientes() != null) {
                        ArrayList<IngredienteRecetaIA> ingredientes = new ArrayList<>(receta.getIngredientes());
                        SeleccionFaltantesDialog dialog = SeleccionFaltantesDialog.newInstance(ingredientes);
                        dialog.show(getChildFragmentManager(), "SeleccionFaltantes");
                    }
                });

                // Listener para abrir el CocinarDialog y ver el paso a paso
                binding.btnCocinar.setOnClickListener(v -> {
                    if (receta != null) {
                        CocinarPasosDialog pasosDialog = CocinarPasosDialog.newInstance(receta);
                        pasosDialog.show(getChildFragmentManager(), "PasosCocina");
                        cocinarViewModel.descontarIngredientesAlacena(receta);
                    } else {
                        Toast.makeText(getContext(), "Error: No se pudo cargar la receta", Toast.LENGTH_SHORT).show();
                    }
                });

                binding.btnGuardar.setOnClickListener(v -> {
                    int idUsuario = usuarioSesion.getId_usuario();
                    int idTipoComida = cocinarViewModel.getTipoComidaSeleccionadoId();
                    long resultado = recetaFavorita.guardarRecetaComoFavorita(receta, idUsuario, idTipoComida);

                    if (resultado > 0) {
                        Toast.makeText(getContext(), "Receta guardada en favoritos", Toast.LENGTH_SHORT).show();
                        binding.btnGuardar.setEnabled(false);
                    } else {
                        Toast.makeText(getContext(), "Error : Esta receta ya se encuentra en tus favoritos", Toast.LENGTH_SHORT).show();
                        binding.btnGuardar.setEnabled(false);
                    }
                });

                binding.btnProgramar.setOnClickListener(v -> {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaProgramada = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        //String fechaProgramada = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        long transaccion = cocinarViewModel.agendarReceta(receta, fechaProgramada, cocinarViewModel.getTipoComidaSeleccionadoId());
                        if(transaccion != -1) {
                            Toast.makeText(getContext(), "Receta agendada para el dia " + fechaProgramada, Toast.LENGTH_SHORT).show();
                        } else {
                            mostrarAlerta("Faltan ingredientes","No tienes los ingredientes necesarios. ¿Deseas guardarla en favoritos?",receta);
                            //Toast.makeText(getContext(), "No tenes el stock en alacena, se recomienda hacer la compra de ingredientes", Toast.LENGTH_LONG).show();
                        }
                    }, year, month, day);

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                    datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Agendar", datePickerDialog);
                    datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", (DialogInterface.OnClickListener) null);

                    datePickerDialog.show();
                });
            }
        }
    }

    private void inyectarDatos(RecetaIA receta) {
        // Título y metadatos
        binding.tvTituloReceta.setText(receta.getTitulo());
        binding.tvTiempo.setText(receta.getTiempo());
        binding.tvDificultad.setText(receta.getDificultad());
        binding.tvComensales.setText(receta.getComensales() + " pers.");

        // Limpieza de contenedores para evitar duplicados
        binding.containerIngredientes.removeAllViews();
        binding.containerInstrucciones.removeAllViews();

        // Carga dinámica de Ingredientes (Solo Nombre)
        if (receta.getIngredientes() != null) {
            for (IngredienteRecetaIA ing : receta.getIngredientes()) {
                TextView tv = new TextView(getContext());
                tv.setText("• " + ing.getNombre()); // Solo mostramos el nombre
                tv.setTextColor(getResources().getColor(R.color.gris_plomo));
                tv.setPadding(0, 8, 0, 8);
                tv.setTextSize(16);
                binding.containerIngredientes.addView(tv);
            }
        }

        // Carga dinámica de Instrucciones
        if (receta.getInstrucciones() != null) {
            int pasoNro = 1;
            for (String paso : receta.getInstrucciones()) {
                TextView tv = new TextView(getContext());
                tv.setText(pasoNro + ". " + paso);
                tv.setPadding(0, 12, 0, 12);
                tv.setTextColor(getResources().getColor(R.color.gris_plomo));
                tv.setTextSize(16);
                binding.containerInstrucciones.addView(tv);
                pasoNro++;
            }
        }
    }

    @Override
    public int getTheme() {
        // Aplica el estilo de bordes redondeados que definimos en themes.xml
        return R.style.CustomBottomSheetDialogTheme;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evita memory leaks al destruir la vista
    }

    public void mostrarAlerta(String titulo, String mensaje,RecetaIA receta) {
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
        btnExtra.setText("Guardar en favoritos");

        btnAceptar.setOnClickListener(v -> dialog.dismiss());
        btnExtra.setOnClickListener(v -> {
            CocinarViewModel cocinarViewModel = new ViewModelProvider(getParentFragment()).get(CocinarViewModel.class);

            int idUsuario = usuarioSesion.getId_usuario();
            int idTipoComida = cocinarViewModel.getTipoComidaSeleccionadoId();
            long resultado = recetaFavorita.guardarRecetaComoFavorita(receta, idUsuario, idTipoComida);

            if (resultado > 0) {
                Toast.makeText(getContext(), "Receta guardada en favoritos", Toast.LENGTH_SHORT).show();
                binding.btnGuardar.setEnabled(false);
            } else {
                Toast.makeText(getContext(), "Error : Esta receta ya se encuentra en tus favoritos", Toast.LENGTH_SHORT).show();
                binding.btnGuardar.setEnabled(false);
            }
        });


        dialog.show();
    }
}