package com.util;

import javax.swing.*;
import java.awt.*;

/**
 * Icono simple de línea de tendencia para los botones de mostrar/ocultar gráfica.
 */
public class IconoGrafica implements Icon {
    private final int width;
    private final int height;
    private final Color color;
    private final Stroke stroke;

    public IconoGrafica(int width, int height, Color color, float thickness) {
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
        int x0 = x + padding;
        int y0 = y + height - padding;
        int x1 = x + width - padding;
        int y1 = y + padding;

        // Ejes
        g2d.drawLine(x0, y0, x0, y + padding); // eje Y
        g2d.drawLine(x0, y0, x1, y0); // eje X

        // Línea de tendencia quebrada
        int paso = (x1 - x0) / 3;
        int punto1X = x0 + paso;
        int punto1Y = y0 - (height / 3);
        int punto2X = x0 + 2 * paso;
        int punto2Y = y0 - (height / 2);

        g2d.drawLine(x0, y0, punto1X, punto1Y);
        g2d.drawLine(punto1X, punto1Y, punto2X, punto2Y);
        g2d.drawLine(punto2X, punto2Y, x1, y1);

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
