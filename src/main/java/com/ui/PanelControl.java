package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelControl extends JPanel {
    private final JButton verificarBoton;
    private final JLabel resultadoLabel;
    private final JButton verPasosBoton;

    public PanelControl(ActionListener verificarListener, ActionListener verPasosListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        verificarBoton = new JButton("Verificar respuesta");
        verificarBoton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        verificarBoton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verificarBoton.addActionListener(verificarListener);

        verPasosBoton = new JButton("Ver pasos");
        verPasosBoton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verPasosBoton.addActionListener(verPasosListener);
        verPasosBoton.setEnabled(false);

        resultadoLabel = new JLabel();
        resultadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultadoLabel.setVisible(false);

        add(verificarBoton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(verPasosBoton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Espacio opcional entre botón y mensaje
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
    }

    public void habilitarVerPasos(boolean habilitado) {
        verPasosBoton.setEnabled(habilitado);
    }
}
