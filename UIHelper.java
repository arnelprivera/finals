package TiwalApp.ui.components;

import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;

public class UIHelper {

    public static void styleModernTextField(JTextField field) {
        field.setFont(Constants.NORMAL_FONT);
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(new Color(255, 255, 255, 220));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 150), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setOpaque(true);
    }

    public static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, Constants.PRIMARY_DARK,
                        getWidth(), 0, Constants.PRIMARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2d.setColor(Color.WHITE);
                for (int i = 0; i < getWidth(); i += 20) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        };
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return menuBar;
    }

    public static JMenu createModernMenu(String text, Color color) {
        JMenu menu = new JMenu(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected()) {
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        menu.setForeground(color);
        menu.setFont(Constants.BOLD_FONT);
        menu.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return menu;
    }

    public static JMenuItem createModernMenuItem(String text, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isArmed()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Constants.PRIMARY_LIGHT);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        item.setForeground(Constants.TEXT_PRIMARY);
        item.setFont(Constants.NORMAL_FONT);
        item.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        item.addActionListener(listener);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                item.setBackground(Constants.PRIMARY_LIGHT);
                item.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                item.setBackground(Color.WHITE);
                item.setForeground(Constants.TEXT_PRIMARY);
            }
        });

        return item;
    }

    public static JLabel createModernLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.setFont(getFont());
                g2d.drawString(getText(), 1, getHeight() - g2d.getFontMetrics().getDescent() + 1);

                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, getHeight() - g2d.getFontMetrics().getDescent());

                g2d.dispose();
            }
        };
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
}