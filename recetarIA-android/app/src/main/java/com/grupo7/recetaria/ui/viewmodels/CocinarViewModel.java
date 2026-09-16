package com.grupo7.recetaria.ui.viewmodels;

import android.app.Application;
import android.se.omapi.Session;
import android.util.Log;
import com.grupo7.recetaria.BuildConfig;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.grupo7.recetaria.auth.SessionManager;
import com.grupo7.recetaria.data.local.AlacenaDAO;
import com.grupo7.recetaria.data.local.CalendarioDAO;
import com.grupo7.recetaria.data.local.ListaCompraDAO;
import com.grupo7.recetaria.data.local.PreferenciasDAO;
import com.grupo7.recetaria.data.local.ProductoDAO;
import com.grupo7.recetaria.data.local.RecetaDAO;
import com.grupo7.recetaria.data.local.RestriccionesDAO;
import com.grupo7.recetaria.data.local.TipoComidaDao;
import com.grupo7.recetaria.data.local.UsuarioDAO;
import com.grupo7.recetaria.models.IngredienteRecetaIA;
import com.grupo7.recetaria.models.ItemAlacena;
import com.grupo7.recetaria.models.ItemListaCompra;
import com.grupo7.recetaria.models.PreferenciaAlimentaria;
import com.grupo7.recetaria.models.RecetaIA;
import com.grupo7.recetaria.models.RestriccionAlimentaria;
import com.grupo7.recetaria.models.TipoComida;
import com.grupo7.recetaria.models.Usuario;
import com.grupo7.recetaria.models.UsuarioSesion;
import com.grupo7.recetaria.ui.otro.Conversor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CocinarViewModel extends AndroidViewModel {
    private final GenerativeModelFutures model;



    public enum CocinarEstado { FORMULARIO, ESPERA, SUGERENCIAS }
    private final MutableLiveData<CocinarEstado> estadoNavegacion = new MutableLiveData<>(CocinarEstado.FORMULARIO);
    private int tipoComidaSeleccionadoId = -1;
    private final MutableLiveData<List<RecetaIA>> recetasSugeridas = new MutableLiveData<>();
    private final TipoComidaDao tipoComidaDao;
    private final RestriccionesDAO restriccionesDAO;
    private final PreferenciasDAO preferenciasDAO;
    private final ProductoDAO productoDAO;
    private final UsuarioDAO usuarioDAO;
    private final CalendarioDAO calendarioDAO;
    private final ListaCompraDAO listaCompraDAO;
    private final RecetaDAO recetaDAO;
    private final AlacenaDAO alacenaDAO;
    private long idRecetaGuardada;
    private final MutableLiveData<List<TipoComida>> _tiposComida = new MutableLiveData<>();
    public LiveData<List<RecetaIA>> getRecetasSugeridas() {
        return recetasSugeridas;
    }

    public CocinarViewModel(@NonNull Application application) {
        super(application);
        this.tipoComidaDao = new TipoComidaDao(application);
        this.restriccionesDAO = new RestriccionesDAO(application);
        this.preferenciasDAO = new PreferenciasDAO(application);
        this.alacenaDAO = new AlacenaDAO(application);
        this.calendarioDAO = new CalendarioDAO(application);
        this.usuarioDAO = new UsuarioDAO(application);
        this.recetaDAO = new RecetaDAO(application);
        this.productoDAO = new ProductoDAO(application);
        this.listaCompraDAO = new ListaCompraDAO(application);

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        configBuilder.temperature = 0.2f;
        GenerationConfig config = configBuilder.build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash-lite",
                BuildConfig.GEMINI_API_KEY,
                config
        );
        this.model = GenerativeModelFutures.from(gm);
    }

    public void hacerPremium() {
        UsuarioSesion usuario = SessionManager.getInstance(getApplication()).usuarioLogueado();
        usuario.setEs_premium(true);
        SessionManager.getInstance(getApplication()).crearSesion(usuario);
        usuarioDAO.suscribirPremium(usuario.getId_usuario());
    }

    public void agregarAlCarrito(ArrayList<IngredienteRecetaIA> ingredientesLista) {
        if (ingredientesLista == null || ingredientesLista.isEmpty()) return;

        // 1. Obtenemos el usuario
        int idUsuario = SessionManager.getInstance(getApplication()).usuarioLogueado().getId_usuario();

        for (IngredienteRecetaIA ing : ingredientesLista) {
            ing.setNombreIngrediente(ing.getNombreIngrediente().toLowerCase());
            int idProducto = productoDAO.obtenerIdPorNombreProducto(ing.getNombreIngrediente());
            if (idProducto == -1) {
                idProducto = (int) productoDAO.insertarNuevoProducto(ing.getNombreIngrediente(), ing.getRubro());
            }

            // 3. Ya esta en la Lista de Compra?
            ItemListaCompra itemEnLista = listaCompraDAO.obtenerItemPorProducto(idUsuario, idProducto);

            if (itemEnLista != null) {
                // SÍ: Convertimos y Sumamos
                double cantidadConvertida = Conversor.convertir(ing.getCantidad(), ing.getUnidad(), itemEnLista.getNombre_unidad()); // TODO: Modificar metodo para convertir
                listaCompraDAO.actualizarCantidad(itemEnLista.getId_producto_lista(), itemEnLista.getCantidad() + cantidadConvertida);
                continue;
            }

            // 4. Esta en la Alacena? (Para respetar su unidad)
            String unidadEnAlacena = alacenaDAO.obtenerUnidadPreferida(idUsuario, idProducto);

            ItemListaCompra nuevoItem = new ItemListaCompra();
            nuevoItem.setId_usuario(idUsuario);
            nuevoItem.setId_producto(idProducto);
            nuevoItem.setNombre_producto(ing.getNombreIngrediente());
            nuevoItem.setNombre_rubro(ing.getRubro());

            if (unidadEnAlacena != null) {
                nuevoItem.setNombre_unidad(unidadEnAlacena);
                nuevoItem.setId_unidad(productoDAO.obtenerIdUnidad(unidadEnAlacena));
                nuevoItem.setCantidad(Conversor.convertir(ing.getCantidad(), ing.getUnidad(), unidadEnAlacena)); // TODO: Modificar metodo para convertir
            } else {
                // Sino toma los datos de la IA
                nuevoItem.setNombre_unidad(ing.getUnidad());
                nuevoItem.setId_unidad(productoDAO.obtenerIdUnidad(ing.getUnidad()));
                nuevoItem.setCantidad(ing.getCantidad());
            }

            listaCompraDAO.agregarProductoALista(nuevoItem);
        }
    }
    public long guardarRecetaComoFavorita(RecetaIA receta, int idUsuario, int idTipoComida) {
        return recetaDAO.guardarRecetaComoFavorita(receta, idUsuario, idTipoComida);
    }

    public void descontarIngredientesAlacena(RecetaIA receta) {
        List<IngredienteRecetaIA> ingredientesLista = receta.getIngredientes();
        if (ingredientesLista == null || ingredientesLista.isEmpty()) return;

        int idUsuario = SessionManager.getInstance(getApplication()).usuarioLogueado().getId_usuario();

        for (IngredienteRecetaIA ing : ingredientesLista) {
            String nombreLimpio = ing.getNombreIngrediente().toLowerCase().trim();
            int idProducto = productoDAO.obtenerIdPorNombreProducto(nombreLimpio);
            if (idProducto == -1) continue;
            String unidadEnAlacena = alacenaDAO.obtenerUnidadPreferida(idUsuario, idProducto);
            if (unidadEnAlacena != null) {
                double cantidadConvertida = Conversor.convertir(
                        ing.getCantidad(),
                        ing.getUnidad(),
                        unidadEnAlacena
                );
                alacenaDAO.restarStock(idUsuario, idProducto, cantidadConvertida, unidadEnAlacena);
            }
        }
    }

    public long agendarReceta(RecetaIA receta, String fechaProgramada, int tipoComidaSeleccionadoId) {
        this.tipoComidaSeleccionadoId = tipoComidaSeleccionadoId;
        UsuarioSesion usuario = SessionManager.getInstance(this.getApplication()).usuarioLogueado();
        if(!receta.isEsGuardada()){
            this.idRecetaGuardada = recetaDAO.guardarRecetaComoFavorita(receta, usuario.getId_usuario(), getTipoComidaSeleccionadoId());
        }
        return calendarioDAO.agendarReceta(idRecetaGuardada, usuario.getId_usuario(), fechaProgramada, getTipoComidaSeleccionadoId());
    }

    public LiveData<List<TipoComida>> getTiposComida() {
        return _tiposComida;
    }

    public void cargarTiposComida() {
        new Thread(() -> {
            List<TipoComida> lista = tipoComidaDao.obtenerTiposComida();
            _tiposComida.postValue(lista); // postValue es para hilos secundarios
        }).start();
    }

    public LiveData<CocinarEstado> getEstadoNavegacion() { return estadoNavegacion; }

    public void setEstado(CocinarEstado nuevoEstado) {
        estadoNavegacion.setValue(nuevoEstado);
    }

    public void setTipoComidaSeleccionadoId(int id) {
        this.tipoComidaSeleccionadoId = id;
    }

    public int getTipoComidaSeleccionadoId() {
        return tipoComidaSeleccionadoId;
    }

    private String armarPromptCompleto(String momento, int adultos, int ninios, String tiempo, String dificultad, boolean soloAlacena, boolean usarPrefs, boolean usarRestr, String contexto, List<PreferenciaAlimentaria> pref, List<RestriccionAlimentaria> rest, List<ItemAlacena> alacena) {

    StringBuilder sb = new StringBuilder();

    sb.append("Eres Paulina Cocina, la famosa chef conocida por tu simplicidad y recetas hogareñas. ")
      .append("Tu tarea es sugerir EXACTAMENTE 5 recetas completas. ")
      .append("Tu tono debe ser cercano, divertido y muy pedagógico, usando modismos segun la nacionalidad del usuario.\n\n");

    String nacionalidad = SessionManager.getInstance(getApplication()).usuarioLogueado().getNacionalidad();
    String rubros = String.join(", ", productoDAO.obtenerListaPlanaRubros());
    sb.append("REGLAS DE NORMALIZACIÓN (ESTRICTAS):\n")
            .append("- RUBROS: ") .append(rubros).append(".\n")
            .append("- DIFICULTAD: [Facil, Moderado, Chef].\n")
            .append("- TIEMPO: Formato 'número min' (ej: '45 min').\n")
            .append("- CAMPO 'nombre': Es la descripción completa para el usuario. Incluye cantidad, unidad y estado (ej: '500g de carne molida de res', '2 filetes de merluza frescos').\n")
            .append("- CAMPO 'nombreIngrediente': Es el IDENTIFICADOR DE PRODUCTO. ")
            .append("Debe ser el nombre específico del producto SIN cantidades, SIN unidades y SIN adjetivos innecesarios. ")
            .append("PROHIBIDO usar categorías generales (no uses 'Carne', usa 'Carne molida'; no uses 'Pescado', usa 'Filet de merluza'). ")
            .append("Debe ser exactamente lo que el usuario buscaría en el supermercado.\n\n")
            .append("Unidades permitidas: ")
            .append(String.join(", ", productoDAO.obtenerListaNombresUnidades()))
            .append("\n\n");

    sb.append("INFORMACIÓN DEL USUARIO:\n")
            .append("- Nacionalidad: ").append(nacionalidad).append("\n")
      .append("- Comida deseada: ").append(momento).append("\n")
      .append("- Comensales: ").append(adultos).append(" adultos y ").append(ninios).append(" niños.\n")
      .append("- Tiempo máximo: ").append(tiempo).append("\n")
      .append("- Nivel de dificultad: ").append(dificultad).append("\n");

        sb.append("INSTRUCCIONES DE LA RECETA:\n")
                .append("- Deben ser lo mas detalladas posibles. No te preocupes por la cantidad de pasos. Deben ser divertidas y faciles de interpretar.").append("\n");

    if (soloAlacena && alacena != null && !alacena.isEmpty()) {
        sb.append("- RESTRICCIÓN DE ALACENA: Tienes DISPONIBLES únicamente estos ingredientes: ")
                .append(simplificarAlacena(alacena))
                .append(". Instrucción: Selecciona solo los que tengan sentido para la receta (puedes usar algunos o todos según convenga). ")
                .append("PROHIBIDO usar ingredientes fuera de esta lista o de los básicos (sal, pimienta, aceite, agua, ajo y cebolla).\n");
    }

    if (usarPrefs && pref != null && !pref.isEmpty()) {
        sb.append("- PREFERENCIAS ALIMENTARIAS: ");
        for (PreferenciaAlimentaria p : pref) sb.append(p.getNombre()).append(", ");
        sb.append("\n");
    }

    if (usarRestr && rest != null && !rest.isEmpty()) {
        sb.append("- RESTRICCIONES MÉDICAS (PROHIBIDO): ");
        for (RestriccionAlimentaria r : rest) sb.append(r.getNombre()).append(", ");
        sb.append("\n");
    }

    if (contexto != null && !contexto.trim().isEmpty()) {
        sb.append("- DESEO EXTRA DEL USUARIO: ").append(contexto).append("\n");
    }

    sb.append("\nFORMATO JSON OBLIGATORIO:\n")
      .append("Responde ÚNICAMENTE con un JSON Array que contenga 5 objetos con esta estructura exacta, sin texto extra:\n")
      .append("[\n")
      .append("  {\n")
      .append("    \"titulo\": \"\",\n")
      .append("    \"tiempo\": \"\",\n")
      .append("    \"dificultad\": \"\",\n")
      .append("    \"comensales\": 0,\n")
      .append("    \"instrucciones\": [\"\"],\n")
      .append("    \"ingredientes\": [\n")
      .append("      { \"nombre\": <el nombre completo. P.ej: 500 gr. de pechuga>\"\", \"nombreIngrediente\": \"\", \"cantidad\": 0.0, \"unidad\": \"\", \"rubro\": \"\" }\n")
      .append("    ]\n")
      .append("  }\n")
      .append("]");

    return sb.toString();
}

    public void solicitarSugerenciasIA(String momento, int adultos, int ninios, String tiempo, String dificultad, boolean soloAlacena, boolean usarPrefs, boolean usarRestr, String contexto) {

        UsuarioSesion usuario = SessionManager.getInstance(this.getApplication()).usuarioLogueado();

        List<PreferenciaAlimentaria> pref = preferenciasDAO.obtenerPreferenciasPorUsuario(usuario.getId_usuario());
        List<RestriccionAlimentaria> rest = restriccionesDAO.obtenerRestriccionesPorUsuario(usuario.getId_usuario());
        List<ItemAlacena> alacena = alacenaDAO.obtenerAlacenaPorUsuarioPrompt(usuario.getId_usuario());

        String promptFinal = armarPromptCompleto(
                momento, adultos, ninios, tiempo, dificultad,
                soloAlacena, usarPrefs, usarRestr,
                contexto, pref, rest, alacena
        );

        solicitarRecetaIA(promptFinal);
    }

    private String simplificarAlacena(List<ItemAlacena> alacena) {
        if (alacena == null || alacena.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (ItemAlacena item : alacena) {
//            String cantidad = formatearCantidad(item.getCantidad());
//            String unidad = (item.getNombre_unidad() != null) ? item.getNombre_unidad() : "";
            String producto = item.getNombre_producto();

            sb.
//            .append(cantidad).append(" ").append(unidad).append(" ").
                    append(producto).append(", ");
        }

        return sb.toString().replaceAll(", $", "");
    }

    private String formatearCantidad(double d) {
        if(d == (long) d)
            return String.format("%d", (long)d);
        else
            return String.format("%s", d);
    }

    public void solicitarRecetaIA(String prompt) {
        UsuarioSesion usuario = SessionManager.getInstance(getApplication()).usuarioLogueado();
        if(usuario.getUsos_ia() > 5){
            setEstado(CocinarEstado.FORMULARIO);
        }
        setEstado(CocinarEstado.ESPERA);
        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    String jsonTexto = result.getText();

                    if (jsonTexto != null) {
                        jsonTexto = jsonTexto.trim();
                        if (jsonTexto.startsWith("```json")) jsonTexto = jsonTexto.substring(7);
                        if (jsonTexto.endsWith("```")) jsonTexto = jsonTexto.substring(0, jsonTexto.length() - 3);
                        jsonTexto = jsonTexto.trim();
                    }

                    com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                            .setLenient()
                            .create();

                    java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<RecetaIA>>(){}.getType();
                    List<RecetaIA> listaRecetas = gson.fromJson(jsonTexto, listType);

                    if (listaRecetas != null) {
                        recetasSugeridas.postValue(listaRecetas);
                        setEstado(CocinarEstado.SUGERENCIAS);
                        if(!usuario.isEs_premium()){
                            usuarioDAO.sumarUsoIA(usuario.getId_usuario());
                            usuario.setUsos_ia(usuario.getUsos_ia()+1);
                            SessionManager.getInstance(getApplication()).crearSesion(usuario);
                        }
                    } else {
                        throw new Exception("La lista parseada es nula");
                    }

                } catch (Exception e) {
                    Log.e("GEMINI_ERROR", "Error al parsear: " + e.getMessage());
                    setEstado(CocinarEstado.FORMULARIO);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("GEMINI_ERROR", "Fallo la conexión: " + t.getMessage());
                setEstado(CocinarEstado.FORMULARIO);
            }
        }, getApplication().getMainExecutor());
    }
}
