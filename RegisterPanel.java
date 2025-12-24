package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.utils.ValidationUtils;
import TiwalApp.models.User;
import TiwalApp.models.Resident;
import TiwalApp.database.ResidentDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends GlassPanel {
    private TiwalApp app;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField birthdateField;

    public RegisterPanel(TiwalApp app) {
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

                Color glassColor = new Color(255, 255, 255, 200);
                g2d.setColor(glassColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;

        JLabel titleLabel = UIHelper.createModernLabel("Create New Account",
                Constants.HEADING_FONT, Constants.PRIMARY_COLOR);
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        String[][] fields = {
                {"Username:", "text"},
                {"Password:", "password"},
                {"Confirm Password:", "password"},
                {"Full Name:", "text"},
                {"Email:", "text"},
                {"Phone:", "text"},
                {"Address:", "text"},
                {"Birthdate (YYYY-MM-DD):", "text"}
        };

        gbc.gridwidth = 1;
        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            formPanel.add(UIHelper.createModernLabel(fields[i][0],
                    Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

            JTextField field;
            if (fields[i][1].equals("password")) {
                field = new JPasswordField(20);
            } else {
                field = new JTextField(20);
            }
            UIHelper.styleModernTextField(field);
            gbc.gridx = 1;
            formPanel.add(field, gbc);

            // Store references to fields
            switch (i) {
                case 0: usernameField = field; break;
                case 1: passwordField = (JPasswordField) field; break;
                case 2: confirmPasswordField = (JPasswordField) field; break;
                case 3: fullNameField = field; break;
                case 4: emailField = field; break;
                case 5: phoneField = field; break;
                case 6: addressField = field; break;
                case 7: birthdateField = field; break;
            }
        }

        // Button Panel
        gbc.gridy = fields.length + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        ModernButton registerBtn = new ModernButton("Register", Constants.PRIMARY_COLOR);
        registerBtn.addActionListener(e -> register());

        ModernButton backBtn = new ModernButton("Back to Login", Constants.SECONDARY_COLOR);
        backBtn.addActionListener(e -> app.showLoginPanel());

        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);

        formPanel.add(buttonPanel, gbc);
        glassPanel.add(formPanel, BorderLayout.CENTER);
        add(glassPanel, BorderLayout.CENTER);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String birthdate = birthdateField.getText();

        // Validation
        if (!ValidationUtils.isNotEmpty(username) ||
                !ValidationUtils.isNotEmpty(password) ||
                !ValidationUtils.isNotEmpty(fullName)) {
            JOptionPane.showMessageDialog(app, "Please fill in all required fields",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(app, "Passwords do not match",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            JOptionPane.showMessageDialog(app, "Password must be at least 6 characters long",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtils.isValidDate(birthdate, "yyyy-MM-dd")) {
            JOptionPane.showMessageDialog(app, "Invalid birthdate format. Use YYYY-MM-DD",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create resident first
            Resident resident = new Resident(fullName, address, birthdate);
            resident.setContactNumber(phone);

            ResidentDAO residentDAO = new ResidentDAO(app.getDatabaseConnection());
            if (!residentDAO.createResident(resident)) {
                throw new Exception("Failed to create resident record");
            }

            // Create user
            User user = new User(username, password, Constants.ROLE_RESIDENT, fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            user.setBirthdate(birthdate);
            user.setResidentId(resident.getResidentId());

            if (app.getAuthService().register(user)) {
                JOptionPane.showMessageDialog(app, "Registration successful! Please login.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                app.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(app, "Username already exists",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(app, "Error during registration: " + e.getMessage(),
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}