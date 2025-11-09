package com.ui;

import javax.swing.*;
import java.awt.*;

public class ConfigIntegralDialog extends JDialog {
    private JRadioButton rbRaiz, rbFraccion, rbTrig, rbAleatoria;
    private JCheckBox cbMostrarPasos, cbAleatorio;
    private JTextField txtLimiteInferior, txtLimiteSuperior;
    private boolean confirmado = false;

    public ConfigIntegralDialog(Frame owner, double limInf, double limSup, boolean mostrar, boolean aleatorio, String tipo) {
        super(owner, "Configuración de Integral", true);
        initUi();

        txtLimiteInferior.setText(formatDouble(limInf));
        txtLimiteSuperior.setText(formatDouble(limSup));
        cbMostrarPasos.setSelected(mostrar);

        switch (tipo) {
            case "raiz" -> rbRaiz.setSelected(true);
            case "fraccion" -> rbFraccion.setSelected(true);
            case "trig" -> rbTrig.setSelected(true);
            default -> rbAleatoria.setSelected(true);
        }

        cbAleatorio.setSelected(aleatorio);
        txtLimiteInferior.setEnabled(!aleatorio);
        txtLimiteSuperior.setEnabled(!aleatorio);

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
        rbAleatoria = new JRadioButton("Aleatoria");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbRaiz);
        grupo.add(rbFraccion);
        grupo.add(rbTrig);
        grupo.add(rbAleatoria);

        rbAleatoria.setSelected(true);

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
        JPanel limitesPanel = new JPanel(new GridBagLayout());
        limitesPanel.setBorder(BorderFactory.createTitledBorder("Límites"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        limitesPanel.add(new JLabel("Límite inferior:"), c);

        txtLimiteInferior = new JTextField("0", 8);
        c.gridx = 1;
        limitesPanel.add(txtLimiteInferior, c);

        c.gridx = 0;
        c.gridy = 1;
        limitesPanel.add(new JLabel("Límite superior:"), c);

        txtLimiteSuperior = new JTextField("1", 8);
        c.gridx = 1;
        limitesPanel.add(txtLimiteSuperior, c);

        cbAleatorio = new JCheckBox("Generar límites aleatorios");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2; // ocupa las dos columnas
        c.anchor = GridBagConstraints.WEST;
        limitesPanel.add(cbAleatorio, c);

        cbAleatorio.addActionListener(e -> {
            boolean aleatorio = cbAleatorio.isSelected();
            txtLimiteInferior.setEnabled(!aleatorio);
            txtLimiteSuperior.setEnabled(!aleatorio);
        });

        txtLimiteInferior.setEnabled(!cbAleatorio.isSelected());
        txtLimiteSuperior.setEnabled(!cbAleatorio.isSelected());

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

        // Organizar el diálogo
        JPanel centroPanel = new JPanel();
        centroPanel.setLayout(new BoxLayout(centroPanel, BoxLayout.Y_AXIS));

        centroPanel.add(tipoPanel);
        centroPanel.add(pasosPanel);
        centroPanel.add(limitesPanel);

        add(centroPanel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Formatear double para mostrar sin decimales si es entero
    private String formatDouble(double x) {
        if (x == (long) x) {
            return String.format("%d", (long) x);  // sin decimal
        } else {
            return String.valueOf(x);             // con decimal
        }
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

    public boolean getLimitesAleatorios() {
        return cbAleatorio.isSelected();
    }
}
