package com.model;

import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

/**
 * Estrategia que agrupa integrales "clásicas" como las de los ejemplos adjuntos
 * (producto con exponenciales o trigonométricas, radicales y fracciones
 * básicas). Cada plantilla define su integrando, su primitiva y una breve guía
 * de pasos para mostrar en la UI.
 */
public class IntegralClasica implements IntegralEstrategia {
    private static final Random RANDOM = new Random();

    private Template plantilla;

    @Override
    public void generarParametros(Dificultad dificultad) {
        plantilla = seleccionarPlantilla(dificultad);
    }

    @Override
    public double calcularResultado(double limiteInferior, double limiteSuperior) {
        if (plantilla == null) {
            throw new IllegalStateException("No hay plantilla configurada");
        }

        plantilla.validarDominio(limiteInferior, limiteSuperior);
        return plantilla.F.applyAsDouble(limiteSuperior) - plantilla.F.applyAsDouble(limiteInferior);
    }

    @Override
    public String getIntegrandoLatex() {
        return plantilla.latex;
    }

    @Override
    public double evaluarIntegrando(double x) {
        return plantilla.f.applyAsDouble(x);
    }

    @Override
    public List<String> getPasos() {
        return plantilla.pasos;
    }

    private Template seleccionarPlantilla(Dificultad dificultad) {
        Template[] candidatas = new Template[]{
                crearIntegralRadical(dificultad),
                crearFraccionCuadratica(dificultad),
                crearPolinomioExponencial(dificultad),
                crearPolinomioTrigonometrico(dificultad),
                crearInversaRaizCuadratica(dificultad)
        };

        return candidatas[RANDOM.nextInt(candidatas.length)];
    }

    /**
     * \int \sqrt{R^2 - x^2}\,dx
     */
    private Template crearIntegralRadical(Dificultad dificultad) {
        int radioBase = switch (dificultad) {
            case FACIL -> 6;
            case DIFICIL -> 10;
            default -> 8;
        };

        int radio = radioBase + RANDOM.nextInt(3); // Pequeña variación

        DoubleUnaryOperator integrando = x -> {
            double inside = radio * radio - x * x;
            if (inside < 0) return Double.NaN;
            return Math.sqrt(inside);
        };

        DoubleUnaryOperator primitiva = x -> {
            double inside = radio * radio - x * x;
            if (inside < 0) {
                throw new ArithmeticException("Fuera de dominio para la raíz");
            }
            return 0.5 * (x * Math.sqrt(inside) + radio * radio * Math.asin(x / radio));
        };

        List<String> pasos = List.of(
                "Usa la sustitución trigonométrica clásica: x = R\\sin(\\theta).",
                "Entonces dx = R\\cos(\\theta)\\, d\\theta y R^2 - x^2 = R^2\\cos^2(\\theta).",
                "La integral queda en términos de cosenos y senos, dando \\frac{1}{2}\\left(x\\sqrt{R^2 - x^2} + R^2 \\arcsin(\\tfrac{x}{R})\\right)."
        );

        String latex = String.format("\\sqrt{%d - x^{2}}", radio * radio);
        return new Template(integrando, primitiva, latex, pasos, (a, b) -> {
            double max = Math.max(Math.abs(a), Math.abs(b));
            if (max >= radio) {
                throw new ArithmeticException("Los límites salen del círculo de radio " + radio);
            }
        });
    }

    /**
     * \int \frac{ax + b}{x^2 + c} dx
     */
    private Template crearFraccionCuadratica(Dificultad dificultad) {
        int maxCoef = switch (dificultad) {
            case FACIL -> 4;
            case DIFICIL -> 10;
            default -> 6;
        };

        int a = RANDOM.nextInt(maxCoef) + 1;
        int b = RANDOM.nextInt(maxCoef * 2) + 1;
        int c = RANDOM.nextInt(maxCoef * 3) + maxCoef; // asegura c > 0

        DoubleUnaryOperator integrando = x -> (a * x + b) / (x * x + c);
        DoubleUnaryOperator primitiva = x -> (a / 2.0) * Math.log(x * x + c) + (b / Math.sqrt(c)) * Math.atan(x / Math.sqrt(c));

        List<String> pasos = List.of(
                "Separar en dos términos: \\frac{a x}{x^2 + c} + \\frac{b}{x^2 + c}.",
                "El primer término se resuelve con sustitución u = x^2 + c; el segundo con la forma estándar \\arctan(x/\\sqrt{c})."
        );

        String latex = String.format("\\frac{%dx + %d}{x^{2} + %d}", a, b, c);
        return new Template(integrando, primitiva, latex, pasos, (aLim, bLim) -> {
            double denomInf = aLim * aLim + c;
            double denomSup = bLim * bLim + c;
            if (denomInf <= 0 || denomSup <= 0) {
                throw new ArithmeticException("Denominador no positivo en los límites");
            }
        });
    }

    /**
     * \int x^2 e^{ax} dx
     */
    private Template crearPolinomioExponencial(Dificultad dificultad) {
        int a = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> RANDOM.nextInt(3) + 3; // [3,5]
            default -> RANDOM.nextInt(2) + 2; // [2,3]
        };

        DoubleUnaryOperator integrando = x -> x * x * Math.exp(a * x);
        DoubleUnaryOperator primitiva = x -> Math.exp(a * x) * (x * x / a - 2 * x / (a * a) + 2 / Math.pow(a, 3));

        List<String> pasos = List.of(
                "Aplicar integración por partes dos veces con u = x^2 y dv = e^{ax} dx.",
                "El resultado general es e^{ax} \\left(\\tfrac{x^2}{a} - \\tfrac{2x}{a^2} + \\tfrac{2}{a^3}\\right)."
        );

        String latex = String.format("x^{2} e^{%dx}", a);
        return new Template(integrando, primitiva, latex, pasos, (aLim, bLim) -> {
            // Sin restricciones de dominio
        });
    }

    /**
     * \int x^2 \cos(kx) dx
     */
    private Template crearPolinomioTrigonometrico(Dificultad dificultad) {
        int k = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> RANDOM.nextInt(3) + 3; // [3,5]
            default -> RANDOM.nextInt(2) + 2; // [2,3]
        };

        DoubleUnaryOperator integrando = x -> x * x * Math.cos(k * x);
        DoubleUnaryOperator primitiva = x -> (x * x * Math.sin(k * x)) / k + (2 * x * Math.cos(k * x)) / (k * k) - (2 * Math.sin(k * x)) / Math.pow(k, 3);

        List<String> pasos = List.of(
                "Integrar por partes con u = x^2 y dv = \\cos(kx)dx.",
                "La segunda integración por partes sobre \\int x \\sin(kx) dx da el término final.",
                "Combinando: \\tfrac{x^2\\sin(kx)}{k} + \\tfrac{2x\\cos(kx)}{k^2} - \\tfrac{2\\sin(kx)}{k^3}."
        );

        String latex = String.format("x^{2} \\cos(%dx)", k);
        return new Template(integrando, primitiva, latex, pasos, (aLim, bLim) -> {
            // Sin restricciones
        });
    }

    /**
     * \int \frac{1}{\sqrt{a + bx^2}} dx
     */
    private Template crearInversaRaizCuadratica(Dificultad dificultad) {
        int a = switch (dificultad) {
            case FACIL -> RANDOM.nextInt(3) + 6; // [6,8]
            case DIFICIL -> RANDOM.nextInt(5) + 4; // [4,8]
            default -> RANDOM.nextInt(3) + 5; // [5,7]
        };

        int b = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> RANDOM.nextInt(3) + 2; // [2,4]
            default -> RANDOM.nextInt(2) + 1; // [1,2]
        };

        DoubleUnaryOperator integrando = x -> 1.0 / Math.sqrt(a + b * x * x);
        DoubleUnaryOperator primitiva = x -> {
            double factor = Math.sqrt((double) b / a);
            return Math.asinh(x * factor) / Math.sqrt(b);
        };

        List<String> pasos = List.of(
                "Completa cuadrado: a + bx^2 = a\\left(1 + \\tfrac{b}{a}x^2\\right).",
                "Sustituye x = \\sqrt{a/b}\\,\\sinh(u) para obtener una integral elemental.",
                "La primitiva es \\tfrac{1}{\\sqrt{b}}\\,\\operatorname{arcsinh}\\left(x\\sqrt{\\tfrac{b}{a}}\\right)."
        );

        String latex = String.format("\\frac{1}{\\sqrt{%d + %dx^{2}}}", a, b);
        return new Template(integrando, primitiva, latex, pasos, (aLim, bLim) -> {
            double valorInf = a + b * aLim * aLim;
            double valorSup = a + b * bLim * bLim;
            if (valorInf <= 0 || valorSup <= 0) {
                throw new ArithmeticException("Expresión bajo la raíz no positiva");
            }
        });
    }

    private static class Template {
        private final DoubleUnaryOperator f;
        private final DoubleUnaryOperator F;
        private final String latex;
        private final List<String> pasos;
        private final DominioChecker dominioChecker;

        private Template(DoubleUnaryOperator f, DoubleUnaryOperator F, String latex, List<String> pasos, DominioChecker dominioChecker) {
            this.f = f;
            this.F = F;
            this.latex = latex;
            this.pasos = pasos;
            this.dominioChecker = dominioChecker;
        }

        private void validarDominio(double a, double b) {
            dominioChecker.validar(a, b);
        }
    }

    @FunctionalInterface
    private interface DominioChecker {
        void validar(double a, double b);
    }
}
