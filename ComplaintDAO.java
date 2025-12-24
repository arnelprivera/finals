package TiwalApp.database;

import TiwalApp.models.Complaint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {
    private Connection connection;

    public ComplaintDAO(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
    }

    public boolean createComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (resident_id, complaint_type, description, status, " +
                "priority, assigned_to, submitted_date, resolved_date, remarks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, complaint.getResidentId());
            pstmt.setString(2, complaint.getComplaintType());
            pstmt.setString(3, complaint.getDescription());
            pstmt.setString(4, complaint.getStatus());
            pstmt.setString(5, complaint.getPriority());
            pstmt.setString(6, complaint.getAssignedTo());
            pstmt.setTimestamp(7, new Timestamp(complaint.getSubmittedDate().getTime()));
            pstmt.setTimestamp(8, complaint.getResolvedDate() != null ?
                    new Timestamp(complaint.getResolvedDate().getTime()) : null);
            pstmt.setString(9, complaint.getRemarks());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        complaint.setComplaintId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Complaint getComplaintById(int complaintId) {
        String sql = "SELECT * FROM complaints WHERE complaint_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, complaintId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractComplaintFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints ORDER BY submitted_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE status = ? ORDER BY submitted_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public List<Complaint> getComplaintsByResident(String residentId) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE resident_id = ? ORDER BY submitted_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, residentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public boolean updateComplaint(Complaint complaint) {
        String sql = "UPDATE complaints SET resident_id = ?, complaint_type = ?, description = ?, " +
                "status = ?, priority = ?, assigned_to = ?, submitted_date = ?, " +
                "resolved_date = ?, remarks = ? WHERE complaint_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, complaint.getResidentId());
            pstmt.setString(2, complaint.getComplaintType());
            pstmt.setString(3, complaint.getDescription());
            pstmt.setString(4, complaint.getStatus());
            pstmt.setString(5, complaint.getPriority());
            pstmt.setString(6, complaint.getAssignedTo());
            pstmt.setTimestamp(7, new Timestamp(complaint.getSubmittedDate().getTime()));
            pstmt.setTimestamp(8, complaint.getResolvedDate() != null ?
                    new Timestamp(complaint.getResolvedDate().getTime()) : null);
            pstmt.setString(9, complaint.getRemarks());
            pstmt.setInt(10, complaint.getComplaintId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateComplaintStatus(int complaintId, String status) {
        String sql = "UPDATE complaints SET status = ?, resolved_date = ? WHERE complaint_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, "Resolved".equals(status) || "Completed".equals(status) ?
                    new Timestamp(System.currentTimeMillis()) : null);
            pstmt.setInt(3, complaintId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteComplaint(int complaintId) {
        String sql = "DELETE FROM complaints WHERE complaint_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Complaint extractComplaintFromResultSet(ResultSet rs) throws SQLException {
        Complaint complaint = new Complaint(
                rs.getString("resident_id"),
                rs.getString("complaint_type"),
                rs.getString("description")
        );
        complaint.setComplaintId(rs.getInt("complaint_id"));
        complaint.setStatus(rs.getString("status"));
        complaint.setPriority(rs.getString("priority"));
        complaint.setAssignedTo(rs.getString("assigned_to"));
        complaint.setSubmittedDate(rs.getTimestamp("submitted_date"));
        complaint.setResolvedDate(rs.getTimestamp("resolved_date"));
        complaint.setRemarks(rs.getString("remarks"));
        return complaint;
    }
}