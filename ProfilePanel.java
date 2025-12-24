package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.models.User;
import TiwalApp.models.Resident;
import TiwalApp.database.UserDAO;
import TiwalApp.database.ResidentDAO;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends GlassPanel {
    private TiwalApp app;
    private JTextField fullNameField;
    private JTextField residentIdField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField birthdateField;
    private JTextField occupationField;
    private JTextField emergencyContactField;
    private ModernButton saveBtn;
    private boolean editMode = false;

    public ProfilePanel(TiwalApp app) {
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

        JLabel titleLabel = UIHelper.createModernLabel("My Profile",
                Constants.HEADING_FONT, Constants.TEXT_PRIMARY);
        leftHeaderPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);
        leftHeaderPanel.add(timeLabel, BorderLayout.SOUTH);

        ModernButton backBtn = new ModernButton("← Back to Dashboard", Constants.PRIMARY_COLOR);
        backBtn.addActionListener(e -> app.showUserDashboard());
        backBtn.setFont(Constants.BUTTON_FONT);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        // Profile Form
        JPanel formPanel = createCardPanel();
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;

        String[][] fields = {
                {"Full Name:", "fullName"},
                {"Resident ID:", "residentId"},
                {"Email:", "email"},
                {"Phone:", "phone"},
                {"Address:", "address"},
                {"Birthdate:", "birthdate"},
                {"Occupation:", "occupation"},
                {"Emergency Contact:", "emergencyContact"}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i;
            gbc.gridx = 0;
            formPanel.add(UIHelper.createModernLabel(fields[i][0],
                    Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

            JTextField field = new JTextField(25);
            UIHelper.styleModernTextField(field);
            field.setEditable(false);
            gbc.gridx = 1;
            formPanel.add(field, gbc);

            // Store reference
            switch (fields[i][1]) {
                case "fullName": fullNameField = field; break;
                case "residentId": residentIdField = field; break;
                case "email": emailField = field; break;
                case "phone": phoneField = field; break;
                case "address": addressField = field; break;
                case "birthdate": birthdateField = field; break;
                case "occupation": occupationField = field; break;
                case "emergencyContact": emergencyContactField = field; break;
            }
        }

        // Button Panel
        gbc.gridy = fields.length;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        ModernButton editBtn = createIconButton("Edit Profile", Constants.PRIMARY_COLOR);
        editBtn.addActionListener(e -> enableEditing());

        saveBtn = createIconButton("Save Changes", Constants.SUCCESS_COLOR);
        saveBtn.addActionListener(e -> saveProfile());
        saveBtn.setEnabled(false);

        buttonPanel.add(editBtn);
        buttonPanel.add(saveBtn);

        formPanel.add(buttonPanel, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    public void loadProfileData() {
        if (app.getCurrentUser() == null) return;

        User user = app.getCurrentUser();
        ResidentDAO residentDAO = new ResidentDAO(app.getDatabaseConnection());
        Resident resident = residentDAO.getResidentById(user.getResidentId());

        if (resident != null) {
            fullNameField.setText(resident.getFullName());
            residentIdField.setText(resident.getResidentId());
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            phoneField.setText(resident.getContactNumber() != null ? resident.getContactNumber() : "");
            addressField.setText(resident.getAddress());
            birthdateField.setText(resident.getBirthdate());
            occupationField.setText(resident.getOccupation() != null ? resident.getOccupation() : "");
            emergencyContactField.setText(resident.getEmergencyContact() != null ?
                    resident.getEmergencyContact() : "");
        }
    }

    private void enableEditing() {
        editMode = true;

        // Enable editable fields
        emailField.setEditable(true);
        phoneField.setEditable(true);
        addressField.setEditable(true);
        occupationField.setEditable(true);
        emergencyContactField.setEditable(true);

        // Style editable fields
        JTextField[] editableFields = {emailField, phoneField, addressField,
                occupationField, emergencyContactField};
        for (JTextField field : editableFields) {
            field.setBackground(new Color(255, 255, 255, 255));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
        }

        saveBtn.setEnabled(true);
    }

    private void saveProfile() {
        if (!editMode) return;

        if (app.getCurrentUser() == null) return;

        try {
            User user = app.getCurrentUser();
            ResidentDAO residentDAO = new ResidentDAO(app.getDatabaseConnection());
            Resident resident = residentDAO.getResidentById(user.getResidentId());

            if (resident != null) {
                // Update user
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim());
                user.setAddress(addressField.getText().trim());

                // Update resident
                resident.setContactNumber(phoneField.getText().trim());
                resident.setAddress(addressField.getText().trim());
                resident.setOccupation(occupationField.getText().trim());
                resident.setEmergencyContact(emergencyContactField.getText().trim());

                UserDAO userDAO = new UserDAO(app.getDatabaseConnection());
                if (userDAO.updateUser(user) && residentDAO.updateResident(resident)) {
                    // Disable editing mode
                    editMode = false;
                    saveBtn.setEnabled(false);

                    // Make fields read-only again
                    JTextField[] fields = {emailField, phoneField, addressField,
                            occupationField, emergencyContactField};
                    for (JTextField field : fields) {
                        field.setEditable(false);
                        UIHelper.styleModernTextField(field);
                    }

                    JOptionPane.showMessageDialog(app, "Profile updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(app, "Error updating profile: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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