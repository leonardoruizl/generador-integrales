package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelControl extends JPanel {
    private final JButton verificarBoton;
    private final JLabel resultadoLabel;
    private final JButton verPasosBoton;
    private final JButton verGraficaBoton;

    public PanelControl(ActionListener verificarListener, ActionListener verPasosListener, ActionListener verGraficaListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 18, 22, 18));
        setBackground(new Color(250, 252, 255));
        setOpaque(true);

        verificarBoton = crearBotonPrimario("Verificar respuesta", new Color(70, 95, 200));
        verificarBoton.addActionListener(verificarListener);

        verPasosBoton = crearBotonSecundario("Ver pasos");
        verPasosBoton.addActionListener(verPasosListener);
        verPasosBoton.setEnabled(false);

        verGraficaBoton = crearBotonSecundario("Ver gráfica");
        verGraficaBoton.addActionListener(verGraficaListener);

        resultadoLabel = new JLabel();
        resultadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultadoLabel.setVisible(false);
        resultadoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 222, 235)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        resultadoLabel.setOpaque(true);
        resultadoLabel.setBackground(new Color(255, 255, 255, 235));

        add(verificarBoton);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(verPasosBoton);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(verGraficaBoton);
        add(Box.createRigidArea(new Dimension(0, 12))); // Espacio opcional entre botón y mensaje
        add(resultadoLabel);
    }

    public void mostrarResultado(String htmlTexto) {
        resultadoLabel.setText(htmlTexto);
        resultadoLabel.setVisible(true);
        verificarBoton.setEnabled(false);
    }

    public void reset() {
        resultadoLabel.setVisible(false);
        resultadoLabel.setText("");
        verificarBoton.setEnabled(true);
        verPasosBoton.setEnabled(false);
        verGraficaBoton.setText("Ver gráfica");
    }

    public void habilitarVerPasos(boolean habilitado) {
        verPasosBoton.setEnabled(habilitado);
    }

    public void actualizarEstadoGrafica(boolean visible) {
        verGraficaBoton.setText(visible ? "Ocultar gráfica" : "Ver gráfica");
    }

    private JButton crearBotonPrimario(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setDisabledTextColor(new Color(235, 238, 245));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        boton.addChangeListener(e -> {
            if (boton.isEnabled()) {
                boton.setBackground(color);
                boton.setForeground(Color.WHITE);
            } else {
                boton.setBackground(color.darker());
                boton.setForeground(new Color(235, 238, 245));
            }
        });
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setBackground(new Color(236, 240, 247));
        boton.setForeground(new Color(54, 65, 92));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
}
