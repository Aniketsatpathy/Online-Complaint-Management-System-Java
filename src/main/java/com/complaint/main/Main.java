package com.complaint.main;

import com.complaint.model.*;
import com.complaint.service.ComplaintService;
import com.complaint.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the Online Complaint Management System.
 * Implements a command-line interface with distinct User and Admin dashboards.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ComplaintService complaintService = new ComplaintService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  WELCOME TO ONLINE COMPLAINT MANAGEMENT SYSTEM   ");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showWelcomeMenu();
            } else {
                if (currentUser.getRole() == Role.ADMIN) {
                    showAdminDashboard();
                } else {
                    showUserDashboard();
                }
            }
        }
        System.out.println("\n[*] Thank you for using the Online Complaint Management System. Goodbye!");
    }

    /**
     * Welcome screen menu shown to unauthenticated visitors.
     */
    private static boolean showWelcomeMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("1. Log In");
        System.out.println("2. Register New User Account");
        System.out.println("3. Exit Application");
        System.out.print("[?] Select an option (1-3): ");

        int choice = readIntegerInput();
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                return false;
            default:
                System.out.println("[!] Invalid choice. Please pick between 1 and 3.");
        }
        return true;
    }

    /**
     * Handles authentication input and validation.
     */
    private static void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userService.loginUser(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("\n[SUCCESS] Welcome back, " + currentUser.getName() + "! (" + currentUser.getRole().getDisplayName() + ")");
        } else {
            System.out.println("\n[ERROR] Authentication failed. Invalid email or password.");
        }
    }

    /**
     * Handles account creation inputs.
     */
    private static void handleRegistration() {
        System.out.println("\n--- CREATE NEW ACCOUNT ---");
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        System.out.println("Choose Role Type:");
        System.out.println("1. Standard User");
        System.out.println("2. Administrator");
        System.out.print("[?] Select role (1-2): ");
        int roleChoice = readIntegerInput();
        Role role = (roleChoice == 2) ? Role.ADMIN : Role.USER;

        try {
            User registered = userService.registerUser(name, email, password, role);
            System.out.println("\n[SUCCESS] Account successfully created for " + registered.getName() + " (ID: " + registered.getId() + ")");
            System.out.println("[*] You can now log in using your credentials.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] Registration failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // ============================ USER DASHBOARD =============================
    // =========================================================================

    private static void showUserDashboard() {
        System.out.println("\n==================================================");
        System.out.println("      USER DASHBOARD - " + currentUser.getName().toUpperCase());
        System.out.println("==================================================");
        System.out.println("1. Submit New Complaint");
        System.out.println("2. View My Complaints & Statuses");
        System.out.println("3. Track Specific Complaint by ID");
        System.out.println("4. Add Comment / Provide Feedback");
        System.out.println("5. Log Out");
        System.out.print("[?] Select an option (1-5): ");

        int choice = readIntegerInput();
        switch (choice) {
            case 1:
                handleUserSubmitComplaint();
                break;
            case 2:
                handleUserViewAllComplaints();
                break;
            case 3:
                handleTrackComplaintById();
                break;
            case 4:
                handleUserAddFeedback();
                break;
            case 5:
                currentUser = null;
                System.out.println("\n[*] Logged out successfully.");
                break;
            default:
                System.out.println("[!] Invalid choice. Please pick between 1 and 5.");
        }
    }

    private static void handleUserSubmitComplaint() {
        System.out.println("\n--- SUBMIT NEW COMPLAINT ---");
        System.out.print("Enter Short Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Detailed Description: ");
        String description = scanner.nextLine().trim();

        System.out.println("Select Complaint Category:");
        ComplaintCategory[] categories = ComplaintCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i].getDisplayName());
        }
        System.out.print("[?] Pick category (1-" + categories.length + "): ");
        int categoryChoice = readIntegerInput();
        
        ComplaintCategory category;
        if (categoryChoice >= 1 && categoryChoice <= categories.length) {
            category = categories[categoryChoice - 1];
        } else {
            System.out.println("[!] Invalid selection. Setting category to 'OTHER'.");
            category = ComplaintCategory.OTHER;
        }

        try {
            Complaint submitted = complaintService.submitComplaint(currentUser.getId(), title, description, category);
            System.out.println("\n[SUCCESS] Complaint submitted successfully!");
            System.out.println("[*] Your Unique Complaint Reference ID is: " + submitted.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] Submission failed: " + e.getMessage());
        }
    }

    private static void handleUserViewAllComplaints() {
        System.out.println("\n--- MY COMPLAINTS & STATUSES ---");
        List<Complaint> myComplaints = complaintService.getComplaintsFiltered(currentUser.getId(), null, null, null);
        
        if (myComplaints.isEmpty()) {
            System.out.println("You have not submitted any complaints yet.");
            return;
        }

        printComplaintHeaders();
        for (Complaint c : myComplaints) {
            printComplaintSummaryRow(c);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void handleTrackComplaintById() {
        System.out.print("\nEnter Complaint Reference ID to track: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        // Restrict standard users to view only their own complaints
        if (currentUser.getRole() == Role.USER && !c.getUserId().equals(currentUser.getId())) {
            System.out.println("\n[ERROR] Access denied. You do not have permission to view this ticket.");
            return;
        }

        printComplaintFullDetails(c);
    }

    private static void handleUserAddFeedback() {
        System.out.print("\nEnter Complaint ID you want to add comment on: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        // Security check
        if (currentUser.getRole() == Role.USER && !c.getUserId().equals(currentUser.getId())) {
            System.out.println("\n[ERROR] Access denied. You cannot comment on someone else's complaint.");
            return;
        }

        if (c.getStatus() == ComplaintStatus.CLOSED) {
            System.out.println("\n[ERROR] This complaint is closed. No further comments are allowed.");
            return;
        }

        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine().trim();
        if (comment.isEmpty()) {
            System.out.println("[ERROR] Comment cannot be empty.");
            return;
        }

        // In a console layout, we store resolutions and follow-up threads in the resolution log fields
        // We append follow-up conversations directly to the resolution string to simulate timeline threads.
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String threadPrefix = "\n[" + timestamp + " - " + currentUser.getName() + "]: ";
        
        String existingResolution = c.getResolution();
        if (existingResolution == null || existingResolution.equalsIgnoreCase("None")) {
            c.setResolution(threadPrefix + comment);
        } else {
            c.setResolution(existingResolution + threadPrefix + comment);
        }

        // Mark updating date
        c.setUpdatedDate(LocalDateTime.now());
        complaintService.updateStatus(c.getId(), c.getStatus()); // Saves status cache and files

        System.out.println("\n[SUCCESS] Comment added to ticket history.");
    }

    // =========================================================================
    // ============================ ADMIN DASHBOARD ============================
    // =========================================================================

    private static void showAdminDashboard() {
        System.out.println("\n==================================================");
        System.out.println("      ADMIN DASHBOARD - " + currentUser.getName().toUpperCase());
        System.out.println("==================================================");
        System.out.println("1. View All Complaints");
        System.out.println("2. Filter Complaints (Status / Category / Priority)");
        System.out.println("3. Assign Complaint to Admin Staff");
        System.out.println("4. Manage Priority Level");
        System.out.println("5. Triage Status (In Progress / Rejected)");
        System.out.println("6. Resolve Complaint & Add Resolution Details");
        System.out.println("7. Close Resolved Complaint");
        System.out.println("8. Log Out");
        System.out.print("[?] Select an option (1-8): ");

        int choice = readIntegerInput();
        switch (choice) {
            case 1:
                handleAdminViewAll();
                break;
            case 2:
                handleAdminFilter();
                break;
            case 3:
                handleAdminAssign();
                break;
            case 4:
                handleAdminPriority();
                break;
            case 5:
                handleAdminTriage();
                break;
            case 6:
                handleAdminResolve();
                break;
            case 7:
                handleAdminClose();
                break;
            case 8:
                currentUser = null;
                System.out.println("\n[*] Logged out successfully.");
                break;
            default:
                System.out.println("[!] Invalid choice. Please pick between 1 and 8.");
        }
    }

    private static void handleAdminViewAll() {
        System.out.println("\n--- GLOBAL COMPLAINTS REGISTRY ---");
        List<Complaint> allComplaints = complaintService.getAllComplaints();
        if (allComplaints.isEmpty()) {
            System.out.println("No complaints registered in the system.");
            return;
        }

        printComplaintHeaders();
        for (Complaint c : allComplaints) {
            printComplaintSummaryRow(c);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void handleAdminFilter() {
        System.out.println("\n--- FILTER COMPLAINTS ---");
        System.out.println("Filter by: 1. Status  2. Category  3. Priority  4. Cancel");
        System.out.print("[?] Option: ");
        int fChoice = readIntegerInput();

        List<Complaint> filteredList = null;
        switch (fChoice) {
            case 1:
                ComplaintStatus[] statuses = ComplaintStatus.values();
                for (int i = 0; i < statuses.length; i++) {
                    System.out.println((i+1) + ". " + statuses[i].getDisplayName());
                }
                System.out.print("Select Status: ");
                int stChoice = readIntegerInput();
                if (stChoice >= 1 && stChoice <= statuses.length) {
                    filteredList = complaintService.getComplaintsFiltered(null, statuses[stChoice - 1], null, null);
                }
                break;
            case 2:
                ComplaintCategory[] categories = ComplaintCategory.values();
                for (int i = 0; i < categories.length; i++) {
                    System.out.println((i+1) + ". " + categories[i].getDisplayName());
                }
                System.out.print("Select Category: ");
                int catChoice = readIntegerInput();
                if (catChoice >= 1 && catChoice <= categories.length) {
                    filteredList = complaintService.getComplaintsFiltered(null, null, categories[catChoice - 1], null);
                }
                break;
            case 3:
                ComplaintPriority[] priorities = ComplaintPriority.values();
                for (int i = 0; i < priorities.length; i++) {
                    System.out.println((i+1) + ". " + priorities[i].getDisplayName());
                }
                System.out.print("Select Priority: ");
                int prioChoice = readIntegerInput();
                if (prioChoice >= 1 && prioChoice <= priorities.length) {
                    filteredList = complaintService.getComplaintsFiltered(null, null, null, priorities[prioChoice - 1]);
                }
                break;
            default:
                System.out.println("[*] Filtering aborted.");
                return;
        }

        if (filteredList == null || filteredList.isEmpty()) {
            System.out.println("\nNo complaints match the selected filter criteria.");
            return;
        }

        System.out.println("\n--- FILTERED RESULTS ---");
        printComplaintHeaders();
        for (Complaint c : filteredList) {
            printComplaintSummaryRow(c);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void handleAdminAssign() {
        System.out.print("\nEnter Complaint Reference ID to assign: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        System.out.println("Available Admin Staff:");
        List<User> admins = userService.getAllAdmins();
        for (int i = 0; i < admins.size(); i++) {
            System.out.println((i + 1) + ". " + admins.get(i).getName() + " (" + admins.get(i).getEmail() + ")");
        }
        System.out.print("[?] Pick assignee (1-" + admins.size() + "): ");
        int assignChoice = readIntegerInput();

        if (assignChoice >= 1 && assignChoice <= admins.size()) {
            String adminName = admins.get(assignChoice - 1).getName();
            try {
                complaintService.assignComplaint(id, adminName);
                System.out.println("\n[SUCCESS] Complaint #" + id + " has been assigned to " + adminName + ".");
            } catch (Exception e) {
                System.out.println("\n[ERROR] Assignment failed: " + e.getMessage());
            }
        } else {
            System.out.println("[!] Invalid staff selection. Assignment cancelled.");
        }
    }

    private static void handleAdminPriority() {
        System.out.print("\nEnter Complaint Reference ID to adjust Priority: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        System.out.println("Select New Priority Level:");
        ComplaintPriority[] priorities = ComplaintPriority.values();
        for (int i = 0; i < priorities.length; i++) {
            System.out.println((i + 1) + ". " + priorities[i].getDisplayName());
        }
        System.out.print("[?] Priority choice: ");
        int prioChoice = readIntegerInput();

        if (prioChoice >= 1 && prioChoice <= priorities.length) {
            try {
                complaintService.updatePriority(id, priorities[prioChoice - 1]);
                System.out.println("\n[SUCCESS] Complaint #" + id + " priority updated to " + priorities[prioChoice - 1].getDisplayName() + ".");
            } catch (Exception e) {
                System.out.println("\n[ERROR] Update failed: " + e.getMessage());
            }
        } else {
            System.out.println("[!] Invalid priority selection.");
        }
    }

    private static void handleAdminTriage() {
        System.out.print("\nEnter Complaint Reference ID to triage status: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        System.out.println("Select Action status:");
        System.out.println("1. Set to In Progress");
        System.out.println("2. Reject Complaint");
        System.out.print("[?] Choice (1-2): ");
        int trChoice = readIntegerInput();

        ComplaintStatus nextStatus;
        if (trChoice == 1) {
            nextStatus = ComplaintStatus.IN_PROGRESS;
        } else if (trChoice == 2) {
            nextStatus = ComplaintStatus.REJECTED;
        } else {
            System.out.println("[!] Triage aborted. Invalid choice.");
            return;
        }

        try {
            complaintService.updateStatus(id, nextStatus);
            System.out.println("\n[SUCCESS] Status of complaint #" + id + " updated to " + nextStatus.getDisplayName() + ".");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Status update failed: " + e.getMessage());
        }
    }

    private static void handleAdminResolve() {
        System.out.print("\nEnter Complaint Reference ID to Resolve: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        System.out.print("Enter Resolution action notes (detailed description of fix): ");
        String note = scanner.nextLine().trim();

        try {
            complaintService.resolveComplaint(id, note);
            System.out.println("\n[SUCCESS] Complaint #" + id + " resolved! User notified via dashboard status.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Verification rejected: " + e.getMessage());
        }
    }

    private static void handleAdminClose() {
        System.out.print("\nEnter Complaint Reference ID to close: ");
        long id = readLongInput();

        Complaint c = complaintService.searchByComplaintId(id);
        if (c == null) {
            System.out.println("\n[ERROR] Complaint Reference ID not found.");
            return;
        }

        if (c.getStatus() != ComplaintStatus.RESOLVED) {
            System.out.println("\n[ERROR] You can only close complaints that are currently in 'Resolved' status.");
            return;
        }

        try {
            complaintService.closeComplaint(id);
            System.out.println("\n[SUCCESS] Complaint #" + id + " is officially closed and archived.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Close transition failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // ======================= ROW PRINTERS & UTILITIES ========================
    // =========================================================================

    private static void printComplaintHeaders() {
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-30s | %-12s | %-10s | %-12s | %-15s |\n",
                "ID", "TITLE", "CATEGORY", "PRIORITY", "STATUS", "ASSIGNEE");
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void printComplaintSummaryRow(Complaint c) {
        String titleTrunc = c.getTitle().length() > 28 ? c.getTitle().substring(0, 25) + "..." : c.getTitle();
        System.out.printf("| %-5d | %-30s | %-12s | %-10s | %-12s | %-15s |\n",
                c.getId(),
                titleTrunc,
                c.getCategory().getDisplayName(),
                c.getPriority().getDisplayName(),
                c.getStatus().getDisplayName(),
                c.getAssignedPerson());
    }

    private static void printComplaintFullDetails(Complaint c) {
        User submitter = userService.getUserById(c.getUserId());
        String submitterName = (submitter != null) ? submitter.getName() : "Unknown User ID: " + c.getUserId();

        System.out.println("\n==================================================");
        System.out.println("         COMPLAINT DETAIL SHEET: #" + c.getId());
        System.out.println("==================================================");
        System.out.println("Reference ID : " + c.getId());
        System.out.println("Submitted By : " + submitterName + " (User ID: " + c.getUserId() + ")");
        System.out.println("Title        : " + c.getTitle());
        System.out.println("Category     : " + c.getCategory().getDisplayName());
        System.out.println("Priority     : " + c.getPriority().getDisplayName());
        System.out.println("Status       : " + c.getStatus().getDisplayName());
        System.out.println("Date Filed   : " + c.getCreatedDate().format(DATE_FORMATTER));
        System.out.println("Last Update  : " + c.getUpdatedDate().format(DATE_FORMATTER));
        System.out.println("Assigned To  : " + c.getAssignedPerson());
        System.out.println("--------------------------------------------------");
        System.out.println("DESCRIPTION  :\n" + c.getDescription());
        System.out.println("--------------------------------------------------");
        System.out.println("RESOLUTION NOTES & FEEDBACK HISTORY :");
        System.out.println(c.getResolution() == null ? "None" : c.getResolution());
        System.out.println("==================================================");
    }

    /**
     * Helper to read integers safely from input buffer, avoiding crashes on string characters.
     */
    private static int readIntegerInput() {
        try {
            String val = scanner.nextLine().trim();
            if (val.isEmpty()) return -1;
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Helper to read long values safely from input buffer.
     */
    private static long readLongInput() {
        try {
            String val = scanner.nextLine().trim();
            if (val.isEmpty()) return -1L;
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}
