package com.model;

public interface IntegralEstrategia {
    /** Genera parámetros aleatorios internos */
    void generarParametros();

    /** Calcula el resultado definido */
    double calcularResultado(int a, int b);

    /** Genera representación LaTeX */
    String getLatex();

    /** Pasos de resolución (si aplica) */
    String getPasos();
}
