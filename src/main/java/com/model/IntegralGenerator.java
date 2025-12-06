package com.model;

public class IntegralGenerator {
    private final EstrategiaFactory estrategiaFactory;
    private final OpcionGenerator opcionGenerator;

    public IntegralGenerator() {
        this(new EstrategiaFactory(), new OpcionGenerator());
    }

    public IntegralGenerator(EstrategiaFactory estrategiaFactory, OpcionGenerator opcionGenerator) {
        this.estrategiaFactory = estrategiaFactory;
        this.opcionGenerator = opcionGenerator;
    }

    public Integral crearIntegral(String tipo, double limiteInferior, double limiteSuperior, Dificultad dificultad, int cantidadOpciones) {
        IntegralEstrategia estrategia = estrategiaFactory.crear(tipo);
        return new Integral(estrategia, limiteInferior, limiteSuperior, dificultad, opcionGenerator, cantidadOpciones);
    }
}
