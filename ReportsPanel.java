package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.ui.dialogs.ReportDialog;
import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends GlassPanel {
    private TiwalApp app;

    public ReportsPanel(TiwalApp app) {
        this.app = app;
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

        JLabel titleLabel = UIHelper.createModernLabel("Generate Reports",
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

        // Reports Panel
        JPanel reportsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        reportsPanel.setOpaque(false);
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        String[][] reports = {
                {"📊 Residents Report", "Generate detailed report of all residents"},
                {"📝 Complaints Report", "Summary of all complaints with status"},
                {"📄 Clearances Report", "Issued and pending clearances report"},
                {"📢 Announcements Report", "Active and expired announcements"},
                {"📈 Monthly Activity", "Monthly statistics and activities"},
                {"👥 Demographics Report", "Resident demographics and statistics"}
        };

        for (String[] report : reports) {
            JPanel reportCard = createCardPanel();
            reportCard.setLayout(new BorderLayout());
            reportCard.setPreferredSize(new Dimension(300, 150));

            JLabel reportTitle = new JLabel(report[0]);
            reportTitle.setFont(Constants.SUBHEADING_FONT);
            reportTitle.setForeground(Constants.PRIMARY_COLOR);
            reportTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

            JLabel reportDesc = new JLabel("<html>" + report[1] + "</html>");
            reportDesc.setFont(Constants.NORMAL_FONT);
            reportDesc.setForeground(Constants.TEXT_SECONDARY);
            reportDesc.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

            String reportType = report[0].replace("📊 ", "").replace("📝 ", "")
                    .replace("📄 ", "").replace("📢 ", "").replace("📈 ", "").replace("👥 ", "");

            ModernButton generateBtn = new ModernButton("Generate", Constants.PRIMARY_COLOR);
            generateBtn.addActionListener(e -> generateReport(reportType));
            generateBtn.setPreferredSize(new Dimension(120, 35));

            reportCard.add(reportTitle, BorderLayout.NORTH);
            reportCard.add(reportDesc, BorderLayout.CENTER);
            reportCard.add(generateBtn, BorderLayout.SOUTH);

            reportsPanel.add(reportCard);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(reportsPanel, BorderLayout.CENTER);
    }

    private void generateReport(String reportType) {
        ReportDialog dialog = new ReportDialog(app, reportType);
        dialog.setVisible(true);
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