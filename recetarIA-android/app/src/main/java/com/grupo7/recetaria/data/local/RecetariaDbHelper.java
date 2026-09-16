package com.grupo7.recetaria.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RecetariaDbHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "recetaria.db";

    private static final int VERSION_BD = 1;

    private String TABLA_USUARIO = "CREATE TABLE Usuario (" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT UNIQUE NOT NULL, " +
            "pass TEXT, " +
            "nombre TEXT, " +
            "apellido TEXT," +
            "google_token TEXT, " +
            "es_validado INTEGER DEFAULT 1," +
            "fecha_nacimiento TEXT," +
            "codigo_validacion INTEGER," +
            "nacionalidad TEXT DEFAULT 'OTRO'," +
            "adultos_familia INTEGER DEFAULT 1, " +
            "ninios_familia INTEGER DEFAULT 0, " +
            "usos_ia INTEGER DEFAULT 0, " +
            "es_premium INTEGER DEFAULT 0);";

    private static final String TABLA_RUBRO = "CREATE TABLE Rubro (" +
            "id_rubro INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT UNIQUE NOT NULL);";

    private static final String TABLA_UNIDAD = "CREATE TABLE Unidad (" +
            "id_unidad INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT UNIQUE NOT NULL, " +
            "categoria TEXT);";

    private static final String TABLA_UNIDAD_RECETA = "CREATE TABLE UnidadReceta (" +
            "id_unidad INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT UNIQUE NOT NULL, " +
            "es_convertible INT DEFAULT 0);";

    private static final String TABLA_PREFERENCIA = "CREATE TABLE PreferenciaAlimentaria (" +
            "id_preferencia INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT UNIQUE NOT NULL);";

    private static final String TABLA_RESTRICCION = "CREATE TABLE RestriccionAlimentaria (" +
            "id_restriccion INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT UNIQUE NOT NULL);";

    private static final String TABLA_PRODUCTO = "CREATE TABLE Producto (" +
            "id_producto INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_rubro INTEGER, " +
            "nombre TEXT NOT NULL, " +
            "FOREIGN KEY (id_rubro) REFERENCES Rubro(id_rubro) ON DELETE SET NULL);";

    private static final String TABLA_PRODUCTO_ALACENA = "CREATE TABLE ProductoAlacena (" +
            "id_producto_alacena INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "id_producto INTEGER NOT NULL, " +
            "id_unidad INTEGER, " +
            "cantidad REAL, " +
            "es_infaltable INTEGER DEFAULT 0, " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_producto) REFERENCES Producto(id_producto), " +
            "FOREIGN KEY (id_unidad) REFERENCES Unidad(id_unidad));";

    private static final String TABLA_PRODUCTO_LISTA = "CREATE TABLE ProductoListaCompra (" +
            "id_producto_lista INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "id_producto INTEGER NOT NULL, " +
            "id_unidad INTEGER, " +
            "cantidad REAL, " +
            "es_adquirido INTEGER DEFAULT 0, " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_producto) REFERENCES Producto(id_producto), " +
            "FOREIGN KEY (id_unidad) REFERENCES Unidad(id_unidad));";

    private static final String TABLA_RECETA = "CREATE TABLE Receta (" +
            "id_receta INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "titulo TEXT NOT NULL, " +
            "id_tipo_comida INTEGER NOT NULL, " +
            "dificultad TEXT, " +
            "comensales INTEGER, " +
            "tiempo TEXT, " + // Cambiado a text porque la IA envia el tiempo como un Str
            "lista_pasos TEXT, " +
            "es_favorito INTEGER DEFAULT 0, "+
            "fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_tipo_comida) REFERENCES TipoComida(id_tipo_comida));";

    private static final String TABLA_INGREDIENTE_RECETA = "CREATE TABLE IngredienteReceta (" +
            "id_ingrediente INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_receta INTEGER NOT NULL, " +
            "nombre_completo TEXT, " +
            "nombre_ingrediente TEXT, " +
            "cantidad REAL, " +
            "unidad TEXT, " +
            "FOREIGN KEY (id_receta) REFERENCES Receta(id_receta) ON DELETE CASCADE);";

    private static final String TABLA_TIPO_COMIDA = "CREATE TABLE TipoComida (" +
            "id_tipo_comida INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT UNIQUE NOT NULL);";

    private static final String TABLA_CALENDARIO = "CREATE TABLE CalendarioRecetas (" +
            "id_calendario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "id_receta INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "id_tipo_comida INTEGER NOT NULL, " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE," +
            "FOREIGN KEY (id_tipo_comida) REFERENCES TipoComida(id_tipo_comida) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_receta) REFERENCES Receta(id_receta) ON DELETE CASCADE);";

    private static final String TABLA_PREFERENCIA_USUARIO = "CREATE TABLE PreferenciaAlimentariaUsuario (" +
            "id_preferencia_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "id_preferencia INTEGER NOT NULL, " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_preferencia) REFERENCES PreferenciaAlimentaria(id_preferencia));";

    private static final String TABLA_RESTRICCION_USUARIO = "CREATE TABLE RestriccionAlimentariaUsuario (" +
            "id_restriccion_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "id_restriccion INTEGER NOT NULL, " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_restriccion) REFERENCES RestriccionAlimentaria(id_restriccion));";



    public RecetariaDbHelper(@Nullable Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL(TABLA_USUARIO);
        db.execSQL(TABLA_RUBRO);
        db.execSQL(TABLA_UNIDAD);
        db.execSQL(TABLA_PREFERENCIA);
        db.execSQL(TABLA_RESTRICCION);
        db.execSQL(TABLA_TIPO_COMIDA);
        db.execSQL(TABLA_PRODUCTO);
        db.execSQL(TABLA_PRODUCTO_ALACENA);
        db.execSQL(TABLA_PRODUCTO_LISTA);
        db.execSQL(TABLA_RECETA);
        db.execSQL(TABLA_INGREDIENTE_RECETA);
        db.execSQL(TABLA_CALENDARIO);
        db.execSQL(TABLA_PREFERENCIA_USUARIO);
        db.execSQL(TABLA_RESTRICCION_USUARIO);
        db.execSQL(TABLA_UNIDAD_RECETA);

        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insertarDatosIniciales(SQLiteDatabase db) {

        db.execSQL("INSERT INTO Unidad (nombre, categoria) VALUES ('Gramos', 'Peso');");
        db.execSQL("INSERT INTO Unidad (nombre, categoria) VALUES ('Kilogramos', 'Peso');");
        db.execSQL("INSERT INTO Unidad (nombre, categoria) VALUES ('Mililitros', 'Volumen');");
        db.execSQL("INSERT INTO Unidad (nombre, categoria) VALUES ('Litros', 'Volumen');");
        db.execSQL("INSERT INTO Unidad (nombre, categoria) VALUES ('Unidades', 'Cantidad');");

        // --- Rubros de Comida (Prioridad Alta) ---
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Almacén');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Lácteos');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Carnes y Embutidos');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Frutas y Verduras');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Congelados');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Bebidas');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Panadería');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Especias y Condimentos');");

// --- Rubros de Hogar y No Comestibles ---
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Perfumería y Cuidado Personal');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Limpieza');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Mascotas');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Bebés');");
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Hogar y Bazar');");

// --- El "Atajate" para errores ---
        db.execSQL("INSERT INTO Rubro (nombre) VALUES ('Otros');");

        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Vegano');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Vegetariano');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Keto');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Bajo en carbohidratos');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Fitness / Proteico');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Carnívoro');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Omnívoro (Come de todo)');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Paleo');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Mediterránea');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Basado en Plantas (Plant-Based)');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Hiperproteica (Alta en proteína)');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Volumen (Superávit calórico)');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Definición (Déficit calórico)');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Sin procesados (Real Fooding)');");
        db.execSQL("INSERT INTO PreferenciaAlimentaria (nombre) VALUES ('Ayuno Intermitente');");

        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Celíaco (Sin TACC)');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Intolerante a la Lactosa');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Diabético');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Hipertenso (Bajo en Sodio)');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Alergia al Maní');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Alergia al Marisco');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Colesterol Alto (Bajo en grasas sat.)');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Ácido Úrico / Gota (Bajo en purinas)');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Insuficiencia Renal');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Colon Irritable / Dieta FODMAP');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Gastritis (Sin picantes ni irritantes)');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Hígado Graso');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Alergia al Huevo');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Alergia a la Soja');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Alergia a los Frutos Secos (General)');");
        db.execSQL("INSERT INTO RestriccionAlimentaria (nombre) VALUES ('Alergia al Pescado');");

        db.execSQL("INSERT INTO TipoComida (nombre) VALUES ('Desayuno');");
        db.execSQL("INSERT INTO TipoComida (nombre) VALUES ('Almuerzo');");
        db.execSQL("INSERT INTO TipoComida (nombre) VALUES ('Merienda');");
        db.execSQL("INSERT INTO TipoComida (nombre) VALUES ('Cena');");
        db.execSQL("INSERT INTO TipoComida (nombre) VALUES ('Postre');");
        db.execSQL("INSERT INTO TipoComida (nombre) VALUES ('Snack');");

        // --- Unidades Métricas (Convertibles = 1) ---
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('Litros', 1);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('Mililitros.', 1);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('cm3', 1);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('Kilogramos', 1);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('Gramos', 1);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('Unidades', 1);");
        // --- Unidades Culinarias y de Conteo (Convertibles = 0) ---
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('pizca', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('cdita.', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('cda.', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('taza', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('puñado', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('feta', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('diente', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('sobre', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('chorrito', 0);");
        db.execSQL("INSERT INTO UnidadReceta (nombre, es_convertible) VALUES ('a gusto', 0);");

        }


    public Cursor obtenerRubros() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Rubro ORDER BY nombre ASC", null);
    }

    public Cursor obtenerUnidades() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Unidad ORDER BY categoria, nombre ASC", null);
    }

    public Cursor obtenerUsuarioPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Usuario WHERE email = ?", new String[]{email});
    }
}