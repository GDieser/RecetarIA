package com.grupo7.recetaria.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.adapter.CalendarioAdapter;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.CalendarioDAO;
import com.grupo7.recetaria.data.local.RecetaDAO;
import com.grupo7.recetaria.models.Calendario;
import com.grupo7.recetaria.models.IngredienteRecetaIA;
import com.grupo7.recetaria.models.Receta;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.ui.dialogs.SeleccionFaltantesDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalendarioFragment extends Fragment {

    private CalendarioAdapter calendarioAdapter;
    private CalendarioDAO calendarioDAO;
    private RecyclerView rvCalendario;
    private CalendarView calendarioView;
    private List<Calendario> listaCalendario;
    private SessionManager session;
    private String hoy = null;

    private String fechaSeleccionada;


    public CalendarioFragment() {
        // Required empty public constructor
    }

    public static CalendarioFragment newInstance() {
        CalendarioFragment fragment = new CalendarioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (session != null && session.usuarioLogueado() != null) {
            cargarRecetasFechaHoy(session.usuarioLogueado().getId_usuario(), fechaSeleccionada);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = SessionManager.getInstance(getContext());
        calendarioDAO = new CalendarioDAO(getContext());
        calendarioAdapter = new CalendarioAdapter();
        calendarioView = view.findViewById(R.id.calendarView);
        calendarioView.setMinDate(System.currentTimeMillis() - 1000); // se deshabilitan los dias anteriores a fechaactual
        rvCalendario = view.findViewById(R.id.rvRecetasCalendario);
        rvCalendario.setAdapter(calendarioAdapter);
        rvCalendario.setLayoutManager(new LinearLayoutManager(getContext()));
        RecetaDAO recetaDAO = new RecetaDAO(getContext());
        int idusuario = session.usuarioLogueado().getId_usuario();

        // si se agenda una receta desde el tab de cocinar se actualiza el dia para mostrar
        // recetas al ir al tab de calendario
        getParentFragmentManager().setFragmentResultListener("planificacion_actualizada", getViewLifecycleOwner(), (requestKey, result) -> {
            cargarRecetasFechaHoy(idusuario, fechaSeleccionada);
        });

        calendarioView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            fechaSeleccionada = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);

            listaCalendario = calendarioDAO.obtenerRecetasPorUsuarioYfecha(idusuario,fechaSeleccionada);
            calendarioAdapter.setListaCalendario(listaCalendario);
                            });

        //cargarRecetasFechaHoy(idusuario,hoy);// carga al iniciar la app ( sin tocar un dia )


        ExtendedFloatingActionButton btnPlanificarReceta = view.findViewById(R.id.btnPlanificarReceta);
        //Verificamos la orientacion del celu y mostramos el texto o icono del boton
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnPlanificarReceta.shrink();
        } else {
            btnPlanificarReceta.extend();
        }
        btnPlanificarReceta.setOnClickListener(v -> {
            mostrarDialogoPlanificar(idusuario, recetaDAO);
        });

        calendarioAdapter.setOnItemClickListener(v -> {
            // Al tocar una receta en el RecyclerView
            Calendario recetaSeleccionada = (Calendario) v.getTag();
            if (recetaSeleccionada == null) return;

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_veropciones_recetacalendario, null);

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .create();

            MaterialButton btnCocinar = dialogView.findViewById(R.id.btnCocinar);
            MaterialButton btnEliminar = dialogView.findViewById(R.id.btnEliminar);
            MaterialButton btnVerDetalle = dialogView.findViewById(R.id.btnVerDetalle);
            MaterialButton btnCambiarFecha = dialogView.findViewById(R.id.btnCambiarFecha);


            TextView tvTitulo = dialogView.findViewById(R.id.textViewNombreReceta);
            if (tvTitulo != null) {
                tvTitulo.setText(recetaSeleccionada.getTituloReceta());
            }



            btnVerDetalle.setOnClickListener(v1 -> {
                abrirDetalle(recetaSeleccionada.getId_receta());
                dialog.dismiss();
            });

            btnCocinar.setOnClickListener(v1 -> {
                abrirDetalle(recetaSeleccionada.getId_receta());
                dialog.dismiss();
            });


            btnEliminar.setOnClickListener(v1 -> {
                confirmarEliminar(recetaSeleccionada);
                dialog.dismiss();
            });
            
            btnCambiarFecha.setOnClickListener(v1 -> {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view2, year1, monthOfYear, dayOfMonth) -> {
                    String nuevaFecha = String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    calendarioDAO.cambiarFechaReceta(recetaSeleccionada.getId_calendario(), nuevaFecha);
                    cargarRecetasFechaHoy(idusuario, fechaSeleccionada);
                    Toast.makeText(getContext(), "Fecha cambiada con éxito", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }, year, month, day);

                datePickerDialog.getDatePicker().setMinDate(calendarioView.getMinDate());
                datePickerDialog.show();
            });

            dialog.show();
        });
    }

    private void abrirDetalle(int idReceta) {
        RecetaDAO recetaDAO = new RecetaDAO(getContext());
        RecetaIA recetaIA = recetaDAO.obtenerRecetaIAPorId(idReceta);
        if (recetaIA != null) {
            DetalleRecetaFragment.newInstance(recetaIA, true,true,true)
                    .show(getChildFragmentManager(), "DetalleReceta");
        }
    }

    private void confirmarEliminar(Calendario c) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirmar_eliminar, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        MaterialButton btnEliminar = dialogView.findViewById(R.id.btnEliminar);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        btnEliminar.setOnClickListener(v -> {
            calendarioDAO.eliminarRecetaPlanificada(c.getId_calendario());
            cargarRecetasFechaHoy(session.usuarioLogueado().getId_usuario(), fechaSeleccionada);
            dialog.dismiss();
            Toast.makeText(getContext(), "Eliminado de la planificación", Toast.LENGTH_SHORT).show();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoPlanificar(int idusuario, RecetaDAO recetaDAO) {
        List<Receta> listaRecetas = recetaDAO.obtenerFavoritasPorUsuario(idusuario);

        if (listaRecetas.isEmpty()) {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sin_favoritas, null);
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .create();


            MaterialButton btnIrCocinar = dialogView.findViewById(R.id.btnIrCocinar);



            btnIrCocinar.setOnClickListener(v -> {
                if (getActivity() != null) {
                    ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(2);
                    }
                }
                dialog.dismiss();
            });

            dialog.show();
            return;
        }


        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_planificar_receta, null);
        Spinner spinnerRecetas = dialogView.findViewById(R.id.spinnerRecetas);
        MaterialButton btnSeleccionar = dialogView.findViewById(R.id.btnSeleccionar);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        MaterialButton btnVerDetalle = dialogView.findViewById(R.id.btnVerDetalle);

        List<String> titulosRecetas = new ArrayList<>();
        for (Receta receta : listaRecetas) {
            titulosRecetas.add(receta.getTitulo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, titulosRecetas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecetas.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnSeleccionar.setOnClickListener(v -> {
            int selectedPos = spinnerRecetas.getSelectedItemPosition();
            Receta recetaSeleccionada = listaRecetas.get(selectedPos);

            Calendario recetaACalendario = new Calendario();
            recetaACalendario.setId_receta(recetaSeleccionada.getId_receta());
            recetaACalendario.setId_usuario(idusuario);
            recetaACalendario.setTituloReceta(recetaSeleccionada.getTitulo());
            recetaACalendario.setFecha(fechaSeleccionada);
            recetaACalendario.setId_tipo_comida(recetaSeleccionada.getId_tipo_comida());
            recetaACalendario.setNombre_tipo_comida(recetaSeleccionada.getTipoComida());
            recetaACalendario.setDificultad(recetaSeleccionada.getDificultad());
            recetaACalendario.setComensales(recetaSeleccionada.getComensales());
            recetaACalendario.setTiempo(recetaSeleccionada.getTiempo());

            if(calendarioDAO.verificarStockReceta(idusuario,recetaSeleccionada.getId_receta()))
            {
                //Toast.makeText(getContext(), "Tenes el STOCK GENIAL !", Toast.LENGTH_SHORT).show();
                calendarioDAO.guardarRecetaPlanificada(recetaACalendario);
                cargarRecetasFechaHoy(idusuario, fechaSeleccionada);
                
                String[] partes = fechaSeleccionada.split("-");
                String fechaFormateada = partes[2] + "-" + partes[1] + "-" + partes[0];
                
                mostrarAlerta("Receta planificada con éxito", recetaSeleccionada.getTitulo() + " para el día: " + fechaFormateada, null);
                Toast.makeText(getContext(), "Receta planificada con éxito", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(getContext(), "NO HAY STOCK", Toast.LENGTH_SHORT).show();
                mostrarAlerta("No tenes el stock necesario","Recomendamos comprar los ingredientes",recetaSeleccionada.getId_receta());

            }

            dialog.dismiss();

        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnVerDetalle.setOnClickListener(v -> {
            int selectedPos = spinnerRecetas.getSelectedItemPosition();
            Receta recetaSeleccionada = listaRecetas.get(selectedPos);
            RecetaIA recetaIA = recetaDAO.obtenerRecetaIAPorId(recetaSeleccionada.getId_receta());

            if (recetaIA != null) {
                DetalleRecetaFragment fragment = DetalleRecetaFragment.newInstance(recetaIA,true,true,true);
                fragment.show(getChildFragmentManager(), "DetalleReceta");
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Error al cargar los detalles de la receta", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void cargarRecetasFechaHoy(int idusuario,String fecha) {
        Calendar c = Calendar.getInstance();
       if(fecha !=null){
           listaCalendario = calendarioDAO.obtenerRecetasPorUsuarioYfecha(idusuario, fecha);
           calendarioAdapter.setListaCalendario(listaCalendario);
       }else{
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        fecha = String.format("%d-%02d-%02d", year, month + 1, day);

        fechaSeleccionada=fecha;
           listaCalendario = calendarioDAO.obtenerRecetasPorUsuarioYfecha(idusuario, fecha);
           calendarioAdapter.setListaCalendario(listaCalendario);}


    }

    public void mostrarAlerta(String titulo, String mensaje, Integer recetaID) {
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

        btnAceptar.setOnClickListener(v -> dialog.dismiss());
        
        if (recetaID == null) {
            btnExtra.setVisibility(View.GONE);
        } else {
            btnExtra.setVisibility(View.VISIBLE);
            btnExtra.setOnClickListener(v -> {
                RecetaIA receta = new RecetaDAO(getContext()).obtenerRecetaIAPorId(recetaID);

                if (receta != null && receta.getIngredientes() != null) {
                    ArrayList<IngredienteRecetaIA> ingredientes = new ArrayList<>(receta.getIngredientes());
                    SeleccionFaltantesDialog ventana = SeleccionFaltantesDialog.newInstance(ingredientes);
                    ventana.show(getChildFragmentManager(), "SeleccionFaltantes");
                }

            });
        }
        dialog.show();
    }
}
