package com.model;

public class IntegralConfig {
    private String tipo;
    private double limiteInferior;
    private double limiteSuperior;
    private boolean mostrarPasos;
    private boolean limitesAleatorios;
    private Dificultad dificultad;
    private int cantidadOpciones;

    public IntegralConfig(String tipo, double limiteInferior, double limiteSuperior, boolean mostrarPasos,
                          boolean limitesAleatorios, Dificultad dificultad, int cantidadOpciones) {
        this.tipo = tipo;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.mostrarPasos = mostrarPasos;
        this.limitesAleatorios = limitesAleatorios;
        this.dificultad = dificultad;
        this.cantidadOpciones = cantidadOpciones;
    }

    public static IntegralConfig predeterminada() {
        return new IntegralConfig("aleatoria", 0.0, 1.0, false, true, Dificultad.MEDIA, 5);
    }

    public IntegralConfig copiar() {
        return new IntegralConfig(tipo, limiteInferior, limiteSuperior, mostrarPasos, limitesAleatorios, dificultad, cantidadOpciones);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(double limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public double getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(double limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public boolean isMostrarPasos() {
        return mostrarPasos;
    }

    public void setMostrarPasos(boolean mostrarPasos) {
        this.mostrarPasos = mostrarPasos;
    }

    public boolean isLimitesAleatorios() {
        return limitesAleatorios;
    }

    public void setLimitesAleatorios(boolean limitesAleatorios) {
        this.limitesAleatorios = limitesAleatorios;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public int getCantidadOpciones() {
        return cantidadOpciones;
    }

    public void setCantidadOpciones(int cantidadOpciones) {
        this.cantidadOpciones = cantidadOpciones;
    }
}
