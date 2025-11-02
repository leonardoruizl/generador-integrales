import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelAppBar extends JPanel {
    public PanelAppBar(ActionListener nuevaIntegralListener) {
        super(new BorderLayout());
        setBackground(new Color(45, 45, 45));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel titulo = new JLabel("Generador de Integrales", SwingConstants.LEFT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton botonNuevaIntegral = new JButton();
        botonNuevaIntegral.setToolTipText("Generar nueva integral");

        IconoPlus icono = new IconoPlus(18, Color.BLACK, 2.5f);
        botonNuevaIntegral.setIcon(icono);

        botonNuevaIntegral.setBackground(Color.WHITE);
        botonNuevaIntegral.setOpaque(true);
        botonNuevaIntegral.setFocusPainted(false);
        botonNuevaIntegral.setBorder(BorderFactory.createEmptyBorder());

        int iconSize = icono.getIconWidth();

        botonNuevaIntegral.setPreferredSize(new Dimension(iconSize + 12, iconSize + 12)); // Ícono + padding
        botonNuevaIntegral.addActionListener(nuevaIntegralListener);

        add(titulo, BorderLayout.WEST);
        add(botonNuevaIntegral, BorderLayout.EAST);
    }
}
