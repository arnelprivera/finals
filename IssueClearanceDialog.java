package TiwalApp.ui.dialogs;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.utils.Constants;
import TiwalApp.models.Clearance;
import TiwalApp.database.ClearanceDAO;
import TiwalApp.database.ResidentDAO;
import TiwalApp.models.Resident;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IssueClearanceDialog extends JDialog {
    private TiwalApp app;
    private boolean saved = false;

    private JComboBox<String> residentCombo;
    private JComboBox<String> typeCombo;
    private JTextField purposeField;

    public IssueClearanceDialog(TiwalApp app) {
        super(app, "Issue Clearance", true);
        this.app = app;
        initUI();
        setLocationRelativeTo(app);
    }

    private void initUI() {
        setSize(500, 300);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Resident selection
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Resident:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        residentCombo = new JComboBox<>();
        ResidentDAO residentDAO = new ResidentDAO(app.getDatabaseConnection());
        List<Resident> residents = residentDAO.getAllResidents();
        residents.forEach(r -> residentCombo.addItem(r.getResidentId() + " - " + r.getFullName()));
        residentCombo.setFont(Constants.NORMAL_FONT);
        residentCombo.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(residentCombo, gbc);

        // Clearance type
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Clearance Type:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        typeCombo = new JComboBox<>(Constants.CLEARANCE_TYPES);
        typeCombo.setFont(Constants.NORMAL_FONT);
        typeCombo.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        // Purpose
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Purpose:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        purposeField = new JTextField(20);
        UIHelper.styleModernTextField(purposeField);
        gbc.gridx = 1;
        formPanel.add(purposeField, gbc);

        // Button Panel
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 220));

        ModernButton saveBtn = new ModernButton("Issue Clearance", Constants.PRIMARY_COLOR);
        saveBtn.addActionListener(e -> saveClearance());

        ModernButton cancelBtn = new ModernButton("Cancel", Constants.SECONDARY_COLOR);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);
        add(formPanel, BorderLayout.CENTER);
    }

    private void saveClearance() {
        String selected = (String) residentCombo.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a resident",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String residentId = selected.split(" - ")[0];
        String type = (String) typeCombo.getSelectedItem();
        String purpose = purposeField.getText().trim();

        if (purpose.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter purpose",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Clearance clearance = new Clearance(residentId, type, purpose);

            ClearanceDAO clearanceDAO = new ClearanceDAO(app.getDatabaseConnection());
            if (clearanceDAO.createClearance(clearance)) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        "Clearance issued successfully! Reference ID: " + clearance.getClearanceId(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error issuing clearance",
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