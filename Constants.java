package TiwalApp.utils;

import java.awt.*;

public class Constants {
    // Enhanced Theme colors with purple gradient scheme
    public static final Color PRIMARY_COLOR = new Color(103, 58, 183); // Deep purple
    public static final Color PRIMARY_DARK = new Color(81, 45, 168); // Darker purple
    public static final Color PRIMARY_LIGHT = new Color(187, 134, 252); // Light purple
    public static final Color SECONDARY_COLOR = new Color(255, 193, 7); // Yellow/Gold
    public static final Color SECONDARY_DARK = new Color(255, 179, 0); // Darker yellow
    public static final Color GRADIENT_START = new Color(147, 112, 219); // Medium purple
    public static final Color GRADIENT_END = new Color(230, 230, 250); // Lavender
    public static final Color BACKGROUND_COLOR = new Color(245, 240, 255); // Very light purple
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color GLASS_BACKGROUND = new Color(255, 255, 255, 200);
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(97, 97, 97);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);
    public static final Color ERROR_COLOR = new Color(211, 47, 47);
    public static final Color INFO_COLOR = new Color(2, 136, 209);
    public static final Color BORDER_COLOR = new Color(224, 224, 224, 150);
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    // Enhanced FONTS
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font LARGE_FONT = new Font("Segoe UI", Font.BOLD, 40);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Database constants
    public static final String DB_URL = "jdbc:sqlite:tiwalapp.db";

    // Role constants
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_RESIDENT = "Resident";

    // Status constants
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_RESOLVED = "Resolved";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";

    // Priority constants
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_URGENT = "Urgent";

    // Complaint types
    public static final String[] COMPLAINT_TYPES = {
            "Noise Complaint", "Garbage Collection", "Street Lights",
            "Water Supply", "Drainage", "Security", "Other"
    };

    // Clearance types
    public static final String[] CLEARANCE_TYPES = {
            "Barangay Clearance", "Certificate of Residency",
            "Certificate of Indigency", "Business Clearance", "Other"
    };

    // Announcement categories
    public static final String[] ANNOUNCEMENT_CATEGORIES = {
            "General", "Meeting", "Event", "Reminder", "Important", "Other"
    };

    // Relationship types
    public static final String[] RELATIONSHIP_TYPES = {
            "Spouse", "Child", "Parent", "Sibling", "Grandparent", "Other"
    };

    // Gender options
    public static final String[] GENDER_OPTIONS = {"Male", "Female", "Other"};

    // Civil status options
    public static final String[] CIVIL_STATUS_OPTIONS = {"Single", "Married", "Divorced", "Widowed"};
}