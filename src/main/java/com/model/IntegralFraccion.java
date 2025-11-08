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
    public double calcularResultado(double limInf, double limiteSup) {
        return F(limiteSup) - F(limInf);
    }

    private double F(double x) {
        double k1 = (double) a / c;
        double k2 = (double) (b * c - a * d) / (c * c);
        return k1 * x + k2 * Math.log(Math.abs(c * x + d));
    }

    @Override
    public String getIntegrandoLatex() {
        return String.format("\\frac{%dx + %d}{%dx + %d}", a, b, c, d);
    }

    @Override
    public String getPasos() {
        return """
                Se reescribe:
                
                (ax + b)/(cx + d)
                = (a/c) + (bc - ad)/(c^2 (cx + d))
                
                Entonces:
                
                ∫(ax + b)/(cx + d) dx
                = (a/c)x + (bc - ad)/(c^2) ln|cx + d| + C
                """;
    }
}
