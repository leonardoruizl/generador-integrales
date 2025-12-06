package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.basic.BasicButtonUI;

import com.model.MetodoResolucion;

public class PanelControl extends JPanel {
    private final JButton verificarBoton;
    private final JLabel resultadoLabel;
    private final JButton verPasosBoton;
    private final JButton verGraficaBoton;
    private final JComboBox<MetodoResolucion> metodoCombo;
    private final JLabel metodosSugeridos;
    private final JPanel selectorPanel;
    private Set<MetodoResolucion> metodosCompatibles;
    private boolean selectorVisible;

    public PanelControl(ActionListener verificarListener, ActionListener verPasosListener, ActionListener verGraficaListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 18, 22, 18));
        setBackground(new Color(250, 252, 255));
        setOpaque(true);

        metodosCompatibles = EnumSet.noneOf(MetodoResolucion.class);

        JLabel metodoLabel = new JLabel("Método de resolución");
        metodoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        metodoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        metodoLabel.setForeground(new Color(50, 63, 94));

        metodoCombo = new JComboBox<>();
        metodoCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        metodoCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        metodoCombo.setPreferredSize(new Dimension(200, 36));
        metodoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        metodoCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (comp instanceof JLabel label) {
                    label.setText(value == null ? "Selecciona un método" : ((MetodoResolucion) value).getDescripcion());
                }
                return comp;
            }
        });

        metodosSugeridos = new JLabel("Selecciona un método para validar si aplica");
        metodosSugeridos.setAlignmentX(Component.CENTER_ALIGNMENT);
        metodosSugeridos.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        metodosSugeridos.setForeground(new Color(90, 105, 130));

        selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        selectorPanel.setOpaque(false);
        selectorPanel.add(metodoLabel);
        selectorPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        selectorPanel.add(metodoCombo);
        selectorPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        selectorPanel.add(metodosSugeridos);
        selectorPanel.setVisible(false);

        verificarBoton = crearBotonPrimario("Verificar respuesta", new Color(70, 95, 200));
        verificarBoton.addActionListener(verificarListener);

        verPasosBoton = crearBotonSecundario("Ver pasos");
        verPasosBoton.addActionListener(verPasosListener);
        verPasosBoton.setEnabled(false);

        verGraficaBoton = crearBotonSecundario("Ver gráfica");
        verGraficaBoton.addActionListener(verGraficaListener);

        resultadoLabel = new JLabel();
        resultadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultadoLabel.setVisible(false);
        resultadoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 222, 235)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        resultadoLabel.setOpaque(true);
        resultadoLabel.setBackground(new Color(255, 255, 255, 235));

        add(selectorPanel);
        add(Box.createRigidArea(new Dimension(0, 12)));

        add(verificarBoton);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(verPasosBoton);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(verGraficaBoton);
        add(Box.createRigidArea(new Dimension(0, 12))); // Espacio opcional entre botón y mensaje
        add(resultadoLabel);
    }

    public void mostrarResultado(String htmlTexto) {
        resultadoLabel.setText(htmlTexto);
        resultadoLabel.setVisible(true);
        verificarBoton.setEnabled(false);
    }

    public void reset() {
        resultadoLabel.setVisible(false);
        resultadoLabel.setText("");
        verificarBoton.setEnabled(true);
        verPasosBoton.setEnabled(false);
        verGraficaBoton.setText("Ver gráfica");
        metodoCombo.setSelectedItem(null);
        metodosCompatibles = EnumSet.noneOf(MetodoResolucion.class);
        metodosSugeridos.setText("Selecciona un método para validar si aplica");
        mostrarSelectorMetodos(false);
    }

    public void habilitarVerPasos(boolean habilitado) {
        verPasosBoton.setEnabled(habilitado);
    }

    public void actualizarEstadoGrafica(boolean visible) {
        verGraficaBoton.setText(visible ? "Ocultar gráfica" : "Ver gráfica");
    }

    public void mostrarSelectorMetodos(boolean visible) {
        selectorVisible = visible;
        selectorPanel.setVisible(visible);
        revalidate();
        repaint();
        if (visible) {
            metodoCombo.requestFocusInWindow();
        }
    }

    public boolean estaSelectorVisible() {
        return selectorVisible;
    }

    public void configurarMetodos(List<MetodoResolucion> compatibles) {
        DefaultComboBoxModel<MetodoResolucion> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        for (MetodoResolucion metodo : MetodoResolucion.values()) {
            model.addElement(metodo);
        }
        metodoCombo.setModel(model);
        metodoCombo.setSelectedItem(null);

        metodosCompatibles = compatibles == null || compatibles.isEmpty()
                ? EnumSet.noneOf(MetodoResolucion.class)
                : EnumSet.copyOf(compatibles);

        if (metodosCompatibles.isEmpty()) {
            metodosSugeridos.setText("No hay métodos sugeridos para esta integral");
        } else {
            String sugeridos = metodosCompatibles.stream()
                    .map(MetodoResolucion::getDescripcion)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("-");
            metodosSugeridos.setText("Sugeridos: " + sugeridos);
        }
    }

    public MetodoResolucion getMetodoSeleccionado() {
        Object seleccionado = metodoCombo.getSelectedItem();
        return seleccionado instanceof MetodoResolucion metodo ? metodo : null;
    }

    public boolean esMetodoCompatibleSeleccionado() {
        MetodoResolucion seleccionado = getMetodoSeleccionado();
        return seleccionado != null && metodosCompatibles.contains(seleccionado);
    }

    private JButton crearBotonPrimario(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setBackground(color);
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        boton.addChangeListener(e -> {
            if (boton.isEnabled()) {
                boton.setBackground(color);
                boton.setForeground(Color.BLACK);
            } else {
                boton.setBackground(color.darker());
                boton.setForeground(Color.BLACK);
            }
        });
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setBackground(new Color(236, 240, 247));
        boton.setForeground(new Color(54, 65, 92));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
}
