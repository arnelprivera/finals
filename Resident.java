package TiwalApp.models;

import java.io.Serializable;
import java.util.Date;

public class Resident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String residentId;
    private String fullName;
    private String address;
    private String birthdate;
    private String gender;
    private String civilStatus;
    private String occupation;
    private String education;
    private String contactNumber;
    private String emergencyContact;
    private Date createdAt;

    public Resident(String fullName, String address, String birthdate) {
        this.residentId = "RES" + System.currentTimeMillis();
        this.fullName = fullName;
        this.address = address;
        this.birthdate = birthdate;
        this.createdAt = new Date();
    }

    // Getters and setters
    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}