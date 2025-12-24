package TiwalApp.utils;

import TiwalApp.models.Clearance;
import TiwalApp.models.Resident;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;

public class PrintUtils {

    public static class PrintableDocument implements Printable {
        private Clearance clearance;
        private Resident resident;

        public PrintableDocument(Clearance clearance, Resident resident) {
            this.clearance = clearance;
            this.resident = resident;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.setColor(Color.BLACK);

            int y = 50;
            g2d.drawString("BARANGAY CLEARANCE", 100, y);
            y += 40;
            g2d.drawLine(50, y, 550, y);
            y += 30;

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));

            String[] details = {
                    "Clearance ID: " + clearance.getClearanceId(),
                    "Resident ID: " + clearance.getResidentId(),
                    "Resident Name: " + (resident != null ? resident.getFullName() : "N/A"),
                    "Address: " + (resident != null ? resident.getAddress() : "N/A"),
                    "Clearance Type: " + clearance.getClearanceType(),
                    "Purpose: " + clearance.getPurpose(),
                    "Status: " + clearance.getStatus(),
                    "Issued By: " + (clearance.getIssuedBy() != null ? clearance.getIssuedBy() : "N/A"),
                    "Issue Date: " + (clearance.getIssueDate() != null ?
                            new SimpleDateFormat("yyyy-MM-dd").format(clearance.getIssueDate()) : "N/A"),
                    "Expiry Date: " + (clearance.getExpiryDate() != null ?
                            new SimpleDateFormat("yyyy-MM-dd").format(clearance.getExpiryDate()) : "N/A")
            };

            for (String detail : details) {
                g2d.drawString(detail, 50, y);
                y += 30;
            }

            y += 40;
            g2d.drawLine(50, y, 550, y);
            y += 30;

            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.drawString("This is a computer-generated document. Official use only.", 100, y);
            y += 20;
            g2d.drawString("TiwalApp Barangay Management System", 150, y);

            return PAGE_EXISTS;
        }
    }

    public static boolean printClearance(Clearance clearance, Resident resident, Component parent) {
        try {
            Printable printable = new PrintableDocument(clearance, resident);
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable(printable);

            if (printerJob.printDialog()) {
                printerJob.print();
                return true;
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Error printing document: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static boolean printComponent(Component component, String jobName) {
        PrinterJob pjob = PrinterJob.getPrinterJob();
        pjob.setJobName(jobName);

        pjob.setPrintable(new Printable() {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) return NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                g2.scale(0.5, 0.5);
                component.print(g2);
                return PAGE_EXISTS;
            }
        });

        if (pjob.printDialog()) {
            try {
                pjob.print();
                return true;
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}