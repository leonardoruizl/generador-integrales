package com.ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class PanelOpciones extends JPanel {
    private final JRadioButton[] botones;
    private ButtonGroup grupoOpciones;
    private final NumberFormat numberFormat;

    public PanelOpciones() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        setOpaque(false);
        botones = new JRadioButton[5];
        grupoOpciones = new ButtonGroup();
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(5);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    // Limpia y genera nuevos botones de opción
    public void mostrarOpciones(double[] opciones) {
        removeAll(); // Limpiar opciones anteriores
        grupoOpciones = new ButtonGroup(); // Reiniciar el grupo de botones

        JPanel contenedor = new JPanel(new GridLayout(0, 1, 6, 6));
        contenedor.setOpaque(false);

        JLabel titulo = new JLabel("Opciones de respuesta", SwingConstants.LEFT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setBorder(BorderFactory.createEmptyBorder(4, 6, 2, 6));
        titulo.setForeground(new Color(52, 61, 83));

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setOpaque(true);
        tarjeta.setBackground(new Color(255, 255, 255, 235));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 235)),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)
        ));

        char[] letras = {'A', 'B', 'C', 'D', 'E'};

        for (int i = 0; i < 5; i++) {
            String textoOpcion = numberFormat.format(redondearCincoDecimales(opciones[i]));
            botones[i] = new JRadioButton(String.format("%s) %s", letras[i], textoOpcion));
            botones[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            botones[i].setOpaque(true);
            botones[i].setBackground(new Color(250, 252, 255));
            botones[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(235, 238, 244)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            botones[i].setFocusPainted(false);
            botones[i].setForeground(new Color(45, 52, 70));
            grupoOpciones.add(botones[i]);
            contenedor.add(botones[i]);
        }

        tarjeta.add(titulo, BorderLayout.NORTH);
        tarjeta.add(contenedor, BorderLayout.CENTER);
        add(tarjeta, BorderLayout.CENTER);

        // Actualizar la interfaz
        revalidate();
        repaint();
    }

    // Devuelve el índice de la opción seleccionada, o -1 si no hay selección
    public int getOpcionSeleccionada() {
        for (int i = 0; i < botones.length; i++) {
            if (botones[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    // Habilita o deshabilita las opciones
    public void setOpcionesHabilitadas(boolean habilitado) {
        for (JRadioButton boton : botones) {
            boton.setEnabled(habilitado);
        }
    }

    public void resaltarRespuestas(int opcionCorrecta, int seleccion) {
        for (int i = 0; i < botones.length; i++) {
            JRadioButton boton = botones[i];
            String texto = boton.getText();

            if (i == opcionCorrecta) {
                boton.setText(String.format("<html><span style='color:green;font-weight:bold;'>%s</span></html>", texto));
            } else if (i == seleccion) {
                boton.setText(String.format("<html><span style='color:red;'>%s</span></html>", texto));
            }

            boton.setEnabled(false);
        }
    }

    private double redondearCincoDecimales(double valor) {
        return BigDecimal.valueOf(valor).setScale(5, RoundingMode.HALF_UP).doubleValue();
    }
}
