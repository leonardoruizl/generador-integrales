package com.ui;

import com.model.Integral;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

public class PanelGrafica extends JPanel {
    private Integral integral;
    private double limiteInferior;
    private double limiteSuperior;

    public PanelGrafica() {
        setPreferredSize(new Dimension(550, 220));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    public void actualizarIntegral(Integral integral, double limiteInferior, double limiteSuperior) {
        this.integral = integral;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (integral == null) {
            dibujarMensaje(g2, "No hay integral para graficar.");
            g2.dispose();
            return;
        }

        double minX = Math.min(limiteInferior, limiteSuperior);
        double maxX = Math.max(limiteInferior, limiteSuperior);
        if (minX == maxX) {
            minX -= 1;
            maxX += 1;
        }

        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();

        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        int samples = 200;
        for (int i = 0; i < samples; i++) {
            double x = minX + i * (maxX - minX) / (samples - 1);
            double y = integral.evaluarIntegrando(x);
            if (Double.isFinite(y)) {
                xs.add(x);
                ys.add(y);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        if (xs.isEmpty()) {
            dibujarMensaje(g2, "No hay valores válidos en el dominio para graficar.");
            g2.dispose();
            return;
        }

        minY = Math.min(minY, 0);
        maxY = Math.max(maxY, 0);

        if (minY == maxY) {
            minY -= 1;
            maxY += 1;
        }

        int width = getWidth();
        int height = getHeight();
        int margin = 40;

        double finalMinX = minX;
        double finalMaxX = maxX;
        double finalMinY = minY;
        double finalMaxY = maxY;

        java.util.function.DoubleFunction<Double> mapX = x -> margin + (x - finalMinX) * (width - 2.0 * margin) / (finalMaxX - finalMinX);
        java.util.function.DoubleFunction<Double> mapY = y -> height - margin - (y - finalMinY) * (height - 2.0 * margin) / (finalMaxY - finalMinY);

        // Ejes
        dibujarGrid(g2, margin, width, height, finalMinX, finalMaxX, finalMinY, finalMaxY, mapX, mapY);

        g2.setColor(new Color(120, 120, 120));
        if (0 >= finalMinY && 0 <= finalMaxY) {
            int y0 = (int) Math.round(mapY.apply(0));
            g2.drawLine(margin, y0, width - margin, y0);
        }
        if (0 >= finalMinX && 0 <= finalMaxX) {
            int x0 = (int) Math.round(mapX.apply(0));
            g2.drawLine(x0, margin, x0, height - margin);
        }

        dibujarAreaBajoCurva(g2, mapX, mapY, minX, maxX);

        // Curva
        g2.setColor(new Color(33, 150, 243));
        Path2D path = new Path2D.Double();
        boolean started = false;
        for (int i = 0; i < xs.size(); i++) {
            double x = xs.get(i);
            double y = ys.get(i);
            double px = mapX.apply(x);
            double py = mapY.apply(y);
            if (!started) {
                path.moveTo(px, py);
                started = true;
            } else {
                path.lineTo(px, py);
            }
        }
        g2.draw(path);

        g2.dispose();
    }

    private void dibujarMensaje(Graphics2D g2, String mensaje) {
        g2.setColor(new Color(80, 80, 80));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 4;
        g2.drawString(mensaje, x, y);
    }

    private void dibujarGrid(Graphics2D g2,
                              int margin,
                              int width,
                              int height,
                              double minX,
                              double maxX,
                              double minY,
                              double maxY,
                              DoubleFunction<Double> mapX,
                              DoubleFunction<Double> mapY) {
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(margin, margin, width - 2 * margin, height - 2 * margin);

        g2.setColor(new Color(225, 225, 225));
        double stepX = obtenerPaso(maxX - minX);
        for (double x = Math.ceil(minX / stepX) * stepX; x <= maxX; x += stepX) {
            int px = (int) Math.round(mapX.apply(x));
            g2.drawLine(px, margin, px, height - margin);
        }

        double stepY = obtenerPaso(maxY - minY);
        for (double y = Math.ceil(minY / stepY) * stepY; y <= maxY; y += stepY) {
            int py = (int) Math.round(mapY.apply(y));
            g2.drawLine(margin, py, width - margin, py);
        }

        g2.setColor(new Color(200, 200, 200));
        g2.drawRect(margin, margin, width - 2 * margin, height - 2 * margin);
    }

    private double obtenerPaso(double rango) {
        if (rango == 0) return 1;

        double base = Math.pow(10, Math.floor(Math.log10(rango)) - 1);
        double[] posibles = {1, 2, 5};
        for (double factor : posibles) {
            if (rango / (factor * base) <= 10) {
                return factor * base;
            }
        }
        return base * 10;
    }

    private void dibujarAreaBajoCurva(Graphics2D g2,
                                      DoubleFunction<Double> mapX,
                                      DoubleFunction<Double> mapY,
                                      double minX,
                                      double maxX) {
        if (integral == null) {
            return;
        }

        int samples = 300;
        Path2D area = new Path2D.Double();
        boolean started = false;

        area.moveTo(mapX.apply(minX), mapY.apply(0));

        for (int i = 0; i < samples; i++) {
            double x = minX + i * (maxX - minX) / (samples - 1);
            double y = integral.evaluarIntegrando(x);

            if (!Double.isFinite(y)) {
                continue;
            }

            double px = mapX.apply(x);
            double py = mapY.apply(y);

            if (!started) {
                started = true;
            }
            area.lineTo(px, py);
        }

        if (!started) {
            return;
        }

        area.lineTo(mapX.apply(maxX), mapY.apply(0));
        area.closePath();

        g2.setColor(new Color(76, 175, 80, 90));
        g2.fill(area);
    }
}
