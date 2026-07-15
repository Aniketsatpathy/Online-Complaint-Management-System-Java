package com.complaint.service;

import com.complaint.model.*;
import com.complaint.utility.FileManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling complaint submission, search/filtering, history logs, and status transitions.
 */
public class ComplaintService {
    private final List<Complaint> complaintsList;

    public ComplaintService() {
        this.complaintsList = new ArrayList<>(FileManager.loadComplaints());
        
        // Seed default complaints if empty, to demonstrate dashboards immediately
        if (this.complaintsList.isEmpty()) {
            seedDefaultComplaints();
        }
    }

    private void seedDefaultComplaints() {
        LocalDateTime now = LocalDateTime.now().minusDays(2);
        
        // 1. A new unassigned Technical complaint (Critical)
        complaintsList.add(new Complaint(1001L, 1L, "Smart Card Reader Failure at Block B Main Entry",
                "The RFID smart card reader at the hostel Block B main entrance gate is completely unresponsive. Students are locked out and security guards have to open the gate manually. Needs urgent technician attention.",
                ComplaintCategory.INFRASTRUCTURE, ComplaintPriority.CRITICAL, ComplaintStatus.OPEN,
                now, now, "Unassigned", "None"));

        // 2. An assigned Infrastructure complaint in progress (Medium)
        complaintsList.add(new Complaint(1002L, 2L, "Water Leakage in Chemistry Lab Toilet",
                "Water is continuously leaking from the flush valve of toilet cabin 2 on the ground floor of the Science Block. The floor is flooded, creating a slip hazard.",
                ComplaintCategory.INFRASTRUCTURE, ComplaintPriority.MEDIUM, ComplaintStatus.IN_PROGRESS,
                now.plusHours(4), now.plusHours(12), "Tech Agent", "None"));

        // 3. A resolved Billing query (Medium)
        complaintsList.add(new Complaint(1003L, 1L, "Double Mess Fee Debit - Transaction Ref #9812A",
                "My mess fee transaction failed on the student portal during the first attempt, but the money ($120.00) was debited from my bank account. On the second attempt, the payment succeeded, meaning I was double-charged.",
                ComplaintCategory.BILLING, ComplaintPriority.MEDIUM, ComplaintStatus.RESOLVED,
                now.plusDays(1), now.plusDays(1).plusHours(6), "System Admin",
                "Verified payment gateway transaction reconciliations. The first failed debit was recognized and has been auto-reversed back to your bank account. Refund reference number: MessPay-78231-REV. Please verify your bank statement in 2-3 business days."));

        // 4. An assigned Technical complaint (High)
        complaintsList.add(new Complaint(1004L, 2L, "Projector Flickering in Computer Lab 3",
                "The ceiling-mounted projector in Computer Lab 3 is flickering red and green lines on the projection screen. The HDMI cable and port appear to be loose. Instructors are unable to hold class properly.",
                ComplaintCategory.TECHNICAL, ComplaintPriority.HIGH, ComplaintStatus.ASSIGNED,
                now.plusDays(1).plusHours(2), now.plusDays(1).plusHours(4), "Tech Agent", "None"));

        // 5. A closed Service request (Low)
        complaintsList.add(new Complaint(1005L, 1L, "Request for Official Degree Transcript Issuance",
                "Requesting the official academic transcript for my completed semesters for higher education applications. I have uploaded the payment receipt for the transcript fee.",
                ComplaintCategory.SERVICE, ComplaintPriority.LOW, ComplaintStatus.CLOSED,
                now.plusDays(1).plusHours(5), now.plusDays(1).plusHours(10), "System Admin",
                "Transcript has been officially generated, signed by the Registrar, and delivered to your registered student email address in digital PDF format. Physical copy dispatched via post. Tracking ID: Post-TR-8723."));

        // 6. A rejected/invalid ticket (Low)
        complaintsList.add(new Complaint(1006L, 2L, "Random Key Testing - ignore this",
                "Testing the system layout from keyboard inputs, ignore please, testing one two three.",
                ComplaintCategory.OTHER, ComplaintPriority.LOW, ComplaintStatus.REJECTED,
                now.plusDays(1).plusHours(8), now.plusDays(1).plusHours(9), "Tech Agent",
                "Rejected: Ticket submitted as test gibberish. Please submit a valid grievance description if you have an issue."));

        FileManager.saveComplaints(complaintsList);
        System.out.println("[Database Seeder] Seeding default complaints complete (6 Tickets loaded).");
    }

    /**
     * Submits a new complaint.
     * Validates field constraints before adding.
     */
    public synchronized Complaint submitComplaint(Long userId, String title, String description,
                                                   ComplaintCategory category) {
        if (title == null || title.trim().length() < 5) {
            throw new IllegalArgumentException("Title must be at least 5 characters long.");
        }
        if (description == null || description.trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters long.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Valid Category must be specified.");
        }

        // Generate next ID starting at 1001
        long nextId = complaintsList.stream()
                .mapToLong(Complaint::getId)
                .max()
                .orElse(1000L) + 1;

        LocalDateTime now = LocalDateTime.now();
        Complaint newComplaint = new Complaint(
                nextId,
                userId,
                title.trim(),
                description.trim(),
                category,
                ComplaintPriority.MEDIUM, // Default Priority
                ComplaintStatus.OPEN,     // Default Status
                now,
                now,
                "Unassigned",
                "None"
        );

        complaintsList.add(newComplaint);
        FileManager.saveComplaints(complaintsList);
        return newComplaint;
    }

    /**
     * Search complaint by its unique ID.
     * Returns null if not found.
     */
    public Complaint searchByComplaintId(Long id) {
        for (Complaint c : complaintsList) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Filters complaints by various optional criteria.
     * If any filter is null, that filter is ignored.
     */
    public List<Complaint> getComplaintsFiltered(Long userId, ComplaintStatus status,
                                                 ComplaintCategory category, ComplaintPriority priority) {
        return complaintsList.stream()
                .filter(c -> userId == null || c.getUserId().equals(userId))
                .filter(c -> status == null || c.getStatus() == status)
                .filter(c -> category == null || c.getCategory() == category)
                .filter(c -> priority == null || c.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Returns all complaints (useful for global Admin panel).
     */
    public List<Complaint> getAllComplaints() {
        return new ArrayList<>(complaintsList);
    }

    /**
     * Assigns a ticket to an admin worker.
     */
    public void assignComplaint(Long complaintId, String adminName) {
        Complaint c = searchByComplaintId(complaintId);
        if (c == null) {
            throw new IllegalArgumentException("Complaint with ID " + complaintId + " does not exist.");
        }
        if (adminName == null || adminName.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignee name cannot be empty.");
        }

        c.setAssignedPerson(adminName.trim());
        c.setStatus(ComplaintStatus.ASSIGNED);
        c.setUpdatedDate(LocalDateTime.now());
        FileManager.saveComplaints(complaintsList);
    }

    /**
     * Changes priority of a complaint (Admin only).
     */
    public void updatePriority(Long complaintId, ComplaintPriority priority) {
        Complaint c = searchByComplaintId(complaintId);
        if (c == null) {
            throw new IllegalArgumentException("Complaint with ID " + complaintId + " does not exist.");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority level must be selected.");
        }

        c.setPriority(priority);
        c.setUpdatedDate(LocalDateTime.now());
        FileManager.saveComplaints(complaintsList);
    }

    /**
     * Updates complaint status (Admin only).
     * Enforces transition constraints to keep state pipeline consistent.
     */
    public void updateStatus(Long complaintId, ComplaintStatus newStatus) {
        Complaint c = searchByComplaintId(complaintId);
        if (c == null) {
            throw new IllegalArgumentException("Complaint with ID " + complaintId + " does not exist.");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        validateStatusTransition(c.getStatus(), newStatus);

        c.setStatus(newStatus);
        c.setUpdatedDate(LocalDateTime.now());
        FileManager.saveComplaints(complaintsList);
    }

    /**
     * Adds a resolution note and marks the ticket as RESOLVED.
     */
    public void resolveComplaint(Long complaintId, String resolutionNote) {
        Complaint c = searchByComplaintId(complaintId);
        if (c == null) {
            throw new IllegalArgumentException("Complaint with ID " + complaintId + " does not exist.");
        }
        if (resolutionNote == null || resolutionNote.trim().length() < 5) {
            throw new IllegalArgumentException("Resolution note must be at least 5 characters long.");
        }

        validateStatusTransition(c.getStatus(), ComplaintStatus.RESOLVED);

        c.setResolution(resolutionNote.trim());
        c.setStatus(ComplaintStatus.RESOLVED);
        c.setUpdatedDate(LocalDateTime.now());
        FileManager.saveComplaints(complaintsList);
    }

    /**
     * Officially closes the complaint.
     */
    public void closeComplaint(Long complaintId) {
        Complaint c = searchByComplaintId(complaintId);
        if (c == null) {
            throw new IllegalArgumentException("Complaint with ID " + complaintId + " does not exist.");
        }

        validateStatusTransition(c.getStatus(), ComplaintStatus.CLOSED);

        c.setStatus(ComplaintStatus.CLOSED);
        c.setUpdatedDate(LocalDateTime.now());
        FileManager.saveComplaints(complaintsList);
    }

    /**
     * Validates status transition rules.
     */
    private void validateStatusTransition(ComplaintStatus current, ComplaintStatus next) {
        if (current == ComplaintStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a closed complaint.");
        }
        if (current == ComplaintStatus.REJECTED && next != ComplaintStatus.OPEN) {
            throw new IllegalStateException("A rejected complaint can only be reopened.");
        }
        
        // Optional: Block transitioning to ASSIGNED if current state is RESOLVED
        if (current == ComplaintStatus.RESOLVED && next != ComplaintStatus.CLOSED && next != ComplaintStatus.IN_PROGRESS) {
            throw new IllegalStateException("A resolved complaint can only be Closed or set back to In Progress.");
        }
    }
}
