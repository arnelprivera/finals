package TiwalApp.ui.dialogs;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.utils.Constants;
import TiwalApp.models.Announcement;
import TiwalApp.database.AnnouncementDAO;

import javax.swing.*;
import java.awt.*;

public class AddAnnouncementDialog extends JDialog {
    private TiwalApp app;
    private boolean saved = false;

    private JTextField titleField;
    private JComboBox<String> categoryCombo;
    private JTextArea contentArea;

    public AddAnnouncementDialog(TiwalApp app) {
        super(app, "Post Announcement", true);
        this.app = app;
        initUI();
        setLocationRelativeTo(app);
    }

    private void initUI() {
        setSize(600, 500);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Title
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Title:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        titleField = new JTextField(30);
        UIHelper.styleModernTextField(titleField);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        // Category
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(UIHelper.createModernLabel("Category:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        categoryCombo = new JComboBox<>(Constants.ANNOUNCEMENT_CATEGORIES);
        categoryCombo.setFont(Constants.NORMAL_FONT);
        categoryCombo.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);

        // Content
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(UIHelper.createModernLabel("Content:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY), gbc);

        gbc.gridy = 3;
        contentArea = new JTextArea(10, 40);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(Constants.NORMAL_FONT);
        contentArea.setBackground(new Color(255, 255, 255, 220));
        contentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 150), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane scrollPane = new JScrollPane(contentArea);
        formPanel.add(scrollPane, gbc);

        // Button Panel
        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 220));

        ModernButton saveBtn = new ModernButton("Post Announcement", Constants.PRIMARY_COLOR);
        saveBtn.addActionListener(e -> saveAnnouncement());

        ModernButton cancelBtn = new ModernButton("Cancel", Constants.SECONDARY_COLOR);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);
        add(formPanel, BorderLayout.CENTER);
    }

    private void saveAnnouncement() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in title and content",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Announcement announcement = new Announcement(title, content,
                    app.getCurrentUser() != null ? app.getCurrentUser().getFullName() : "System");
            announcement.setCategory(category);

            AnnouncementDAO announcementDAO = new AnnouncementDAO(app.getDatabaseConnection());
            if (announcementDAO.createAnnouncement(announcement)) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Announcement posted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error posting announcement",
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