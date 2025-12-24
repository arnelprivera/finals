package TiwalApp.models;

import java.io.Serializable;
import java.util.Date;

public class FamilyMember implements Serializable {
    private static final long serialVersionUID = 1L;

    private int familyMemberId;
    private String residentId;
    private String fullName;
    private String relationship;
    private String gender;
    private String birthdate;
    private String occupation;
    private Date createdAt;

    public FamilyMember(String residentId, String fullName, String relationship) {
        this.familyMemberId = generateId();
        this.residentId = residentId;
        this.fullName = fullName;
        this.relationship = relationship;
        this.createdAt = new Date();
    }

    private int generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    // Getters and setters
    public int getFamilyMemberId() { return familyMemberId; }
    public void setFamilyMemberId(int familyMemberId) { this.familyMemberId = familyMemberId; }

    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}