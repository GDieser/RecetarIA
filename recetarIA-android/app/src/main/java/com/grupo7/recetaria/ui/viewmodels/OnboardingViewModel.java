package com.grupo7.recetaria.ui.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grupo7.recetaria.data.local.PreferenciasDAO;
import com.grupo7.recetaria.data.local.RestriccionesDAO;
import com.grupo7.recetaria.models.PreferenciaAlimentaria;
import com.grupo7.recetaria.models.RestriccionAlimentaria;

import java.util.List;

// 1. Cambiamos a AndroidViewModel para tener contexto
public class OnboardingViewModel extends AndroidViewModel {

    private PreferenciasDAO preferenciasDAO;
    private RestriccionesDAO restriccionesDAO;
    private final MutableLiveData<List<RestriccionAlimentaria>> _restriccionesAlimentarias = new MutableLiveData<>();
    private final MutableLiveData<List<PreferenciaAlimentaria>> _preferenciasAlimentarias = new MutableLiveData<>();

    public OnboardingViewModel(@NonNull Application application) {
        super(application);
        this.preferenciasDAO = new PreferenciasDAO(application);
        this.restriccionesDAO = new RestriccionesDAO(application);
    }

    public LiveData<List<PreferenciaAlimentaria>> getPreferenciasAlimentarias() {
        return _preferenciasAlimentarias;
    }

    public LiveData<List<RestriccionAlimentaria>> getRestriccionesAlimentarias() {
        return _restriccionesAlimentarias;
    }

    public void obtenerPreferencias(){
        new Thread(() -> {
            List<PreferenciaAlimentaria> lista = preferenciasDAO.obtenerPreferenciasAlimentarias();
            _preferenciasAlimentarias.postValue(lista);
        }).start();
    }

    public void obtenerRestricciones(){
        new Thread(() -> {
            List<RestriccionAlimentaria> lista = restriccionesDAO.obtenerRestriccionesAlimentarias();
            _restriccionesAlimentarias.postValue(lista);
        }).start();
    }
}