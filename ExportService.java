package TiwalApp.services;

import TiwalApp.utils.ExportUtils;
import TiwalApp.models.Clearance;
import TiwalApp.models.Resident;

import javax.swing.*;
import java.io.File;

public class ExportService {

    public static void exportClearance(Clearance clearance, Resident resident, String format, File file) {
        switch (format.toUpperCase()) {
            case "PDF":
                // Implement PDF export
                break;
            case "WORD":
                ExportUtils.exportClearanceToWord(clearance, resident, file);
                break;
            case "JPEG":
            case "PNG":
                ExportUtils.exportClearanceAsImage(clearance, resident, file, format);
                break;
            default:
                JOptionPane.showMessageDialog(null,
                        "Unsupported export format: " + format,
                        "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}