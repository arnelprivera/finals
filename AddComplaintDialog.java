package TiwalApp.ui.dialogs;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.utils.Constants;
import TiwalApp.models.Complaint;
import TiwalApp.database.ComplaintDAO;
import TiwalApp.database.ResidentDAO;
import TiwalApp.models.Resident;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddComplaintDialog extends JDialog {
    private TiwalApp app;
    private boolean saved = false;

    private JComboBox<String> residentCombo;
    private JComboBox<String> typeCombo;
    private JComboBox<String> priorityCombo;
    private JTextArea descriptionArea;

    public AddComplaintDialog(TiwalApp app) {
        super(app, "Add Complaint", true);
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

        // Complaint type
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Complaint Type:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        typeCombo = new JComboBox<>(Constants.COMPLAINT_TYPES);
        typeCombo.setFont(Constants.NORMAL_FONT);
        typeCombo.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        // Priority
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Priority:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High", "Urgent"});
        priorityCombo.setFont(Constants.NORMAL_FONT);
        priorityCombo.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(priorityCombo, gbc);

        // Description
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(UIHelper.createModernLabel("Description:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        gbc.gridy = 4;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(Constants.NORMAL_FONT);
        descriptionArea.setBackground(new Color(255, 255, 255, 220));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 150), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Button Panel
        gbc.gridy = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 220));

        ModernButton saveBtn = new ModernButton("Submit", Constants.PRIMARY_COLOR);
        saveBtn.addActionListener(e -> saveComplaint());

        ModernButton cancelBtn = new ModernButton("Cancel", Constants.SECONDARY_COLOR);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);
        add(formPanel, BorderLayout.CENTER);
    }

    private void saveComplaint() {
        String selected = (String) residentCombo.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a resident",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String residentId = selected.split(" - ")[0];
        String type = (String) typeCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Complaint complaint = new Complaint(residentId, type, description);
            complaint.setPriority((String) priorityCombo.getSelectedItem());

            ComplaintDAO complaintDAO = new ComplaintDAO(app.getDatabaseConnection());
            if (complaintDAO.createComplaint(complaint)) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        "Complaint submitted successfully! Reference ID: " + complaint.getComplaintId(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error saving complaint",
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