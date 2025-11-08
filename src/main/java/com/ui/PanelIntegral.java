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
        latexLabel = new JLabel();
        latexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(latexLabel, BorderLayout.CENTER);
    }

    public void mostrarIntegral(Integral integral) {
        String latex = integral.getLatex();

        if (latex == null || latex.isEmpty()) {
            latexLabel.setText("La integral no está disponible.");
            return;
        }

        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 32);
        latexLabel.setIcon(icon);
    }
}
