package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.utils.PrintUtils;
import TiwalApp.database.ClearanceDAO;
import TiwalApp.database.ResidentDAO;
import TiwalApp.models.Clearance;
import TiwalApp.models.Resident;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ClearancesPanel extends GlassPanel {
    private TiwalApp app;
    private JTable clearancesTable;
    private DefaultTableModel tableModel;
    private ClearanceDAO clearanceDAO;
    private ResidentDAO residentDAO;

    public ClearancesPanel(TiwalApp app) {
        this.app = app;
        this.clearanceDAO = new ClearanceDAO(app.getDatabaseConnection());
        this.residentDAO = new ResidentDAO(app.getDatabaseConnection());
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

        JLabel titleLabel = UIHelper.createModernLabel("Clearance Management",
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

        // Main Content Panel
        JPanel contentPanel = createCardPanel();
        contentPanel.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "Resident ID", "Type", "Purpose", "Status", "Issue Date", "Expiry Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clearancesTable = new JTable(tableModel);
        // Style table

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ModernButton addBtn = createIconButton("Issue Clearance", Constants.PRIMARY_COLOR);
        addBtn.addActionListener(e -> showIssueClearanceDialog());

        ModernButton approveBtn = createIconButton("Approve", Constants.SUCCESS_COLOR);
        approveBtn.addActionListener(e -> approveClearance());

        ModernButton rejectBtn = createIconButton("Reject", Constants.ERROR_COLOR);
        rejectBtn.addActionListener(e -> rejectClearance());

        ModernButton printBtn = createIconButton("Print", Constants.INFO_COLOR);
        printBtn.addActionListener(e -> printClearance());

        ModernButton exportBtn = createIconButton("Export", Constants.WARNING_COLOR);
        exportBtn.addActionListener(e -> showExportMenu(exportBtn));

        buttonPanel.add(addBtn);
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(new JScrollPane(clearancesTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Clearance> clearances = clearanceDAO.getAllClearances();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Clearance clearance : clearances) {
            tableModel.addRow(new Object[]{
                    clearance.getClearanceId(),
                    clearance.getResidentId(),
                    clearance.getClearanceType(),
                    clearance.getPurpose(),
                    clearance.getStatus(),
                    clearance.getIssueDate() != null ? sdf.format(clearance.getIssueDate()) : "",
                    clearance.getExpiryDate() != null ? sdf.format(clearance.getExpiryDate()) : ""
            });
        }
    }

    private void showIssueClearanceDialog() {
        // Implementation
    }

    private void approveClearance() {
        int selectedRow = clearancesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a clearance",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clearanceId = (int) clearancesTable.getValueAt(selectedRow, 0);
        Clearance clearance = clearanceDAO.getClearanceById(clearanceId);

        if (clearance != null) {
            clearance.setStatus("Approved");
            clearance.setIssueDate(new java.util.Date());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 6);
            clearance.setExpiryDate(cal.getTime());
            clearance.setIssuedBy(app.getCurrentUser() != null ?
                    app.getCurrentUser().getFullName() : "System");

            if (clearanceDAO.updateClearance(clearance)) {
                JOptionPane.showMessageDialog(app, "Clearance approved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    private void rejectClearance() {
        int selectedRow = clearancesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a clearance",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clearanceId = (int) clearancesTable.getValueAt(selectedRow, 0);
        Clearance clearance = clearanceDAO.getClearanceById(clearanceId);

        if (clearance != null) {
            clearance.setStatus("Rejected");
            if (clearanceDAO.updateClearance(clearance)) {
                refreshTable();
            }
        }
    }

    private void printClearance() {
        int selectedRow = clearancesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a clearance to print",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clearanceId = (int) clearancesTable.getValueAt(selectedRow, 0);
        Clearance clearance = clearanceDAO.getClearanceById(clearanceId);

        if (clearance != null) {
            Resident resident = residentDAO.getResidentById(clearance.getResidentId());
            if (PrintUtils.printClearance(clearance, resident, app)) {
                JOptionPane.showMessageDialog(app,
                        "Clearance sent to printer successfully!",
                        "Print Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private ModernButton createIconButton(String text, Color bgColor) {
        return new ModernButton(text, bgColor);
    }

    private JPanel createCardPanel() {
        return new JPanel();
    }

    private void showExportMenu(JButton parent) {
        // Implementation
    }
}