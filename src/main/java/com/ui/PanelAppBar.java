package com.ui;

import com.util.IconoMenu;
import com.util.IconoPlus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelAppBar extends JPanel {
    private final JButton botonMenu;

    public PanelAppBar(ActionListener nuevaIntegralListener, ActionListener menuListener) {
        super(new BorderLayout());
        setBackground(new Color(45, 45, 45));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));

        // Título
        JLabel titulo = new JLabel("Generador de Integrales", SwingConstants.LEFT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titulo, BorderLayout.WEST);

        // Panel derecho de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);

        // Botón de "+" para nueva integral
        JButton botonNuevaIntegral = new JButton();
        botonNuevaIntegral.setToolTipText("Generar nueva integral");

        IconoPlus iconoPlus = new IconoPlus(18, Color.BLACK, 2.5f);
        botonNuevaIntegral.setIcon(iconoPlus);

        botonNuevaIntegral.setBackground(Color.WHITE);
        botonNuevaIntegral.setOpaque(true);
        botonNuevaIntegral.setFocusPainted(false);
        botonNuevaIntegral.setBorder(BorderFactory.createEmptyBorder());

        int iconoPlusSize = iconoPlus.getIconWidth();

        botonNuevaIntegral.setPreferredSize(new Dimension(iconoPlusSize + 12, iconoPlusSize + 12));

        if (nuevaIntegralListener != null) {
            botonNuevaIntegral.addActionListener(nuevaIntegralListener);
        }

        panelBotones.add(botonNuevaIntegral);

        // Botón de menú (☰)
        botonMenu = new JButton();
        botonMenu.setToolTipText("Opciones");

        IconoMenu iconoMenu = new IconoMenu(18, Color.BLACK, 2.5f);
        botonMenu.setIcon(iconoMenu);

        botonMenu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        botonMenu.setForeground(Color.BLACK);
        botonMenu.setBackground(Color.WHITE);
        botonMenu.setOpaque(true);
        botonMenu.setFocusPainted(false);
        botonMenu.setBorder(BorderFactory.createEmptyBorder());

        int iconoMenuSize = iconoMenu.getIconWidth();

        botonMenu.setPreferredSize(new Dimension(iconoMenuSize + 12, iconoMenuSize + 12));

        if (menuListener != null) {
            botonMenu.addActionListener(menuListener);
        }

        panelBotones.add(botonMenu);

        // Agregar el panel de botones al este
        add(panelBotones, BorderLayout.EAST);
    }

    public JButton getBotonMenu() {
        return botonMenu;
    }
}
