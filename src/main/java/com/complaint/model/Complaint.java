package com.complaint.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * POJO representing a Complaint.
 */
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId; // Submitter's User ID
    private String title;
    private String description;
    private ComplaintCategory category;
    private ComplaintPriority priority;
    private ComplaintStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String assignedPerson; // Name/Username of admin assigned to this complaint
    private String resolution; // Resolution details note added when closing/resolving

    // Constructors
    public Complaint() {}

    public Complaint(Long id, Long userId, String title, String description,
                     ComplaintCategory category, ComplaintPriority priority, ComplaintStatus status,
                     LocalDateTime createdDate, LocalDateTime updatedDate, String assignedPerson, String resolution) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.assignedPerson = assignedPerson;
        this.resolution = resolution;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssignedPerson(String assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", priority=" + priority +
                ", status=" + status +
                ", assignedPerson='" + assignedPerson + '\'' +
                '}';
    }
}
