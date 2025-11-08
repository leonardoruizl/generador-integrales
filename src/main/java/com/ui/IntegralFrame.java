package com.ui;

import com.model.Integral;

import javax.swing.*;
import java.awt.*;

public class IntegralFrame extends JFrame {
    private Integral integral; // El modelo de la integral
    private final PanelIntegral panelIntegral;
    private ConfigIntegralDialog configDialog;
    private JPopupMenu popupMenu;
    private final PanelAppBar panelAppBar;
    private final PanelOpciones panelOpciones;
    private final PanelControl panelControl;

    public IntegralFrame() {
        setTitle("Generador de Integrales");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 510);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Crear los paneles junto con los listeners
        panelAppBar = new PanelAppBar(
                e -> generarNuevaIntegral(),
                e -> mostrarMenu()
        );

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

        crearMenu();

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

    private void crearMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem configurar = new JMenuItem("Configurar Integral...");
        popupMenu.add(configurar);

        popupMenu.addSeparator();

        JMenuItem raiz = new JMenuItem("Raíz");
        JMenuItem fraccion = new JMenuItem("Fracción");
        JMenuItem trig = new JMenuItem("Trigonométrica");
        JMenuItem aleatoria = new JMenuItem("Aleatoria");

        popupMenu.add(raiz);
        popupMenu.add(fraccion);
        popupMenu.add(trig);
        popupMenu.addSeparator();
        popupMenu.add(aleatoria);

        // Listeners
        configurar.addActionListener(e -> mostrarConfiguracionIntegral());
        raiz.addActionListener(e -> seleccionarTipoIntegral("raiz"));
        fraccion.addActionListener(e -> seleccionarTipoIntegral("fraccion"));
        trig.addActionListener(e -> seleccionarTipoIntegral("trigonometrica"));
        aleatoria.addActionListener(e -> seleccionarTipoIntegral("aleatoria"));
    }

    private void mostrarMenu() {
        JButton boton = panelAppBar.getBotonMenu();
        popupMenu.show(boton, 0, boton.getHeight());
    }

    private void seleccionarTipoIntegral(String tipo) {
        System.out.println("Tipo de integral seleccionado: " + tipo);
        // Aquí puedes implementar la lógica para cambiar el tipo de integral
    }

    private void generarNuevaIntegralAleatoria() {
        System.out.println("Generando Integral Aleatoria...");
        integral = new Integral(); // Genera una nueva integral aleatoria
        panelIntegral.mostrarIntegral(integral);
        panelOpciones.mostrarOpciones(integral.getOpciones());
        panelControl.reset();
        revalidate();
        repaint();
    }

    private void mostrarConfiguracionIntegral() {
        ConfigIntegralDialog = new ConfigIntegralDialog(this);
        configDialog.setVisible(true);

        if (!configDialog.getConfirmado()) {
            return; // El usuario canceló la configuración
        }

        // Obtener los valores configurados
        String tipo = configDialog.getTipoIntegral();
        boolean mostrarPasos = configDialog.getMostrarPasos();
        Double limiteInferior = configDialog.getLimiteInferior();
        Double limiteSuperior = configDialog.getLimiteSuperior();

        // Validaciones
        if (limiteInferior != null && limiteSuperior != null && limiteInferior >= limiteSuperior) {
            JOptionPane.showMessageDialog(this, "El límite inferior debe ser menor que el límite superior.", "Error de configuración", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generar una nueva integral con la configuración seleccionada
        integral = new Integral(tipo, limiteInferior, limiteSuperior, mostrarPasos);

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
            String msg = String.format("<html><span style='color:red; font-weight:bold;'>Incorrecto.</span> " +
                    "La respuesta era <span style='color:green;'>%.5f</span></html>", integral.getResultado());
            panelControl.mostrarResultado(msg);
        }
    }
}
