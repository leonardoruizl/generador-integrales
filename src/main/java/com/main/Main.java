package com.main;

import com.ui.IntegralFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si falla, seguimos con el look and feel por defecto
            }
            new IntegralFrame();
        });
    }
}
