package com.grupo7.recetaria.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.adapter.AlacenaAdapter;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.AlacenaDAO;
import com.grupo7.recetaria.models.ItemAlacena;
import com.grupo7.recetaria.models.UsuarioSesion;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.button.MaterialButton;


import java.util.ArrayList;
import java.util.List;

public class AlacenaVirtualFragment extends Fragment {

    private RecyclerView rvAlacena;
    private AlacenaAdapter adapter;
    private AlacenaDAO alacenaDAO;
    private int idUsuarioActual;

    public AlacenaVirtualFragment() {
    }

    public static AlacenaVirtualFragment newInstance() {
        return new AlacenaVirtualFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alacena_virtual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alacenaDAO = new AlacenaDAO(requireContext());
        UsuarioSesion usuarioLogueado = SessionManager.getInstance(requireContext()).usuarioLogueado();

        if (usuarioLogueado != null) {
            idUsuarioActual = usuarioLogueado.getId_usuario();
        } else {
            Toast.makeText(getContext(), "Error: Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        rvAlacena = view.findViewById(R.id.rvAlacena);
        rvAlacena.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AlacenaAdapter(new AlacenaAdapter.OnItemClickListener() {
            @Override
            public void onSumarCantidad(ItemAlacena item) {
                double nuevaCant = item.getCantidad() + 1;
                alacenaDAO.actualizarCantidad(item.getId_producto_alacena(), nuevaCant);
                cargarDatosAlacena();
            }

            @Override
            public void onRestarCantidad(ItemAlacena item) {
                if (item.getCantidad() > 0) {
                    double nuevaCant = item.getCantidad() - 1;
                    if(nuevaCant < 0) nuevaCant = 0;
                    alacenaDAO.actualizarCantidad(item.getId_producto_alacena(), nuevaCant);
                    cargarDatosAlacena();
                }
            }

            @Override
            public void onEliminar(ItemAlacena item) {
                mostrarAlerta("Eliminar producto", "¿Estás seguro de que deseas quitar " + item.getNombre_producto() + " de tu alacena?",item);
//                new MaterialAlertDialogBuilder(requireContext())
//                        .setTitle("Eliminar producto")
//                        .setMessage("¿Estás seguro de que deseas quitar " + item.getNombre_producto() + " de tu alacena?")
//                        .setPositiveButton("Eliminar", (dialog, which) -> {
//                            alacenaDAO.eliminarProductoAlacena(item.getId_producto_alacena());
//                            cargarDatosAlacena();
//                            Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
//                        })
//                        .setNegativeButton("Cancelar", null)
//                        .show();
            }

            @Override
            public void onToggleInfaltable(ItemAlacena item) {
                int nuevoEstado = (item.getEs_infaltable() == 1) ? 0 : 1;
                alacenaDAO.alternarInfaltable(item.getId_producto_alacena(), nuevoEstado);
                cargarDatosAlacena();
            }
            @Override
            public void onEditarCantidad(ItemAlacena item) {
                mostrarDialogoEdicionRapida(item);
            }

            @Override
            public void onEditarCompleto(ItemAlacena item) {
                mostrarDialogoEdicionCompleta(item);
            }

        });

        rvAlacena.setAdapter(adapter);

        cargarDatosAlacena();

        ExtendedFloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarProducto);
        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarProducto());

        AutoCompleteTextView actvFiltroRubro = view.findViewById(R.id.actvFiltroRubro);

        List<String> opcionesFiltro = new ArrayList<>();
        opcionesFiltro.add("Todos");
        opcionesFiltro.addAll(alacenaDAO.obtenerNombresRubros());

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, opcionesFiltro);
        actvFiltroRubro.setAdapter(adapterFiltro);

        actvFiltroRubro.setOnItemClickListener((parent, view1, position, id) -> {
            String rubroSeleccionado = opcionesFiltro.get(position);
            filtrarAlacenaPorRubro(rubroSeleccionado);
        });
    }

    private void cargarDatosAlacena() {
        List<ItemAlacena> listaDB = alacenaDAO.obtenerAlacenaPorUsuario(idUsuarioActual);
        adapter.setListaAlacena(listaDB);
    }

    private void mostrarDialogoAgregarProducto() {

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_producto, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        AutoCompleteTextView actvRubro = dialogView.findViewById(R.id.actvRubro);
        TextInputEditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        AutoCompleteTextView actvUnidad = dialogView.findViewById(R.id.actvUnidad);
        MaterialSwitch switchInfaltable = dialogView.findViewById(R.id.switchInfaltable);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarProducto);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarGuardado);

        ArrayAdapter<String> adapterRubros = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresRubros());
        actvRubro.setAdapter(adapterRubros);

        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresUnidades());
        actvUnidad.setAdapter(adapterUnidades);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {

            String nombreOriginal = etNombre.getText().toString();
            String nombreFormateado = formatearNombreProducto(nombreOriginal);
            String rubro = actvRubro.getText().toString().trim();
            String unidad = actvUnidad.getText().toString().trim();
            String cantidadStr = etCantidad.getText().toString().trim();

            if (nombreFormateado.isEmpty() || rubro.isEmpty() || cantidadStr.isEmpty() || unidad.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nombreFormateado.length() > 30) {
                Toast.makeText(getContext(), "El nombre es muy largo (Máx 30 caracteres)", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Cantidad inválida. Ingresá un número correcto.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cantidad <= 0) {
                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cantidad > 9999) {
                Toast.makeText(getContext(), "Cantidad demasiado grande", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nombreFormateado.isEmpty() || rubro.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            cantidad = Double.parseDouble(cantidadStr);
            if (cantidad <= 0) {
                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean guardado = alacenaDAO.agregarProductoAAlacena(idUsuarioActual, nombreFormateado, rubro, unidad, cantidad, switchInfaltable.isChecked() ? 1 : 0);

            if (guardado) {
                Toast.makeText(getContext(), "Producto agregado", Toast.LENGTH_SHORT).show();
                cargarDatosAlacena();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void filtrarAlacenaPorRubro(String rubroSeleccionado) {
        Toast.makeText(getContext(), "Tocaste: " + rubroSeleccionado, Toast.LENGTH_SHORT).show();

        List<ItemAlacena> listaCompleta = alacenaDAO.obtenerAlacenaPorUsuario(idUsuarioActual);

        if(!listaCompleta.isEmpty()) {
            String rubroBD = listaCompleta.get(0).getNombre_rubro();
            Toast.makeText(getContext(), "En BD el primer item es: " + rubroBD, Toast.LENGTH_SHORT).show();
        }

        if (rubroSeleccionado.equals("Todos")) {
            adapter.setListaAlacena(listaCompleta);
        } else {
            List<ItemAlacena> listaFiltrada = new ArrayList<>();
            for (ItemAlacena item : listaCompleta) {
                String rubroItem = item.getNombre_rubro() != null ? item.getNombre_rubro().toLowerCase() : "";
                String filtro = rubroSeleccionado.toLowerCase();

                if (rubroItem.contains(filtro)) {
                    listaFiltrada.add(item);
                }
            }
            adapter.setListaAlacena(listaFiltrada);
        }
    }

    private void mostrarDialogoEdicionRapida(ItemAlacena item) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_cantidad, null);

        TextInputEditText etCantidad = dialogView.findViewById(R.id.etEditarCantidad);
        AutoCompleteTextView actvUnidad = dialogView.findViewById(R.id.actvEditarUnidad);

        etCantidad.setText(String.valueOf(item.getCantidad()));

        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresUnidades());
        actvUnidad.setAdapter(adapterUnidades);

        if (item.getNombre_unidad() != null) {
            actvUnidad.setText(item.getNombre_unidad(), false);
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Editar " + item.getNombre_producto())
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String valorIngresado = etCantidad.getText().toString().trim();
                    String unidadIngresada = actvUnidad.getText().toString();

                    if (!valorIngresado.isEmpty() && !unidadIngresada.isEmpty()) {
                        try {
                            double nuevaCant = Double.parseDouble(valorIngresado);
                            if (nuevaCant >= 0) {
                                alacenaDAO.actualizarCantidadYUnidad(item.getId_producto_alacena(), nuevaCant, unidadIngresada);
                                cargarDatosAlacena();
                                Toast.makeText(getContext(), "Stock actualizado", Toast.LENGTH_SHORT).show();
                            }else if (nuevaCant <= 0) {
                                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                            } else if (nuevaCant > 9999) {
                                Toast.makeText(getContext(), "Cantidad demasiado grande", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "La cantidad no puede ser negativa", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Número inválido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Completá cantidad y unidad", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alacenaDAO != null) {
            cargarDatosAlacena();
        }
    }

    // normalizar entradas
    private String formatearNombreProducto(String texto) {
        if (texto == null || texto.trim().isEmpty()) return "";

        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }
        return resultado.toString().trim();
    }

    public void mostrarAlerta(String titulo, String mensaje, ItemAlacena item) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alerta, null);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        TextView tvTitulo = dialogView.findViewById(R.id.tvDialogTitulo);
        TextView tvMensaje = dialogView.findViewById(R.id.tvDialogMensaje);
        MaterialButton btnAceptar = dialogView.findViewById(R.id.btnDialogAceptar);
        MaterialButton btnExtra = dialogView.findViewById(R.id.btnExtra);

        tvTitulo.setText(titulo);
        tvMensaje.setText(mensaje);
        btnAceptar.setText("Eliminar");
        btnExtra.setText("Cancelar");

        btnAceptar.setOnClickListener(v -> {

            alacenaDAO.eliminarProductoAlacena(item.getId_producto_alacena());
            cargarDatosAlacena();
            dialog.dismiss();
            Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
        });
        btnExtra.setOnClickListener(v -> dialog.dismiss());




        dialog.show();
    }

    private void mostrarDialogoEdicionCompleta(ItemAlacena itemAEditar) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_producto, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        AutoCompleteTextView actvRubro = dialogView.findViewById(R.id.actvRubro);
        TextInputEditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        AutoCompleteTextView actvUnidad = dialogView.findViewById(R.id.actvUnidad);
        MaterialSwitch switchInfaltable = dialogView.findViewById(R.id.switchInfaltable);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarProducto);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarGuardado);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloDialog);
        if (tvTitulo != null) tvTitulo.setText("Editar Producto");

        ArrayAdapter<String> adapterRubros = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresRubros());
        actvRubro.setAdapter(adapterRubros);

        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresUnidades());
        actvUnidad.setAdapter(adapterUnidades);

        etNombre.setText(itemAEditar.getNombre_producto());
        etCantidad.setText(String.valueOf(itemAEditar.getCantidad()));
        switchInfaltable.setChecked(itemAEditar.getEs_infaltable() == 1);

        if (itemAEditar.getNombre_rubro() != null) {
            actvRubro.setText(itemAEditar.getNombre_rubro(), false);
        }
        if (itemAEditar.getNombre_unidad() != null) {
            actvUnidad.setText(itemAEditar.getNombre_unidad(), false);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombreOriginal = etNombre.getText().toString();
            String nombreFormateado = formatearNombreProducto(nombreOriginal);
            String rubro = actvRubro.getText().toString().trim();
            String unidad = actvUnidad.getText().toString().trim();
            String cantidadStr = etCantidad.getText().toString().trim();

            if (nombreFormateado.isEmpty() || rubro.isEmpty() || cantidadStr.isEmpty() || unidad.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nombreFormateado.length() > 30) {
                Toast.makeText(getContext(), "El nombre es muy largo (Máx 30 caracteres)", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Cantidad inválida. Ingresá un número correcto.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cantidad <= 0 || cantidad > 9999) {
                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0 y menor a 9999", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean guardado = alacenaDAO.editarProductoCompleto(
                    itemAEditar.getId_producto_alacena(),
                    idUsuarioActual,
                    nombreFormateado,
                    rubro,
                    unidad,
                    cantidad,
                    switchInfaltable.isChecked() ? 1 : 0
            );

            if (guardado) {
                Toast.makeText(getContext(), "Producto actualizado", Toast.LENGTH_SHORT).show();
                cargarDatosAlacena();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Error al actualizar la base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

}