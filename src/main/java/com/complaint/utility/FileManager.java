package com.complaint.utility;

import com.complaint.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle File I/O operations for Users and Complaints.
 * Persists data in simple text files using the pipe (|) delimiter.
 */
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String COMPLAINTS_FILE = DATA_DIR + "/complaints.txt";
    private static final String DELIMITER = "\\|";
    private static final String DELIMITER_CHAR = "|";

    static {
        // Ensure data directory exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ================= USER PERSISTENCE =================

    /**
     * Loads all users from the users file.
     * If the file is missing or empty, returns an empty list.
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return users;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER);
                if (parts.length == 5) {
                    try {
                        Long id = Long.parseLong(parts[0]);
                        String name = parts[1];
                        String email = parts[2];
                        String password = parts[3];
                        Role role = Role.valueOf(parts[4]);
                        users.add(new User(id, name, email, password, role));
                    } catch (Exception e) {
                        System.err.println("Error parsing user record line: " + line + ". Details: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
        return users;
    }

    /**
     * Saves the entire list of users to the users file.
     */
    public static void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                String line = String.join(DELIMITER_CHAR,
                        String.valueOf(user.getId()),
                        escapeField(user.getName()),
                        escapeField(user.getEmail()),
                        escapeField(user.getPassword()),
                        user.getRole().name()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing users file: " + e.getMessage());
        }
    }

    // ================= COMPLAINT PERSISTENCE =================

    /**
     * Loads all complaints from the complaints file.
     */
    public static List<Complaint> loadComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        File file = new File(COMPLAINTS_FILE);
        if (!file.exists()) {
            return complaints;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1); // Allow empty trailing fields (e.g. resolution)
                if (parts.length >= 11) {
                    try {
                        Long id = Long.parseLong(parts[0]);
                        Long userId = Long.parseLong(parts[1]);
                        String title = unescapeField(parts[2]);
                        String description = unescapeField(parts[3]);
                        ComplaintCategory category = ComplaintCategory.valueOf(parts[4]);
                        ComplaintPriority priority = ComplaintPriority.valueOf(parts[5]);
                        ComplaintStatus status = ComplaintStatus.valueOf(parts[6]);
                        LocalDateTime createdDate = LocalDateTime.parse(parts[7]);
                        LocalDateTime updatedDate = LocalDateTime.parse(parts[8]);
                        String assignedPerson = unescapeField(parts[9]);
                        String resolution = unescapeField(parts[10]);

                        complaints.add(new Complaint(id, userId, title, description, category, priority, status,
                                createdDate, updatedDate, assignedPerson, resolution));
                    } catch (Exception e) {
                        System.err.println("Error parsing complaint record line: " + line + ". Details: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading complaints file: " + e.getMessage());
        }
        return complaints;
    }

    /**
     * Saves the entire list of complaints to the complaints file.
     */
    public static void saveComplaints(List<Complaint> complaints) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPLAINTS_FILE))) {
            for (Complaint c : complaints) {
                String line = String.join(DELIMITER_CHAR,
                        String.valueOf(c.getId()),
                        String.valueOf(c.getUserId()),
                        escapeField(c.getTitle()),
                        escapeField(c.getDescription()),
                        c.getCategory().name(),
                        c.getPriority().name(),
                        c.getStatus().name(),
                        c.getCreatedDate().toString(),
                        c.getUpdatedDate().toString(),
                        escapeField(c.getAssignedPerson()),
                        escapeField(c.getResolution())
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing complaints file: " + e.getMessage());
        }
    }

    // ================= HELPERS FOR STRING ESCAPING =================

    /**
     * Replace newlines with a spacer to prevent multiple physical lines per record.
     */
    private static String escapeField(String field) {
        if (field == null) return "";
        return field.replace("\r", "").replace("\n", "\\n").replace(DELIMITER_CHAR, "\\pipe");
    }

    private static String unescapeField(String field) {
        if (field == null) return "";
        return field.replace("\\n", "\n").replace("\\pipe", DELIMITER_CHAR);
    }
}
