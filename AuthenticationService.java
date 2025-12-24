package TiwalApp.services;

import TiwalApp.database.UserDAO;
import TiwalApp.database.DatabaseConnection;
import TiwalApp.models.User;
import TiwalApp.database.ActivityLogDAO;

public class AuthenticationService {
    private UserDAO userDAO;
    private ActivityLogDAO activityLogDAO;

    public AuthenticationService(DatabaseConnection dbConnection) {
        this.userDAO = new UserDAO(dbConnection);
        this.activityLogDAO = new ActivityLogDAO(dbConnection);
    }

    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            // Log the activity
            activityLogDAO.logActivity(user.getUsername(), user.getFullName(),
                    "LOGIN", "User logged in: " + username);
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        if (userDAO.usernameExists(user.getUsername())) {
            return false;
        }

        boolean success = userDAO.createUser(user);
        if (success) {
            activityLogDAO.logActivity(user.getUsername(), user.getFullName(),
                    "REGISTER", "New user registered: " + user.getUsername());
        }
        return success;
    }

    public void logout(User user) {
        if (user != null) {
            activityLogDAO.logActivity(user.getUsername(), user.getFullName(),
                    "LOGOUT", "User logged out: " + user.getUsername());
        }
    }
}