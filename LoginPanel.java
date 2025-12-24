package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends GlassPanel {
    private TiwalApp app;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(TiwalApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel glassPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color glassColor = new Color(255, 255, 255, 180);
                g2d.setColor(glassColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Left Panel with Logo and Welcome
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(400, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Add logo and title here...

        // Right Panel with Login Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridwidth = 2;

        JLabel loginTitle = UIHelper.createModernLabel("Login to Your Account",
                Constants.HEADING_FONT, Constants.PRIMARY_COLOR);
        gbc.gridy = 0;
        rightPanel.add(loginTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        rightPanel.add(UIHelper.createModernLabel("Username:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        usernameField = new JTextField(20);
        UIHelper.styleModernTextField(usernameField);
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(UIHelper.createModernLabel("Password:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        passwordField = new JPasswordField(20);
        UIHelper.styleModernTextField(passwordField);
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        ModernButton loginBtn = new ModernButton("Login", Constants.PRIMARY_COLOR);
        loginBtn.addActionListener(e -> login());

        ModernButton registerBtn = new ModernButton("Register", Constants.SECONDARY_COLOR);
        registerBtn.addActionListener(e -> app.showRegisterPanel());

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        rightPanel.add(buttonPanel, gbc);

        glassPanel.add(leftPanel, BorderLayout.WEST);
        glassPanel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.CENTER);
        glassPanel.add(rightPanel, BorderLayout.EAST);

        add(glassPanel, BorderLayout.CENTER);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(app, "Please enter username and password",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            var user = app.getAuthService().login(username, password);
            if (user != null) {
                app.setCurrentUser(user);
                if ("Admin".equals(user.getRole())) {
                    app.showAdminDashboard();
                } else {
                    app.showUserDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(app, "Invalid username or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(app, "Error during login: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}