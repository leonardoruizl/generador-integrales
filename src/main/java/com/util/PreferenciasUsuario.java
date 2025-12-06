package com.util;

import com.model.Dificultad;
import com.model.IntegralConfig;

import java.util.prefs.Preferences;

public class PreferenciasUsuario {
    private static final Preferences PREFS = Preferences.userNodeForPackage(PreferenciasUsuario.class);
    private static final String KEY_TIPO = "tipo";
    private static final String KEY_LIM_INF = "limiteInferior";
    private static final String KEY_LIM_SUP = "limiteSuperior";
    private static final String KEY_MOSTRAR_PASOS = "mostrarPasos";
    private static final String KEY_LIM_ALEATORIOS = "limitesAleatorios";
    private static final String KEY_DIFICULTAD = "dificultad";
    private static final String KEY_CANTIDAD_OPCIONES = "cantidadOpciones";

    private PreferenciasUsuario() {
    }

    public static IntegralConfig cargarConfig() {
        IntegralConfig base = IntegralConfig.predeterminada();
        IntegralConfig config = base.copiar();

        config.setTipo(PREFS.get(KEY_TIPO, base.getTipo()));
        config.setLimiteInferior(PREFS.getDouble(KEY_LIM_INF, base.getLimiteInferior()));
        config.setLimiteSuperior(PREFS.getDouble(KEY_LIM_SUP, base.getLimiteSuperior()));
        config.setMostrarPasos(PREFS.getBoolean(KEY_MOSTRAR_PASOS, base.isMostrarPasos()));
        config.setLimitesAleatorios(PREFS.getBoolean(KEY_LIM_ALEATORIOS, base.isLimitesAleatorios()));
        config.setCantidadOpciones(validarCantidadOpciones(PREFS.getInt(KEY_CANTIDAD_OPCIONES, base.getCantidadOpciones())));

        String dificultadGuardada = PREFS.get(KEY_DIFICULTAD, base.getDificultad().name());
        try {
            config.setDificultad(Dificultad.valueOf(dificultadGuardada));
        } catch (IllegalArgumentException ex) {
            config.setDificultad(base.getDificultad());
        }

        return config;
    }

    public static void guardarConfig(IntegralConfig config) {
        PREFS.put(KEY_TIPO, config.getTipo());
        PREFS.putDouble(KEY_LIM_INF, config.getLimiteInferior());
        PREFS.putDouble(KEY_LIM_SUP, config.getLimiteSuperior());
        PREFS.putBoolean(KEY_MOSTRAR_PASOS, config.isMostrarPasos());
        PREFS.putBoolean(KEY_LIM_ALEATORIOS, config.isLimitesAleatorios());
        PREFS.put(KEY_DIFICULTAD, config.getDificultad().name());
        PREFS.putInt(KEY_CANTIDAD_OPCIONES, validarCantidadOpciones(config.getCantidadOpciones()));
    }

    private static int validarCantidadOpciones(int valor) {
        int minimo = 3;
        int maximo = 8;
        if (valor < minimo) return minimo;
        return Math.min(valor, maximo);
    }
}
