package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.ui.components.ModernTable;
import TiwalApp.ui.dialogs.AddFamilyMemberDialog;
import TiwalApp.utils.Constants;
import TiwalApp.database.FamilyMemberDAO;
import TiwalApp.models.FamilyMember;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FamilyPanel extends GlassPanel {
    private TiwalApp app;
    private ModernTable familyTable;
    private DefaultTableModel tableModel;
    private FamilyMemberDAO familyMemberDAO;

    public FamilyPanel(TiwalApp app) {
        this.app = app;
        this.familyMemberDAO = new FamilyMemberDAO(app.getDatabaseConnection());
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

        JLabel titleLabel = UIHelper.createModernLabel("Family Members",
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

        // Main Content Panel
        JPanel contentPanel = createCardPanel();
        contentPanel.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "Name", "Relationship", "Gender", "Birthdate", "Occupation"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        familyTable = new ModernTable(tableModel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ModernButton addBtn = createIconButton("Add Family Member", Constants.PRIMARY_COLOR);
        addBtn.addActionListener(e -> showAddFamilyMemberDialog());

        ModernButton editBtn = createIconButton("Edit", Constants.SECONDARY_COLOR);
        editBtn.addActionListener(e -> editFamilyMember());

        ModernButton deleteBtn = createIconButton("Delete", Constants.ERROR_COLOR);
        deleteBtn.addActionListener(e -> deleteFamilyMember());

        ModernButton exportBtn = createIconButton("Export", Constants.SUCCESS_COLOR);
        exportBtn.addActionListener(e -> showExportMenu(exportBtn));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(new JScrollPane(familyTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        if (app.getCurrentUser() == null) return;

        List<FamilyMember> members = familyMemberDAO.getFamilyMembersByResident(
                app.getCurrentUser().getResidentId());

        for (FamilyMember member : members) {
            tableModel.addRow(new Object[]{
                    member.getFamilyMemberId(),
                    member.getFullName(),
                    member.getRelationship(),
                    member.getGender() != null ? member.getGender() : "",
                    member.getBirthdate() != null ? member.getBirthdate() : "",
                    member.getOccupation() != null ? member.getOccupation() : ""
            });
        }
    }

    private void showAddFamilyMemberDialog() {
        AddFamilyMemberDialog dialog = new AddFamilyMemberDialog(app);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshTable();
        }
    }

    private void editFamilyMember() {
        int selectedRow = familyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a family member",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) familyTable.getValueAt(selectedRow, 0);
        FamilyMember member = familyMemberDAO.getFamilyMemberById(memberId);

        if (member != null) {
            String newName = JOptionPane.showInputDialog(app,
                    "Enter new name:", member.getFullName());
            if (newName != null && !newName.trim().isEmpty()) {
                member.setFullName(newName);
                if (familyMemberDAO.updateFamilyMember(member)) {
                    refreshTable();
                }
            }
        }
    }

    private void deleteFamilyMember() {
        int selectedRow = familyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a family member",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) familyTable.getValueAt(selectedRow, 0);
        String memberName = (String) familyTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(app,
                "Are you sure you want to delete family member: " + memberName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (familyMemberDAO.deleteFamilyMember(memberId)) {
                JOptionPane.showMessageDialog(app, "Family member deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
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

        JMenuItem excelItem = new JMenuItem("Export to Excel");
        excelItem.addActionListener(e -> exportToExcel());
        excelItem.setFont(Constants.NORMAL_FONT);

        exportMenu.add(csvItem);
        exportMenu.add(pdfItem);
        exportMenu.add(excelItem);

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

    private void exportToExcel() {
        // Implementation
        JOptionPane.showMessageDialog(app, "Export to Excel functionality");
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