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

        try {
            ImageIcon icono = new ImageIcon("src/icons/add_circle.png");
            botonNuevaIntegral.setIcon(icono);
        } catch (Exception e) {
            botonNuevaIntegral.setText("Nueva Integral");
        }

        botonNuevaIntegral.setBackground(Color.WHITE);
        botonNuevaIntegral.setForeground(Color.BLACK);
        botonNuevaIntegral.setFocusPainted(false);
        botonNuevaIntegral.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        botonNuevaIntegral.addActionListener(nuevaIntegralListener);

        add(titulo, BorderLayout.WEST);
        add(botonNuevaIntegral, BorderLayout.EAST);
    }
}
