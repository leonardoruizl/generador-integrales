package com.model;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class EstrategiaFactory {
    private final Random random;

    public EstrategiaFactory() {
        this(new Random());
    }

    public EstrategiaFactory(Random random) {
        this.random = random;
    }

    public IntegralEstrategia crear(String tipo) {
        Objects.requireNonNull(tipo, "tipo no puede ser null");
        String key = tipo.toLowerCase(Locale.ROOT);

        return switch (key) {
            case "raiz" -> new IntegralRaiz();
            case "fraccion" -> new IntegralFraccion();
            case "trig" -> new IntegralTrig();
            case "clasica" -> new IntegralClasica();
            case "aleatoria" -> crear(tipoAleatorio());
            default -> throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        };
    }

    private String tipoAleatorio() {
        String[] pool = {"raiz", "fraccion", "trig", "clasica"};
        return pool[random.nextInt(pool.length)];
    }
}
