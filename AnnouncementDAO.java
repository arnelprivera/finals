package TiwalApp.database;

import TiwalApp.models.Announcement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    private Connection connection;

    public AnnouncementDAO(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
    }

    public boolean createAnnouncement(Announcement announcement) {
        String sql = "INSERT INTO announcements (title, content, category, posted_by, " +
                "posted_date, expiry_date, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setString(3, announcement.getCategory());
            pstmt.setString(4, announcement.getPostedBy());
            pstmt.setTimestamp(5, new Timestamp(announcement.getPostedDate().getTime()));
            pstmt.setTimestamp(6, announcement.getExpiryDate() != null ?
                    new Timestamp(announcement.getExpiryDate().getTime()) : null);
            pstmt.setInt(7, announcement.isActive() ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        announcement.setAnnouncementId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Announcement getAnnouncementById(int announcementId) {
        String sql = "SELECT * FROM announcements WHERE announcement_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAnnouncementFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements ORDER BY posted_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    public List<Announcement> getActiveAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements WHERE is_active = 1 ORDER BY posted_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    public List<Announcement> getAnnouncementsByCategory(String category) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements WHERE category = ? AND is_active = 1 ORDER BY posted_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    public boolean updateAnnouncement(Announcement announcement) {
        String sql = "UPDATE announcements SET title = ?, content = ?, category = ?, posted_by = ?, " +
                "posted_date = ?, expiry_date = ?, is_active = ? WHERE announcement_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setString(3, announcement.getCategory());
            pstmt.setString(4, announcement.getPostedBy());
            pstmt.setTimestamp(5, new Timestamp(announcement.getPostedDate().getTime()));
            pstmt.setTimestamp(6, announcement.getExpiryDate() != null ?
                    new Timestamp(announcement.getExpiryDate().getTime()) : null);
            pstmt.setInt(7, announcement.isActive() ? 1 : 0);
            pstmt.setInt(8, announcement.getAnnouncementId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleAnnouncementStatus(int announcementId, boolean isActive) {
        String sql = "UPDATE announcements SET is_active = ? WHERE announcement_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, isActive ? 1 : 0);
            pstmt.setInt(2, announcementId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAnnouncement(int announcementId) {
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Announcement extractAnnouncementFromResultSet(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement(
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("posted_by")
        );
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setCategory(rs.getString("category"));
        announcement.setPostedDate(rs.getTimestamp("posted_date"));
        announcement.setExpiryDate(rs.getTimestamp("expiry_date"));
        announcement.setActive(rs.getInt("is_active") == 1);
        return announcement;
    }
}