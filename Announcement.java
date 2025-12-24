package TiwalApp.models;

import java.io.Serializable;
import java.util.Date;

public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    private int announcementId;
    private String title;
    private String content;
    private String category;
    private String postedBy;
    private Date postedDate;
    private Date expiryDate;
    private boolean isActive;

    public Announcement(String title, String content, String postedBy) {
        this.announcementId = generateId();
        this.title = title;
        this.content = content;
        this.postedBy = postedBy;
        this.category = "General";
        this.postedDate = new Date();
        this.isActive = true;
    }

    private int generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    // Getters and setters
    public int getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(int announcementId) { this.announcementId = announcementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public Date getPostedDate() { return postedDate; }
    public void setPostedDate(Date postedDate) { this.postedDate = postedDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}