package TiwalApp.utils;

import TiwalApp.models.*;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportUtils {

    public static void exportTableToPDF(JTable table, String title, File file) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add title
            document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph(" "));

            // Add generation date
            document.add(new Paragraph("Generated on: " +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new Paragraph(" "));

            // Create table
            com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Add headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                pdfTable.addCell(new Phrase(table.getColumnName(i),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            }

            // Add data
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    pdfTable.addCell(new Phrase(value != null ? value.toString() : ""));
                }
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(null,
                    "Exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error exporting to PDF: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportTableToExcel(JTable table, String title, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(title);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < table.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumnName(i));
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            // Add data rows
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    Cell cell = dataRow.createCell(col);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < table.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            JOptionPane.showMessageDialog(null,
                    "Exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error exporting to Excel: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportTableToCSV(JTable table, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                writer.print(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) {
                    writer.print(",");
                }
            }
            writer.println();

            // Write data
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    if (value != null) {
                        String cellValue = value.toString();
                        // Escape quotes and wrap in quotes if contains comma
                        if (cellValue.contains(",") || cellValue.contains("\"")) {
                            cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                        }
                        writer.print(cellValue);
                    }
                    if (col < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            JOptionPane.showMessageDialog(null,
                    "Exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error exporting to CSV: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportClearanceToWord(Clearance clearance, Resident resident, File file) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Title
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("BARANGAY CLEARANCE");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Empty line
            document.createParagraph().createRun().addBreak();

            // Details
            XWPFParagraph details = document.createParagraph();
            details.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun detailsRun = details.createRun();

            String[] detailLines = {
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

            for (String line : detailLines) {
                detailsRun.setText(line);
                detailsRun.addBreak();
            }

            // Footer
            document.createParagraph().createRun().addBreak();
            XWPFParagraph footer = document.createParagraph();
            footer.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun footerRun = footer.createRun();
            footerRun.setItalic(true);
            footerRun.setText("This is a computer-generated document. Official use only.");
            footerRun.addBreak();
            footerRun.setText("TiwalApp Barangay Management System");

            try (FileOutputStream out = new FileOutputStream(file)) {
                document.write(out);
            }

            JOptionPane.showMessageDialog(null,
                    "Exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error exporting to Word: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportClearanceAsImage(Clearance clearance, Resident resident, File file, String format) {
        try {
            int width = 800;
            int height = 1000;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Fill with white background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Draw content
            drawClearanceContent(g2d, clearance, resident, width, height);

            g2d.dispose();

            if (format.equalsIgnoreCase("JPEG")) {
                ImageIO.write(image, "JPEG", file);
            } else {
                ImageIO.write(image, "PNG", file);
            }

            JOptionPane.showMessageDialog(null,
                    "Exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error exporting image: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void drawClearanceContent(Graphics2D g2d, Clearance clearance, Resident resident, int width, int height) {
        // Implementation of drawing clearance content
        // Similar to original drawClearanceImageContent method
    }

    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}