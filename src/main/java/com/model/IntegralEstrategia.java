package com.model;

public interface IntegralEstrategia {
    /** Genera parámetros aleatorios internos */
    void generarParametros();

    /** Calcula el resultado definido */
    double calcularResultado(double limiteInferior, double limiteSuperior);

    /** Genera representación LaTeX */
    String getIntegrandoLatex();

    /** Pasos de resolución (si aplica) */
    String getPasos();
}
