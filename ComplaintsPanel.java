package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.database.ComplaintDAO;
import TiwalApp.models.Complaint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ComplaintsPanel extends GlassPanel {
    private TiwalApp app;
    private JTable complaintsTable;
    private DefaultTableModel tableModel;
    private ComplaintDAO complaintDAO;

    public ComplaintsPanel(TiwalApp app) {
        this.app = app;
        this.complaintDAO = new ComplaintDAO(app.getDatabaseConnection());
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

        JLabel titleLabel = UIHelper.createModernLabel("Complaint Management",
                Constants.HEADING_FONT, Constants.TEXT_PRIMARY);
        leftHeaderPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);
        leftHeaderPanel.add(timeLabel, BorderLayout.SOUTH);

        ModernButton backBtn = new ModernButton("← Back to Dashboard", Constants.PRIMARY_COLOR);
        backBtn.addActionListener(e -> app.showAdminDashboard());

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        // Control Panel
        JPanel controlPanel = createCardPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel statusLabel = UIHelper.createModernLabel("Update Status:",
                Constants.BOLD_FONT, Constants.TEXT_PRIMARY);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
                "Pending", "In Progress", "Resolved", "Completed"
        });
        statusCombo.setFont(Constants.NORMAL_FONT);
        statusCombo.setBackground(new Color(255, 255, 255, 220));

        ModernButton updateBtn = createIconButton("Update Status", Constants.PRIMARY_COLOR);
        updateBtn.addActionListener(e -> updateComplaintStatus(statusCombo));

        ModernButton assignBtn = createIconButton("Assign to Staff", Constants.SECONDARY_COLOR);
        assignBtn.addActionListener(e -> assignComplaint());

        controlPanel.add(statusLabel);
        controlPanel.add(statusCombo);
        controlPanel.add(updateBtn);
        controlPanel.add(assignBtn);

        headerPanel.add(controlPanel, BorderLayout.SOUTH);

        // Main Content Panel
        JPanel contentPanel = createCardPanel();
        contentPanel.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "Resident ID", "Type", "Description", "Status", "Priority", "Submitted Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintsTable = new JTable(tableModel);
        // Style table similar to ResidentsPanel

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ModernButton addBtn = createIconButton("Add Complaint", Constants.PRIMARY_COLOR);
        addBtn.addActionListener(e -> showAddComplaintDialog());

        ModernButton viewBtn = createIconButton("View Details", Constants.SECONDARY_COLOR);
        viewBtn.addActionListener(e -> viewComplaintDetails());

        ModernButton exportBtn = createIconButton("Export", Constants.SUCCESS_COLOR);
        exportBtn.addActionListener(e -> showExportMenu(exportBtn));

        buttonPanel.add(addBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(new JScrollPane(complaintsTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Complaint> complaints = complaintDAO.getAllComplaints();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Complaint complaint : complaints) {
            tableModel.addRow(new Object[]{
                    complaint.getComplaintId(),
                    complaint.getResidentId(),
                    complaint.getComplaintType(),
                    complaint.getDescription().length() > 50 ?
                            complaint.getDescription().substring(0, 47) + "..." : complaint.getDescription(),
                    complaint.getStatus(),
                    complaint.getPriority(),
                    complaint.getSubmittedDate() != null ? sdf.format(complaint.getSubmittedDate()) : ""
            });
        }
    }

    private void updateComplaintStatus(JComboBox<String> statusCombo) {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a complaint",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);
        String newStatus = (String) statusCombo.getSelectedItem();

        if (complaintDAO.updateComplaintStatus(complaintId, newStatus)) {
            JOptionPane.showMessageDialog(app, "Complaint status updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(app, "Error updating complaint status",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignComplaint() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a complaint",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);
        String staffName = JOptionPane.showInputDialog(app,
                "Enter staff name to assign complaint " + complaintId + " to:",
                "Assign Complaint", JOptionPane.QUESTION_MESSAGE);

        if (staffName != null && !staffName.trim().isEmpty()) {
            Complaint complaint = complaintDAO.getComplaintById(complaintId);
            if (complaint != null) {
                complaint.setAssignedTo(staffName);
                complaint.setStatus("In Progress");
                if (complaintDAO.updateComplaint(complaint)) {
                    refreshTable();
                }
            }
        }
    }

    private void showAddComplaintDialog() {
        // Implementation
    }

    private void viewComplaintDetails() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a complaint",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);
        Complaint complaint = complaintDAO.getComplaintById(complaintId);

        if (complaint != null) {
            StringBuilder details = new StringBuilder();
            details.append("Complaint ID: ").append(complaint.getComplaintId()).append("\n");
            details.append("Resident ID: ").append(complaint.getResidentId()).append("\n");
            details.append("Type: ").append(complaint.getComplaintType()).append("\n");
            details.append("Status: ").append(complaint.getStatus()).append("\n");
            details.append("Priority: ").append(complaint.getPriority()).append("\n");
            details.append("Submitted: ").append(complaint.getSubmittedDate()).append("\n");
            if (complaint.getAssignedTo() != null) {
                details.append("Assigned to: ").append(complaint.getAssignedTo()).append("\n");
            }
            details.append("\nDescription:\n").append(complaint.getDescription());

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(Constants.NORMAL_FONT);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(app, scrollPane,
                    "Complaint Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private ModernButton createIconButton(String text, Color bgColor) {
        // Similar to ResidentsPanel implementation
        return new ModernButton(text, bgColor);
    }

    private JPanel createCardPanel() {
        // Similar to ResidentsPanel implementation
        return new JPanel();
    }

    private void showExportMenu(JButton parent) {
        // Similar to ResidentsPanel implementation
    }
}