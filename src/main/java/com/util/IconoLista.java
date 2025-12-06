package com.util;

import javax.swing.*;
import java.awt.*;

/**
 * Icono simple de lista con viñetas.
 */
public class IconoLista implements Icon {
    private final int width;
    private final int height;
    private final Color color;
    private final Stroke stroke;

    public IconoLista(int width, int height, Color color, float thickness) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.stroke = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(stroke);

        int padding = Math.max(2, Math.min(width, height) / 6);
        int lineLength = width - padding * 3;

        for (int i = 0; i < 3; i++) {
            int yPos = y + padding + i * (height - 2 * padding) / 2;
            g2d.fillOval(x + padding, yPos - 2, 4, 4);
            g2d.drawLine(x + padding + 6, yPos, x + padding + 6 + lineLength, yPos);
        }

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
