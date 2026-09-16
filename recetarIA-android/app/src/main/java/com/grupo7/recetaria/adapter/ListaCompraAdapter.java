package com.grupo7.recetaria.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.grupo7.recetaria.R;
import com.grupo7.recetaria.models.ItemListaCompra;

import java.util.ArrayList;
import java.util.List;

public class ListaCompraAdapter extends RecyclerView.Adapter<ListaCompraAdapter.ListaCompraViewHolder> {

    private List<ItemListaCompra> listaCompras = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSumarCantidad(ItemListaCompra item);
        void onRestarCantidad(ItemListaCompra item);
        void onEliminar(ItemListaCompra item);
        void onEditarCantidad(ItemListaCompra item);
        void onToggleCarrito(ItemListaCompra item, boolean enCarrito);
        void onEditarCompleto(ItemListaCompra item);

    }

    public ListaCompraAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setListaCompras(List<ItemListaCompra> nuevaLista) {
        this.listaCompras = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListaCompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_compra, parent, false);
        return new ListaCompraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaCompraViewHolder holder, int position) {
        ItemListaCompra itemActual = listaCompras.get(position);

        holder.tvNombre.setText(itemActual.getNombre_producto());

        String rubro = itemActual.getNombre_rubro() != null ? itemActual.getNombre_rubro() : "General";
        String unidad = itemActual.getNombre_unidad() != null ? itemActual.getNombre_unidad() : "Unidades";
        holder.tvCategoriaUnidad.setText(rubro + " • " + unidad);

        holder.tvCantidad.setText(com.grupo7.recetaria.ui.otro.Conversor.formatearCantidad(itemActual.getCantidad()));

        holder.cbAdquirido.setOnCheckedChangeListener(null);
        holder.cbAdquirido.setChecked(itemActual.isEs_adquirido());

        if (itemActual.isEs_adquirido()) {
            holder.tvNombre.setPaintFlags(holder.tvNombre.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvNombre.setTextColor(0xFF9E9E9E);
        } else {
            holder.tvNombre.setPaintFlags(holder.tvNombre.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvNombre.setTextColor(0xFFFB8C00);
        }

        holder.cbAdquirido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemActual.setEs_adquirido(isChecked);

            if (isChecked) {
                holder.tvNombre.setPaintFlags(holder.tvNombre.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvNombre.setTextColor(0xFF9E9E9E);
            } else {
                holder.tvNombre.setPaintFlags(holder.tvNombre.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvNombre.setTextColor(0xFFFB8C00);
            }

            listener.onToggleCarrito(itemActual, isChecked);
        });

        holder.btnSumar.setOnClickListener(v -> listener.onSumarCantidad(itemActual));
        holder.btnRestar.setOnClickListener(v -> listener.onRestarCantidad(itemActual));
        holder.ivEliminar.setOnClickListener(v -> listener.onEliminar(itemActual));
        holder.tvCantidad.setOnClickListener(v -> listener.onEditarCantidad(itemActual));
        holder.containerNombre.setOnClickListener(v -> listener.onEditarCompleto(itemActual));
    }

    @Override
    public int getItemCount() {
        return listaCompras.size();
    }

    public static class ListaCompraViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvCategoriaUnidad, tvCantidad;
        ImageView ivEliminar;
        ImageButton btnRestar, btnSumar;
        MaterialCheckBox cbAdquirido;
        View containerNombre;

        public ListaCompraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCategoriaUnidad = itemView.findViewById(R.id.tvCategoriaUnidad);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            ivEliminar = itemView.findViewById(R.id.ivEliminar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            cbAdquirido = itemView.findViewById(R.id.cbAdquirido);
            containerNombre = itemView.findViewById(R.id.containerNombre);
        }
    }
}
