package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.database.ResidentDAO;
import TiwalApp.database.ComplaintDAO;
import TiwalApp.database.ClearanceDAO;
import TiwalApp.database.AnnouncementDAO;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends GlassPanel {
    private TiwalApp app;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private JPanel[] statCards;

    public AdminDashboardPanel(TiwalApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Header Panel with Time and Logout
        JPanel headerPanel = createCardPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel leftHeaderPanel = new JPanel(new BorderLayout());
        leftHeaderPanel.setOpaque(false);

        welcomeLabel = UIHelper.createModernLabel("Welcome, Administrator",
                Constants.HEADING_FONT, Constants.TEXT_PRIMARY);
        leftHeaderPanel.add(welcomeLabel, BorderLayout.NORTH);

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);
        leftHeaderPanel.add(timeLabel, BorderLayout.SOUTH);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeaderPanel.setOpaque(false);

        ModernButton logoutBtn = new ModernButton("Logout", Constants.ERROR_COLOR);
        logoutBtn.addActionListener(e -> app.logout());
        logoutBtn.setFont(Constants.BUTTON_FONT);
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        rightHeaderPanel.add(logoutBtn);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        String[] stats = {"Total Residents", "Pending Complaints", "Issued Clearances", "Active Announcements"};
        Color[] colors = {Constants.PRIMARY_COLOR, Constants.WARNING_COLOR,
                Constants.SUCCESS_COLOR, Constants.INFO_COLOR};

        statCards = new JPanel[4];
        for (int i = 0; i < stats.length; i++) {
            statCards[i] = createModernStatCard(stats[i], "0", colors[i]);
            statsPanel.add(statCards[i]);
        }

        // Quick Actions
        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        String[][] actions = {
                {"Manage Residents", "residents", "Add, edit, delete resident records"},
                {"Manage Complaints", "complaints", "Track, update status, assign complaints"},
                {"Manage Clearances", "clearances", "Issue, approve, reject clearances"},
                {"Manage Announcements", "announcements", "Create, edit barangay announcements"},
                {"Generate Reports", "reports", "Generate various reports"},
                {"Activity Log", "activityLog", "View system activities"}
        };

        for (String[] action : actions) {
            JButton btn = createDashboardButton(action[0], action[1], action[2]);
            actionsPanel.add(btn);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    public void updateDashboardStats() {
        if (app.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + app.getCurrentUser().getFullName());
        }

        // Update stats from database
        ResidentDAO residentDAO = new ResidentDAO(app.getDatabaseConnection());
        ComplaintDAO complaintDAO = new ComplaintDAO(app.getDatabaseConnection());
        ClearanceDAO clearanceDAO = new ClearanceDAO(app.getDatabaseConnection());
        AnnouncementDAO announcementDAO = new AnnouncementDAO(app.getDatabaseConnection());

        int totalResidents = residentDAO.getAllResidents().size();
        long pendingComplaints = complaintDAO.getComplaintsByStatus("Pending").size();
        long issuedClearances = clearanceDAO.getClearancesByStatus("Approved").size();
        long activeAnnouncements = announcementDAO.getActiveAnnouncements().size();

        // Update stat cards
        updateStatCard(statCards[0], String.valueOf(totalResidents));
        updateStatCard(statCards[1], String.valueOf(pendingComplaints));
        updateStatCard(statCards[2], String.valueOf(issuedClearances));
        updateStatCard(statCards[3], String.valueOf(activeAnnouncements));
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

    private JPanel createModernStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, color.brighter(),
                        0, getHeight(), color
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(color.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                g2d.dispose();
            }
        };

        card.setOpaque(false);
        card.setPreferredSize(new Dimension(250, 120));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.BOLD_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(Constants.LARGE_FONT);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        valueLabel.setName("valueLabel");

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void updateStatCard(JPanel card, String newValue) {
        BorderLayout layout = (BorderLayout) card.getLayout();
        JLabel valueLabel = (JLabel) layout.getLayoutComponent(BorderLayout.CENTER);
        if (valueLabel != null) {
            valueLabel.setText(newValue);
        }
    }

    private JButton createDashboardButton(String text, String action, String description) {
        JButton btn = new JButton("<html><center><b>" + text + "</b><br><font size='2' color='#666666'>" +
                description + "</font></center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color glassColor = new Color(255, 255, 255, 200);
                if (getModel().isRollover()) {
                    glassColor = new Color(255, 255, 255, 230);
                }

                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 20, 20);

                g2d.setColor(glassColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2d.dispose();
            }
        };

        btn.setFont(Constants.SUBHEADING_FONT);
        btn.setForeground(Constants.PRIMARY_COLOR);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(250, 120));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);

        btn.addActionListener(e -> navigateTo(action));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Constants.PRIMARY_DARK);
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Constants.PRIMARY_COLOR);
                btn.repaint();
            }
        });

        return btn;
    }

    private void navigateTo(String panel) {
        switch (panel) {
            case "residents": app.showResidentsPanel(); break;
            case "complaints": app.showComplaintsPanel(); break;
            case "clearances": app.showClearancesPanel(); break;
            case "announcements": app.showAnnouncementsPanel(); break;
            case "reports": app.showReportsPanel(); break;
            case "activityLog": app.showActivityLogPanel(); break;
        }
    }
}