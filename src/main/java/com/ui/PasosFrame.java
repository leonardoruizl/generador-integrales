package com.ui;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PasosFrame extends JFrame {
    private final JLabel lblPaso;
    private final JButton btnAnterior, btnSiguiente;
    private final List<String> pasos;
    private int index;

    public PasosFrame(List<String> pasos) {
        super("Pasos de resolución");

        if (pasos == null || pasos.isEmpty()) {
            throw new IllegalArgumentException("La lista de pasos está vacía o es null.");
        }

        this.pasos = pasos;
        this.index = 0;

        lblPaso = new JLabel();
        lblPaso.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scroll = new JScrollPane(lblPaso);
        scroll.setPreferredSize(new Dimension(500, 200));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        btnAnterior = new JButton("Anterior");
        btnSiguiente = new JButton("Siguiente");

        btnAnterior.setFocusPainted(false);
        btnSiguiente.setFocusPainted(false);

        btnAnterior.addActionListener(e -> mostrarAnterior());
        btnSiguiente.addActionListener(e -> mostrarSiguiente());

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAnterior);
        panelBotones.add(btnSiguiente);

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        actualizarPaso();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void mostrarAnterior() {
        if (index > 0) {
            index--;
            actualizarPaso();
        }
    }

    private void mostrarSiguiente() {
        if (index < pasos.size() - 1) {
            index++;
            actualizarPaso();
        }
    }

    private void actualizarPaso() {
        String pasoLatex = pasos.get(index);

        try {
            // Intentar renderizar LaTeX
            TeXFormula formula = new TeXFormula(pasoLatex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

            lblPaso.setIcon(icon);
            lblPaso.setText(null);
        } catch (Exception ex) {
            // Si falla, mostrar como texto plano
            lblPaso.setIcon(null);
            lblPaso.setText("<Error TeX>\n" + pasoLatex);
        }

        btnAnterior.setEnabled(index > 0);
        btnSiguiente.setEnabled(index < pasos.size() - 1);
    }
}
