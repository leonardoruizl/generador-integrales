package com.ui;

import com.model.Integral;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PanelGrafica extends JPanel {
    private static final Color COLOR_FONDO = new Color(248, 249, 252);
    private static final Color COLOR_CONTORNO = new Color(200, 208, 218);
    private static final Color COLOR_GRILLA = new Color(225, 228, 235);
    private static final Color COLOR_GRILLA_SUAVE = new Color(235, 238, 244);
    private static final Color COLOR_EJES = new Color(40, 40, 40);
    private static final Color COLOR_CURVA = new Color(33, 150, 243);
    private static final Color COLOR_AREA = new Color(120, 200, 120, 120);
    private static final Color COLOR_AREA_BORDE = new Color(76, 175, 80, 180);
    private static final Color COLOR_LIMITE = new Color(72, 104, 173, 190);
    private static final Stroke TRAZO_CURVA = new BasicStroke(2.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke TRAZO_EJES = new BasicStroke(2.4f);
    private static final Stroke TRAZO_GRILLA = new BasicStroke(1f);
    private static final Stroke TRAZO_GRILLA_SUAVE = new BasicStroke(0.6f);
    private static final double LIMITE_VALOR_Y = 1e4;
    private final DecimalFormat formato = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private Integral integral;
    private double limiteInferior;
    private double limiteSuperior;
    private double vistaMinX;
    private double vistaMaxX;
    private double vistaMinY;
    private double vistaMaxY;
    private boolean vistaInicializada;
    private Point ultimoArrastre;
    private final int margen = 48;

    public PanelGrafica() {
        setPreferredSize(new Dimension(620, 620));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 224)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        MouseAdapter manejador = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ultimoArrastre = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ultimoArrastre = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (ultimoArrastre == null) {
                    return;
                }

                double dx = e.getX() - ultimoArrastre.x;
                double dy = e.getY() - ultimoArrastre.y;

                double escalaX = (vistaMaxX - vistaMinX) / (getWidth() - 2.0 * margen);
                double escalaY = (vistaMaxY - vistaMinY) / (getHeight() - 2.0 * margen);

                vistaMinX -= dx * escalaX;
                vistaMaxX -= dx * escalaX;
                vistaMinY += dy * escalaY;
                vistaMaxY += dy * escalaY;

                ultimoArrastre = e.getPoint();
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = Math.pow(1.1, e.getPreciseWheelRotation());

                double puntoX = pantallaAX(e.getX());
                double puntoY = pantallaAY(e.getY());

                double nuevoAncho = (vistaMaxX - vistaMinX) * factor;
                double nuevoAlto = (vistaMaxY - vistaMinY) * factor;

                vistaMinX = puntoX + (vistaMinX - puntoX) * factor;
                vistaMaxX = vistaMinX + nuevoAncho;

                vistaMinY = puntoY + (vistaMinY - puntoY) * factor;
                vistaMaxY = vistaMinY + nuevoAlto;

                limitarZoom();
                repaint();
            }
        };

        addMouseListener(manejador);
        addMouseMotionListener(manejador);
        addMouseWheelListener(manejador);
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

        if (!vistaInicializada) {
            inicializarVista();
        }

        int width = getWidth();
        int height = getHeight();

        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();

        int muestras = 420;
        for (int i = 0; i < muestras; i++) {
            double x = vistaMinX + i * (vistaMaxX - vistaMinX) / (muestras - 1);
            double y = integral.evaluarIntegrando(x);
            if (Double.isFinite(y) && Math.abs(y) <= LIMITE_VALOR_Y) {
                xs.add(x);
                ys.add(y);
            } else {
                xs.add(x);
                ys.add(Double.NaN);
            }
        }

        if (xs.isEmpty()) {
            dibujarMensaje(g2, "No hay valores válidos en el dominio para graficar.");
            g2.dispose();
            return;
        }

        java.util.function.DoubleFunction<Double> mapX = x -> margen + (x - vistaMinX) * (width - 2.0 * margen) / (vistaMaxX - vistaMinX);
        java.util.function.DoubleFunction<Double> mapY = y -> height - margen - (y - vistaMinY) * (height - 2.0 * margen) / (vistaMaxY - vistaMinY);

        dibujarContorno(g2, width, height);
        dibujarGrilla(g2, width, height, mapX, mapY);

        dibujarLimitesIntegracion(g2, mapX, height);
        double baseY = mapY.apply(0);
        dibujarAreaIntegral(g2, mapX, mapY, baseY);
        dibujarCurva(g2, xs, ys, mapX, mapY);

        g2.dispose();
    }

    private void dibujarCurva(Graphics2D g2, List<Double> xs, List<Double> ys,
                               java.util.function.DoubleFunction<Double> mapX,
                               java.util.function.DoubleFunction<Double> mapY) {
        g2.setColor(COLOR_CURVA);
        g2.setStroke(TRAZO_CURVA);
        Path2D path = new Path2D.Double();
        boolean started = false;

        for (int i = 0; i < xs.size(); i++) {
            double y = ys.get(i);
            if (!Double.isFinite(y)) {
                started = false;
                continue;
            }
            double px = mapX.apply(xs.get(i));
            double py = mapY.apply(y);
            if (!started) {
                path.moveTo(px, py);
                started = true;
            } else {
                path.lineTo(px, py);
            }
        }

        g2.draw(path);
    }

    private void dibujarAreaIntegral(Graphics2D g2,
                                     java.util.function.DoubleFunction<Double> mapX,
                                     java.util.function.DoubleFunction<Double> mapY,
                                     double baseY) {
        double inicio = Math.min(limiteInferior, limiteSuperior);
        double fin = Math.max(limiteInferior, limiteSuperior);

        if (inicio == fin) {
            return;
        }

        int pasos = 260;
        double paso = (fin - inicio) / pasos;

        GradientPaint relleno = new GradientPaint(0, 0, COLOR_AREA, 0, (float) getHeight(), new Color(120, 200, 120, 30));
        g2.setPaint(relleno);
        g2.setStroke(new BasicStroke(1.2f));

        Path2D area = new Path2D.Double();
        boolean enTrazo = false;
        double ultimoX = 0;

        for (int i = 0; i <= pasos; i++) {
            double x = inicio + i * paso;
            double valor = integral.evaluarIntegrando(x);

            if (!Double.isFinite(valor) || Math.abs(valor) > LIMITE_VALOR_Y) {
                if (enTrazo) {
                    area.lineTo(mapX.apply(ultimoX), baseY);
                    g2.fill(area);
                    g2.setColor(COLOR_AREA_BORDE);
                    g2.draw(area);
                    g2.setPaint(relleno);
                    enTrazo = false;
                    area.reset();
                }
                continue;
            }

            double px = mapX.apply(x);
            double py = mapY.apply(valor);

            if (!enTrazo) {
                area.moveTo(px, baseY);
                enTrazo = true;
            }

            area.lineTo(px, py);
            ultimoX = x;
        }

        if (enTrazo) {
            area.lineTo(mapX.apply(fin), baseY);
            area.closePath();
            g2.fill(area);
            g2.setColor(COLOR_AREA_BORDE);
            g2.draw(area);
        }
    }

    private void dibujarLimitesIntegracion(Graphics2D g2,
                                           java.util.function.DoubleFunction<Double> mapX,
                                           int height) {
        double inicio = Math.min(limiteInferior, limiteSuperior);
        double fin = Math.max(limiteInferior, limiteSuperior);

        Stroke previo = g2.getStroke();
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[]{6f, 6f}, 0f));
        g2.setColor(COLOR_LIMITE);

        double[] limites = {inicio, fin};
        for (double limite : limites) {
            double px = mapX.apply(limite);
            g2.drawLine((int) Math.round(px), margen, (int) Math.round(px), height - margen);
            dibujarEtiquetaLimite(g2, formato.format(limite), px, margen + 6);
        }

        g2.setStroke(previo);
    }

    private void dibujarEtiquetaLimite(Graphics2D g2, String texto, double x, int y) {
        Font original = g2.getFont();
        g2.setFont(original.deriveFont(Font.BOLD, 11f));
        FontMetrics fm = g2.getFontMetrics();
        int ancho = fm.stringWidth(texto) + 8;
        int alto = fm.getHeight();
        int dibujarX = (int) Math.round(x - ancho / 2.0);
        int dibujarY = y;

        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect(dibujarX, dibujarY - alto + 2, ancho, alto, 10, 10);
        g2.setColor(COLOR_LIMITE);
        g2.drawRoundRect(dibujarX, dibujarY - alto + 2, ancho, alto, 10, 10);
        g2.drawString(texto, dibujarX + 4, dibujarY);
        g2.setFont(original);
    }

    private void dibujarGrilla(Graphics2D g2, int width, int height,
                               java.util.function.DoubleFunction<Double> mapX,
                               java.util.function.DoubleFunction<Double> mapY) {
        double pasoX = calcularPaso(vistaMaxX - vistaMinX);
        double pasoY = calcularPaso(vistaMaxY - vistaMinY);

        dibujarSubGrilla(g2, width, height, mapX, mapY, pasoX / 2.0, pasoY / 2.0);

        g2.setStroke(TRAZO_GRILLA);
        g2.setColor(COLOR_GRILLA);

        for (double x = Math.ceil(vistaMinX / pasoX) * pasoX; x <= vistaMaxX; x += pasoX) {
            int px = (int) Math.round(mapX.apply(x));
            g2.drawLine(px, margen, px, height - margen);
            dibujarEtiqueta(g2, formato.format(x), px, height - margen + 18, true);
        }

        for (double y = Math.ceil(vistaMinY / pasoY) * pasoY; y <= vistaMaxY; y += pasoY) {
            int py = (int) Math.round(mapY.apply(y));
            g2.drawLine(margen, py, width - margen, py);
            dibujarEtiqueta(g2, formato.format(y), margen - 12, py + 4, false);
        }

        g2.setStroke(TRAZO_EJES);
        g2.setColor(COLOR_EJES);

        if (0 >= vistaMinY && 0 <= vistaMaxY) {
            int y0 = (int) Math.round(mapY.apply(0));
            g2.drawLine(margen, y0, width - margen + 8, y0);
            dibujarFlecha(g2, width - margen + 8, y0, true);
        }
        if (0 >= vistaMinX && 0 <= vistaMaxX) {
            int x0 = (int) Math.round(mapX.apply(0));
            g2.drawLine(x0, height - margen, x0, margen - 8);
            dibujarFlecha(g2, x0, margen - 8, false);
        }
    }

    private void dibujarSubGrilla(Graphics2D g2, int width, int height,
                                  java.util.function.DoubleFunction<Double> mapX,
                                  java.util.function.DoubleFunction<Double> mapY,
                                  double pasoX, double pasoY) {
        g2.setStroke(TRAZO_GRILLA_SUAVE);
        g2.setColor(COLOR_GRILLA_SUAVE);

        for (double x = Math.ceil(vistaMinX / pasoX) * pasoX; x <= vistaMaxX; x += pasoX) {
            int px = (int) Math.round(mapX.apply(x));
            g2.drawLine(px, margen, px, height - margen);
        }

        for (double y = Math.ceil(vistaMinY / pasoY) * pasoY; y <= vistaMaxY; y += pasoY) {
            int py = (int) Math.round(mapY.apply(y));
            g2.drawLine(margen, py, width - margen, py);
        }
    }

    private void dibujarEtiqueta(Graphics2D g2, String texto, int x, int y, boolean esEjeX) {
        if ("0".equals(texto) || "0.0".equals(texto)) {
            return;
        }
        Font fuenteOriginal = g2.getFont();
        g2.setFont(fuenteOriginal.deriveFont(Font.PLAIN, 11f));
        g2.setColor(new Color(90, 90, 90));

        int ancho = g2.getFontMetrics().stringWidth(texto);
        if (esEjeX) {
            g2.drawString(texto, x - ancho / 2, y);
        } else {
            g2.drawString(texto, x - ancho - 4, y + 4);
        }
        g2.setFont(fuenteOriginal);
    }

    private void dibujarFlecha(Graphics2D g2, int x, int y, boolean horizontal) {
        int tamaño = 8;
        Polygon flecha = new Polygon();
        if (horizontal) {
            flecha.addPoint(x, y);
            flecha.addPoint(x - tamaño, y - tamaño / 2);
            flecha.addPoint(x - tamaño, y + tamaño / 2);
        } else {
            flecha.addPoint(x, y);
            flecha.addPoint(x - tamaño / 2, y + tamaño);
            flecha.addPoint(x + tamaño / 2, y + tamaño);
        }
        g2.fillPolygon(flecha);
    }

    private double calcularPaso(double rango) {
        double objetivoLineas = 8;
        double pasoBase = Math.pow(10, Math.floor(Math.log10(rango / objetivoLineas)));
        double[] multiplicadores = {1, 2, 5};

        for (double m : multiplicadores) {
            double paso = pasoBase * m;
            if (rango / paso <= objetivoLineas + 2) {
                return paso;
            }
        }

        return pasoBase * 10;
    }

    private void dibujarContorno(Graphics2D g2, int width, int height) {
        Shape contorno = new RoundRectangle2D.Double(8, 8, width - 16, height - 16, 18, 18);
        GradientPaint fondo = new GradientPaint(0, 0, new Color(253, 254, 255), 0, height,
                new Color(243, 246, 251));
        g2.setPaint(fondo);
        g2.fill(contorno);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(COLOR_CONTORNO);
        g2.draw(contorno);
    }

    private void inicializarVista() {
        double minX = Math.min(limiteInferior, limiteSuperior);
        double maxX = Math.max(limiteInferior, limiteSuperior);
        if (minX == maxX) {
            minX -= 1;
            maxX += 1;
        }

        double rellenoX = Math.max(1, (maxX - minX) * 0.2);
        vistaMinX = minX - rellenoX;
        vistaMaxX = maxX + rellenoX;

        double[] rangoY = calcularRangoY(vistaMinX, vistaMaxX);
        double minY = rangoY[0];
        double maxY = rangoY[1];

        if (minY > 0) minY = 0;
        if (maxY < 0) maxY = 0;
        if (minY == maxY) {
            minY -= 1;
            maxY += 1;
        }

        double rellenoY = Math.max(1, (maxY - minY) * 0.2);
        vistaMinY = minY - rellenoY;
        vistaMaxY = maxY + rellenoY;

        vistaInicializada = true;
    }

    private double[] calcularRangoY(double minX, double maxX) {
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        int muestras = 200;

        for (int i = 0; i < muestras; i++) {
            double x = minX + i * (maxX - minX) / (muestras - 1);
            double y = integral.evaluarIntegrando(x);
            if (Double.isFinite(y) && Math.abs(y) <= LIMITE_VALOR_Y) {
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        if (minY == Double.POSITIVE_INFINITY || maxY == Double.NEGATIVE_INFINITY) {
            minY = -1;
            maxY = 1;
        }

        return new double[]{minY, maxY};
    }

    private double pantallaAX(double px) {
        double proporcion = (px - margen) / (getWidth() - 2.0 * margen);
        return vistaMinX + proporcion * (vistaMaxX - vistaMinX);
    }

    private double pantallaAY(double py) {
        double proporcion = (py - margen) / (getHeight() - 2.0 * margen);
        return vistaMaxY - proporcion * (vistaMaxY - vistaMinY);
    }

    private void limitarZoom() {
        double minimo = 0.1;
        double rangoX = vistaMaxX - vistaMinX;
        double rangoY = vistaMaxY - vistaMinY;

        if (rangoX < minimo) {
            double centroX = (vistaMaxX + vistaMinX) / 2.0;
            vistaMinX = centroX - minimo / 2.0;
            vistaMaxX = centroX + minimo / 2.0;
        }

        if (rangoY < minimo) {
            double centroY = (vistaMaxY + vistaMinY) / 2.0;
            vistaMinY = centroY - minimo / 2.0;
            vistaMaxY = centroY + minimo / 2.0;
        }
    }

    private void dibujarMensaje(Graphics2D g2, String mensaje) {
        g2.setColor(new Color(80, 80, 80));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 4;
        g2.drawString(mensaje, x, y);
    }
}
