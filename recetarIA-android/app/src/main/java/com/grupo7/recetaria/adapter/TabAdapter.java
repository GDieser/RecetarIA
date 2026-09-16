package com.grupo7.recetaria.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.grupo7.recetaria.ui.fragments.AlacenaVirtualFragment;
import com.grupo7.recetaria.ui.fragments.CalendarioFragment;
import com.grupo7.recetaria.ui.fragments.CocinarFragment;
import com.grupo7.recetaria.ui.fragments.ListaComprasFragment;
import com.grupo7.recetaria.ui.fragments.RecetasFragment;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecetasFragment();
            case 1:
                return new CalendarioFragment();
            case 2:
                return new CocinarFragment();
            case 3:
                return new AlacenaVirtualFragment();
            case 4:
                return new ListaComprasFragment();
            default:
                return new RecetasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
