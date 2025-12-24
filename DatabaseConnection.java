package TiwalApp.database;

import TiwalApp.utils.Constants;
import java.sql.*;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(Constants.DB_URL);
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String[] createTableSQL = {
                // Users table
                "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "password TEXT NOT NULL, " +
                        "role TEXT NOT NULL, " +
                        "full_name TEXT NOT NULL, " +
                        "email TEXT, " +
                        "phone TEXT, " +
                        "address TEXT, " +
                        "birthdate TEXT, " +
                        "resident_id TEXT UNIQUE, " +
                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)",

                // Residents table
                "CREATE TABLE IF NOT EXISTS residents (" +
                        "resident_id TEXT PRIMARY KEY, " +
                        "full_name TEXT NOT NULL, " +
                        "address TEXT NOT NULL, " +
                        "birthdate TEXT, " +
                        "gender TEXT, " +
                        "civil_status TEXT, " +
                        "occupation TEXT, " +
                        "education TEXT, " +
                        "contact_number TEXT, " +
                        "emergency_contact TEXT, " +
                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)",

                // Complaints table
                "CREATE TABLE IF NOT EXISTS complaints (" +
                        "complaint_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "resident_id TEXT NOT NULL, " +
                        "complaint_type TEXT NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "status TEXT DEFAULT 'Pending', " +
                        "priority TEXT DEFAULT 'Medium', " +
                        "assigned_to TEXT, " +
                        "submitted_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "resolved_date DATETIME, " +
                        "remarks TEXT, " +
                        "FOREIGN KEY (resident_id) REFERENCES residents(resident_id))",

                // Clearances table
                "CREATE TABLE IF NOT EXISTS clearances (" +
                        "clearance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "resident_id TEXT NOT NULL, " +
                        "clearance_type TEXT NOT NULL, " +
                        "purpose TEXT NOT NULL, " +
                        "status TEXT DEFAULT 'Pending', " +
                        "issue_date DATETIME, " +
                        "expiry_date DATETIME, " +
                        "issued_by TEXT, " +
                        "file_path TEXT, " +
                        "FOREIGN KEY (resident_id) REFERENCES residents(resident_id))",

                // Announcements table
                "CREATE TABLE IF NOT EXISTS announcements (" +
                        "announcement_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "category TEXT DEFAULT 'General', " +
                        "posted_by TEXT NOT NULL, " +
                        "posted_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "expiry_date DATETIME, " +
                        "is_active INTEGER DEFAULT 1)",

                // Family members table
                "CREATE TABLE IF NOT EXISTS family_members (" +
                        "family_member_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "resident_id TEXT NOT NULL, " +
                        "full_name TEXT NOT NULL, " +
                        "relationship TEXT NOT NULL, " +
                        "gender TEXT, " +
                        "birthdate TEXT, " +
                        "occupation TEXT, " +
                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (resident_id) REFERENCES residents(resident_id))",

                // Activity logs table
                "CREATE TABLE IF NOT EXISTS activity_logs (" +
                        "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id TEXT NOT NULL, " +
                        "user_name TEXT NOT NULL, " +
                        "action TEXT NOT NULL, " +
                        "details TEXT, " +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : createTableSQL) {
                stmt.execute(sql);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}