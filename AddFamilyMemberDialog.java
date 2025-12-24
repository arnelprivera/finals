package TiwalApp.ui.dialogs;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.utils.Constants;
import TiwalApp.models.FamilyMember;
import TiwalApp.database.FamilyMemberDAO;

import javax.swing.*;
import java.awt.*;

public class AddFamilyMemberDialog extends JDialog {
    private TiwalApp app;
    private boolean saved = false;

    private JTextField fullNameField;
    private JComboBox<String> relationshipCombo;
    private JComboBox<String> genderCombo;
    private JTextField birthdateField;
    private JTextField occupationField;

    public AddFamilyMemberDialog(TiwalApp app) {
        super(app, "Add Family Member", true);
        this.app = app;
        initUI();
        setLocationRelativeTo(app);
    }

    private void initUI() {
        setSize(500, 400);
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
                {"Relationship:", "relationship"},
                {"Gender:", "gender"},
                {"Birthdate (YYYY-MM-DD):", "birthdate"},
                {"Occupation:", "occupation"}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i;
            gbc.gridx = 0;
            formPanel.add(UIHelper.createModernLabel(fields[i][0],
                    Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

            if (fields[i][0].contains("Relationship")) {
                relationshipCombo = new JComboBox<>(Constants.RELATIONSHIP_TYPES);
                relationshipCombo.setFont(Constants.NORMAL_FONT);
                relationshipCombo.setBackground(new Color(255, 255, 255, 220));
                gbc.gridx = 1;
                formPanel.add(relationshipCombo, gbc);
            } else if (fields[i][0].contains("Gender")) {
                genderCombo = new JComboBox<>(Constants.GENDER_OPTIONS);
                genderCombo.setFont(Constants.NORMAL_FONT);
                genderCombo.setBackground(new Color(255, 255, 255, 220));
                gbc.gridx = 1;
                formPanel.add(genderCombo, gbc);
            } else {
                JTextField field = new JTextField(20);
                UIHelper.styleModernTextField(field);
                gbc.gridx = 1;
                formPanel.add(field, gbc);

                // Store reference
                switch (fields[i][1]) {
                    case "fullName": fullNameField = field; break;
                    case "birthdate": birthdateField = field; break;
                    case "occupation": occupationField = field; break;
                }
            }
        }

        // Button Panel
        gbc.gridy = fields.length;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 220));

        ModernButton saveBtn = new ModernButton("Add Member", Constants.PRIMARY_COLOR);
        saveBtn.addActionListener(e -> saveFamilyMember());

        ModernButton cancelBtn = new ModernButton("Cancel", Constants.SECONDARY_COLOR);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);
        add(formPanel, BorderLayout.CENTER);
    }

    private void saveFamilyMember() {
        String fullName = fullNameField.getText().trim();
        String relationship = (String) relationshipCombo.getSelectedItem();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter full name",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (app.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "No user logged in",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            FamilyMember member = new FamilyMember(
                    app.getCurrentUser().getResidentId(),
                    fullName,
                    relationship
            );
            member.setGender((String) genderCombo.getSelectedItem());
            member.setBirthdate(birthdateField.getText().trim());
            member.setOccupation(occupationField.getText().trim());

            FamilyMemberDAO memberDAO = new FamilyMemberDAO(app.getDatabaseConnection());
            if (memberDAO.createFamilyMember(member)) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Family member added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding family member",
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