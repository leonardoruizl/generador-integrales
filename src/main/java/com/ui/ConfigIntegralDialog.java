package com.ui;

import javax.swing.*;
import java.awt.*;

public class ConfigIntegralDialog extends JDialog {
    private JRadioButton rbRaiz;
    private JRadioButton rbFraccion;
    private JRadioButton rbTrig;
    private JCheckBox cbMostrarPasos;
    private JTextField txtLimiteInferior, txtLimiteSuperior;
    private boolean confirmado = false;

    public ConfigIntegralDialog(Frame owner) {
        super(owner, "Configuración de Integral", true);
        initUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        setLayout(new BorderLayout(10, 10));

        // Tipo de integral
        JPanel tipoPanel = new JPanel(new GridLayout(0, 1));
        tipoPanel.setBorder(BorderFactory.createTitledBorder("Tipo de Integral"));

        rbRaiz = new JRadioButton("Raíz");
        rbFraccion = new JRadioButton("Fracción");
        rbTrig = new JRadioButton("Trigonométrica");
        JRadioButton rbAleatoria = new JRadioButton("Aleatoria", true);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbRaiz);
        grupo.add(rbFraccion);
        grupo.add(rbTrig);
        grupo.add(rbAleatoria);

        rbAleatoria.setSelected(true); // Selección por defecto

        tipoPanel.add(rbRaiz);
        tipoPanel.add(rbFraccion);
        tipoPanel.add(rbTrig);
        tipoPanel.add(rbAleatoria);

        // Panel de mostrar pasos
        cbMostrarPasos = new JCheckBox("Mostrar pasos de solución");

        JPanel pasosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pasosPanel.setBorder(BorderFactory.createTitledBorder("Mostrar pasos"));
        pasosPanel.add(cbMostrarPasos);

        // Panel de límites
        JPanel limitesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        limitesPanel.setBorder(BorderFactory.createTitledBorder("Límites"));

        limitesPanel.add(new JLabel("Límite inferior:"));
        txtLimiteInferior = new JTextField("-3", 5);
        limitesPanel.add(txtLimiteInferior);

        limitesPanel.add(new JLabel("Límite superior:"));
        txtLimiteSuperior = new JTextField("3", 5);
        limitesPanel.add(txtLimiteSuperior);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        // Organizar todo en el diálogo
        JPanel centroPanel = new JPanel(new GridLayout(3, 1));
        centroPanel.add(tipoPanel);
        centroPanel.add(pasosPanel);
        centroPanel.add(limitesPanel);

        add(centroPanel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public boolean getConfirmado() {
        return confirmado;
    }

    public String getTipoIntegral() {
        if (rbRaiz.isSelected()) return "raiz";
        if (rbFraccion.isSelected()) return "fraccion";
        if (rbTrig.isSelected()) return "trig";
        return "aleatoria";
    }

    public boolean getMostrarPasos() {
        return cbMostrarPasos.isSelected();
    }

    public Double getLimiteInferior() {
        try {
            return Double.parseDouble(txtLimiteInferior.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getLimiteSuperior() {
        try {
            return Double.parseDouble(txtLimiteSuperior.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
