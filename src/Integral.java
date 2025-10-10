import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Integral {
    private int k, c, d, n, m, a, b;
    private double resultado;
    private int opcionCorrecta;
    private double[] opciones;
    private static JFrame frame;

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
        n = rand.nextInt(3) + 1;          // Potencia del binomio 1 o 2
        m = rand.nextInt(3) + 1;          // Potencia superior 1 a 3
        a = rand.nextInt(6) - 4;          // Límite inferior -4 a 1
        b = rand.nextInt(4) + 1;          // Límite superior 1 a 4
    }

    // Calcula el resultado exacto de la integral definida
    private void calcularResultado() {
        double coeficiente, valB, valA;

        if (n == 1) {
            // Caso especial: integral de tipo logarítmica
            coeficiente = (double) k / (c * (m + 1));
            valB = Math.log(Math.abs(c * Math.pow(b, m + 1) + d));
            valA = Math.log(Math.abs(c * Math.pow(a, m + 1) + d));
        } else {
            // Caso general
            coeficiente = (double) k / (c * (m + 1) * (1 - n));
            valB = Math.pow(c * Math.pow(b, m + 1) + d, 1 - n);
            valA = Math.pow(c * Math.pow(a, m + 1) + d, 1 - n);
        }

        resultado = coeficiente * (valB - valA);
    }

    // Nuevo: genera 5 opciones distintas (una correcta)
    private void generarOpciones() {
        Random rand = new Random();
        opciones = new double[5];
        opcionCorrecta = rand.nextInt(5);

        double roundedCorrect = round5(resultado);
        Set<Double> usados = new HashSet<>();
        usados.add(roundedCorrect);

        opciones[opcionCorrecta] = resultado;

        for (int i = 0; i < 5; i++) {
            if (i == opcionCorrecta) continue;

            int intentos = 0;
            while (intentos < 1000) {
                intentos++;
                double candidato;

                if (Double.isNaN(resultado) || !Double.isFinite(resultado)) {
                    candidato = (rand.nextDouble() * 2 - 1);
                } else if (Math.abs(resultado) < 1e-8) {
                    double escala = 1.0 + Math.abs(c) + Math.abs(d) + Math.abs(k);
                    double magnitud = (0.1 + rand.nextDouble() * 1.9) * escala;
                    candidato = (rand.nextDouble() * 2 - 1) * magnitud;

                    if (rand.nextInt(6) == 0) {
                        candidato = (rand.nextDouble() * (0.1 + Math.abs(d)));
                    }
                } else {
                    double factorAleatorio = 1.0 + (rand.nextDouble() * 0.6 - 0.3);
                    double offset = (rand.nextDouble() - 0.5) * Math.abs(resultado) * 0.5;
                    candidato = resultado * factorAleatorio + offset;
                }

                if (!Double.isFinite(candidato)) {
                    candidato = (rand.nextDouble() * 2 - 1);
                }

                double r = round5(candidato);
                if (!usados.contains(r)) {
                    usados.add(r);
                    opciones[i] = candidato;
                    break;
                }
            }

            if (!Double.isFinite(opciones[i])) {
                opciones[i] = resultado + (i + 1) * 0.12345;
            }
        }
    }

    // Redondea a 5 decimales (lo que se muestra al usuario)
    private double round5(double v) {
        return Math.rint(v * 1e5) / 1e5;
    }

    // Muestra la integral y las opciones
    public void mostrarIntegral() {
        System.out.println("Resuelve la siguiente integral definida:\n");
        System.out.printf(" ∫ de %d a %d  (%dx^%d) / (%dx^%d + %d)^%d dx\n\n",
                a, b, k, m, c, m + 1, d, n);

        System.out.println("Opciones:");
        for (int i = 0; i < 5; i++) {
            System.out.printf("%d) %.5f\n", i + 1, opciones[i]);
        }
    }

    // Muestra la integral en formato LaTeX usando Swing
    public void mostrarIntegralLatex() {
        // Cierra la ventana anterior si existe
        if (frame != null) {
            frame.dispose();
        }

        // La expresión en formato LaTeX
        String latex = String.format("\\int_{%d}^{%d} \\frac{%dx^{%d}}{(%dx^{%d}+%d)^{%d}}\\,dx", a, b, k, m, c, m + 1, d, n);

        // Crear fórmula LaTeX
        TeXFormula formula = new TeXFormula(latex);

        // Crear un icono de la fórmula
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 40);

        // Crear un JLabel para mostrarlo
        JLabel label = new JLabel();
        label.setIcon(icon);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        // Crear ventana
        frame = new JFrame("Integral generada");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLayout(new BorderLayout());
        frame.add(label, BorderLayout.CENTER);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Verifica si la respuesta es correcta
    public void verificarRespuesta(int eleccion) {
        if (eleccion - 1 == opcionCorrecta) {
            System.out.println("\n¡Correcto!\n");
        } else {
            System.out.println("\nIncorrecto. La respuesta correcta era la opción " + (opcionCorrecta + 1));
            System.out.printf("Resultado exacto: %.5f\n\n", resultado);
        }
    }

    // Método para ejecutar el cuestionario completo
    public void ejecutar(Scanner sc) {
        mostrarIntegral();
        System.out.print("\nElige la opción correcta (1-5): ");
        int eleccion = sc.nextInt();

        verificarRespuesta(eleccion);
    }
}
