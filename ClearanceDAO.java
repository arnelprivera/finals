package TiwalApp.database;

import TiwalApp.models.Clearance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClearanceDAO {
    private Connection connection;

    public ClearanceDAO(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
    }

    public boolean createClearance(Clearance clearance) {
        String sql = "INSERT INTO clearances (resident_id, clearance_type, purpose, status, " +
                "issue_date, expiry_date, issued_by, file_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, clearance.getResidentId());
            pstmt.setString(2, clearance.getClearanceType());
            pstmt.setString(3, clearance.getPurpose());
            pstmt.setString(4, clearance.getStatus());
            pstmt.setTimestamp(5, clearance.getIssueDate() != null ?
                    new Timestamp(clearance.getIssueDate().getTime()) : null);
            pstmt.setTimestamp(6, clearance.getExpiryDate() != null ?
                    new Timestamp(clearance.getExpiryDate().getTime()) : null);
            pstmt.setString(7, clearance.getIssuedBy());
            pstmt.setString(8, clearance.getFilePath());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        clearance.setClearanceId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Clearance getClearanceById(int clearanceId) {
        String sql = "SELECT * FROM clearances WHERE clearance_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, clearanceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractClearanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Clearance> getAllClearances() {
        List<Clearance> clearances = new ArrayList<>();
        String sql = "SELECT * FROM clearances ORDER BY issue_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clearances.add(extractClearanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clearances;
    }

    public List<Clearance> getClearancesByStatus(String status) {
        List<Clearance> clearances = new ArrayList<>();
        String sql = "SELECT * FROM clearances WHERE status = ? ORDER BY issue_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                clearances.add(extractClearanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clearances;
    }

    public List<Clearance> getClearancesByResident(String residentId) {
        List<Clearance> clearances = new ArrayList<>();
        String sql = "SELECT * FROM clearances WHERE resident_id = ? ORDER BY issue_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, residentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                clearances.add(extractClearanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clearances;
    }

    public boolean updateClearance(Clearance clearance) {
        String sql = "UPDATE clearances SET resident_id = ?, clearance_type = ?, purpose = ?, " +
                "status = ?, issue_date = ?, expiry_date = ?, issued_by = ?, file_path = ? " +
                "WHERE clearance_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, clearance.getResidentId());
            pstmt.setString(2, clearance.getClearanceType());
            pstmt.setString(3, clearance.getPurpose());
            pstmt.setString(4, clearance.getStatus());
            pstmt.setTimestamp(5, clearance.getIssueDate() != null ?
                    new Timestamp(clearance.getIssueDate().getTime()) : null);
            pstmt.setTimestamp(6, clearance.getExpiryDate() != null ?
                    new Timestamp(clearance.getExpiryDate().getTime()) : null);
            pstmt.setString(7, clearance.getIssuedBy());
            pstmt.setString(8, clearance.getFilePath());
            pstmt.setInt(9, clearance.getClearanceId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteClearance(int clearanceId) {
        String sql = "DELETE FROM clearances WHERE clearance_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, clearanceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Clearance extractClearanceFromResultSet(ResultSet rs) throws SQLException {
        Clearance clearance = new Clearance(
                rs.getString("resident_id"),
                rs.getString("clearance_type"),
                rs.getString("purpose")
        );
        clearance.setClearanceId(rs.getInt("clearance_id"));
        clearance.setStatus(rs.getString("status"));
        clearance.setIssueDate(rs.getTimestamp("issue_date"));
        clearance.setExpiryDate(rs.getTimestamp("expiry_date"));
        clearance.setIssuedBy(rs.getString("issued_by"));
        clearance.setFilePath(rs.getString("file_path"));
        return clearance;
    }
}