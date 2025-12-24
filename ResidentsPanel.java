package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.database.ResidentDAO;
import TiwalApp.database.UserDAO;
import TiwalApp.models.Resident;
import TiwalApp.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ResidentsPanel extends GlassPanel {
    private TiwalApp app;
    private JTable residentsTable;
    private DefaultTableModel tableModel;
    private ResidentDAO residentDAO;
    private UserDAO userDAO;

    public ResidentsPanel(TiwalApp app) {
        this.app = app;
        this.residentDAO = new ResidentDAO(app.getDatabaseConnection());
        this.userDAO = new UserDAO(app.getDatabaseConnection());
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

        JLabel titleLabel = UIHelper.createModernLabel("Resident Management",
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
        String[] columns = {"Resident ID", "Full Name", "Address", "Birthdate", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        residentsTable = new JTable(tableModel);
        styleModernTable(residentsTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ModernButton addBtn = createIconButton("Add Resident", Constants.PRIMARY_COLOR);
        addBtn.addActionListener(e -> showAddResidentDialog());

        ModernButton editBtn = createIconButton("Edit Resident", Constants.SECONDARY_COLOR);
        editBtn.addActionListener(e -> showEditResidentDialog());

        ModernButton deleteBtn = createIconButton("Delete Resident", Constants.ERROR_COLOR);
        deleteBtn.addActionListener(e -> deleteResident());

        ModernButton refreshBtn = createIconButton("Refresh", Constants.INFO_COLOR);
        refreshBtn.addActionListener(e -> refreshTable());

        ModernButton exportBtn = createIconButton("Export", Constants.SUCCESS_COLOR);
        exportBtn.addActionListener(e -> showExportMenu(exportBtn));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(new JScrollPane(residentsTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Resident> residents = residentDAO.getAllResidents();
        List<User> users = userDAO.getAllUsers();

        for (Resident resident : residents) {
            String email = users.stream()
                    .filter(u -> resident.getResidentId().equals(u.getResidentId()))
                    .map(User::getEmail)
                    .findFirst()
                    .orElse("");

            tableModel.addRow(new Object[]{
                    resident.getResidentId(),
                    resident.getFullName(),
                    resident.getAddress(),
                    resident.getBirthdate(),
                    resident.getContactNumber() != null ? resident.getContactNumber() : "",
                    email,
                    "Active"
            });
        }
    }

    private void styleModernTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(Constants.TABLE_FONT);
        table.setForeground(Constants.TEXT_PRIMARY);
        table.setBackground(new Color(255, 255, 255, 220));
        table.getTableHeader().setBackground(Constants.PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(Constants.TABLE_HEADER_FONT);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setGridColor(new Color(200, 200, 200, 100));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(
                Constants.PRIMARY_COLOR.getRed(),
                Constants.PRIMARY_COLOR.getGreen(),
                Constants.PRIMARY_COLOR.getBlue(),
                150));
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(Constants.PRIMARY_COLOR);
                setForeground(Color.WHITE);
                setFont(Constants.TABLE_HEADER_FONT);
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(255, 255, 255, 200));
                    } else {
                        c.setBackground(new Color(245, 245, 255, 200));
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        });
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
        button.setPreferredSize(new Dimension(180, 45));

        return button;
    }

    private void showAddResidentDialog() {
        // Implementation similar to original dialog
        JOptionPane.showMessageDialog(app, "Add Resident dialog would open here");
    }

    private void showEditResidentDialog() {
        int selectedRow = residentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a resident to edit",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String residentId = (String) residentsTable.getValueAt(selectedRow, 0);
        Resident resident = residentDAO.getResidentById(residentId);

        if (resident != null) {
            // Show edit dialog
            JOptionPane.showMessageDialog(app, "Edit Resident dialog would open here");
        }
    }

    private void deleteResident() {
        int selectedRow = residentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Please select a resident to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String residentId = (String) residentsTable.getValueAt(selectedRow, 0);
        String residentName = (String) residentsTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(app,
                "Are you sure you want to delete resident: " + residentName + "?\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (residentDAO.deleteResident(residentId)) {
                JOptionPane.showMessageDialog(app, "Resident deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(app, "Error deleting resident",
                        "Error", JOptionPane.ERROR_MESSAGE);
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
        // Export implementation
        JOptionPane.showMessageDialog(app, "Export to CSV functionality");
    }

    private void exportToPDF() {
        // Export implementation
        JOptionPane.showMessageDialog(app, "Export to PDF functionality");
    }

    private void exportToExcel() {
        // Export implementation
        JOptionPane.showMessageDialog(app, "Export to Excel functionality");
    }
}