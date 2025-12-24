package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.database.ComplaintDAO;
import TiwalApp.database.ClearanceDAO;

import javax.swing.*;
import java.awt.*;

public class UserDashboardPanel extends GlassPanel {

    private final TiwalApp app;
    private JLabel welcomeLabel;
    private JLabel notificationLabel;

    public UserDashboardPanel(TiwalApp app) {
        this.app = app;
        setLayout(new BorderLayout(20, 20));
        initUI();
    }

    private void initUI() {

        /* ================= HEADER ================= */
        JPanel headerPanel = createCardPanel();
        headerPanel.setLayout(new BorderLayout(10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);

        welcomeLabel = UIHelper.createModernLabel(
                "Welcome, Resident",
                Constants.HEADING_FONT,
                Constants.TEXT_PRIMARY
        );

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);

        leftHeader.add(welcomeLabel, BorderLayout.NORTH);
        leftHeader.add(timeLabel, BorderLayout.SOUTH);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);

        notificationLabel = new JLabel();
        notificationLabel.setFont(Constants.NORMAL_FONT);
        notificationLabel.setForeground(Constants.PRIMARY_COLOR);

        ModernButton logoutBtn = new ModernButton("Logout", Constants.ERROR_COLOR);
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        logoutBtn.addActionListener(e -> app.logout());

        rightHeader.add(notificationLabel);
        rightHeader.add(logoutBtn);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        /* ================= ACTIONS ================= */
        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        String[][] actions = {
                {"View Profile", "profile", "View and update your personal information"},
                {"Submit Complaint", "submitComplaint", "Report issues to barangay officials"},
                {"Request Clearance", "requestClearance", "Request barangay clearances"},
                {"Family Members", "family", "Manage family member information"},
                {"View Announcements", "userAnnouncements", "View latest barangay updates"},
                {"Check Status", "checkStatus", "Check your complaints and clearances"}
        };

        for (String[] action : actions) {
            actionsPanel.add(createDashboardButton(action[0], action[1], action[2]));
        }

        add(headerPanel, BorderLayout.NORTH);
        add(actionsPanel, BorderLayout.CENTER);
    }

    /* ================= UPDATE DATA ================= */
    public void updateWelcomeMessage() {
        if (app.getCurrentUser() == null) return;

        welcomeLabel.setText("Welcome, " + app.getCurrentUser().getFullName());

        ComplaintDAO complaintDAO = new ComplaintDAO(app.getDatabaseConnection());
        ClearanceDAO clearanceDAO = new ClearanceDAO(app.getDatabaseConnection());

        long pendingComplaints = complaintDAO
                .getComplaintsByResident(app.getCurrentUser().getResidentId())
                .stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count();

        long pendingClearances = clearanceDAO
                .getClearancesByResident(app.getCurrentUser().getResidentId())
                .stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count();

        int total = (int) (pendingComplaints + pendingClearances);
        notificationLabel.setText(total > 0 ? "You have " + total + " pending item(s)" : "");
    }

    /* ================= GLASS CARD ================= */
    private JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Constants.SHADOW_COLOR);
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 15, 15);

                g2.setColor(Constants.GLASS_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        return panel;
    }

    /* ================= DASHBOARD BUTTON ================= */
    private JButton createDashboardButton(String title, String action, String desc) {

        JButton btn = new JButton(
                "<html><center><b>" + title + "</b><br>"
                        + "<font size='2' color='#666666'>" + desc + "</font></center></html>"
        ) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getModel().isRollover()
                        ? new Color(255, 255, 255, 230)
                        : new Color(255, 255, 255, 200);

                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.dispose();

                // ✅ REQUIRED: draws text + HTML
                super.paintComponent(g);
            }
        };

        btn.setFont(Constants.SUBHEADING_FONT);
        btn.setForeground(Constants.PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(260, 130));

        btn.addActionListener(e -> navigateTo(action));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Constants.PRIMARY_DARK);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Constants.PRIMARY_COLOR);
            }
        });

        return btn;
    }

    /* ================= NAVIGATION ================= */
    private void navigateTo(String panel) {
        switch (panel) {
            case "profile" -> app.showProfilePanel();
            case "submitComplaint" -> JOptionPane.showMessageDialog(app, "Submit Complaint dialog");
            case "requestClearance" -> JOptionPane.showMessageDialog(app, "Request Clearance dialog");
            case "family" -> app.showFamilyPanel();
            case "userAnnouncements" -> app.showUserAnnouncements();
            case "checkStatus" -> showStatusDialog();
        }
    }

    /* ================= STATUS DIALOG ================= */
    private void showStatusDialog() {
        if (app.getCurrentUser() == null) return;

        JTextArea area = new JTextArea("Status details go here...");
        area.setEditable(false);
        area.setFont(Constants.NORMAL_FONT);

        JOptionPane.showMessageDialog(
                app,
                new JScrollPane(area),
                "Status Check",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
