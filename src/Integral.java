import java.util.Random;

public class Integral {
    private int k, c, d, n, m, a, b;
    private double resultado;
    private int opcionCorrecta;
    private double[] opciones;

    public Integral() {
        generarIntegral();
        calcularResultado();
        generarOpciones();
    }

    // Genera los valores aleatorios de la integral
    private void generarIntegral() {
        Random rand = new Random();
        k = rand.nextInt(5) + 1;          // Coeficiente del término superior 1 a 5
        c = rand.nextInt(4) + 1;          // Coeficiente del primer término del binomio 1 a 4
        d = rand.nextInt(5) + 1;          // Constante del binomio 1 a 5
        n = rand.nextInt(2) + 2;          // Potencia del binomio 2 o 3
        m = rand.nextInt(3) + 1;          // Potencia superior 1 a 3
        a = rand.nextInt(6) - 4;          // Límite inferior -4 a 1
        b = rand.nextInt(4) + 1;          // Límite superior 1 a 4
    }

    // Calcula el resultado exacto de la integral definida
    private void calcularResultado() {
        double coeficiente = (double) k / (c * (m + 1) * (1 - n));
        double valB = Math.pow(c * Math.pow(b, m + 1) + d, 1 - n);
        double valA = Math.pow(c * Math.pow(a, m + 1) + d, 1 - n);
        resultado = coeficiente * (valB - valA);

        // Manejo especial si el resultado es cercano a cero
        if (Math.abs(resultado) < 0.00001) {
            resultado = 0;
            for (int i = 0; i < opciones.length; i++) {
                opciones[i] = (Math.random() - 0.5) * 0.2; // ±0.1
            }
        }
    }

    // Genera 5 opciones distintas (una correcta)
    private void generarOpciones() {
        opciones = new double[5];
        Random rand = new Random();
        opcionCorrecta = rand.nextInt(5);

        // Evitar cero absoluto
        if (Math.abs(resultado) < 0.00001) resultado = 0;

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

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getK() {
        return k;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }
}
