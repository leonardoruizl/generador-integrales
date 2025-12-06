package com.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class OpcionGenerator {
    private final Random random;

    public  record OpcionesGeneradas(double[] opciones, int opcionCorrecta, double resultadoNormalizado) {}

    public OpcionGenerator() {
        this(new Random());
    }

    public OpcionGenerator(Random random) {
        this.random = random;
    }

    public OpcionesGeneradas generarOpciones(double resultado, int cantidadOpciones) {
        double[] opciones = new double[cantidadOpciones];
        int opcionCorrecta = random.nextInt(opciones.length);

        double base = Math.abs(resultado) < 1e-5 ? 0.0 : resultado;

        Set<BigDecimal> valoresUnicos = new HashSet<>();
        valoresUnicos.add(redondearCincoDecimales(base));

        opciones[opcionCorrecta] = base;

        for (int i = 0; i < opciones.length; i++) {
            if (i == opcionCorrecta) {
                continue;
            }
            opciones[i] = generarOpcionUnica(base, valoresUnicos);
        }

        return new OpcionesGeneradas(opciones, opcionCorrecta, base);
    }

    private double generarOpcionUnica(double base, Set<BigDecimal> valoresUnicos) {
        while (true) {
            double candidato = base + variacion(base);
            BigDecimal clave = redondearCincoDecimales(candidato);
            if (valoresUnicos.add(clave)) {
                return candidato;
            }
        }
    }

    private double variacion(double base) {
        if (base == 0) {
            return (random.nextDouble() - 0.5) * 0.2;
        }
        double factor = 0.3 * (random.nextDouble() - 0.5);
        return base * factor;
    }

    private BigDecimal redondearCincoDecimales(double valor) {
        return BigDecimal.valueOf(valor).setScale(5, RoundingMode.HALF_UP);
    }
}
