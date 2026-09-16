package com.grupo7.recetaria.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.ListaCompraDAO;
import com.grupo7.recetaria.models.ItemListaCompra;

import java.util.List;

public class ListaCompraViewModel extends AndroidViewModel {

    private MutableLiveData<List<ItemListaCompra>> _lista = new MutableLiveData<>();
    private ListaCompraDAO listaCompraDAO;

    public ListaCompraViewModel(@NonNull Application application) {
        super(application);
        this.listaCompraDAO = new ListaCompraDAO(application);
    }
    public LiveData<List<ItemListaCompra>> getListaCompletaOriginal(){
        return _lista;
    }

    public void actualizarListaUsuario() {
        int idUsuarioActual = SessionManager.getInstance(getApplication()).usuarioLogueado().getId_usuario();
        _lista.setValue(listaCompraDAO.obtenerListaPorUsuario(idUsuarioActual));
    }
}
