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
        // Crear cadena LaTeX
        String latex = String.format(
                "\\int_{%d}^{%d} \\frac{%dx^{%d}}{(%dx^{%d} + %d)^{%d}} \\, dx",
                integral.getA(), integral.getB(),
                integral.getK(), integral.getM(),
                integral.getC(), integral.getM() + 1,
                integral.getD(), integral.getN()
        );

        // Crear y mostrar la integral con LaTeX
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 32);
        latexLabel.setIcon(icon);
    }
}
