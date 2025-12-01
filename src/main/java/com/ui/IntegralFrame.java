package com.ui;

import com.model.Dificultad;
import com.model.Integral;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class IntegralFrame extends JFrame {
    private String tipo = "aleatoria";
    private double limiteInferior = 0;
    private double limiteSuperior = 1;
    private boolean mostrarPasos = false;
    private boolean limitesAleatorios = true;
    private Dificultad dificultad = Dificultad.MEDIA;

    private Integral integral; // El modelo de la integral
    private final PanelIntegral panelIntegral;
    private JPopupMenu popupMenu;
    private final PanelAppBar panelAppBar;
    private final PanelOpciones panelOpciones;
    private final PanelControl panelControl;
    private final NumberFormat numberFormat;
    private List<String> pasosActuales;

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
        panelControl = new PanelControl(e -> verificarRespuesta(), e -> mostrarPasos());
        numberFormat = NumberFormat.getNumberInstance(new Locale("es", "ES"));
        numberFormat.setMaximumFractionDigits(5);
        numberFormat.setMinimumFractionDigits(0);

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
        if (limitesAleatorios) {
            Random r = new Random();
            int a = r.nextInt(11) - 5;   // [-5, 5]
            int b = r.nextInt(11) - 5;

            if (a > b) {
                int tmp = a;
                a = b;
                b = tmp;
            }

            limiteInferior = a;
            limiteSuperior = b;
        }

        try {
            integral = new Integral(tipo, limiteInferior, limiteSuperior, dificultad);

            panelIntegral.mostrarIntegral(integral);
            panelOpciones.mostrarOpciones(integral.getOpciones());
            panelControl.reset();
            pasosActuales = null;

            revalidate();
            repaint();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar una integral válida. Intenta nuevamente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem configurar = new JMenuItem("Configurar Integral...");
        popupMenu.add(configurar);

        // Listeners
        configurar.addActionListener(e -> mostrarConfiguracionIntegral());
    }

    private void mostrarMenu() {
        JButton boton = panelAppBar.getBotonMenu();
        popupMenu.show(boton, 0, boton.getHeight());
    }

    private void mostrarConfiguracionIntegral() {
        ConfigIntegralDialog configDialog = new ConfigIntegralDialog(this, limiteInferior, limiteSuperior, mostrarPasos, limitesAleatorios, tipo, dificultad);
        configDialog.setVisible(true);

        if (!configDialog.getConfirmado()) {
            return;
        }

        tipo = configDialog.getTipoIntegral();
        mostrarPasos = configDialog.getMostrarPasos();
        limitesAleatorios = configDialog.getLimitesAleatorios();
        dificultad = configDialog.getDificultadSeleccionada();

        if (!limitesAleatorios) {
            limiteInferior = configDialog.getLimiteInferior();
            limiteSuperior = configDialog.getLimiteSuperior();
        }
    }

    private void verificarRespuesta() {
        int seleccion = panelOpciones.getOpcionSeleccionada();

        if (seleccion == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una opción", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Deshabilitar opciones y resaltarlas
        panelOpciones.resaltarRespuestas(integral.getOpcionCorrecta(), seleccion);

        // Mostrar resultado
        if (seleccion == integral.getOpcionCorrecta()) {
            panelControl.mostrarResultado("<html><span style='color:green; font-weight:bold;'>¡Correcto!</span></html>");
        } else {
            String msg = String.format("<html><span style='color:red; font-weight:bold;'>Incorrecto.</span> " +
                    "La respuesta era <span style='color:green;'>%s</span></html>", numberFormat.format(integral.getResultado()));
            panelControl.mostrarResultado(msg);
        }

        pasosActuales = integral.getPasos();
        panelControl.habilitarVerPasos(mostrarPasos);
    }

    private void mostrarPasos() {
        if (!mostrarPasos) {
            JOptionPane.showMessageDialog(this,
                    "Activa 'Mostrar pasos de solución' en la configuración para verlos.",
                    "Pasos no disponibles",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (pasosActuales == null || pasosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay pasos disponibles para esta integral.",
                    "Pasos no disponibles",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new PasosFrame(pasosActuales);
    }
}
