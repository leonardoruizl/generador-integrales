package com.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegralTest {

    @Test
    void opcionesRedondeadasSonUnicas() {
        IntegralGenerator generator = new IntegralGenerator();
        Integral integral = generator.crearIntegral("raiz", 0, 1, Dificultad.MEDIA, 4);

        double[] opciones = integral.getOpciones();

        Set<BigDecimal> opcionesRedondeadas = Arrays.stream(opciones)
                .mapToObj(valor -> BigDecimal.valueOf(valor).setScale(5, RoundingMode.HALF_UP))
                .collect(Collectors.toSet());

        assertEquals(opciones.length, opcionesRedondeadas.size(),
                "Las opciones deberían ser únicas al redondear a 5 decimales");
    }
}
