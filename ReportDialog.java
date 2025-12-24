package TiwalApp.ui.dialogs;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReportDialog extends JDialog {
    private TiwalApp app;
    private String reportType;

    public ReportDialog(TiwalApp app, String reportType) {
        super(app, "Generate " + reportType, true);
        this.app = app;
        this.reportType = reportType;
        initUI();
        setLocationRelativeTo(app);
    }

    private void initUI() {
        setSize(300, 200);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBackground(new Color(255, 255, 255, 220));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIHelper.createModernLabel("Select Export Format",
                Constants.HEADING_FONT, Constants.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ModernButton pdfBtn = new ModernButton("PDF Format", Constants.PRIMARY_COLOR);
        pdfBtn.addActionListener(e -> exportReport("PDF"));

        ModernButton excelBtn = new ModernButton("Excel Format", Constants.SUCCESS_COLOR);
        excelBtn.addActionListener(e -> exportReport("Excel"));

        ModernButton csvBtn = new ModernButton("CSV Format", Constants.INFO_COLOR);
        csvBtn.addActionListener(e -> exportReport("CSV"));

        mainPanel.add(titleLabel);
        mainPanel.add(pdfBtn);
        mainPanel.add(excelBtn);
        mainPanel.add(csvBtn);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void exportReport(String format) {
        String extension = "";
        switch (format) {
            case "PDF": extension = ".pdf"; break;
            case "Excel": extension = ".xlsx"; break;
            case "CSV": extension = ".csv"; break;
        }

        String fileName = reportType.toLowerCase().replace(" ", "_") + "_report" + extension;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Show progress dialog
            JDialog progressDialog = new JDialog(this, "Generating Report", true);
            progressDialog.setSize(300, 100);
            progressDialog.setLayout(new BorderLayout());
            progressDialog.setLocationRelativeTo(this);

            JLabel progressLabel = new JLabel("Generating " + reportType + " in " + format + " format...");
            progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
            progressDialog.add(progressLabel, BorderLayout.CENTER);

            // Use SwingWorker for background processing
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Generate report
                    // This would call the appropriate service method
                    Thread.sleep(1000); // Simulate work
                    return null;
                }

                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        get(); // Check for exceptions
                        JOptionPane.showMessageDialog(ReportDialog.this,
                                reportType + " generated successfully in " + format + " format!\n" +
                                        "Saved to: " + file.getAbsolutePath(),
                                "Report Generated", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ReportDialog.this,
                                "Error generating report: " + e.getMessage(),
                                "Report Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
            progressDialog.setVisible(true);
        }
    }
}