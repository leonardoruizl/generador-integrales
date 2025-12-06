package com.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Icono estilizado de un símbolo de integral para el encabezado.
 */
public class IconoIntegral implements Icon {
    private final int width;
    private final int height;
    private final Color color;
    private final Stroke stroke;

    public IconoIntegral(int width, int height, Color color, float thickness) {
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

        float padding = Math.max(2, Math.min(width, height) / 6f);
        float startX = x + width * 0.45f;
        float startY = y + padding;
        float endY = y + height - padding;

        GeneralPath path = new GeneralPath();
        path.moveTo(startX, endY);
        path.curveTo(x + width * 0.15f, endY * 0.9f, x + width * 0.2f, height * 0.55f, startX, height * 0.5f);
        path.curveTo(x + width * 0.7f, height * 0.45f, x + width * 0.65f, height * 0.15f, startX, startY);

        g2d.draw(path);
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
