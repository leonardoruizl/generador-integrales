package com.ui;

import javax.swing.*;
import java.awt.*;

public class PanelOpciones extends JPanel {
    private final JRadioButton[] botones;
    private ButtonGroup grupoOpciones;

    public PanelOpciones() {
        setLayout(new GridLayout(0, 1, 5, 2));
        botones = new JRadioButton[5];
        grupoOpciones = new ButtonGroup();
    }

    // Limpia y genera nuevos botones de opción
    public void mostrarOpciones(double[] opciones) {
        removeAll(); // Limpiar opciones anteriores
        grupoOpciones = new ButtonGroup(); // Reiniciar el grupo de botones

        for (int i = 0; i < 5; i++) {
            String textoOpcion = String.format("%.5f", opciones[i]);
            botones[i] = new JRadioButton(textoOpcion);
            botones[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            grupoOpciones.add(botones[i]);
            add(botones[i]);
        }

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
}
