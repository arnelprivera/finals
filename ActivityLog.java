package TiwalApp.models;

import java.io.Serializable;
import java.util.Date;

public class ActivityLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int logId;
    private String userId;
    private String userName;
    private String action;
    private String details;
    private Date timestamp;

    public ActivityLog(String userId, String userName, String action, String details) {
        this.logId = generateId();
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.details = details;
        this.timestamp = new Date();
    }

    private int generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    // Getters and setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}