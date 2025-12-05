package com.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

import com.model.Dificultad;

public class ConfigIntegralDialog extends JDialog {
    private JRadioButton rbRaiz, rbFraccion, rbTrig, rbClasica, rbAleatoria;
    private JRadioButton rbFacil, rbMedio, rbDificil;
    private JCheckBox cbMostrarPasos, cbAleatorio;
    private JSpinner spLimiteInferior, spLimiteSuperior;
    private JLabel etiquetaRango;
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

        actualizarResumenLimites();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        setPreferredSize(new Dimension(460, 500));

        JLabel encabezado = new JLabel("Personaliza el ejercicio antes de generarlo");
        encabezado.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(encabezado, BorderLayout.NORTH);

        // Tipo de integral
        JPanel tipoPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        tipoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Tipo de Integral"),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));

        rbRaiz = new JRadioButton("Raíz");
        rbRaiz.setToolTipText("Integrales que involucran expresiones con raíz");
        rbFraccion = new JRadioButton("Fracción");
        rbFraccion.setToolTipText("Función racional con cocientes de polinomios");
        rbTrig = new JRadioButton("Trigonométrica");
        rbTrig.setToolTipText("Funciones seno, coseno o combinaciones trigonométricas");
        rbClasica = new JRadioButton("Clásicas");
        rbClasica.setToolTipText("Formas básicas de integración");
        rbAleatoria = new JRadioButton("Aleatoria");
        rbAleatoria.setToolTipText("Selecciona un tipo válido al azar");

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
        cbMostrarPasos.setToolTipText("Habilita una ventana con los pasos al finalizar");

        JPanel pasosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pasosPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Mostrar pasos"),
                BorderFactory.createEmptyBorder(2, 8, 6, 8)));
        pasosPanel.add(cbMostrarPasos);

        // Panel de dificultad
        JPanel dificultadPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        dificultadPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dificultad"),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));

        rbFacil = new JRadioButton("Fácil");
        rbFacil.setToolTipText("Intervalos cortos y operaciones sencillas");
        rbMedio = new JRadioButton("Media");
        rbMedio.setToolTipText("Un balance entre precisión y variedad");
        rbDificil = new JRadioButton("Difícil");
        rbDificil.setToolTipText("Mayores intervalos y expresiones más exigentes");

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
        limitesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Límites"),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        limitesPanel.add(new JLabel("Límite inferior:"), c);

        spLimiteInferior = crearSpinner();
        spLimiteInferior.setToolTipText("Ingresa el valor inferior del intervalo");
        c.gridx = 1;
        limitesPanel.add(spLimiteInferior, c);

        c.gridx = 0;
        c.gridy = 1;
        limitesPanel.add(new JLabel("Límite superior:"), c);

        spLimiteSuperior = crearSpinner();
        spLimiteSuperior.setToolTipText("Ingresa el valor superior del intervalo");
        c.gridx = 1;
        limitesPanel.add(spLimiteSuperior, c);

        JButton btnIntercambiar = new JButton("↔");
        btnIntercambiar.setToolTipText("Intercambia los límites inferior y superior");
        btnIntercambiar.addActionListener(e -> intercambiarLimites());
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 0;
        limitesPanel.add(btnIntercambiar, c);

        cbAleatorio = new JCheckBox("Generar límites aleatorios (recomendado)");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3; // ocupa las columnas
        c.anchor = GridBagConstraints.WEST;
        limitesPanel.add(cbAleatorio, c);

        cbAleatorio.addActionListener(e -> {
            boolean aleatorio = cbAleatorio.isSelected();
            spLimiteInferior.setEnabled(!aleatorio);
            spLimiteSuperior.setEnabled(!aleatorio);
            actualizarResumenLimites();
        });

        spLimiteInferior.setEnabled(!cbAleatorio.isSelected());
        spLimiteSuperior.setEnabled(!cbAleatorio.isSelected());

        etiquetaRango = new JLabel();
        etiquetaRango.setFont(etiquetaRango.getFont().deriveFont(Font.ITALIC, 11f));
        c.gridy = 3;
        limitesPanel.add(etiquetaRango, c);

        ChangeListener rangeListener = e -> actualizarResumenLimites();
        spLimiteInferior.addChangeListener(rangeListener);
        spLimiteSuperior.addChangeListener(rangeListener);

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
        centroPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        centroPanel.add(tipoPanel);
        centroPanel.add(dificultadPanel);
        centroPanel.add(pasosPanel);
        centroPanel.add(limitesPanel);

        JScrollPane scrollCentro = new JScrollPane(centroPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCentro.setBorder(BorderFactory.createEmptyBorder());

        add(scrollCentro, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JSpinner crearSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.25);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.###"));
        spinner.setPreferredSize(new Dimension(90, spinner.getPreferredSize().height));
        return spinner;
    }

    private void intercambiarLimites() {
        double inferior = getLimiteInferior();
        double superior = getLimiteSuperior();
        spLimiteInferior.setValue(superior);
        spLimiteSuperior.setValue(inferior);
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
        double rango = limSup - limInf;

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

        if (rango < 0.1) {
            JOptionPane.showMessageDialog(this,
                    "Usa un rango mayor para obtener ejercicios más interesantes (≥ 0.1).",
                    "Rango demasiado corto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void actualizarResumenLimites() {
        if (cbAleatorio.isSelected()) {
            etiquetaRango.setForeground(Color.DARK_GRAY);
            etiquetaRango.setText("Los límites se generarán automáticamente dentro de un rango acotado.");
            return;
        }

        double limInf = getLimiteInferior();
        double limSup = getLimiteSuperior();
        double rango = limSup - limInf;

        if (limInf >= limSup) {
            etiquetaRango.setForeground(new Color(180, 40, 40));
            etiquetaRango.setText("El límite inferior debe ser menor al superior.");
        } else {
            etiquetaRango.setForeground(new Color(60, 90, 60));
            etiquetaRango.setText(String.format("Rango actual: %.3f unidades (%.3f a %.3f)", rango, limInf, limSup));
        }
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
