import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;

public class TestIntegralGUI extends JFrame {
    private Integral integral;
    private final JLabel resultadoLabel;
    private JRadioButton[] botones;
    private final JPanel panelPrincipal, panelIntegral, opcionesPanel, panelInferior;
    private final JButton verificarBoton;

    public TestIntegralGUI() {
        setTitle("Generador de Integrales");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 510);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // AppBar superior
        JPanel appBar = new JPanel(new BorderLayout());
        appBar.setBackground(new Color(45, 45, 45));
        appBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel titulo = new JLabel("Generador de Integrales", SwingConstants.LEFT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton botonNuevaIntegral = new JButton();
        botonNuevaIntegral.setToolTipText("Generar nueva integral");

        try {
            ImageIcon icono = new ImageIcon("src/icons/add_circle.png");
            botonNuevaIntegral.setIcon(icono);
        } catch (Exception e) {
            botonNuevaIntegral.setText("Nueva Integral");
        }

        botonNuevaIntegral.setBackground(Color.WHITE);
        botonNuevaIntegral.setForeground(Color.BLACK);
        botonNuevaIntegral.setFocusPainted(false);
        botonNuevaIntegral.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        botonNuevaIntegral.addActionListener(e -> generarNuevaIntegral());

        // Agregar los componentes al AppBar
        appBar.add(titulo, BorderLayout.WEST);
        appBar.add(botonNuevaIntegral, BorderLayout.EAST);
        add(appBar, BorderLayout.NORTH);

        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Integral
        panelIntegral = new JPanel();
        panelIntegral.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Opciones
        opcionesPanel = new JPanel();
        opcionesPanel.setLayout(new GridLayout(0, 1, 5, 2));
        opcionesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar scroll
        JScrollPane scrollOpciones = new JScrollPane(opcionesPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollOpciones.setPreferredSize(new Dimension(550, 150));
        scrollOpciones.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollOpciones.setViewportBorder(null);

        panelPrincipal.add(panelIntegral);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 0))); // Espacio entre integral y opciones
        panelPrincipal.add(scrollOpciones);

        // Agregar el panel principal al frame
        add(panelPrincipal, BorderLayout.CENTER);

        // Panel inferior
        panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        verificarBoton = new JButton("Verificar respuesta");
        verificarBoton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        verificarBoton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verificarBoton.addActionListener(e -> verificarRespuesta());
        panelInferior.add(verificarBoton);

        // Espacio opcional entre botón y mensaje
        panelInferior.add(Box.createRigidArea(new Dimension(0, 10)));

        // ResultadoLabel agregado al panel inferior
        resultadoLabel = new JLabel();
        resultadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultadoLabel.setVisible(false);
        panelInferior.add(resultadoLabel);

        add(panelInferior, BorderLayout.SOUTH);

        // Generar la primera integral
        generarNuevaIntegral();

        setVisible(true);
    }

    private void generarNuevaIntegral() {
        integral = new Integral();

        // Limpiar el frame
        panelIntegral.removeAll();
        opcionesPanel.removeAll();

        // Crear cadena LaTeX limpia
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
        JLabel latexLabel = new JLabel(icon);
        latexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelIntegral.add(latexLabel, BorderLayout.CENTER);

        // Crear opciones
        botones = new JRadioButton[5];
        ButtonGroup grupoOpciones = new ButtonGroup();
        double[] opciones = integral.getOpciones();

        for (int i = 0; i < 5; i++) {
            botones[i] = new JRadioButton(String.format("%d) %.5f", i + 1, opciones[i]));
            botones[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            grupoOpciones.add(botones[i]);
            opcionesPanel.add(botones[i]);
            botones[i].setEnabled(true);
        }

        resultadoLabel.setVisible(false);
        resultadoLabel.setText(" ");
        verificarBoton.setEnabled(true);
        revalidate();
        repaint();
    }

    // Verifica la respuesta seleccionada
    private void verificarRespuesta() {
        int seleccion = -1;

        for (int i = 0; i < botones.length; i++) {
            if (botones[i].isSelected()) {
                seleccion = i;
                break;
            }
        }

        if (seleccion == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una opción", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Deshabilitar opciones y el botón de verificar
        for (int i = 0; i < botones.length; i++) {
            botones[i].setEnabled(false);
            verificarBoton.setEnabled(false);
        }

        if (!panelInferior.isAncestorOf(resultadoLabel)) {
            panelInferior.add(Box.createRigidArea(new Dimension(0, 10))); // opcional: pequeño espacio
            panelInferior.add(resultadoLabel);
        }

        // Mostrar resultado
        if (seleccion == integral.getOpcionCorrecta()) {
            resultadoLabel.setText("<html><span style='color:green; font-weight:bold;'>¡Correcto!</span></html>");
        } else {
            resultadoLabel.setText(String.format("<html><span style='color:red; font-weight:bold;'>Incorrecto.</span> " +
                    "La respuesta era <span style='color:green;'>%.5f</span></html>", integral.getResultado()));
        }

        resultadoLabel.setVisible(true);
        panelInferior.revalidate();
        panelInferior.repaint();
    }
}
