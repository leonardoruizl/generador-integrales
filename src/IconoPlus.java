import javax.swing.*;
import java.awt.*;

public class IconoPlus implements Icon {
    private final int size;
    private final Color color;
    private final BasicStroke stroke;

    public IconoPlus(int size, Color color, float thickness) {
        this.size = size;
        this.color = color;
        this.stroke = new BasicStroke(thickness);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Activar Antialiasing para bordes suaves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(color);
        g2d.setStroke(stroke);

        // Calcular coordenadas para centrar el "+"
        int padding = size / 5; // Un pequeño margen
        int x_center = x + size / 2;
        int y_center = y + size / 2;
        int lineLength = size - (padding * 2);

        // Dibujar línea horizontal
        g2d.drawLine(x + padding, y_center, x + padding + lineLength, y_center);

        // Dibujar línea vertical
        g2d.drawLine(x_center, y + padding, x_center, y + padding + lineLength);

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