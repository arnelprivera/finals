package TiwalApp.database;

import TiwalApp.models.FamilyMember;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FamilyMemberDAO {
    private Connection connection;

    public FamilyMemberDAO(DatabaseConnection dbConnection) {
        this.connection = dbConnection.getConnection();
    }

    public boolean createFamilyMember(FamilyMember member) {
        String sql = "INSERT INTO family_members (resident_id, full_name, relationship, " +
                "gender, birthdate, occupation) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, member.getResidentId());
            pstmt.setString(2, member.getFullName());
            pstmt.setString(3, member.getRelationship());
            pstmt.setString(4, member.getGender());
            pstmt.setString(5, member.getBirthdate());
            pstmt.setString(6, member.getOccupation());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        member.setFamilyMemberId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public FamilyMember getFamilyMemberById(int familyMemberId) {
        String sql = "SELECT * FROM family_members WHERE family_member_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, familyMemberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractFamilyMemberFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FamilyMember> getFamilyMembersByResident(String residentId) {
        List<FamilyMember> members = new ArrayList<>();
        String sql = "SELECT * FROM family_members WHERE resident_id = ? ORDER BY relationship, full_name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, residentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                members.add(extractFamilyMemberFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public boolean updateFamilyMember(FamilyMember member) {
        String sql = "UPDATE family_members SET full_name = ?, relationship = ?, gender = ?, " +
                "birthdate = ?, occupation = ? WHERE family_member_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getFullName());
            pstmt.setString(2, member.getRelationship());
            pstmt.setString(3, member.getGender());
            pstmt.setString(4, member.getBirthdate());
            pstmt.setString(5, member.getOccupation());
            pstmt.setInt(6, member.getFamilyMemberId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFamilyMember(int familyMemberId) {
        String sql = "DELETE FROM family_members WHERE family_member_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, familyMemberId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countFamilyMembersByResident(String residentId) {
        String sql = "SELECT COUNT(*) FROM family_members WHERE resident_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, residentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private FamilyMember extractFamilyMemberFromResultSet(ResultSet rs) throws SQLException {
        FamilyMember member = new FamilyMember(
                rs.getString("resident_id"),
                rs.getString("full_name"),
                rs.getString("relationship")
        );
        member.setFamilyMemberId(rs.getInt("family_member_id"));
        member.setGender(rs.getString("gender"));
        member.setBirthdate(rs.getString("birthdate"));
        member.setOccupation(rs.getString("occupation"));
        member.setCreatedAt(rs.getTimestamp("created_at"));
        return member;
    }
}