package TiwalApp.services;

import TiwalApp.database.*;
import TiwalApp.models.*;
import TiwalApp.utils.ExportUtils;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportService {
    private DatabaseConnection dbConnection;
    private ResidentDAO residentDAO;
    private ComplaintDAO complaintDAO;
    private ClearanceDAO clearanceDAO;
    private AnnouncementDAO announcementDAO;

    public ReportService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.residentDAO = new ResidentDAO(dbConnection);
        this.complaintDAO = new ComplaintDAO(dbConnection);
        this.clearanceDAO = new ClearanceDAO(dbConnection);
        this.announcementDAO = new AnnouncementDAO(dbConnection);
    }

    public void generateResidentsReport(String format, File file) {
        List<Resident> residents = residentDAO.getAllResidents();

        // Create table data
        String[] columns = {"Resident ID", "Full Name", "Address", "Birthdate",
                "Gender", "Civil Status", "Occupation", "Contact Number"};
        Object[][] data = new Object[residents.size()][columns.length];

        for (int i = 0; i < residents.size(); i++) {
            Resident r = residents.get(i);
            data[i][0] = r.getResidentId();
            data[i][1] = r.getFullName();
            data[i][2] = r.getAddress();
            data[i][3] = r.getBirthdate();
            data[i][4] = r.getGender();
            data[i][5] = r.getCivilStatus();
            data[i][6] = r.getOccupation();
            data[i][7] = r.getContactNumber();
        }

        generateReport("Residents Report", columns, data, format, file);
    }

    public void generateComplaintsReport(String format, File file) {
        List<Complaint> complaints = complaintDAO.getAllComplaints();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String[] columns = {"ID", "Resident ID", "Type", "Description", "Status",
                "Priority", "Submitted Date", "Resolved Date"};
        Object[][] data = new Object[complaints.size()][columns.length];

        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            data[i][0] = c.getComplaintId();
            data[i][1] = c.getResidentId();
            data[i][2] = c.getComplaintType();
            data[i][3] = c.getDescription();
            data[i][4] = c.getStatus();
            data[i][5] = c.getPriority();
            data[i][6] = c.getSubmittedDate() != null ? sdf.format(c.getSubmittedDate()) : "";
            data[i][7] = c.getResolvedDate() != null ? sdf.format(c.getResolvedDate()) : "";
        }

        generateReport("Complaints Report", columns, data, format, file);
    }

    public void generateDemographicsReport(String format, File file) {
        List<Resident> residents = residentDAO.getAllResidents();

        // Analyze demographics
        Map<String, Integer> genderCount = new HashMap<>();
        Map<String, Integer> civilStatusCount = new HashMap<>();
        int youth = 0, adult = 0, senior = 0;
        Calendar now = Calendar.getInstance();

        for (Resident resident : residents) {
            // Gender count
            String gender = resident.getGender() != null ? resident.getGender() : "Unknown";
            genderCount.put(gender, genderCount.getOrDefault(gender, 0) + 1);

            // Civil status count
            String civilStatus = resident.getCivilStatus() != null ? resident.getCivilStatus() : "Unknown";
            civilStatusCount.put(civilStatus, civilStatusCount.getOrDefault(civilStatus, 0) + 1);

            // Age group
            if (resident.getBirthdate() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date birthDate = sdf.parse(resident.getBirthdate());
                    Calendar birthCal = Calendar.getInstance();
                    birthCal.setTime(birthDate);

                    int age = now.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
                    if (age <= 18) youth++;
                    else if (age <= 59) adult++;
                    else senior++;
                } catch (Exception e) {
                    // Skip invalid dates
                }
            }
        }

        // Prepare data
        List<String> categories = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        categories.add("Total Residents");
        counts.add(residents.size());

        for (Map.Entry<String, Integer> entry : genderCount.entrySet()) {
            categories.add("Gender - " + entry.getKey());
            counts.add(entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : civilStatusCount.entrySet()) {
            categories.add("Civil Status - " + entry.getKey());
            counts.add(entry.getValue());
        }

        categories.add("Age Group - Youth (0-18)");
        counts.add(youth);
        categories.add("Age Group - Adult (19-59)");
        counts.add(adult);
        categories.add("Age Group - Senior (60+)");
        counts.add(senior);

        String[] columns = {"Category", "Count"};
        Object[][] data = new Object[categories.size()][2];

        for (int i = 0; i < categories.size(); i++) {
            data[i][0] = categories.get(i);
            data[i][1] = counts.get(i);
        }

        generateReport("Demographics Report", columns, data, format, file);
    }

    private void generateReport(String title, String[] columns, Object[][] data, String format, File file) {
        // Create a temporary table
        JTable table = new JTable(data, columns);

        switch (format.toUpperCase()) {
            case "PDF":
                ExportUtils.exportTableToPDF(table, title, file);
                break;
            case "EXCEL":
                ExportUtils.exportTableToExcel(table, title, file);
                break;
            case "CSV":
                ExportUtils.exportTableToCSV(table, file);
                break;
            case "WORD":
                // Implement Word export if needed
                break;
        }
    }
}