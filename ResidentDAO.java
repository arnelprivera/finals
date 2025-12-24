package TiwalApp.database;

import TiwalApp.models.Resident;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResidentDAO {
    private Connection connection;

    public ResidentDAO(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
    }

    public boolean createResident(Resident resident) {
        String sql = "INSERT INTO residents (resident_id, full_name, address, birthdate, gender, " +
                "civil_status, occupation, education, contact_number, emergency_contact) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, resident.getResidentId());
            pstmt.setString(2, resident.getFullName());
            pstmt.setString(3, resident.getAddress());
            pstmt.setString(4, resident.getBirthdate());
            pstmt.setString(5, resident.getGender());
            pstmt.setString(6, resident.getCivilStatus());
            pstmt.setString(7, resident.getOccupation());
            pstmt.setString(8, resident.getEducation());
            pstmt.setString(9, resident.getContactNumber());
            pstmt.setString(10, resident.getEmergencyContact());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Resident getResidentById(String residentId) {
        String sql = "SELECT * FROM residents WHERE resident_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, residentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Resident resident = new Resident(
                        rs.getString("full_name"),
                        rs.getString("address"),
                        rs.getString("birthdate")
                );
                resident.setResidentId(rs.getString("resident_id"));
                resident.setGender(rs.getString("gender"));
                resident.setCivilStatus(rs.getString("civil_status"));
                resident.setOccupation(rs.getString("occupation"));
                resident.setEducation(rs.getString("education"));
                resident.setContactNumber(rs.getString("contact_number"));
                resident.setEmergencyContact(rs.getString("emergency_contact"));
                resident.setCreatedAt(rs.getTimestamp("created_at"));
                return resident;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Resident> getAllResidents() {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Resident resident = new Resident(
                        rs.getString("full_name"),
                        rs.getString("address"),
                        rs.getString("birthdate")
                );
                resident.setResidentId(rs.getString("resident_id"));
                resident.setGender(rs.getString("gender"));
                resident.setCivilStatus(rs.getString("civil_status"));
                resident.setOccupation(rs.getString("occupation"));
                resident.setEducation(rs.getString("education"));
                resident.setContactNumber(rs.getString("contact_number"));
                resident.setEmergencyContact(rs.getString("emergency_contact"));
                resident.setCreatedAt(rs.getTimestamp("created_at"));
                residents.add(resident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return residents;
    }

    public boolean updateResident(Resident resident) {
        String sql = "UPDATE residents SET full_name = ?, address = ?, birthdate = ?, gender = ?, " +
                "civil_status = ?, occupation = ?, education = ?, contact_number = ?, " +
                "emergency_contact = ? WHERE resident_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, resident.getFullName());
            pstmt.setString(2, resident.getAddress());
            pstmt.setString(3, resident.getBirthdate());
            pstmt.setString(4, resident.getGender());
            pstmt.setString(5, resident.getCivilStatus());
            pstmt.setString(6, resident.getOccupation());
            pstmt.setString(7, resident.getEducation());
            pstmt.setString(8, resident.getContactNumber());
            pstmt.setString(9, resident.getEmergencyContact());
            pstmt.setString(10, resident.getResidentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteResident(String residentId) {
        String sql = "DELETE FROM residents WHERE resident_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, residentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}