package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.ui.components.ModernTable;
import TiwalApp.ui.dialogs.AddAnnouncementDialog;
import TiwalApp.utils.Constants;
import TiwalApp.database.AnnouncementDAO;
import TiwalApp.models.Announcement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AnnouncementsPanel extends GlassPanel {
    private TiwalApp app;
    private ModernTable announcementsTable;
    private DefaultTableModel tableModel;
    private AnnouncementDAO announcementDAO;

    public AnnouncementsPanel(TiwalApp app) {
        this.app = app;
        this.announcementDAO = new AnnouncementDAO(app.getDatabaseConnection());
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

        JLabel titleLabel = UIHelper.createModernLabel("Announcement Management",
                Constants.HEADING_FONT, Constants.TEXT_PRIMARY);
        leftHeaderPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);
        leftHeaderPanel.add(timeLabel, BorderLayout.SOUTH);

        ModernButton backBtn = new ModernButton("← Back to Dashboard", Constants.PRIMARY_COLOR);
        backBtn.addActionListener(e -> app.showAdminDashboard());
        backBtn.setFont(Constants.BUTTON_FONT);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        // Main Content Panel
        JPanel contentPanel = createCardPanel();
        contentPanel.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "Title", "Category", "Posted By", "Posted Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        announcementsTable = new ModernTable(tableModel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ModernButton addBtn = createIconButton("Post Announcement", Constants.PRIMARY_COLOR);
        addBtn.addActionListener(e -> showAddAnnouncementDialog());

        ModernButton editBtn = createIconButton("Edit", Constants.SECONDARY_COLOR);
        editBtn.addActionListener(e -> editAnnouncement());

        ModernButton deleteBtn = createIconButton("Delete", Constants.ERROR_COLOR);
        deleteBtn.addActionListener(e -> deleteAnnouncement());

        ModernButton toggleBtn = createIconButton("Toggle Status", Constants.WARNING_COLOR);
        toggleBtn.addActionListener(e -> toggleAnnouncementStatus());

        ModernButton exportBtn = createIconButton("Export", Constants.SUCCESS_COLOR);
        exportBtn.addActionListener(e -> showExportMenu(exportBtn));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(toggleBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(new JScrollPane(announcementsTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Announcement> announcements = announcementDAO.getAllAnnouncements();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Announcement announcement : announcements) {
            tableModel.addRow(new Object[]{
                    announcement.getAnnouncementId(),
                    announcement.getTitle(),
                    announcement.getCategory(),
                    announcement.getPostedBy(),
                    announcement.getPostedDate() != null ? sdf.format(announcement.getPostedDate()) : "",
                    announcement.isActive() ? "Active" : "Inactive"
            });
        }
    }

    private void showAddAnnouncementDialog() {
        AddAnnouncementDialog dialog = new AddAnnouncementDialog(app);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshTable();
        }
    }

    private void editAnnouncement() {
        int selectedRow = announcementsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select an announcement",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int announcementId = (int) announcementsTable.getValueAt(selectedRow, 0);
        Announcement announcement = announcementDAO.getAnnouncementById(announcementId);

        if (announcement != null) {
            String newTitle = JOptionPane.showInputDialog(app,
                    "Enter new title:", announcement.getTitle());
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                announcement.setTitle(newTitle);
                if (announcementDAO.updateAnnouncement(announcement)) {
                    refreshTable();
                }
            }
        }
    }

    private void deleteAnnouncement() {
        int selectedRow = announcementsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select an announcement",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int announcementId = (int) announcementsTable.getValueAt(selectedRow, 0);
        String title = (String) announcementsTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(app,
                "Are you sure you want to delete announcement: " + title + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (announcementDAO.deleteAnnouncement(announcementId)) {
                JOptionPane.showMessageDialog(app, "Announcement deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    private void toggleAnnouncementStatus() {
        int selectedRow = announcementsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select an announcement",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int announcementId = (int) announcementsTable.getValueAt(selectedRow, 0);
        boolean isActive = "Active".equals(announcementsTable.getValueAt(selectedRow, 5));

        if (announcementDAO.toggleAnnouncementStatus(announcementId, !isActive)) {
            refreshTable();
        }
    }

    private void showExportMenu(JButton parent) {
        JPopupMenu exportMenu = new JPopupMenu();

        JMenuItem csvItem = new JMenuItem("Export to CSV");
        csvItem.addActionListener(e -> exportToCSV());
        csvItem.setFont(Constants.NORMAL_FONT);

        JMenuItem pdfItem = new JMenuItem("Export to PDF");
        pdfItem.addActionListener(e -> exportToPDF());
        pdfItem.setFont(Constants.NORMAL_FONT);

        JMenuItem wordItem = new JMenuItem("Export to Word");
        wordItem.addActionListener(e -> exportToWord());
        wordItem.setFont(Constants.NORMAL_FONT);

        exportMenu.add(csvItem);
        exportMenu.add(pdfItem);
        exportMenu.add(wordItem);

        exportMenu.show(parent, 0, parent.getHeight());
    }

    private void exportToCSV() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to CSV functionality");
    }

    private void exportToPDF() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to PDF functionality");
    }

    private void exportToWord() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to Word functionality");
    }

    private ModernButton createIconButton(String text, Color bgColor) {
        // Similar implementation to other panels
        return new ModernButton(text, bgColor);
    }

    private JPanel createCardPanel() {
        // Similar implementation to other panels
        return new JPanel();
    }
}