package com.ui;

import com.model.Integral;
import com.model.IntegralConfig;
import com.model.IntegralGenerator;
import com.model.MetodoResolucion;
import com.util.PreferenciasUsuario;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class IntegralFrame extends JFrame {
    private IntegralConfig config;
    private boolean graficaVisible = false;

    private Integral integral; // El modelo de la integral
    private final PanelIntegral panelIntegral;
    private final PanelGrafica panelGrafica;
    private JPopupMenu popupMenu;
    private final PanelAppBar panelAppBar;
    private final PanelOpciones panelOpciones;
    private final PanelControl panelControl;
    private JScrollPane scrollPrincipal;
    private final NumberFormat numberFormat;
    private final IntegralGenerator integralGenerator;
    private List<String> pasosActuales;

    private static final Dimension GRAFICA_RETRAIDA = new Dimension(640, 320);
    private static final Dimension GRAFICA_EXTENDIDA = new Dimension(680, 460);

    public IntegralFrame() {
        setTitle("Generador de Integrales");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 678);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(242, 245, 250));
        setLayout(new BorderLayout(10, 10));

        // Crear los paneles junto con los listeners
        panelAppBar = new PanelAppBar(
                e -> generarNuevaIntegral(),
                e -> mostrarMenu()
        );

        panelIntegral = new PanelIntegral();
        panelGrafica = new PanelGrafica();
        panelOpciones = new PanelOpciones();
        panelControl = new PanelControl(e -> verificarRespuesta(), e -> mostrarPasos(), e -> alternarGrafica());
        panelOpciones.setOnSelectionChange(() -> {
            boolean haySeleccion = panelOpciones.haySeleccion();
            panelControl.setVerificarHabilitado(haySeleccion);
        });
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(5);
        numberFormat.setMinimumFractionDigits(0);

        config = PreferenciasUsuario.cargarConfig();
        integralGenerator = new IntegralGenerator();

        // Panel superior
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        panelPrincipal.setOpaque(false);

        JScrollPane scrollOpciones = new JScrollPane(panelOpciones, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollOpciones.setPreferredSize(new Dimension(550, 170));
        scrollOpciones.setBorder(BorderFactory.createEmptyBorder());
        scrollOpciones.getViewport().setOpaque(false);
        scrollOpciones.setOpaque(false);

        JPanel seccionSuperior = new JPanel();
        seccionSuperior.setLayout(new BoxLayout(seccionSuperior, BoxLayout.Y_AXIS));
        seccionSuperior.setOpaque(false);
        seccionSuperior.add(panelIntegral);
        seccionSuperior.add(Box.createRigidArea(new Dimension(0, 12)));
        seccionSuperior.add(panelGrafica);

        panelPrincipal.add(seccionSuperior);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 12)));
        panelPrincipal.add(scrollOpciones);

        scrollPrincipal = new JScrollPane(
                panelPrincipal,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPrincipal.setBorder(BorderFactory.createEmptyBorder());
        scrollPrincipal.getViewport().setOpaque(false);
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);

        add(panelAppBar, BorderLayout.NORTH);
        add(scrollPrincipal, BorderLayout.CENTER);
        add(panelControl, BorderLayout.SOUTH);

        crearMenu();

        generarNuevaIntegral();
        setVisible(true);
    }

    private void generarNuevaIntegral() {
        IntegralConfig configActual = config.copiar();

        if (configActual.isLimitesAleatorios()) {
            Random r = new Random();
            int a = r.nextInt(11) - 5;   // [-5, 5]
            int b = r.nextInt(11) - 5;

            if (a > b) {
                int tmp = a;
                a = b;
                b = tmp;
            }

            if (a == b) {
                b = a + 1;
            }

            configActual.setLimiteInferior(a);
            configActual.setLimiteSuperior(b);
        }

        try {
            integral = integralGenerator.crearIntegral(
                    configActual.getTipo(),
                    configActual.getLimiteInferior(),
                    configActual.getLimiteSuperior(),
                    configActual.getDificultad(),
                    configActual.getCantidadOpciones()
            );

            panelIntegral.mostrarIntegral(integral);
            panelGrafica.actualizarIntegral(integral, configActual.getLimiteInferior(), configActual.getLimiteSuperior());
            graficaVisible = false;
            panelGrafica.setVisible(false);
            panelGrafica.setPreferredSize(GRAFICA_RETRAIDA);
            panelControl.actualizarEstadoGrafica(false);
            panelOpciones.mostrarOpciones(integral.getOpciones());
            panelControl.reset();
            panelControl.configurarMetodos(integral.getMetodosCompatibles());
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
        ConfigIntegralDialog configDialog = new ConfigIntegralDialog(this, config);
        configDialog.setVisible(true);

        if (!configDialog.getConfirmado()) {
            return;
        }

        config.setTipo(configDialog.getTipoIntegral());
        config.setMostrarPasos(configDialog.getMostrarPasos());
        config.setLimitesAleatorios(configDialog.getLimitesAleatorios());
        config.setDificultad(configDialog.getDificultadSeleccionada());
        config.setCantidadOpciones(configDialog.getCantidadOpciones());
        config.setLimiteInferior(configDialog.getLimiteInferior());
        config.setLimiteSuperior(configDialog.getLimiteSuperior());

        PreferenciasUsuario.guardarConfig(config);

        generarNuevaIntegral();
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
        panelControl.habilitarVerPasos(config.isMostrarPasos());
    }

    private void mostrarPasos() {
        if (!config.isMostrarPasos()) {
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

        if (!panelControl.estaSelectorVisible()) {
            panelControl.mostrarSelectorMetodos(true);
            JOptionPane.showMessageDialog(this,
                    "Elige un método de resolución para validar si aplica a esta integral.",
                    "Selecciona un método",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MetodoResolucion metodoSeleccionado = panelControl.getMetodoSeleccionado();
        if (metodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un método de resolución para validar si aplica a esta integral.",
                    "Método no seleccionado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!panelControl.esMetodoCompatibleSeleccionado()) {
            JOptionPane.showMessageDialog(this,
                    "El método elegido no es el recomendado para resolver esta integral. Prueba con uno de los sugeridos.",
                    "Método no aplicable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        new PasosFrame(pasosActuales);
    }

    private void alternarGrafica() {
        if (integral == null) {
            return;
        }
        graficaVisible = !graficaVisible;
        panelGrafica.setVisible(graficaVisible);
        panelGrafica.setPreferredSize(graficaVisible ? GRAFICA_EXTENDIDA : GRAFICA_RETRAIDA);
        panelControl.actualizarEstadoGrafica(graficaVisible);
        if (graficaVisible) {
            SwingUtilities.invokeLater(() -> panelGrafica.scrollRectToVisible(panelGrafica.getBounds()));
        }
        revalidate();
        repaint();
    }
}
