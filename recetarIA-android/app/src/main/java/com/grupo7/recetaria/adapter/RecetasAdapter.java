package com.grupo7.recetaria.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo7.recetaria.R;
import com.grupo7.recetaria.models.Receta;

import java.util.ArrayList;
import java.util.List;

public class RecetasAdapter extends RecyclerView.Adapter<RecetasAdapter.RecetaViewHolder> {

    private List<Receta> listaRecetas = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRecetaClick(Receta receta);
        void onToggleFavorito(Receta receta);
    }

    public RecetasAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setListaRecetas(List<Receta> nuevaLista) {
        this.listaRecetas = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta recetaActual = listaRecetas.get(position);

        holder.tvNombre.setText(recetaActual.getTitulo());
        holder.tvTiempo.setText(recetaActual.getTiempo());
        holder.tvDificultad.setText(recetaActual.getDificultad());
        holder.tvComensales.setText(recetaActual.getComensales()+" pers.");
        holder.tvTipoComida.setText(recetaActual.getTipoComida());
        holder.itemView.setOnClickListener(v -> listener.onRecetaClick(recetaActual));
        holder.btnFavorito.setOnClickListener(v -> listener.onToggleFavorito(recetaActual));

    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public static class RecetaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvTiempo, tvDificultad, tvTipoComida, tvComensales;
        ImageView btnFavorito;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreReceta);
            tvTiempo = itemView.findViewById(R.id.tvTiempo);
            tvDificultad = itemView.findViewById(R.id.tvDificultad);
            tvTipoComida = itemView.findViewById(R.id.tvTipoComida);
            tvComensales = itemView.findViewById(R.id.tvComensales);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }
}
