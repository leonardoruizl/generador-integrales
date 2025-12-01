package com.model;

import java.util.List;

import com.model.Dificultad;

public interface IntegralEstrategia {
    /** Genera parámetros aleatorios internos */
    void generarParametros(Dificultad dificultad);

    /** Calcula el resultado definido */
    double calcularResultado(double limiteInferior, double limiteSuperior);

    /** Genera representación LaTeX */
    String getIntegrandoLatex();

    /** Evalúa el integrando en el punto dado */
    double evaluarIntegrando(double x);

    /** Pasos de resolución (si aplica) */
    List<String> getPasos();
}
