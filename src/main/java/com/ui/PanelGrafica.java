package com.ui;

import com.model.Integral;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

public class PanelGrafica extends JPanel {
    private Integral integral;
    private double limiteInferior;
    private double limiteSuperior;
    private double vistaMinX;
    private double vistaMaxX;
    private double vistaMinY;
    private double vistaMaxY;
    private Point ultimoArrastre;

    public PanelGrafica() {
        setPreferredSize(new Dimension(550, 220));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        MouseAdapter manejadorRaton = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ultimoArrastre = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (ultimoArrastre == null) return;
                double dx = e.getX() - ultimoArrastre.getX();
                double dy = e.getY() - ultimoArrastre.getY();
                ultimoArrastre = e.getPoint();

                double rangoX = vistaMaxX - vistaMinX;
                double rangoY = vistaMaxY - vistaMinY;
                double ancho = getWidth() - 80.0;
                double alto = getHeight() - 80.0;

                if (ancho > 0 && alto > 0) {
                    double deltaX = -dx * rangoX / ancho;
                    double deltaY = dy * rangoY / alto;
                    vistaMinX += deltaX;
                    vistaMaxX += deltaX;
                    vistaMinY += deltaY;
                    vistaMaxY += deltaY;
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = e.getPreciseWheelRotation() > 0 ? 1.1 : 0.9;
                double cursorX = e.getX();
                double cursorY = e.getY();

                double xCoord = pantallaAX(margin(), cursorX);
                double yCoord = pantallaAY(margin(), cursorY);

                escalarVista(xCoord, yCoord, factor);
            }
        };

        addMouseListener(manejadorRaton);
        addMouseMotionListener(manejadorRaton);
        addMouseWheelListener(manejadorRaton);
    }

    public void actualizarIntegral(Integral integral, double limiteInferior, double limiteSuperior) {
        this.integral = integral;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        inicializarVista();
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

        double minX = vistaMinX;
        double maxX = vistaMaxX;

        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();

        int width = getWidth();
        int height = getHeight();
        int margin = margin();

        java.util.function.DoubleFunction<Double> mapX = x -> margin + (x - vistaMinX) * (width - 2.0 * margin) / (vistaMaxX - vistaMinX);
        java.util.function.DoubleFunction<Double> mapY = y -> height - margin - (y - vistaMinY) * (height - 2.0 * margin) / (vistaMaxY - vistaMinY);

        dibujarGrilla(g2, mapX, mapY, width, height, margin);
        dibujarEjes(g2, mapX, mapY, width, height, margin);

        int samples = 400;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
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

        double baseY = mapY.apply(0);

        dibujarArea(g2, mapX, mapY, baseY);
        dibujarCurva(g2, mapX, mapY, xs, ys);

        g2.dispose();
    }

    private void dibujarMensaje(Graphics2D g2, String mensaje) {
        g2.setColor(new Color(80, 80, 80));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 4;
        g2.drawString(mensaje, x, y);
    }

    private void inicializarVista() {
        double minX = Math.min(limiteInferior, limiteSuperior);
        double maxX = Math.max(limiteInferior, limiteSuperior);
        if (minX == maxX) {
            minX -= 1;
            maxX += 1;
        }

        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        int samples = 200;
        for (int i = 0; i < samples; i++) {
            double x = minX + i * (maxX - minX) / (samples - 1);
            double y = integral.evaluarIntegrando(x);
            if (Double.isFinite(y)) {
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        if (minY == Double.POSITIVE_INFINITY || maxY == Double.NEGATIVE_INFINITY) {
            minY = -1;
            maxY = 1;
        }

        if (minY > 0) minY = 0;
        if (maxY < 0) maxY = 0;
        if (minY == maxY) {
            minY -= 1;
            maxY += 1;
        }

        double paddingX = (maxX - minX) * 0.1;
        double paddingY = (maxY - minY) * 0.1;
        if (paddingX == 0) paddingX = 1;
        if (paddingY == 0) paddingY = 1;

        vistaMinX = minX - paddingX;
        vistaMaxX = maxX + paddingX;
        vistaMinY = minY - paddingY;
        vistaMaxY = maxY + paddingY;
    }

    private void dibujarGrilla(Graphics2D g2, java.util.function.DoubleFunction<Double> mapX,
                               java.util.function.DoubleFunction<Double> mapY, int width, int height, int margin) {
        g2.setColor(new Color(240, 240, 240));
        double pasoX = calcularPaso(vistaMinX, vistaMaxX, width - 2.0 * margin);
        double pasoY = calcularPaso(vistaMinY, vistaMaxY, height - 2.0 * margin);

        double inicioX = Math.ceil(vistaMinX / pasoX) * pasoX;
        for (double x = inicioX; x <= vistaMaxX; x += pasoX) {
            int px = (int) Math.round(mapX.apply(x));
            g2.drawLine(px, margin, px, height - margin);
        }

        double inicioY = Math.ceil(vistaMinY / pasoY) * pasoY;
        for (double y = inicioY; y <= vistaMaxY; y += pasoY) {
            int py = (int) Math.round(mapY.apply(y));
            g2.drawLine(margin, py, width - margin, py);
        }
    }

    private void dibujarEjes(Graphics2D g2, java.util.function.DoubleFunction<Double> mapX,
                             java.util.function.DoubleFunction<Double> mapY, int width, int height, int margin) {
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(margin - 5, margin - 5, width - 2 * margin + 10, height - 2 * margin + 10, 12, 12);

        g2.setStroke(new BasicStroke(1.4f));
        g2.setColor(new Color(100, 100, 100));
        if (0 >= vistaMinY && 0 <= vistaMaxY) {
            int y0 = (int) Math.round(mapY.apply(0));
            g2.drawLine(margin, y0, width - margin, y0);
        }
        if (0 >= vistaMinX && 0 <= vistaMaxX) {
            int x0 = (int) Math.round(mapX.apply(0));
            g2.drawLine(x0, margin, x0, height - margin);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void dibujarCurva(Graphics2D g2, java.util.function.DoubleFunction<Double> mapX,
                              java.util.function.DoubleFunction<Double> mapY, List<Double> xs, List<Double> ys) {
        g2.setColor(new Color(33, 150, 243));
        g2.setStroke(new BasicStroke(2f));
        Path2D path = new Path2D.Double();
        boolean started = false;
        for (int i = 0; i < xs.size(); i++) {
            double px = mapX.apply(xs.get(i));
            double py = mapY.apply(ys.get(i));
            if (!started) {
                path.moveTo(px, py);
                started = true;
            } else {
                path.lineTo(px, py);
            }
        }
        g2.draw(path);
    }

    private void dibujarArea(Graphics2D g2, java.util.function.DoubleFunction<Double> mapX,
                             java.util.function.DoubleFunction<Double> mapY, double baseY) {
        double min = Math.min(limiteInferior, limiteSuperior);
        double max = Math.max(limiteInferior, limiteSuperior);
        int columnas = 120;
        double paso = (max - min) / columnas;

        g2.setColor(new Color(173, 214, 255, 120));
        for (int i = 0; i < columnas; i++) {
            double x = min + i * paso;
            double xSiguiente = x + paso;
            double y = integral.evaluarIntegrando(x);
            double ySiguiente = integral.evaluarIntegrando(xSiguiente);
            if (!Double.isFinite(y) || !Double.isFinite(ySiguiente)) continue;
            double px = mapX.apply(x);
            double px2 = mapX.apply(xSiguiente);
            double py = mapY.apply(y);
            double py2 = mapY.apply(ySiguiente);

            g2.drawLine((int) px, (int) baseY, (int) px, (int) py);
            g2.drawLine((int) px2, (int) baseY, (int) px2, (int) py2);
            Path2D barra = new Path2D.Double();
            barra.moveTo(px, baseY);
            barra.lineTo(px, py);
            barra.lineTo(px2, py2);
            barra.lineTo(px2, baseY);
            barra.closePath();
            g2.fill(barra);
        }
    }

    private double calcularPaso(double min, double max, double pixelDisponible) {
        double rango = max - min;
        if (rango <= 0 || pixelDisponible <= 0) return 1;
        double objetivo = pixelDisponible / 10.0;
        double pasoAproximado = rango / objetivo;
        double exponente = Math.floor(Math.log10(pasoAproximado));
        double base = Math.pow(10, exponente);
        double[] candidatos = {base, 2 * base, 5 * base, 10 * base};
        double mejor = candidatos[0];
        double diferencia = Math.abs(pasoAproximado - candidatos[0]);
        for (double candidato : candidatos) {
            double diff = Math.abs(pasoAproximado - candidato);
            if (diff < diferencia) {
                diferencia = diff;
                mejor = candidato;
            }
        }
        return mejor;
    }

    private double pantallaAX(int margin, double xPantalla) {
        double ancho = getWidth() - 2.0 * margin;
        if (ancho <= 0) return 0;
        return vistaMinX + (xPantalla - margin) * (vistaMaxX - vistaMinX) / ancho;
    }

    private double pantallaAY(int margin, double yPantalla) {
        double alto = getHeight() - 2.0 * margin;
        if (alto <= 0) return 0;
        return vistaMaxY - (yPantalla - margin) * (vistaMaxY - vistaMinY) / alto;
    }

    private void escalarVista(double centroX, double centroY, double factor) {
        double rangoX = vistaMaxX - vistaMinX;
        double rangoY = vistaMaxY - vistaMinY;
        double nuevoRangoX = rangoX * factor;
        double nuevoRangoY = rangoY * factor;

        vistaMinX = centroX - (centroX - vistaMinX) * (nuevoRangoX / rangoX);
        vistaMaxX = centroX + (vistaMaxX - centroX) * (nuevoRangoX / rangoX);
        vistaMinY = centroY - (centroY - vistaMinY) * (nuevoRangoY / rangoY);
        vistaMaxY = centroY + (vistaMaxY - centroY) * (nuevoRangoY / rangoY);
        repaint();
    }

    private int margin() {
        return 40;
    }
}
