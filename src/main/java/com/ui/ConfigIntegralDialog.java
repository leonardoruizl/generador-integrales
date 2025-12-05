package com.ui;

import javax.swing.*;
import java.awt.*;

import com.model.Dificultad;

public class ConfigIntegralDialog extends JDialog {
    private JRadioButton rbRaiz, rbFraccion, rbTrig, rbClasica, rbAleatoria;
    private JRadioButton rbFacil, rbMedio, rbDificil;
    private JCheckBox cbMostrarPasos, cbAleatorio;
    private JSpinner spLimiteInferior, spLimiteSuperior;
    private boolean confirmado = false;

    public ConfigIntegralDialog(Frame owner, double limInf, double limSup, boolean mostrar, boolean aleatorio, String tipo, Dificultad dificultad) {
        super(owner, "Configuración de Integral", true);
        initUi();

        spLimiteInferior.setValue(limInf);
        spLimiteSuperior.setValue(limSup);
        cbMostrarPasos.setSelected(mostrar);

        switch (dificultad) {
            case FACIL -> rbFacil.setSelected(true);
            case DIFICIL -> rbDificil.setSelected(true);
            default -> rbMedio.setSelected(true);
        }

        switch (tipo) {
            case "raiz" -> rbRaiz.setSelected(true);
            case "fraccion" -> rbFraccion.setSelected(true);
            case "trig" -> rbTrig.setSelected(true);
            case "clasica" -> rbClasica.setSelected(true);
            default -> rbAleatoria.setSelected(true);
        }

        cbAleatorio.setSelected(aleatorio);
        spLimiteInferior.setEnabled(!aleatorio);
        spLimiteSuperior.setEnabled(!aleatorio);

        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Tipo de integral
        JPanel tipoPanel = new JPanel(new GridLayout(0, 1));
        tipoPanel.setBorder(BorderFactory.createTitledBorder("Tipo de Integral"));

        rbRaiz = new JRadioButton("Raíz");
        rbFraccion = new JRadioButton("Fracción");
        rbTrig = new JRadioButton("Trigonométrica");
        rbClasica = new JRadioButton("Clásicas");
        rbAleatoria = new JRadioButton("Aleatoria");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbRaiz);
        grupo.add(rbFraccion);
        grupo.add(rbTrig);
        grupo.add(rbClasica);
        grupo.add(rbAleatoria);

        rbAleatoria.setSelected(true);

        tipoPanel.add(rbRaiz);
        tipoPanel.add(rbFraccion);
        tipoPanel.add(rbTrig);
        tipoPanel.add(rbClasica);
        tipoPanel.add(rbAleatoria);

        // Panel de mostrar pasos
        cbMostrarPasos = new JCheckBox("Mostrar pasos de solución");

        JPanel pasosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pasosPanel.setBorder(BorderFactory.createTitledBorder("Mostrar pasos"));
        pasosPanel.add(cbMostrarPasos);

        // Panel de dificultad
        JPanel dificultadPanel = new JPanel(new GridLayout(0, 1));
        dificultadPanel.setBorder(BorderFactory.createTitledBorder("Dificultad"));

        rbFacil = new JRadioButton("Fácil");
        rbMedio = new JRadioButton("Media");
        rbDificil = new JRadioButton("Difícil");

        ButtonGroup grupoDificultad = new ButtonGroup();
        grupoDificultad.add(rbFacil);
        grupoDificultad.add(rbMedio);
        grupoDificultad.add(rbDificil);

        rbMedio.setSelected(true);

        dificultadPanel.add(rbFacil);
        dificultadPanel.add(rbMedio);
        dificultadPanel.add(rbDificil);

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

        spLimiteInferior = crearSpinner();
        c.gridx = 1;
        limitesPanel.add(spLimiteInferior, c);

        c.gridx = 0;
        c.gridy = 1;
        limitesPanel.add(new JLabel("Límite superior:"), c);

        spLimiteSuperior = crearSpinner();
        c.gridx = 1;
        limitesPanel.add(spLimiteSuperior, c);

        cbAleatorio = new JCheckBox("Generar límites aleatorios");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2; // ocupa las dos columnas
        c.anchor = GridBagConstraints.WEST;
        limitesPanel.add(cbAleatorio, c);

        cbAleatorio.addActionListener(e -> {
            boolean aleatorio = cbAleatorio.isSelected();
            spLimiteInferior.setEnabled(!aleatorio);
            spLimiteSuperior.setEnabled(!aleatorio);
        });

        spLimiteInferior.setEnabled(!cbAleatorio.isSelected());
        spLimiteSuperior.setEnabled(!cbAleatorio.isSelected());

        JLabel tipLimites = new JLabel("Usa las flechas o escribe valores decimales para ajustar los límites.");
        tipLimites.setFont(tipLimites.getFont().deriveFont(Font.ITALIC, 11f));
        c.gridy = 3;
        limitesPanel.add(tipLimites, c);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            if (validarLimites()) {
                confirmado = true;
                dispose();
            }
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        // Organizar el diálogo
        JPanel centroPanel = new JPanel();
        centroPanel.setLayout(new BoxLayout(centroPanel, BoxLayout.Y_AXIS));

        centroPanel.add(tipoPanel);
        centroPanel.add(dificultadPanel);
        centroPanel.add(pasosPanel);
        centroPanel.add(limitesPanel);

        add(centroPanel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JSpinner crearSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.5);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.###"));
        spinner.setPreferredSize(new Dimension(90, spinner.getPreferredSize().height));
        return spinner;
    }

    public boolean getConfirmado() {
        return confirmado;
    }

    public String getTipoIntegral() {
        if (rbRaiz.isSelected()) return "raiz";
        if (rbFraccion.isSelected()) return "fraccion";
        if (rbTrig.isSelected()) return "trig";
        if (rbClasica.isSelected()) return "clasica";
        return "aleatoria";
    }

    public boolean getMostrarPasos() {
        return cbMostrarPasos.isSelected();
    }

    public Dificultad getDificultadSeleccionada() {
        if (rbFacil.isSelected()) return Dificultad.FACIL;
        if (rbDificil.isSelected()) return Dificultad.DIFICIL;
        return Dificultad.MEDIA;
    }

    private boolean validarLimites() {
        if (cbAleatorio.isSelected()) {
            return true;
        }

        double limInf = getLimiteInferior();
        double limSup = getLimiteSuperior();

        if (limInf == 0 && limSup == 0) {
            JOptionPane.showMessageDialog(this,
                    "Los límites no pueden ser ambos 0.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (limInf == limSup) {
            JOptionPane.showMessageDialog(this,
                    "Los límites no pueden ser iguales.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (limInf > limSup) {
            JOptionPane.showMessageDialog(this,
                    "El límite inferior debe ser menor al superior.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public double getLimiteInferior() {
        return ((Number) spLimiteInferior.getValue()).doubleValue();
    }

    public double getLimiteSuperior() {
        return ((Number) spLimiteSuperior.getValue()).doubleValue();
    }

    public boolean getLimitesAleatorios() {
        return cbAleatorio.isSelected();
    }
}
