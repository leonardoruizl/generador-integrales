package com.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pequeño runner independiente de JUnit para validar que las opciones generadas
 * sean únicas al redondear a 5 decimales. Útil cuando no se dispone de las
 * dependencias de prueba en el entorno.
 */
public class ManualIntegralTest {
    public static void main(String[] args) {
        IntegralGenerator generator = new IntegralGenerator();
        Integral integral = generator.crearIntegral("raiz", 0, 1, Dificultad.MEDIA, 4);

        double[] opciones = integral.getOpciones();
        Set<BigDecimal> opcionesRedondeadas = Arrays.stream(opciones)
                .mapToObj(valor -> BigDecimal.valueOf(valor).setScale(5, RoundingMode.HALF_UP))
                .collect(Collectors.toSet());

        if (opcionesRedondeadas.size() != opciones.length) {
            throw new AssertionError("Las opciones deberían ser únicas al redondear a 5 decimales");
        }

        System.out.println("Prueba manual superada: las opciones son únicas al redondear a 5 decimales.");
    }
}
