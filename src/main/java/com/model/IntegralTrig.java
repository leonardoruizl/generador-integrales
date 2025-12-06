package com.model;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.model.Dificultad;
import com.model.MetodoResolucion;

public class IntegralTrig implements IntegralEstrategia {
    private static final Random RAND = new Random();
    private int k;
    private int amplitud;
    private double fase;
    private boolean usaCoseno;

    @Override
    public void generarParametros(Dificultad dificultad) {
        int rangoMax = switch (dificultad) {
            case FACIL -> 3;
            case DIFICIL -> 10;
            default -> 6;
        };

        k = RAND.nextInt(rangoMax) + 1;
        amplitud = RAND.nextInt(rangoMax) + 1;

        // Desplazamiento de fase en cuartos de pi para evitar equivalencias triviales
        int pasosFase = dificultad == Dificultad.FACIL ? 4 : 8;
        fase = RAND.nextInt(pasosFase) * (Math.PI / 4.0);

        // Alternar seno/coseno para ampliar el catálogo de integrales trigonométricas
        usaCoseno = RAND.nextBoolean();
    }

    @Override
    public double calcularResultado(double limiteInf, double limiteSup) {
        return F(limiteSup) - F(limiteInf);
    }

    /**
     * F(x) =
     *  - A/k * cos(kx + fase)   si integrando es A * sin(kx + fase)
     *    A/k * sin(kx + fase)   si integrando es A * cos(kx + fase)
     */
    private double F(double x) {
        double argumento = k * x + fase;
        return usaCoseno
                ? (amplitud / (double) k) * Math.sin(argumento)
                : -(amplitud / (double) k) * Math.cos(argumento);
    }

    @Override
    public String getIntegrandoLatex() {
        String funcion = usaCoseno ? "cos" : "sin";
        String faseLatex = fase == 0 ? "" : String.format(Locale.US, "%+.2f", fase);
        return String.format(Locale.US, "%s\\%s(%dx%s)",
                amplitud == 1 ? "" : amplitud, funcion, k, faseLatex);
    }

    @Override
    public double evaluarIntegrando(double x) {
        double argumento = k * x + fase;
        return amplitud * (usaCoseno ? Math.cos(argumento) : Math.sin(argumento));
    }

    @Override
    public List<String> getPasos() {
        return List.of(
                "Cambio de variable d y du para simplificar el argumento trigonométrico.",
                String.format(Locale.US, "Sea \\(u = %dx%+2.2f\\), entonces \\(du = %d\\,dx\\).", k, fase, k),
                usaCoseno
                        ? "\\int A\\cos(u)\\,du = A\\,\\sin(u)"
                        : "\\int A\\sin(u)\\,du = -A\\,\\cos(u)",
                "Reemplaza u por kx + \\varphi para obtener la primitiva final."
        );
    }

    @Override
    public List<MetodoResolucion> getMetodosCompatibles() {
        return List.of(MetodoResolucion.SUSTITUCION, MetodoResolucion.FORMULA_DIRECTA);
    }
}
