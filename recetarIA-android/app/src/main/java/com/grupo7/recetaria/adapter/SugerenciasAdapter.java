package com.grupo7.recetaria.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo7.recetaria.databinding.ItemRecetaIaBinding;
import com.grupo7.recetaria.models.RecetaIA;

import java.util.List;

public class SugerenciasAdapter extends RecyclerView.Adapter<SugerenciasAdapter.SugerenciaViewHolder> {

    private List<RecetaIA> recetas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RecetaIA receta);
    }

    public SugerenciasAdapter(List<RecetaIA> recetas, OnItemClickListener listener) {
        this.recetas = recetas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SugerenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecetaIaBinding binding = ItemRecetaIaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SugerenciaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SugerenciaViewHolder holder, int position) {
        holder.bind(recetas.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    public void updateData(List<RecetaIA> nuevasRecetas) {
        this.recetas = nuevasRecetas;
        notifyDataSetChanged();
    }

    public static class SugerenciaViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecetaIaBinding binding;

        public SugerenciaViewHolder(ItemRecetaIaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RecetaIA receta, OnItemClickListener listener) {
            binding.tvNombreReceta.setText(receta.getTitulo());
            binding.tvDescripcionCorta.setText(receta.getInstrucciones().get(0));
            binding.tvTiempo.setText(receta.getTiempo());
            binding.tvDificultad.setText(receta.getDificultad());
            String comensales = receta.getComensales() + " pers.";
            binding.tvComensales.setText(comensales);

            itemView.setOnClickListener(v -> listener.onItemClick(receta));
        }
    }
}