package com.grupo7.recetaria.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo7.recetaria.R;
import com.grupo7.recetaria.models.ItemAlacena;

import java.util.ArrayList;
import java.util.List;

public class AlacenaAdapter extends RecyclerView.Adapter<AlacenaAdapter.AlacenaViewHolder> {

    private List<ItemAlacena> listaAlacena = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSumarCantidad(ItemAlacena item);
        void onRestarCantidad(ItemAlacena item);
        void onEliminar(ItemAlacena item);
        void onToggleInfaltable(ItemAlacena item);
        void onEditarCantidad(ItemAlacena item);
        void onEditarCompleto(ItemAlacena item);
    }

    public AlacenaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setListaAlacena(List<ItemAlacena> nuevaLista) {
        this.listaAlacena = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlacenaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alacena, parent, false);
        return new AlacenaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlacenaViewHolder holder, int position) {
        ItemAlacena itemActual = listaAlacena.get(position);

        holder.tvNombre.setText(itemActual.getNombre_producto());

        String unidad = itemActual.getNombre_unidad() != null ? itemActual.getNombre_unidad() : "Unidades";
        holder.tvCategoriaUnidad.setText(unidad);

        holder.tvCantidad.setText(com.grupo7.recetaria.ui.otro.Conversor.formatearCantidad(itemActual.getCantidad()));

        if (itemActual.getEs_infaltable() == 1) {
            holder.ivInfaltable.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivInfaltable.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.btnSumar.setOnClickListener(v -> listener.onSumarCantidad(itemActual));
        holder.btnRestar.setOnClickListener(v -> listener.onRestarCantidad(itemActual));
        holder.ivEliminar.setOnClickListener(v -> listener.onEliminar(itemActual));
        holder.ivInfaltable.setOnClickListener(v -> listener.onToggleInfaltable(itemActual));
        holder.tvCantidad.setOnClickListener(v -> listener.onEditarCantidad(itemActual));
        holder.tvNombre.setOnClickListener(v -> listener.onEditarCompleto(itemActual));
    }

    @Override
    public int getItemCount() {
        return listaAlacena.size();
    }

    public static class AlacenaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvCategoriaUnidad, tvCantidad;
        ImageView ivInfaltable, ivEliminar;
        ImageButton btnRestar, btnSumar;

        public AlacenaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCategoriaUnidad = itemView.findViewById(R.id.tvCategoriaUnidad);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            ivInfaltable = itemView.findViewById(R.id.ivInfaltable);
            ivEliminar = itemView.findViewById(R.id.ivEliminar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            btnSumar = itemView.findViewById(R.id.btnSumar);
        }
    }
}
