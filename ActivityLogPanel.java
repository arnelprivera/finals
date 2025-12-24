package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.ui.components.ModernTable;
import TiwalApp.utils.Constants;
import TiwalApp.database.ActivityLogDAO;
import TiwalApp.models.ActivityLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityLogPanel extends GlassPanel {
    private TiwalApp app;
    private ModernTable activityTable;
    private DefaultTableModel tableModel;
    private ActivityLogDAO activityLogDAO;

    public ActivityLogPanel(TiwalApp app) {
        this.app = app;
        this.activityLogDAO = new ActivityLogDAO(app.getDatabaseConnection());
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Header Panel
        JPanel headerPanel = createCardPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel leftHeaderPanel = new JPanel(new BorderLayout());
        leftHeaderPanel.setOpaque(false);

        JLabel titleLabel = UIHelper.createModernLabel("Activity Log",
                Constants.HEADING_FONT, Constants.TEXT_PRIMARY);
        leftHeaderPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);
        leftHeaderPanel.add(timeLabel, BorderLayout.SOUTH);

        ModernButton backBtn = new ModernButton("← Back to Dashboard", Constants.PRIMARY_COLOR);
        backBtn.addActionListener(e -> app.showAdminDashboard());
        backBtn.setFont(Constants.BUTTON_FONT);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        // Main Content Panel
        JPanel contentPanel = createCardPanel();
        contentPanel.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Log ID", "User", "Action", "Details", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activityTable = new ModernTable(tableModel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ModernButton refreshBtn = createIconButton("Refresh", Constants.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> refreshTable());

        ModernButton clearBtn = createIconButton("Clear Log", Constants.ERROR_COLOR);
        clearBtn.addActionListener(e -> clearLog());

        ModernButton exportBtn = createIconButton("Export Log", Constants.SUCCESS_COLOR);
        exportBtn.addActionListener(e -> showExportMenu(exportBtn));

        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(new JScrollPane(activityTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<ActivityLog> logs = activityLogDAO.getAllActivityLogs();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ActivityLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.getLogId(),
                    log.getUserName() + " (" + log.getUserId() + ")",
                    log.getAction(),
                    log.getDetails(),
                    sdf.format(log.getTimestamp())
            });
        }
    }

    private void clearLog() {
        int confirm = JOptionPane.showConfirmDialog(app,
                "Are you sure you want to clear all activity logs?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (activityLogDAO.clearAllActivityLogs()) {
                JOptionPane.showMessageDialog(app, "Activity logs cleared successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    private void showExportMenu(JButton parent) {
        JPopupMenu exportMenu = new JPopupMenu();

        JMenuItem csvItem = new JMenuItem("Export to CSV");
        csvItem.addActionListener(e -> exportToCSV());
        csvItem.setFont(Constants.NORMAL_FONT);

        JMenuItem pdfItem = new JMenuItem("Export to PDF");
        pdfItem.addActionListener(e -> exportToPDF());
        pdfItem.setFont(Constants.NORMAL_FONT);

        JMenuItem excelItem = new JMenuItem("Export to Excel");
        excelItem.addActionListener(e -> exportToExcel());
        excelItem.setFont(Constants.NORMAL_FONT);

        exportMenu.add(csvItem);
        exportMenu.add(pdfItem);
        exportMenu.add(excelItem);

        exportMenu.show(parent, 0, parent.getHeight());
    }

    private void exportToCSV() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to CSV functionality");
    }

    private void exportToPDF() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to PDF functionality");
    }

    private void exportToExcel() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to Excel functionality");
    }

    private ModernButton createIconButton(String text, Color bgColor) {
        ModernButton button = new ModernButton(text, bgColor) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.darker());
                } else {
                    g2d.setColor(bgColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), 15, (getHeight() + textHeight) / 2 - fm.getDescent());

                g2d.dispose();
            }
        };

        button.setFont(Constants.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(150, 45));

        return button;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color glassColor = Constants.GLASS_BACKGROUND;
                g2d.setColor(Constants.SHADOW_COLOR);
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);

                g2d.setColor(glassColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }
}