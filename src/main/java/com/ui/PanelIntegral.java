package com.ui;

import com.model.Integral;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;

public class PanelIntegral extends JPanel {
    private final JLabel latexLabel;

    public PanelIntegral() {
        super(new BorderLayout());
        setOpaque(false);

        latexLabel = new JLabel();
        latexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        latexLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(218, 223, 232)),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18)
                )));
        tarjeta.setBackground(new Color(255, 255, 255, 240));
        tarjeta.add(latexLabel, BorderLayout.CENTER);

        add(tarjeta, BorderLayout.CENTER);
    }

    public void mostrarIntegral(Integral integral) {
        String latex = integral.getLatex();

        if (latex == null || latex.isEmpty()) {
            latexLabel.setText("La integral no está disponible.");
            return;
        }

        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 32);
            latexLabel.setIcon(icon);
            latexLabel.setText(null);
        } catch (Exception e) {
            latexLabel.setIcon(null);
            latexLabel.setText("No se pudo renderizar la integral.");
        }
    }
}
