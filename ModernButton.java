package TiwalApp.ui.components;

import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {
    private Color backgroundColor;

    public ModernButton(String text, Color backgroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        initButton();
    }

    private void initButton() {
        setFont(Constants.BUTTON_FONT);
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(180, 50));
        setMinimumSize(new Dimension(150, 45));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2d.setColor(backgroundColor.darker().darker());
        } else if (getModel().isRollover()) {
            g2d.setColor(backgroundColor.darker());
        } else {
            g2d.setColor(backgroundColor);
        }

        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getHeight();
        g2d.drawString(getText(),
                (getWidth() - textWidth) / 2,
                (getHeight() + textHeight) / 2 - fm.getDescent());

        g2d.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(backgroundColor.darker());
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
        g2d.dispose();
    }
}