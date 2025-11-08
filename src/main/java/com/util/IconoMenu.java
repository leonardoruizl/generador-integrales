package com.util;

import javax.swing.*;
import java.awt.*;

public class IconoMenu implements Icon {
    private final int size;
    private final Color color;
    private final BasicStroke stroke;

    public IconoMenu(int size, Color color, float thickness) {
        this.size = size;
        this.color = color;
        this.stroke = new BasicStroke(thickness);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(color);
        g2d.setStroke(stroke);

        int padding = size / 5;
        int barLength = size - padding * 2;
        int availableHeight = size - 2 * padding;

        // 3 líneas → 2 espacios intermedios → spacing = availableHeight / 2
        int spacing = availableHeight / 2;

        for (int i = 0; i < 3; i++) {
            int yPos = y + padding + i * spacing;
            g2d.drawLine(x + padding, yPos, x + padding + barLength, yPos);
        }

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}