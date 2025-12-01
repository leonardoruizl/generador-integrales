package com.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.model.Dificultad;

public class Integral {
    private final IntegralEstrategia estrategia;
    private final double limiteInferior, limiteSuperior;
    private final Dificultad dificultad;

    private double resultado;
    private double[] opciones;
    private int opcionCorrecta;
    private String latex;

    public Integral(String tipo, double limiteInf, double limiteSup, Dificultad dificultad) {
        this.limiteInferior = limiteInf;
        this.limiteSuperior = limiteSup;
        this.dificultad = dificultad == null ? Dificultad.MEDIA : dificultad;

        this.estrategia = crearEstrategia(tipo);
        estrategia.generarParametros(this.dificultad);

        resolver();
        generarOpciones();
    }

    private IntegralEstrategia crearEstrategia(String tipo) {
        Objects.requireNonNull(tipo, "tipo no puede ser null");
        String key = tipo.toLowerCase(Locale.ROOT);

        return switch (key) {
            case "raiz" -> new IntegralRaiz();
            case "fraccion" -> new IntegralFraccion();
            case "trig" -> new IntegralTrig();

            case "aleatoria" -> {
                String[] pool = {"raiz", "fraccion", "trig"};
                yield crearEstrategia(pool[new Random().nextInt(pool.length)]);
            }

            default -> throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        };
    }

    private void resolver() {
        final int maxIntentos = 3;
        for (int i = 0; i < maxIntentos; i++) {
            try {
                resultado = estrategia.calcularResultado(limiteInferior, limiteSuperior);
                break;
            } catch (ArithmeticException e) {
                estrategia.generarParametros(dificultad);
                if (i == maxIntentos - 1) {
                    throw new IllegalStateException("No se pudo generar una integral válida tras varios intentos", e);
                }
            }
        }

        latex = String.format(
                "\\int_{%s}^{%s} %s \\, dx",
                fmt(limiteInferior),
                fmt(limiteSuperior),
                estrategia.getIntegrandoLatex()
        );
    }

    private String fmt(double x) {
        if (x == (long) x) return String.format("%d", (long) x);
        return String.valueOf(x);
    }

    private void generarOpciones() {
        opciones = new double[5];
        Random r = new Random();
        opcionCorrecta = r.nextInt(opciones.length);

        // Resultado pequeño → evita ruido numérico
        double base = Math.abs(resultado) < 1e-5 ? 0.0 : resultado;

        Set<BigDecimal> valoresUnicos = new HashSet<>();
        valoresUnicos.add(redondearCincoDecimales(base));

        // Asignar la opción correcta
        opciones[opcionCorrecta] = base;
        resultado = base;

        // Generar opciones cercanas
        for (int i = 0; i < opciones.length; i++) {
            if (i == opcionCorrecta) {
                continue;
            }

            opciones[i] = generarOpcionUnica(base, r, valoresUnicos);
        }
    }

    private double variacion(double base, Random r) {
        if (base == 0) {
            return (r.nextDouble() - 0.5) * 0.2;
        }
        // Variación hasta ±30% aprox
        double factor = 0.3 * (r.nextDouble() - 0.5);
        return base * factor;
    }

    private double generarOpcionUnica(double base, Random random, Set<BigDecimal> valoresUnicos) {
        while (true) {
            double candidato = base + variacion(base, random);
            BigDecimal clave = redondearCincoDecimales(candidato);
            if (valoresUnicos.add(clave)) {
                return candidato;
            }
        }
    }

    private BigDecimal redondearCincoDecimales(double valor) {
        return BigDecimal.valueOf(valor).setScale(5, RoundingMode.HALF_UP);
    }

    public double[] getOpciones() {
        return opciones;
    }

    public int getOpcionCorrecta() {
        return opcionCorrecta;
    }

    public double getResultado() {
        return resultado;
    }

    public String getLatex() {
        return latex;
    }

    public List<String> getPasos() {
        return estrategia.getPasos();
    }

    public double evaluarIntegrando(double x) {
        return estrategia.evaluarIntegrando(x);
    }

    public double getLimiteInferior() {
        return limiteInferior;
    }

    public double getLimiteSuperior() {
        return limiteSuperior;
    }
}
