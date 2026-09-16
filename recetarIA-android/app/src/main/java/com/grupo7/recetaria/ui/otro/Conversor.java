package com.grupo7.recetaria.ui.otro;

public class Conversor {
    public static String normalizarUnidad(String unidadCruda) {
        if (unidadCruda == null || unidadCruda.trim().isEmpty()) return "Unidades";

        String u = unidadCruda.trim().toLowerCase();

        // pesos
        if (u.equals("g") || u.equals("gr") || u.equals("gramo") || u.equals("gramos")) return "Gramos";
        if (u.equals("kg") || u.equals("kilo") || u.equals("kilos") || u.equals("kilogramos") || u.equals("kilogramo")) return "Kilogramos";

        // pesos alt
        if (u.equals("lb") || u.equals("libra") || u.equals("libras")) return "Gramos";
        if (u.equals("oz") || u.equals("onza") || u.equals("onzas")) return "Gramos";

        // volumenes
        if (u.equals("ml") || u.equals("cc") || u.equals("mililitro") || u.equals("mililitros")) return "Mililitros";
        if (u.equals("l") || u.equals("litro") || u.equals("litros")) return "Litros";

        // otros
        if (u.contains("taza") || u.contains("vaso")) return "Gramos";  // 1 taza  250g
        if (u.contains("cucharada") || u.equals("cda")) return "Gramos"; // 15g
        if (u.contains("cucharadita") || u.equals("cdita")) return "Gramos"; // 5g
        if (u.contains("pizca") || u.contains("puñado") || u.contains("chorrito")) return "Gramos";// 1g

        return "Unidades";
    }

    // Conversioans matematicas
    public static double convertir(double cantidadOriginal, String unidadOrigenCruda, String unidadDestinoCruda) {

        String origen = normalizarUnidad(unidadOrigenCruda);
        String destino = normalizarUnidad(unidadDestinoCruda);

        if (origen.equals(destino)) {
            return cantidadOriginal;
        }

        // equivalentes varios
        String uOriLower = unidadOrigenCruda.trim().toLowerCase();

        if (uOriLower.contains("taza") || uOriLower.contains("vaso")) cantidadOriginal *= 250.0;
        else if (uOriLower.contains("cucharada") || uOriLower.equals("cda")) cantidadOriginal *= 15.0;
        else if (uOriLower.contains("cucharadita") || uOriLower.equals("cdita")) cantidadOriginal *= 5.0;
        else if (uOriLower.contains("puñado")) cantidadOriginal *= 30.0;
        else if (uOriLower.contains("chorrito")) cantidadOriginal *= 15.0;
        else if (uOriLower.contains("pizca")) cantidadOriginal *= 1.0;

        // pesos alt
        else if (uOriLower.equals("lb") || uOriLower.equals("libra") || uOriLower.equals("libras")) cantidadOriginal *= 453.59;
        else if (uOriLower.equals("oz") || uOriLower.equals("onza") || uOriLower.equals("onzas")) cantidadOriginal *= 28.35;


        // gr a kg y kg a gr
        if (origen.equals("Gramos") && destino.equals("Kilogramos")) return cantidadOriginal / 1000.0;
        if (origen.equals("Kilogramos") && destino.equals("Gramos")) return cantidadOriginal * 1000.0;

        // ml a litros y L a ml
        if (origen.equals("Mililitros") && destino.equals("Litros")) return cantidadOriginal / 1000.0;
        if (origen.equals("Litros") && destino.equals("Mililitros")) return cantidadOriginal * 1000.0;

        // 1g = 1ml (solidos a liquidos / viceversa)
        if (origen.equals("Gramos") && destino.equals("Mililitros")) return cantidadOriginal;
        if (origen.equals("Mililitros") && destino.equals("Gramos")) return cantidadOriginal;

        if (origen.equals("Kilogramos") && destino.equals("Litros")) return cantidadOriginal;
        if (origen.equals("Litros") && destino.equals("Kilogramos")) return cantidadOriginal;

        if (origen.equals("Gramos") && destino.equals("Litros")) return cantidadOriginal / 1000.0;
        if (origen.equals("Litros") && destino.equals("Gramos")) return cantidadOriginal * 1000.0;

        if (origen.equals("Mililitros") && destino.equals("Kilogramos")) return cantidadOriginal / 1000.0;
        if (origen.equals("Kilogramos") && destino.equals("Mililitros")) return cantidadOriginal * 1000.0;

        // 1 uni aprox 166.6 gr (1Kg / 6)
        double pesoPromedioUnidadGramos = 166.66;

        // uni a gr/ml
        if (origen.equals("Unidades") && destino.equals("Gramos")) return cantidadOriginal * pesoPromedioUnidadGramos;
        if (origen.equals("Unidades") && destino.equals("Mililitros")) return cantidadOriginal * pesoPromedioUnidadGramos;

        // gr/ml a uni
        if (origen.equals("Gramos") && destino.equals("Unidades")) return cantidadOriginal / pesoPromedioUnidadGramos;
        if (origen.equals("Mililitros") && destino.equals("Unidades")) return cantidadOriginal / pesoPromedioUnidadGramos;

        // uni a kg/L
        if (origen.equals("Unidades") && destino.equals("Kilogramos")) return cantidadOriginal / 6.0;
        if (origen.equals("Unidades") && destino.equals("Litros")) return cantidadOriginal / 6.0;

        // kg/L a uni
        if (origen.equals("Kilogramos") && destino.equals("Unidades")) return cantidadOriginal * 6.0;
        if (origen.equals("Litros") && destino.equals("Unidades")) return cantidadOriginal * 6.0;

        return cantidadOriginal;
    }

    // para num periodicos
    public static String formatearCantidad(double cantidad) {
        double cantidadRedondeada = Math.round(cantidad * 100.0) / 100.0;

        if (cantidadRedondeada == (long) cantidadRedondeada) {
            return String.format("%d", (long) cantidadRedondeada);
        } else {
            return String.valueOf(cantidadRedondeada);
        }
    }


}
