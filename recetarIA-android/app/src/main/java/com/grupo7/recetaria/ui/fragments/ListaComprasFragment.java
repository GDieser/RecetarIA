package com.grupo7.recetaria.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.AlacenaDAO;
import com.grupo7.recetaria.data.local.ListaCompraDAO;
import com.grupo7.recetaria.models.ItemListaCompra;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.models.UsuarioSesion;

import com.grupo7.recetaria.adapter.ListaCompraAdapter;
import com.grupo7.recetaria.ui.viewmodels.CocinarViewModel;
import com.grupo7.recetaria.ui.viewmodels.ListaCompraViewModel;

import java.util.List;


public class ListaComprasFragment extends Fragment {

    private RecyclerView rvListaCompras;
    private ListaCompraAdapter adapter;
    private ListaCompraDAO listaCompraDAO;
    private AlacenaDAO alacenaDAO;
    private int idUsuarioActual;
    private ListaCompraViewModel listaCompraViewModel;

    private List<ItemListaCompra> listaCompletaOriginal;

    public ListaComprasFragment() {
    }

    public static ListaComprasFragment newInstance() {
        return new ListaComprasFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_compras, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listaCompraViewModel != null) {
            listaCompraViewModel.actualizarListaUsuario();
        }
        cargarDatosLista();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listaCompraViewModel = new ViewModelProvider(this).get(ListaCompraViewModel.class);
        listaCompraViewModel.actualizarListaUsuario();

        listaCompraViewModel.getListaCompletaOriginal().observe(getViewLifecycleOwner(), lista -> {
            this.listaCompletaOriginal = lista;
        });

        listaCompraDAO = new ListaCompraDAO(requireContext());
        alacenaDAO = new AlacenaDAO(requireContext());
        UsuarioSesion usuario = SessionManager.getInstance(requireContext()).usuarioLogueado();

        if (usuario != null) {
            idUsuarioActual = usuario.getId_usuario();
        } else {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        rvListaCompras = view.findViewById(R.id.rvListaCompras);
        rvListaCompras.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ListaCompraAdapter(new ListaCompraAdapter.OnItemClickListener() {
            @Override
            public void onSumarCantidad(ItemListaCompra item) {
                double nuevaCant = item.getCantidad() + 1;
                listaCompraDAO.actualizarCantidadYUnidad(item.getId_producto_lista(), nuevaCant, item.getNombre_unidad());
                cargarDatosLista();
            }

            @Override
            public void onRestarCantidad(ItemListaCompra item) {
                if (item.getCantidad() > 0) {
                    double nuevaCant = item.getCantidad() - 1;
                    if (nuevaCant < 0) nuevaCant = 0;
                    listaCompraDAO.actualizarCantidadYUnidad(item.getId_producto_lista(), nuevaCant, item.getNombre_unidad());
                    cargarDatosLista();
                }
            }

            @Override
            public void onEliminar(ItemListaCompra item) {
                mostrarAlerta("Quitar de la lista", "¿Eliminar " + item.getNombre_producto() + " de las compras?",item);

//                new MaterialAlertDialogBuilder(requireContext())
//                        .setTitle("Quitar de la lista")
//                        .setMessage("¿Eliminar " + item.getNombre_producto() + " de las compras?")
//                        .setPositiveButton("Eliminar", (dialog, which) -> {
//                            listaCompraDAO.eliminarItemLista(item.getId_producto_lista());
//                            cargarDatosLista();
//                        })
//                        .setNegativeButton("Cancelar", null)
//                        .show();
            }

            @Override
            public void onEditarCantidad(ItemListaCompra item) {
                mostrarDialogoEdicionRapida(item);
            }

            @Override
            public void onToggleCarrito(ItemListaCompra item, boolean enCarrito) {
                listaCompraDAO.alternarEstadoCompra(item.getId_producto_lista(), enCarrito);
            }

            @Override
            public void onEditarCompleto(ItemListaCompra item) {
                mostrarDialogoEdicionCompleta(item);
            }

        });

        TextInputEditText etBuscador = view.findViewById(R.id.etBuscador);

        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLista(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        rvListaCompras.setAdapter(adapter);
        cargarDatosLista();

        FloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarProductoLista);
        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarProducto());

        androidx.appcompat.widget.AppCompatButton btnTerminar = view.findViewById(R.id.btnVolcarAlacena);
        btnTerminar.setOnClickListener(v -> terminarCompra());
    }

    private void cargarDatosLista() {
        listaCompletaOriginal = listaCompraDAO.obtenerListaPorUsuario(idUsuarioActual);
        adapter.setListaCompras(listaCompletaOriginal);
    }

    private void mostrarDialogoAgregarProducto() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_producto, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        AutoCompleteTextView actvRubro = dialogView.findViewById(R.id.actvRubro);
        TextInputEditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        AutoCompleteTextView actvUnidad = dialogView.findViewById(R.id.actvUnidad);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarProducto);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarGuardado);

        dialogView.findViewById(R.id.switchInfaltable).setVisibility(View.GONE);
        dialogView.findViewById(R.id.tvAyudaInfaltable).setVisibility(View.GONE);

        ArrayAdapter<String> adapterRubros = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresRubros());
        actvRubro.setAdapter(adapterRubros);
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresUnidades());
        actvUnidad.setAdapter(adapterUnidades);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
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

            ItemListaCompra itemExistente = listaCompraDAO.obtenerItemPorNombre(idUsuarioActual, nombreFormateado);
            if (itemExistente != null) {
                double nuevaCant = itemExistente.getCantidad() + cantidad;
                listaCompraDAO.actualizarCantidadYUnidad(itemExistente.getId_producto_lista(), nuevaCant, unidad);
                Toast.makeText(getContext(), "Ya estaba en la lista. Se sumó la cantidad.", Toast.LENGTH_LONG).show();
            } else {
                listaCompraDAO.agregarProductoALista(idUsuarioActual, nombreFormateado, rubro, unidad, cantidad, 0);
            }

            cargarDatosLista();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarDialogoEdicionRapida(ItemListaCompra item) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_cantidad, null);
        TextInputEditText etCantidad = dialogView.findViewById(R.id.etEditarCantidad);
        AutoCompleteTextView actvUnidad = dialogView.findViewById(R.id.actvEditarUnidad);

        etCantidad.setText(String.valueOf(item.getCantidad()));
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresUnidades());
        actvUnidad.setAdapter(adapterUnidades);
        if (item.getNombre_unidad() != null) actvUnidad.setText(item.getNombre_unidad(), false);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Editar " + item.getNombre_producto())
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String valor = etCantidad.getText().toString().trim();
                    String unidad = actvUnidad.getText().toString();

                    if (!valor.isEmpty() && !unidad.isEmpty()) {
                        double nuevaCant = Double.parseDouble(valor);
                        listaCompraDAO.actualizarCantidadYUnidad(item.getId_producto_lista(), nuevaCant, unidad);
                        cargarDatosLista();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void terminarCompra() {
        List<ItemListaCompra> listaActual = listaCompraDAO.obtenerListaPorUsuario(idUsuarioActual);
        int comprados = 0;

        for (ItemListaCompra item : listaActual) {
            if (item.isEs_adquirido()) {
                comprados++;
                alacenaDAO.agregarProductoAAlacena(
                        idUsuarioActual,
                        item.getNombre_producto(),
                        item.getNombre_rubro(),
                        item.getNombre_unidad(),
                        item.getCantidad(),
                        0 // 0 por def
                );
            }
        }

        if (comprados > 0) {
            listaCompraDAO.limpiarComprados(idUsuarioActual);
            cargarDatosLista();

            mostrarAlerta("Compra Exitosa", "Se guardaron " + comprados + " productos en tu alacena virtual.",null);
//            new MaterialAlertDialogBuilder(requireContext())
//                    .setTitle("¡Compra Exitosa!")
//                    .setMessage("Se guardaron " + comprados + " productos en tu alacena virtual.")
//                    .setPositiveButton("Entendido", null)
//                    .show();
        } else {
            Toast.makeText(getContext(), "No marcaste ningún producto en el carrito.", Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarAlerta(String titulo, String mensaje,ItemListaCompra item) {
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

        if (item == null) {

            btnAceptar.setText("Entendido");
            btnExtra.setVisibility(View.GONE);
            btnAceptar.setOnClickListener(v -> dialog.dismiss());
        } else {

            btnAceptar.setText("Eliminar");
            btnExtra.setText("Cancelar");
            btnExtra.setVisibility(View.VISIBLE);

            btnAceptar.setOnClickListener(v -> {
                listaCompraDAO.eliminarItemLista(item.getId_producto_lista());
                cargarDatosLista();
                dialog.dismiss();
            });
            btnExtra.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }
    

    // calajo, no me termina de funcar && JAJAJAJA... NO TE PREOCUPES AMIGO, YA VA A SALIR
    private void filtrarLista(String textoBusqueda) {
        if (listaCompletaOriginal == null) return;

        List<ItemListaCompra> listaFiltrada = new ArrayList<>();

        if (textoBusqueda.isEmpty()) {
            listaFiltrada.addAll(listaCompletaOriginal);
        } else {
            String filtro = textoBusqueda.toLowerCase();

            for (ItemListaCompra item : listaCompletaOriginal) {
                if (item.getNombre_producto() != null &&
                        item.getNombre_producto().toLowerCase().contains(filtro)) {
                    listaFiltrada.add(item);
                }
            }
        }

        adapter.setListaCompras(listaFiltrada);
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

    private void mostrarDialogoEdicionCompleta(ItemListaCompra itemAEditar) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_producto, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        AutoCompleteTextView actvRubro = dialogView.findViewById(R.id.actvRubro);
        TextInputEditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        AutoCompleteTextView actvUnidad = dialogView.findViewById(R.id.actvUnidad);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarProducto);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarGuardado);

        dialogView.findViewById(R.id.switchInfaltable).setVisibility(View.GONE);
        dialogView.findViewById(R.id.tvAyudaInfaltable).setVisibility(View.GONE);

        ArrayAdapter<String> adapterRubros = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresRubros());
        actvRubro.setAdapter(adapterRubros);
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, alacenaDAO.obtenerNombresUnidades());
        actvUnidad.setAdapter(adapterUnidades);

        etNombre.setText(itemAEditar.getNombre_producto());
        etCantidad.setText(String.valueOf(itemAEditar.getCantidad()));
        if (itemAEditar.getNombre_rubro() != null) actvRubro.setText(itemAEditar.getNombre_rubro(), false);
        if (itemAEditar.getNombre_unidad() != null) actvUnidad.setText(itemAEditar.getNombre_unidad(), false);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Editar " + itemAEditar.getNombre_producto())
                .setView(dialogView)
                .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombreFormateado = formatearNombreProducto(etNombre.getText().toString());
            String rubro = actvRubro.getText().toString().trim();
            String unidad = actvUnidad.getText().toString().trim();
            String cantidadStr = etCantidad.getText().toString().trim();

            if (nombreFormateado.isEmpty() || rubro.isEmpty() || cantidadStr.isEmpty() || unidad.isEmpty()) {
                Toast.makeText(getContext(), "Completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad > 0 && cantidad <= 9999) {
                    boolean exito = listaCompraDAO.editarProductoCompleto(
                            itemAEditar.getId_producto_lista(), idUsuarioActual,
                            nombreFormateado, rubro, unidad, cantidad
                    );
                    if (exito) {
                        cargarDatosLista();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Número inválido", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

}