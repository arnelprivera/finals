package TiwalApp.ui.components;

import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;

public class GlassPanel extends JPanel {

    public GlassPanel() {
        super();
        setOpaque(false);
    }

    public GlassPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
                0, 0, Constants.GRADIENT_START,
                getWidth(), getHeight(), Constants.GRADIENT_END
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
        g2d.setColor(Color.WHITE);
        int size = 60;
        for (int x = 0; x < getWidth(); x += size) {
            for (int y = 0; y < getHeight(); y += size) {
                if ((x/size + y/size) % 2 == 0) {
                    g2d.fillOval(x + 10, y + 10, size - 20, size - 20);
                }
            }
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}