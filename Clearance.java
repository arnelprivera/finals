package TiwalApp.models;

import java.io.Serializable;
import java.util.Date;

public class Clearance implements Serializable {
    private static final long serialVersionUID = 1L;

    private int clearanceId;
    private String residentId;
    private String clearanceType;
    private String purpose;
    private Date issueDate;
    private Date expiryDate;
    private String status;
    private String issuedBy;
    private String filePath;

    public Clearance(String residentId, String clearanceType, String purpose) {
        this.clearanceId = generateId();
        this.residentId = residentId;
        this.clearanceType = clearanceType;
        this.purpose = purpose;
        this.status = "Pending";
    }

    private int generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    // Getters and setters
    public int getClearanceId() { return clearanceId; }
    public void setClearanceId(int clearanceId) { this.clearanceId = clearanceId; }

    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }

    public String getClearanceType() { return clearanceType; }
    public void setClearanceType(String clearanceType) { this.clearanceType = clearanceType; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}