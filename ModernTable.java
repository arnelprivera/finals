package TiwalApp.ui.components;

import TiwalApp.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable() {
        initTable();
    }

    public ModernTable(DefaultTableModel model) {
        super(model);
        initTable();
    }

    private void initTable() {
        setRowHeight(40);
        setFont(Constants.TABLE_FONT);
        setForeground(Constants.TEXT_PRIMARY);
        setBackground(new Color(255, 255, 255, 220));
        setGridColor(new Color(200, 200, 200, 100));
        setShowGrid(true);
        setSelectionBackground(new Color(
                Constants.PRIMARY_COLOR.getRed(),
                Constants.PRIMARY_COLOR.getGreen(),
                Constants.PRIMARY_COLOR.getBlue(),
                150));
        setSelectionForeground(Color.WHITE);
        setIntercellSpacing(new Dimension(0, 0));

        // Custom header renderer
        JTableHeader header = getTableHeader();
        header.setBackground(Constants.PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(Constants.TABLE_HEADER_FONT);
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new ModernTableHeaderRenderer());

        // Custom cell renderer
        setDefaultRenderer(Object.class, new ModernTableCellRenderer());
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        if (c instanceof JComponent) {
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        return c;
    }

    // Custom header renderer with gradient
    private static class ModernTableHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setFont(Constants.TABLE_HEADER_FONT);

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create gradient background
            GradientPaint gradient = new GradientPaint(
                    0, 0, Constants.PRIMARY_COLOR,
                    getWidth(), 0, Constants.PRIMARY_DARK
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw text
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
    }

    // Custom cell renderer with alternating colors
    private static class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setOpaque(false);
            setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                if (row % 2 == 0) {
                    setBackground(new Color(255, 255, 255, 200));
                } else {
                    setBackground(new Color(245, 245, 255, 200));
                }
                setForeground(Constants.TEXT_PRIMARY);
            }

            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill background with transparency
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw text
            g2d.setColor(getForeground());
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int textHeight = fm.getHeight();

            String text = getText();
            if (text != null) {
                // Handle text that's too long
                int maxWidth = getWidth() - 20; // 10px padding on each side
                if (fm.stringWidth(text) > maxWidth) {
                    while (text.length() > 3 && fm.stringWidth(text + "...") > maxWidth) {
                        text = text.substring(0, text.length() - 1);
                    }
                    text = text + "...";
                }

                g2d.drawString(text, 10, (getHeight() + textHeight) / 2 - fm.getDescent());
            }

            g2d.dispose();
        }
    }

    public void setColumnWidths(int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            if (i < getColumnModel().getColumnCount()) {
                getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    public void autoResizeColumns() {
        for (int column = 0; column < getColumnCount(); column++) {
            int width = 50; // Minimum width
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer renderer = getCellRenderer(row, column);
                Component comp = prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 20, width);
            }
            if (width > 300) width = 300; // Maximum width
            getColumnModel().getColumn(column).setPreferredWidth(width);
        }
    }
}