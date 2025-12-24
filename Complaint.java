package TiwalApp.models;

import java.io.Serializable;
import java.util.Date;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private int complaintId;
    private String residentId;
    private String complaintType;
    private String description;
    private String status;
    private String priority;
    private String assignedTo;
    private Date submittedDate;
    private Date resolvedDate;
    private String remarks;

    public Complaint(String residentId, String complaintType, String description) {
        this.complaintId = generateId();
        this.residentId = residentId;
        this.complaintType = complaintType;
        this.description = description;
        this.status = "Pending";
        this.priority = "Medium";
        this.submittedDate = new Date();
    }

    private int generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    // Getters and setters
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }

    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public Date getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(Date submittedDate) { this.submittedDate = submittedDate; }

    public Date getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(Date resolvedDate) { this.resolvedDate = resolvedDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}