package com.grupo7.recetaria.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo7.recetaria.R;
import com.grupo7.recetaria.models.Calendario;

import java.util.List;

public class CalendarioAdapter extends RecyclerView.Adapter<CalendarioAdapter.CalendarioViewHolder> {

    private List<Calendario> lista;
    private View.OnClickListener listener;
    public void setListaCalendario(List<Calendario> lista) {
        this.lista = lista;
        notifyDataSetChanged();//para avisar que cambió de día
    }

    @NonNull
    @Override
    public CalendarioAdapter.CalendarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta_calendario, parent, false);
        return new CalendarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarioAdapter.CalendarioViewHolder holder, int position) {
        Calendario calendario = lista.get(position);
        holder.tvNombreReceta.setText(calendario.getTituloReceta());
        holder.tvComensales.setText(calendario.getComensales() + " pers.");
        holder.tvDificultad.setText(calendario.getDificultad());
        holder.tvTiempo.setText((calendario.getTiempo()));
        holder.tvTipoComida.setText((calendario.getNombre_tipo_comida()));
        holder.itemView.setTag(calendario);
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size():0;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }


    public class CalendarioViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreReceta, tvTiempo, tvDificultad, tvTipoComida, tvComensales;

        public CalendarioViewHolder(@NonNull View itemView) {
            super(itemView);
           tvNombreReceta= itemView.findViewById(R.id.tvNombreReceta);
            tvTiempo = itemView.findViewById(R.id.tvTiempo);
            tvDificultad = itemView.findViewById(R.id.tvDificultad);
            tvComensales = itemView.findViewById(R.id.tvComensales);
            tvTipoComida =  itemView.findViewById(R.id.tvTipoComida);

        }
    }
}
