import javax.swing.*;
import java.awt.*;

public class IntegralFrame extends JFrame {
    private Integral integral; // El modelo de la integral
    private final PanelAppBar panelAppBar;
    private final PanelIntegral panelIntegral;
    private final PanelOpciones panelOpciones;
    private final PanelControl panelControl;

    public IntegralFrame() {
        setTitle("Generador de Integrales");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 510);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Crear los paneles junto con los listeners
        panelAppBar = new PanelAppBar(e -> generarNuevaIntegral());
        panelIntegral = new PanelIntegral();
        panelOpciones = new PanelOpciones();
        panelControl = new PanelControl(e -> verificarRespuesta());

        // Panel superior
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollOpciones = new JScrollPane(panelOpciones, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollOpciones.setPreferredSize(new Dimension(550, 150));
        scrollOpciones.setBorder(BorderFactory.createEmptyBorder());

        panelPrincipal.add(panelIntegral);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(scrollOpciones);

        add(panelAppBar, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelControl, BorderLayout.SOUTH);

        generarNuevaIntegral();
        setVisible(true);
    }

    private void generarNuevaIntegral() {
        integral = new Integral();

        panelIntegral.mostrarIntegral(integral);
        panelOpciones.mostrarOpciones(integral.getOpciones());
        panelControl.reset();

        revalidate();
        repaint();
    }

    private void verificarRespuesta() {
        int seleccion = panelOpciones.getOpcionSeleccionada();

        if (seleccion == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una opción", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Deshabilitar opciones
        panelOpciones.setOpcionesHabilitadas(false);

        // Mostrar resultado
        if (seleccion == integral.getOpcionCorrecta()) {
            panelControl.mostrarResultado("<html><span style='color:green; font-weight:bold;'>¡Correcto!</span></html>");
        } else {
            String msg = String.format("<html><span style'color:red; font-weight:bold;'>Incorrecto.</span> " +
                    "La respuesta era <span style='color:green;'>%.5f</span></html>", integral.getResultado());
            panelControl.mostrarResultado(msg);
        }
    }
}
