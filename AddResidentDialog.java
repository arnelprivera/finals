package TiwalApp.ui.dialogs;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.utils.Constants;
import TiwalApp.models.Resident;
import TiwalApp.database.ResidentDAO;

import javax.swing.*;
import java.awt.*;

public class AddResidentDialog extends JDialog {
    private TiwalApp app;
    private boolean saved = false;

    private JTextField fullNameField;
    private JTextField addressField;
    private JTextField birthdateField;
    private JComboBox<String> genderCombo;
    private JComboBox<String> civilStatusCombo;
    private JTextField occupationField;
    private JTextField educationField;
    private JTextField contactField;
    private JTextField emergencyContactField;

    public AddResidentDialog(TiwalApp app) {
        super(app, "Add New Resident", true);
        this.app = app;
        initUI();
        setLocationRelativeTo(app);
    }

    private void initUI() {
        setSize(500, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        String[][] fields = {
                {"Full Name:", "fullName"},
                {"Address:", "address"},
                {"Birthdate (YYYY-MM-DD):", "birthdate"},
                {"Gender:", "gender"},
                {"Civil Status:", "civilStatus"},
                {"Occupation:", "occupation"},
                {"Education:", "education"},
                {"Contact Number:", "contact"},
                {"Emergency Contact:", "emergency"}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i;
            gbc.gridx = 0;
            formPanel.add(UIHelper.createModernLabel(fields[i][0],
                    Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

            if (fields[i][0].contains("Gender")) {
                genderCombo = new JComboBox<>(Constants.GENDER_OPTIONS);
                genderCombo.setFont(Constants.NORMAL_FONT);
                genderCombo.setBackground(new Color(255, 255, 255, 220));
                gbc.gridx = 1;
                formPanel.add(genderCombo, gbc);
            } else if (fields[i][0].contains("Civil Status")) {
                civilStatusCombo = new JComboBox<>(Constants.CIVIL_STATUS_OPTIONS);
                civilStatusCombo.setFont(Constants.NORMAL_FONT);
                civilStatusCombo.setBackground(new Color(255, 255, 255, 220));
                gbc.gridx = 1;
                formPanel.add(civilStatusCombo, gbc);
            } else {
                JTextField field = new JTextField(20);
                UIHelper.styleModernTextField(field);
                gbc.gridx = 1;
                formPanel.add(field, gbc);

                // Store reference
                switch (fields[i][1]) {
                    case "fullName": fullNameField = field; break;
                    case "address": addressField = field; break;
                    case "birthdate": birthdateField = field; break;
                    case "occupation": occupationField = field; break;
                    case "education": educationField = field; break;
                    case "contact": contactField = field; break;
                    case "emergency": emergencyContactField = field; break;
                }
            }
        }

        // Button Panel
        gbc.gridy = fields.length;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 220));

        ModernButton saveBtn = new ModernButton("Save", Constants.PRIMARY_COLOR);
        saveBtn.addActionListener(e -> saveResident());

        ModernButton cancelBtn = new ModernButton("Cancel", Constants.SECONDARY_COLOR);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);
        add(formPanel, BorderLayout.CENTER);
    }

    private void saveResident() {
        String fullName = fullNameField.getText().trim();
        String address = addressField.getText().trim();
        String birthdate = birthdateField.getText().trim();

        if (fullName.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in required fields",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Resident resident = new Resident(fullName, address, birthdate);
            resident.setGender((String) genderCombo.getSelectedItem());
            resident.setCivilStatus((String) civilStatusCombo.getSelectedItem());
            resident.setOccupation(occupationField.getText().trim());
            resident.setEducation(educationField.getText().trim());
            resident.setContactNumber(contactField.getText().trim());
            resident.setEmergencyContact(emergencyContactField.getText().trim());

            ResidentDAO residentDAO = new ResidentDAO(app.getDatabaseConnection());
            if (residentDAO.createResident(resident)) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Resident added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error saving resident",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}