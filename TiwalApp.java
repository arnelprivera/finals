package TiwalApp;

import TiwalApp.database.DatabaseConnection;
import TiwalApp.models.User;
import TiwalApp.services.AuthenticationService;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.panels.*;
import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class TiwalApp extends JFrame {
    // Services
    private DatabaseConnection dbConnection;
    private AuthenticationService authService;

    // Current user
    private User currentUser;

    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Map<String, JPanel> panels;

    // Gradient background panel
    private JPanel gradientBackgroundPanel;

    // Menu components
    private JMenu adminMenu;
    private JMenu residentMenu;

    public TiwalApp() {
        initializeServices();
        setupUI();
        createMenuBar();
        initializePanels();
        showLoginPanel();
    }

    private void initializeServices() {
        dbConnection = new DatabaseConnection();
        authService = new AuthenticationService(dbConnection);
    }

    private void setupUI() {
        setTitle("TiwalApp - Barangay Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        // Create gradient background panel
        gradientBackgroundPanel = new GlassPanel();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        panels = new HashMap<>();
        gradientBackgroundPanel.add(mainPanel, BorderLayout.CENTER);
        add(gradientBackgroundPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = UIHelper.createMenuBar();

        // Create menus
        JMenu fileMenu = UIHelper.createModernMenu("File", Constants.TEXT_LIGHT);
        fileMenu.add(UIHelper.createModernMenuItem("Logout", e -> logout()));
        fileMenu.addSeparator();
        fileMenu.add(UIHelper.createModernMenuItem("Exit", e -> System.exit(0)));

        adminMenu = UIHelper.createModernMenu("Admin", Constants.TEXT_LIGHT);
        adminMenu.setVisible(false);
        residentMenu = UIHelper.createModernMenu("Resident", Constants.TEXT_LIGHT);
        residentMenu.setVisible(false);

        JMenu helpMenu = UIHelper.createModernMenu("Help", Constants.TEXT_LIGHT);
        helpMenu.add(UIHelper.createModernMenuItem("About", e -> showAbout()));

        menuBar.add(fileMenu);
        menuBar.add(adminMenu);
        menuBar.add(residentMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initializePanels() {
        // Create all panels
        panels.put("login", new LoginPanel(this));
        panels.put("register", new RegisterPanel(this));
        panels.put("adminDashboard", new AdminDashboardPanel(this));
        panels.put("userDashboard", new UserDashboardPanel(this));
        panels.put("residents", new ResidentsPanel(this));
        panels.put("complaints", new ComplaintsPanel(this));
        panels.put("clearances", new ClearancesPanel(this));
        panels.put("announcements", new AnnouncementsPanel(this));
        panels.put("profile", new ProfilePanel(this));
        panels.put("family", new FamilyPanel(this));
        panels.put("userAnnouncements", new UserAnnouncementsPanel(this));
        panels.put("reports", new ReportsPanel(this));
        panels.put("activityLog", new ActivityLogPanel(this));

        // Add all panels to main panel
        for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
            mainPanel.add(entry.getValue(), entry.getKey());
        }
    }

    // Navigation methods
    public void showLoginPanel() {
        cardLayout.show(mainPanel, "login");
    }

    public void showRegisterPanel() {
        cardLayout.show(mainPanel, "register");
    }

    public void showAdminDashboard() {
        ((AdminDashboardPanel) panels.get("adminDashboard")).updateDashboardStats();
        cardLayout.show(mainPanel, "adminDashboard");
    }

    public void showUserDashboard() {
        ((UserDashboardPanel) panels.get("userDashboard")).updateWelcomeMessage();
        cardLayout.show(mainPanel, "userDashboard");
    }

    public void showResidentsPanel() {
        ((ResidentsPanel) panels.get("residents")).refreshTable();
        cardLayout.show(mainPanel, "residents");
    }

    public void showComplaintsPanel() {
        ((ComplaintsPanel) panels.get("complaints")).refreshTable();
        cardLayout.show(mainPanel, "complaints");
    }

    public void showClearancesPanel() {
        ((ClearancesPanel) panels.get("clearances")).refreshTable();
        cardLayout.show(mainPanel, "clearances");
    }

    public void showAnnouncementsPanel() {
        ((AnnouncementsPanel) panels.get("announcements")).refreshTable();
        cardLayout.show(mainPanel, "announcements");
    }

    public void showProfilePanel() {
        ((ProfilePanel) panels.get("profile")).loadProfileData();
        cardLayout.show(mainPanel, "profile");
    }

    public void showFamilyPanel() {
        ((FamilyPanel) panels.get("family")).refreshTable();
        cardLayout.show(mainPanel, "family");
    }

    public void showUserAnnouncements() {
        ((UserAnnouncementsPanel) panels.get("userAnnouncements")).refreshAnnouncements();
        cardLayout.show(mainPanel, "userAnnouncements");
    }

    public void showReportsPanel() {
        cardLayout.show(mainPanel, "reports");
    }

    public void showActivityLogPanel() {
        ((ActivityLogPanel) panels.get("activityLog")).refreshTable();
        cardLayout.show(mainPanel, "activityLog");
    }

    // Getters and setters
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        // Update menu visibility based on role
        if ("Admin".equals(user.role)) {
            adminMenu.setVisible(true);
            residentMenu.setVisible(false);
        } else {
            adminMenu.setVisible(false);
            residentMenu.setVisible(true);
        }
    }

    public DatabaseConnection getDatabaseConnection() {
        return dbConnection;
    }

    public AuthenticationService getAuthService() {
        return authService;
    }

    public void logout() {
        currentUser = null;
        adminMenu.setVisible(false);
        residentMenu.setVisible(false);
        showLoginPanel();
    }

    private void showAbout() {
        String aboutText = "TiwalApp - Barangay Management System\n\n" +
                "Version: 1.0.0\n" +
                "Developed for efficient barangay management\n\n" +
                "Features:\n" +
                "• Resident Management\n" +
                "• Complaint Tracking\n" +
                "• Clearance Processing\n" +
                "• Announcement System\n" +
                "• Activity Logging\n\n" +
                "Database: SQLite\n" +
                "Export Formats: PDF, Word, Excel, CSV, JPEG, PNG\n\n" +
                "© 2025 All rights reserved";

        JOptionPane.showMessageDialog(this, aboutText,
                "About TiwalApp", JOptionPane.INFORMATION_MESSAGE);
    }
}