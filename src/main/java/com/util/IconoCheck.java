package com.util;

import javax.swing.*;
import java.awt.*;

/**
 * Icono de marca de verificación para acciones primarias.
 */
public class IconoCheck implements Icon {
    private final int width;
    private final int height;
    private final Color color;
    private final Stroke stroke;

    public IconoCheck(int width, int height, Color color, float thickness) {
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

        int padding = Math.max(2, Math.min(width, height) / 5);
        int startX = x + padding;
        int startY = y + height - padding - 2;
        int midX = x + width / 2 - 1;
        int midY = y + height - padding / 2 - 3;
        int endX = x + width - padding;
        int endY = y + padding + 1;

        g2d.drawLine(startX, midY, midX, startY);
        g2d.drawLine(midX, startY, endX, endY);

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
