package com.model;

import java.util.Random;

public class Integral {
    private IntegralEstrategia estrategia;
    private int limiteInferior, limiteSuperior;
    private boolean mostrarPasos;

    private double resultado;
    private double[] opciones;
    private int opcionCorrecta;

    private String latex;
    private String pasos;

    public Integral(String tipo, int limiteInferior, int limiteSuperior, boolean pasos) {
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.mostrarPasos = pasos;

        seleccionarEstrategia(tipo);
        estrategia.generarParametros();
        resolver();
        generarOpciones();
    }

    private void seleccionarEstrategia(String tipo) {
        Random r = new Random();

        switch (tipo) {
            case "raiz":
                estrategia = new IntegralRaiz();
                break;
            case "fraccion":
                estrategia = new IntegralFraccion();
                break;
            case "trig":
                estrategia = new IntegralTrig();
                break;
            case "aleatoria":
                // Elegir un tipo al azar
                String[] tipos = {"raiz", "fraccion", "trig"};
                estrategia = switch (tipos[r.nextInt(tipos.length)]) {
                    case "raiz" -> new IntegralRaiz();
                    case "fraccion" -> new IntegralFraccion();
                    default -> new IntegralTrig();
                };
                break;
            default:
                throw new IllegalArgumentException("Tipo desconocido");
        }
    }

    private void resolver() {
        resultado = estrategia.calcularResultado(limiteInferior, limiteSuperior);
        latex = estrategia.getLatex();
        pasos = mostrarPasos ? estrategia.getPasos() : "";
    }

    // Genera 5 opciones distintas (una correcta)
    private void generarOpciones() {
        opciones = new double[5];
        Random rand = new Random();
        opcionCorrecta = rand.nextInt(5);

        // Evitar cero absoluto
        if (Math.abs(resultado) < 0.00001) {
            resultado = 0;

            for (int i = 0; i < opciones.length; i++) {
                opciones[i] = (Math.random() - 0.5) * 0.2; // ±0.1
            }
            opciones[opcionCorrecta] = resultado;
            return;
        }

        // Índice de las opciones incorrectas
        int[] indices = {0, 1, 2, 3, 4};
        // Sacar el índice de la opción correcta
        int temp = indices[opcionCorrecta];
        indices[opcionCorrecta] = indices[4];
        indices[4] = temp;

        // Generar 3 opciones cercanas con errores comunes
        for (int i = 0; i < 3; i++) {
            int idx = indices[i];
            double variacion = 0;

            int tipoError = rand.nextInt(3);
            switch (tipoError) {
                case 0:
                    // signo equivocado
                    variacion = -resultado * (rand.nextDouble() * 0.1); // hasta ±10%
                    break;
                case 1:
                    // factor multiplicativo olvidado
                    variacion = resultado * (0.1 + rand.nextDouble() * 0.2); // +10% a +30%
                    if (rand.nextBoolean()) variacion *= -1;
                    break;
                case 2:
                    // redondeo parcial o límite mal usado
                    variacion = (rand.nextDouble() - 0.5) * 0.2 * Math.abs(resultado); // ±10%
                    break;
            }
            opciones[idx] = resultado + variacion;
        }

        // Generar 2 opciones lejanas
        for (int i = 3; i < 5; i++) {
            int idx = indices[i];
            double variacion = (rand.nextDouble() - 0.5) * 2 * Math.abs(resultado); // ±100%
            // Aumentamos la diferencia si accidentalmente se acerca al resultado
            if (Math.abs(variacion) < 0.3 * Math.abs(resultado)) {
                variacion += (variacion > 0 ? 0.3 : -0.3) * Math.abs(resultado);
            }
            opciones[idx] = resultado + variacion;
        }

        // Asignar la opción correcta
        opciones[opcionCorrecta] = resultado;
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

    public String getPasos() {
        return pasos;
    }
}
