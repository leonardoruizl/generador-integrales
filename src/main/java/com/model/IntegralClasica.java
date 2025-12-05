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
                crearInversaRaizCuadratica(dificultad),
                crearPotenciaConDerivada(dificultad),
                crearRacionalDerivada(dificultad)
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
                "Sustitución trigonométrica para raíces cuadráticas: x = R\\sin(\\theta).",
                "Entonces dx = R\\cos(\\theta)\\, d\\theta y R^2 - x^2 = R^2\\cos^2(\\theta).",
                "Integrando en \\theta se obtiene por tablas el resultado \\frac{1}{2}\\left(x\\sqrt{R^2 - x^2} + R^2 \\arcsin(\\tfrac{x}{R})\\right)."
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
                "Fracciones parciales: separar \\frac{ax + b}{x^2 + c} en un término lineal y otro racional.",
                "Cambio de variable d/du en el término \\frac{a x}{x^2 + c} con u = x^2 + c.",
                "El segundo término coincide con la entrada de tablas \\int \\frac{1}{x^2 + c} dx = \\frac{1}{\\sqrt{c}} \\arctan(x/\\sqrt{c})."
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
        int grado = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> 3;
            default -> 2;
        };

        int a = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> RANDOM.nextInt(3) + 3; // [3,5]
            default -> RANDOM.nextInt(2) + 2; // [2,3]
        };

        int desplazamiento = dificultad == Dificultad.FACIL ? 0 : RANDOM.nextInt(3);
        double[] coeficientes = generarPolinomio(grado);

        DoubleUnaryOperator integrando = x -> evaluarPolinomio(coeficientes, x) * Math.exp(a * x + desplazamiento);
        DoubleUnaryOperator primitiva = x -> integrarPolinomioPorExponencial(coeficientes, a, desplazamiento, x);

        List<String> pasos = List.of(
                "Integración por partes tabular para productos P(x)e^{ax}.",
                "Deriva el polinomio hasta anularlo y alterna signos multiplicando por potencias de a^{-1}.",
                "El resultado general es e^{ax+b}\\sum_{k=0}^{n}(-1)^k \\frac{P^{(k)}(x)}{a^{k+1}}."
        );

        String latex = String.format("(%s) e^{%dx%+d}", formatearPolinomio(coeficientes), a, desplazamiento);
        return new Template(integrando, primitiva, latex, pasos, (aLim, bLim) -> {
            // Sin restricciones de dominio
        });
    }

    /**
     * \int x^2 \cos(kx) dx
     */
    private Template crearPolinomioTrigonometrico(Dificultad dificultad) {
        int grado = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> 3;
            default -> 2;
        };

        int k = switch (dificultad) {
            case FACIL -> 1;
            case DIFICIL -> RANDOM.nextInt(3) + 3; // [3,5]
            default -> RANDOM.nextInt(2) + 2; // [2,3]
        };

        boolean usaSeno = RANDOM.nextBoolean();
        double[] coeficientes = generarPolinomio(grado);

        DoubleUnaryOperator integrando = x -> evaluarPolinomio(coeficientes, x) * (usaSeno ? Math.sin(k * x) : Math.cos(k * x));
        DoubleUnaryOperator primitiva = x -> integrarPolinomioPorTrigonometrica(coeficientes, k, x, usaSeno);

        List<String> pasos = List.of(
                "Integración por partes tabular sobre P(x)\\sin(kx) o P(x)\\cos(kx).",
                "Deriva el polinomio y alterna seno/coseno multiplicando por potencias de k^{-1}.",
                "El esquema coincide con los ejemplos con potencias de x junto a senos o cosenos (método del cacahuate)."
        );

        String latex = String.format("(%s) \\%s(%dx)", formatearPolinomio(coeficientes), usaSeno ? "sin" : "cos", k);
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
            return asinh(x * factor) / Math.sqrt(b);
        };

        List<String> pasos = List.of(
                "Sustitución trigonométrica (forma hiperbólica) para integrales tipo \\frac{1}{\\sqrt{a + bx^2}}.",
                "Completa cuadrado: a + bx^2 = a\\left(1 + \\tfrac{b}{a}x^2\\right) y toma x = \\sqrt{a/b}\\,\\sinh(u).",
                "Usa la entrada de tablas de \\int \\frac{1}{\\sqrt{1 + t^2}} dt para obtener \\tfrac{1}{\\sqrt{b}}\\,\\operatorname{arcsinh}\\left(x\\sqrt{\\tfrac{b}{a}}\\right)."
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

    /**
     * \int k x^{p-1} (ax^{p} + b)^{n} dx
     * Ejemplos: x^2(3x^3-2)^3 o potencias trigonométricas tipo (sen(2x))^m cos(2x).
     */
    private Template crearPotenciaConDerivada(Dificultad dificultad) {
        int p = dificultad == Dificultad.FACIL ? 2 : RANDOM.nextInt(2) + 2; // 2 o 3
        int a = RANDOM.nextInt(3) + 2; // 2..4
        int b = RANDOM.nextInt(4) - 2; // -2..1
        int n = dificultad == Dificultad.DIFICIL ? RANDOM.nextInt(3) + 2 : RANDOM.nextInt(2) + 1; // 1..3 o 2..4

        double factor = dificultad == Dificultad.FACIL ? 1.0 : (RANDOM.nextBoolean() ? a * p : 1.0);

        DoubleUnaryOperator integrando = x -> factor * Math.pow(x, p - 1) * Math.pow(a * Math.pow(x, p) + b, n);
        DoubleUnaryOperator primitiva = x -> factor / (a * p * (n + 1)) * Math.pow(a * Math.pow(x, p) + b, n + 1);

        List<String> pasos = List.of(
                String.format("Sustitución directa u = %dx^{%d} %+d.", a, p, b),
                String.format("Entonces du = %d x^{%d} dx y la integral queda proporcional a u^{%d}.", a * p, p - 1, n),
                "Se obtiene u^{n+1}/[(n+1)ap] evaluado en los límites."
        );

        String latex = String.format("%s x^{%d}(%dx^{%d}%+d)^{%d}",
                factor == 1.0 ? "" : String.format("%.0f", factor), p - 1, a, p, b, n);
        return new Template(integrando, primitiva, latex.trim(), pasos, (aLim, bLim) -> {
            // sin restricciones adicionales
        });
    }

    /**
     * \int c (2ax + b) / (ax^2 + bx + c)^n dx —
     * cubre ejemplos tipo \int 2x/(7x^2-2)^2 dx o variantes con potencias > 1.
     */
    private Template crearRacionalDerivada(Dificultad dificultad) {
        int a = RANDOM.nextInt(4) + 2; // 2..5
        int b = RANDOM.nextInt(5) - 2; // -2..2
        int c = RANDOM.nextInt(4) + 3; // 3..6
        int potencia = dificultad == Dificultad.FACIL ? 2 : RANDOM.nextInt(2) + 2; // 2 o 3

        double factor = dificultad == Dificultad.DIFICIL ? RANDOM.nextInt(3) + 1 : 1.0;

        DoubleUnaryOperator integrando = x -> factor * (2 * a * x + b) / Math.pow(a * x * x + b * x + c, potencia);
        DoubleUnaryOperator primitiva = x -> factor / (1 - potencia) * 1.0 / Math.pow(a * x * x + b * x + c, potencia - 1);

        List<String> pasos = List.of(
                "Reconocer la derivada del denominador: d/dx(ax^2 + bx + c) = 2ax + b.",
                "Se usa u = ax^2 + bx + c ⇒ du = (2ax + b) dx.",
                String.format("La integral queda c\\int u^{-%d} du = \\tfrac{c}{1-%d} u^{1-%d}.", potencia, potencia, potencia)
        );

        String factorLatex = factor == 1.0 ? "" : String.format("%.0f\\,", factor);
        String latex = String.format("\\frac{%s(2 %dx %+d)}{(%dx^{2} %+d x %+d)^{%d}}",
                factorLatex, a, b, a, b, c, potencia);

        return new Template(integrando, primitiva, latex, pasos, (aLim, bLim) -> {
            double denomInf = a * aLim * aLim + b * aLim + c;
            double denomSup = a * bLim * bLim + b * bLim + c;
            if (denomInf == 0 || denomSup == 0) {
                throw new ArithmeticException("Denominador nulo en los límites");
            }
        });
    }

    private double[] generarPolinomio(int grado) {
        double[] coef = new double[grado + 1];
        for (int i = 0; i <= grado; i++) {
            int valor = RANDOM.nextInt(4) + 1; // 1..4
            if (i != grado && RANDOM.nextBoolean()) {
                valor *= -1;
            }
            coef[i] = valor;
        }
        return coef;
    }

    private double evaluarPolinomio(double[] coef, double x) {
        double suma = 0;
        double potencia = 1;
        for (double v : coef) {
            suma += v * potencia;
            potencia *= x;
        }
        return suma;
    }

    private double integrarPolinomioPorExponencial(double[] coef, double a, double desplazamiento, double x) {
        double suma = 0;
        double[] derivada = coef.clone();

        for (int k = 0; k < coef.length; k++) {
            double valorPoli = evaluarPolinomio(derivada, x);
            double termino = Math.pow(-1, k) * valorPoli / Math.pow(a, k + 1);
            suma += termino;
            derivada = derivar(derivada);
            if (derivada.length == 0) {
                break;
            }
        }

        return Math.exp(a * x + desplazamiento) * suma;
    }

    private double integrarPolinomioPorTrigonometrica(double[] coef, double k, double x, boolean conSeno) {
        double suma = 0;
        double[] derivada = coef.clone();

        for (int j = 0; j < coef.length; j++) {
            double valorPoli = evaluarPolinomio(derivada, x);
            double factor = Math.pow(-1, conSeno ? j + 1 : j) / Math.pow(k, j + 1);

            double trig = obtenerTrigonometrica(k, x, conSeno, j);
            suma += factor * valorPoli * trig;

            derivada = derivar(derivada);
            if (derivada.length == 0) {
                break;
            }
        }

        return suma;
    }

    private double obtenerTrigonometrica(double k, double x, boolean integrandoEraSeno, int orden) {
        boolean usarSeno = integrandoEraSeno ? orden % 2 == 1 : orden % 2 == 0;
        return usarSeno ? Math.sin(k * x) : Math.cos(k * x);
    }

    private double[] derivar(double[] coef) {
        if (coef.length <= 1) {
            return new double[0];
        }

        double[] derivada = new double[coef.length - 1];
        for (int i = 1; i < coef.length; i++) {
            derivada[i - 1] = coef[i] * i;
        }
        return derivada;
    }

    private String formatearPolinomio(double[] coef) {
        StringBuilder sb = new StringBuilder();
        for (int i = coef.length - 1; i >= 0; i--) {
            double c = coef[i];
            if (c == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(c > 0 ? " + " : " - ");
            } else if (c < 0) {
                sb.append("-");
            }

            double abs = Math.abs(c);
            if (!(abs == 1 && i > 0)) {
                sb.append((int) abs);
            }

            if (i >= 1) {
                sb.append("x");
                if (i > 1) {
                    sb.append("^{").append(i).append("}");
                }
            }
        }
        return sb.toString();
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

    /**
     * Implementación independiente de {@code asinh} para entornos donde no esté disponible en {@link Math}.
     */
    private static double asinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    @FunctionalInterface
    private interface DominioChecker {
        void validar(double a, double b);
    }
}
