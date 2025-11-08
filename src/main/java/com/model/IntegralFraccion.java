package com.model;

import java.util.Random;

public class IntegralFraccion implements IntegralEstrategia {
    private int a, b, c, d;

    @Override
    public void generarParametros() {
        Random r = new Random();
        a = r.nextInt(5) + 1;
        b = r.nextInt(5) + 1;
        c = r.nextInt(5) + 1;
        d = r.nextInt(5) + 1;
    }

    @Override
    public double calcularResultado(double limiteInferior, double limiteSuperior) {
        return F(limiteSuperior) - F(limiteInferior);
    }

    private double F(double x) {
        double term1 = (double) a / c * x;
        double term2 = (double) (b * c - a * d) / (c * c) * Math.log(Math.abs(c * x + d));
        return term1 + term2;
    }

    @Override
    public String getIntegrandoLatex() {
        return "\\frac{" + a + "x + " + b + "}{" + c + "x + " + d + "}";
    }


    @Override
    public String getPasos() {
        return """
                Separación en suma:
                (ax + b)/(cx + d) = (a/c) + (bc - ad)/(c^2(cx+d))
                Se integra:
                ∫(a/c) dx + ∫((bc-ad)/(c^2))/ (cx + d) dx
                """;
    }
}
