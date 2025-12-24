package TiwalApp.database;

import TiwalApp.models.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {
    private Connection connection;

    public ActivityLogDAO(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
    }

    public boolean logActivity(String userId, String userName, String action, String details) {
        String sql = "INSERT INTO activity_logs (user_id, user_name, action, details) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, action);
            pstmt.setString(4, details);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ActivityLog> getAllActivityLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(extractActivityLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public List<ActivityLog> getActivityLogsByUser(String userId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractActivityLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public List<ActivityLog> getActivityLogsByAction(String action) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE action = ? ORDER BY timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, action);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractActivityLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public boolean clearAllActivityLogs() {
        String sql = "DELETE FROM activity_logs";

        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearActivityLogsByDate(Date beforeDate) {
        String sql = "DELETE FROM activity_logs WHERE timestamp < ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(beforeDate.getTime()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ActivityLog extractActivityLogFromResultSet(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog(
                rs.getString("user_id"),
                rs.getString("user_name"),
                rs.getString("action"),
                rs.getString("details")
        );
        log.setLogId(rs.getInt("log_id"));
        log.setTimestamp(rs.getTimestamp("timestamp"));
        return log;
    }
}