package com.ui;

import com.model.Integral;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
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
    private boolean vistaInicializada;
    private Point ultimoArrastre;
    private final int margen = 40;

    public PanelGrafica() {
        setPreferredSize(new Dimension(550, 220));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
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

        int muestras = 220;
        for (int i = 0; i < muestras; i++) {
            double x = vistaMinX + i * (vistaMaxX - vistaMinX) / (muestras - 1);
            double y = integral.evaluarIntegrando(x);
            if (Double.isFinite(y)) {
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

        double baseY = mapY.apply(0);
        dibujarBarrasIntegrales(g2, mapX, mapY, baseY);
        dibujarCurva(g2, xs, ys, mapX, mapY);

        g2.dispose();
    }

    private void dibujarCurva(Graphics2D g2, List<Double> xs, List<Double> ys,
                               java.util.function.DoubleFunction<Double> mapX,
                               java.util.function.DoubleFunction<Double> mapY) {
        g2.setColor(new Color(33, 150, 243));
        g2.setStroke(new BasicStroke(2f));
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

    private void dibujarBarrasIntegrales(Graphics2D g2,
                                         java.util.function.DoubleFunction<Double> mapX,
                                         java.util.function.DoubleFunction<Double> mapY,
                                         double baseY) {
        double inicio = Math.min(limiteInferior, limiteSuperior);
        double fin = Math.max(limiteInferior, limiteSuperior);

        if (inicio == fin) {
            return;
        }

        int barras = 60;
        double anchoPaso = (fin - inicio) / barras;
        double opacidad = 90;
        g2.setColor(new Color(33, 150, 243, (int) opacidad));

        for (int i = 0; i < barras; i++) {
            double xCentro = inicio + (i + 0.5) * anchoPaso;
            if (xCentro < vistaMinX || xCentro > vistaMaxX) {
                continue;
            }

            double valor = integral.evaluarIntegrando(xCentro);
            if (!Double.isFinite(valor)) {
                continue;
            }

            double altura = mapY.apply(valor);
            double base = baseY;
            double x1 = mapX.apply(xCentro - anchoPaso / 2.0);
            double x2 = mapX.apply(xCentro + anchoPaso / 2.0);

            double y1 = Math.min(base, altura);
            double y2 = Math.max(base, altura);

            Rectangle barra = new Rectangle((int) Math.round(x1), (int) Math.round(y1),
                    (int) Math.max(1, Math.round(x2 - x1)), (int) Math.max(1, Math.round(y2 - y1)));
            g2.fill(barra);
        }
    }

    private void dibujarGrilla(Graphics2D g2, int width, int height,
                               java.util.function.DoubleFunction<Double> mapX,
                               java.util.function.DoubleFunction<Double> mapY) {
        double pasoX = calcularPaso(vistaMaxX - vistaMinX);
        double pasoY = calcularPaso(vistaMaxY - vistaMinY);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(230, 230, 230));

        for (double x = Math.ceil(vistaMinX / pasoX) * pasoX; x <= vistaMaxX; x += pasoX) {
            int px = (int) Math.round(mapX.apply(x));
            g2.drawLine(px, margen, px, height - margen);
        }

        for (double y = Math.ceil(vistaMinY / pasoY) * pasoY; y <= vistaMaxY; y += pasoY) {
            int py = (int) Math.round(mapY.apply(y));
            g2.drawLine(margen, py, width - margen, py);
        }

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(120, 120, 120));

        if (0 >= vistaMinY && 0 <= vistaMaxY) {
            int y0 = (int) Math.round(mapY.apply(0));
            g2.drawLine(margen, y0, width - margen, y0);
        }
        if (0 >= vistaMinX && 0 <= vistaMaxX) {
            int x0 = (int) Math.round(mapX.apply(0));
            g2.drawLine(x0, margen, x0, height - margen);
        }
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
        g2.setColor(new Color(248, 250, 253));
        g2.fill(contorno);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(200, 208, 218));
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
            if (Double.isFinite(y)) {
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
