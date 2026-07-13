package com.complaint.service;

import com.complaint.model.Role;
import com.complaint.model.User;
import com.complaint.utility.FileManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling User registration, login, and query operations.
 */
public class UserService {
    private final List<User> usersList;

    public UserService() {
        // Load users from text database file on initialization
        this.usersList = new ArrayList<>(FileManager.loadUsers());
        
        // Add default/seed data if system has no users
        if (this.usersList.isEmpty()) {
            seedDefaultUsers();
        }
    }

    /**
     * Seeds default accounts into the database for demonstration/testing.
     */
    private void seedDefaultUsers() {
        usersList.add(new User(1L, "Alice Johnson", "alice@example.com", "password123", Role.USER));
        usersList.add(new User(2L, "Bob Smith", "bob@example.com", "password123", Role.USER));
        usersList.add(new User(3L, "System Admin", "admin@complaint.com", "adminpassword", Role.ADMIN));
        usersList.add(new User(4L, "Tech Agent", "agent@complaint.com", "agentpassword", Role.ADMIN));
        FileManager.saveUsers(usersList);
        System.out.println("[Database Seeder] Seeding default users complete (2 Users, 2 Admins).");
    }

    /**
     * Registers a new user.
     * Throws an IllegalArgumentException if fields are empty or username is taken.
     */
    public synchronized User registerUser(String name, String email, String password, Role role) {
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Name, Email, and Password cannot be empty.");
        }

        // Check if email already registered
        for (User u : usersList) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException("User with email '" + email + "' already exists.");
            }
        }

        // Generate next ID
        long nextId = usersList.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L) + 1;

        User newUser = new User(nextId, name.trim(), email.trim(), password, role);
        usersList.add(newUser);
        FileManager.saveUsers(usersList);
        return newUser;
    }

    /**
     * Authenticates a user by email and password.
     * Returns the User object if successful, null if failed.
     */
    public User loginUser(String email, String password) {
        if (email == null || password == null) return null;

        for (User u : usersList) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && u.getPassword().equals(password)) {
                return u; // Auth success
            }
        }
        return null; // Auth failed
    }

    /**
     * Retrieves a user by their User ID.
     */
    public User getUserById(Long userId) {
        for (User u : usersList) {
            if (u.getId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    /**
     * List all admins (useful for assigning complaints to active administrators).
     */
    public List<User> getAllAdmins() {
        List<User> admins = new ArrayList<>();
        for (User u : usersList) {
            if (u.getRole() == Role.ADMIN) {
                admins.add(u);
            }
        }
        return admins;
    }
}
