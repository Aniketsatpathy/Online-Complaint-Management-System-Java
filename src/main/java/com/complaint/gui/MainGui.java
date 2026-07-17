package com.complaint.gui;

import com.complaint.model.*;
import com.complaint.service.ComplaintService;
import com.complaint.service.UserService;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modern, Sleek & Professional Swing GUI for the Online Complaint Management System.
 * Powered by FlatLaf dark look-and-feel.
 */
public class MainGui extends JFrame {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final UserService userService = new UserService();
    private final ComplaintService complaintService = new ComplaintService();

    private User currentUser = null;
    private Complaint selectedComplaint = null;

    // CardLayout for view switching
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);

    // Color Palette
    private static final Color COLOR_PRIMARY = new Color(99, 102, 241);   // Indigo-500
    private static final Color COLOR_ACCENT = new Color(139, 92, 246);    // Violet-500
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);   // Emerald-500
    private static final Color COLOR_WARNING = new Color(245, 158, 11);   // Amber-500
    private static final Color COLOR_DANGER = new Color(239, 68, 68);     // Rose-500
    private static final Color COLOR_BG_PANEL = new Color(30, 41, 59);    // Slate-800

    public MainGui() {
        setTitle("Online Complaint Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null); // Center window on screen

        // Initialize UI containers
        mainContainer.add(createLoginPanel(), "LOGIN");
        mainContainer.add(createUserPortalPanel(), "USER_PORTAL");
        mainContainer.add(createAdminPortalPanel(), "ADMIN_PORTAL");

        add(mainContainer);
        cardLayout.show(mainContainer, "LOGIN");
    }

    public static void main(String[] args) {
        // Setup modern FlatLaf dark theme
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme. Reverting to default Swing.");
        }

        SwingUtilities.invokeLater(() -> new MainGui().setVisible(true));
    }

    // =========================================================================
    // ============================ LOGIN SCREEN ===============================
    // =========================================================================

    private JTextField txtLoginEmail;
    private JPasswordField txtLoginPassword;

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 23, 42)); // Slate-900

        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(COLOR_BG_PANEL);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // Header Title
        JLabel lblTitle = new JLabel("Online Help Desk");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Submit and Track Grievances Instantly");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(new Color(148, 163, 184)); // Slate-400
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEmail.setForeground(new Color(203, 213, 225)); // Slate-300
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLoginEmail = new JTextField(20);
        txtLoginEmail.setMaximumSize(new Dimension(320, 35));
        txtLoginEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPassword.setForeground(new Color(203, 213, 225));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLoginPassword = new JPasswordField(20);
        txtLoginPassword.setMaximumSize(new Dimension(320, 35));
        txtLoginPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Actions
        JButton btnLogin = new JButton("Log In");
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(320, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        JButton btnRegister = new JButton("Don't have an account? Sign Up");
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setForeground(COLOR_ACCENT);
        btnRegister.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> performRegistrationDialog());

        // Demo login triggers
        JLabel lblDemo = new JLabel("Demo Accounts Quick Login:");
        lblDemo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblDemo.setForeground(new Color(148, 163, 184));
        lblDemo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel demoButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        demoButtons.setBackground(COLOR_BG_PANEL);
        demoButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnDemoUser = new JButton("Demo User");
        btnDemoUser.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnDemoUser.addActionListener(e -> {
            txtLoginEmail.setText("alice@example.com");
            txtLoginPassword.setText("password123");
            performLogin();
        });

        JButton btnDemoAdmin = new JButton("Demo Admin");
        btnDemoAdmin.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnDemoAdmin.addActionListener(e -> {
            txtLoginEmail.setText("admin@complaint.com");
            txtLoginPassword.setText("adminpassword");
            performLogin();
        });

        demoButtons.add(btnDemoUser);
        demoButtons.add(btnDemoAdmin);

        // Assemble login card
        loginCard.add(lblTitle);
        loginCard.add(Box.createRigidArea(new Dimension(0, 5)));
        loginCard.add(lblSub);
        loginCard.add(Box.createRigidArea(new Dimension(0, 25)));

        // Encapsulate form fields in a center-aligned panel to fix layout alignment shifts
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_BG_PANEL);
        formPanel.setMaximumSize(new Dimension(320, 130));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(lblEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtLoginEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(lblPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtLoginPassword);

        loginCard.add(formPanel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));
        loginCard.add(btnLogin);
        loginCard.add(Box.createRigidArea(new Dimension(0, 5)));
        loginCard.add(btnRegister);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));
        loginCard.add(lblDemo);
        loginCard.add(Box.createRigidArea(new Dimension(0, 5)));
        loginCard.add(demoButtons);

        // Center card in GBL
        panel.add(loginCard, new GridBagConstraints());
        return panel;
    }

    private void performLogin() {
        String email = txtLoginEmail.getText().trim();
        String password = new String(txtLoginPassword.getPassword());

        User user = userService.loginUser(email, password);
        if (user != null) {
            currentUser = user;
            txtLoginEmail.setText("");
            txtLoginPassword.setText("");
            
            if (user.getRole() == Role.ADMIN) {
                lblAdminWelcome.setText("Welcome back, " + user.getName() + " (Administrator)");
                refreshAdminTable();
                cardLayout.show(mainContainer, "ADMIN_PORTAL");
            } else {
                lblUserWelcome.setText("Welcome, " + user.getName() + " | User Portal");
                refreshUserTable();
                cardLayout.show(mainContainer, "USER_PORTAL");
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid email or password. Please try again.",
                    "Login Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRegistrationDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<Role> roleCombo = new JComboBox<>(Role.values());

        Object[] message = {
                "Full Name:", nameField,
                "Email Address:", emailField,
                "Password:", passField,
                "Account Type:", roleCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register New Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            Role role = (Role) roleCombo.getSelectedItem();

            try {
                User registered = userService.registerUser(name, email, password, role);
                JOptionPane.showMessageDialog(this,
                        "Registration Successful! Account created for " + registered.getName() + ".\nClick Demo Login or sign in with your email.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Registration Failed: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // ============================ USER PORTAL ================================
    // =========================================================================

    private JLabel lblUserWelcome;
    private JTable tblUserComplaints;
    private DefaultTableModel modelUserComplaints;

    // User Details Sheet components
    private JLabel lblUserId, lblUserCategory, lblUserPriority, lblUserStatus, lblUserAssignee, lblUserDateFiled, lblUserLastUpdate;
    private JTextArea txtUserDescription, txtUserTimeline;
    private JTextField txtUserCommentInput;
    private JButton btnUserAddComment;

    private JPanel createUserPortalPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        lblUserWelcome = new JLabel("Welcome User");
        lblUserWelcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblUserWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Log Out");
        btnLogout.setBackground(COLOR_DANGER);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> handleLogOut());

        headerPanel.add(lblUserWelcome, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        // Core Center split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setResizeWeight(0.5);

        // Left Container: List & Submission Button
        JPanel leftContainer = new JPanel(new BorderLayout(10, 10));
        leftContainer.setBorder(new EmptyBorder(15, 15, 15, 10));

        JLabel lblListTitle = new JLabel("My Submitted Complaints");
        lblListTitle.setFont(new Font("SansSerif", Font.BOLD, 15));

        modelUserComplaints = new DefaultTableModel(new Object[]{"ID", "Title", "Category", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblUserComplaints = new JTable(modelUserComplaints);
        tblUserComplaints.getSelectionModel().addListSelectionListener(e -> {
            int row = tblUserComplaints.getSelectedRow();
            if (row >= 0) {
                Long id = (Long) tblUserComplaints.getValueAt(row, 0);
                loadUserComplaintDetails(id);
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblUserComplaints);

        JButton btnNewComplaint = new JButton("+ File New Complaint");
        btnNewComplaint.setBackground(COLOR_PRIMARY);
        btnNewComplaint.setForeground(Color.WHITE);
        btnNewComplaint.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnNewComplaint.addActionListener(e -> showFileComplaintDialog());

        leftContainer.add(lblListTitle, BorderLayout.NORTH);
        leftContainer.add(scrollTable, BorderLayout.CENTER);
        leftContainer.add(btnNewComplaint, BorderLayout.SOUTH);

        // Right Container: Dynamic Ticket details viewer
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBorder(new EmptyBorder(15, 10, 15, 15));

        JPanel detailsSheet = new JPanel(new BorderLayout(5, 5));
        detailsSheet.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                "Complaint Details", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), Color.WHITE
        ));

        // Metadata grid
        JPanel metaGrid = new JPanel(new GridLayout(4, 2, 8, 8));
        metaGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        metaGrid.setBackground(COLOR_BG_PANEL);

        lblUserId = new JLabel("ID: --");
        lblUserCategory = new JLabel("Category: --");
        lblUserPriority = new JLabel("Priority: --");
        lblUserStatus = new JLabel("Status: --");
        lblUserAssignee = new JLabel("Assignee: --");
        lblUserDateFiled = new JLabel("Filed Date: --");
        lblUserLastUpdate = new JLabel("Last Update: --");

        metaGrid.add(lblUserId);
        metaGrid.add(lblUserCategory);
        metaGrid.add(lblUserPriority);
        metaGrid.add(lblUserStatus);
        metaGrid.add(lblUserAssignee);
        metaGrid.add(lblUserDateFiled);
        metaGrid.add(lblUserLastUpdate);

        // Center split: Description & Comments log
        JPanel midPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        midPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        txtUserDescription = new JTextArea();
        txtUserDescription.setEditable(false);
        txtUserDescription.setLineWrap(true);
        txtUserDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtUserDescription);
        scrollDesc.setBorder(BorderFactory.createTitledBorder("Complaint Description"));

        txtUserTimeline = new JTextArea();
        txtUserTimeline.setEditable(false);
        txtUserTimeline.setLineWrap(true);
        txtUserTimeline.setWrapStyleWord(true);
        JScrollPane scrollTimeline = new JScrollPane(txtUserTimeline);
        scrollTimeline.setBorder(BorderFactory.createTitledBorder("Resolution Notes & Discussion History"));

        midPanel.add(scrollDesc);
        midPanel.add(scrollTimeline);

        // Chat Input footer
        JPanel chatFooter = new JPanel(new BorderLayout(5, 5));
        chatFooter.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        txtUserCommentInput = new JTextField();
        txtUserCommentInput.setEnabled(false);
        btnUserAddComment = new JButton("Send Comment");
        btnUserAddComment.setEnabled(false);
        btnUserAddComment.addActionListener(e -> handleUserPostComment());

        chatFooter.add(txtUserCommentInput, BorderLayout.CENTER);
        chatFooter.add(btnUserAddComment, BorderLayout.EAST);

        detailsSheet.add(metaGrid, BorderLayout.NORTH);
        detailsSheet.add(midPanel, BorderLayout.CENTER);
        detailsSheet.add(chatFooter, BorderLayout.SOUTH);

        rightContainer.add(detailsSheet, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftContainer);
        splitPane.setRightComponent(rightContainer);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private void refreshUserTable() {
        modelUserComplaints.setRowCount(0);
        if (currentUser == null) return;

        List<Complaint> myComplaints = complaintService.getComplaintsFiltered(currentUser.getId(), null, null, null);
        for (Complaint c : myComplaints) {
            modelUserComplaints.addRow(new Object[]{
                    c.getId(),
                    c.getTitle(),
                    c.getCategory().getDisplayName(),
                    c.getStatus().getDisplayName()
            });
        }
    }

    private void loadUserComplaintDetails(Long id) {
        selectedComplaint = complaintService.searchByComplaintId(id);
        if (selectedComplaint == null) return;

        lblUserId.setText("Ticket ID: #" + selectedComplaint.getId());
        lblUserCategory.setText("Category: " + selectedComplaint.getCategory().getDisplayName());
        lblUserPriority.setText("Priority: " + selectedComplaint.getPriority().getDisplayName());
        lblUserStatus.setText("Status: " + selectedComplaint.getStatus().getDisplayName());
        lblUserAssignee.setText("Assigned to: " + selectedComplaint.getAssignedPerson());
        lblUserDateFiled.setText("Filed: " + selectedComplaint.getCreatedDate().format(DATE_FORMATTER));
        lblUserLastUpdate.setText("Updated: " + selectedComplaint.getUpdatedDate().format(DATE_FORMATTER));

        txtUserDescription.setText(selectedComplaint.getDescription());
        txtUserTimeline.setText(selectedComplaint.getResolution() == null || selectedComplaint.getResolution().equals("None")
                ? "No updates or comments yet." : selectedComplaint.getResolution());

        // Comment input locking for closed tickets
        boolean active = selectedComplaint.getStatus() != ComplaintStatus.CLOSED;
        txtUserCommentInput.setEnabled(active);
        btnUserAddComment.setEnabled(active);
    }

    private void handleUserPostComment() {
        if (selectedComplaint == null) return;

        String text = txtUserCommentInput.getText().trim();
        if (text.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String threadPrefix = "\n[" + timestamp + " - " + currentUser.getName() + "]: ";

        String existingResolution = selectedComplaint.getResolution();
        if (existingResolution == null || existingResolution.equalsIgnoreCase("None")) {
            selectedComplaint.setResolution(threadPrefix + text);
        } else {
            selectedComplaint.setResolution(existingResolution + threadPrefix + text);
        }

        selectedComplaint.setUpdatedDate(LocalDateTime.now());
        complaintService.updateStatus(selectedComplaint.getId(), selectedComplaint.getStatus()); // Forces save

        txtUserCommentInput.setText("");
        loadUserComplaintDetails(selectedComplaint.getId()); // refresh detail panes
    }

    private void showFileComplaintDialog() {
        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JComboBox<ComplaintCategory> catCombo = new JComboBox<>(ComplaintCategory.values());

        Object[] message = {
                "Short Title (min 5 chars):", titleField,
                "Detailed Description (min 10 chars):", new JScrollPane(descArea),
                "Category Classification:", catCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "File New Complaint Ticket", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descArea.getText().trim();
            ComplaintCategory category = (ComplaintCategory) catCombo.getSelectedItem();

            try {
                Complaint submitted = complaintService.submitComplaint(currentUser.getId(), title, description, category);
                JOptionPane.showMessageDialog(this,
                        "Complaint submitted successfully!\nTicket Reference ID: #" + submitted.getId(),
                        "Submission Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Submission Failed: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // ============================ ADMIN PORTAL ===============================
    // =========================================================================

    private JLabel lblAdminWelcome;
    private JTable tblAdminComplaints;
    private DefaultTableModel modelAdminComplaints;

    // Filters
    private JComboBox<String> cmbFilterStatus, cmbFilterCategory, cmbFilterPriority;

    // Admin Details Pane
    private JLabel lblAdminId, lblAdminCategory, lblAdminPriority, lblAdminStatus, lblAdminAssignee, lblAdminDateFiled;
    private JTextArea txtAdminDescription, txtAdminTimeline;

    // Admin Actions
    private JComboBox<User> cmbAdminStaff;
    private JComboBox<ComplaintPriority> cmbSetPriority;
    private JButton btnAssign, btnSetPriority, btnTriageProgress, btnTriageReject, btnResolve, btnClose;
    private JTextArea txtAdminResolutionNote;
    private JTextField txtAdminCommentInput;
    private JButton btnAdminAddComment;

    private JPanel createAdminPortalPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG_PANEL);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        lblAdminWelcome = new JLabel("Welcome Admin");
        lblAdminWelcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAdminWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Log Out");
        btnLogout.setBackground(COLOR_DANGER);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> handleLogOut());

        headerPanel.add(lblAdminWelcome, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        // Core Center split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setResizeWeight(0.5);

        // LEFT SIDE: Filters & Table Grid
        JPanel leftContainer = new JPanel(new BorderLayout(10, 10));
        leftContainer.setBorder(new EmptyBorder(15, 15, 15, 10));

        // Filters Panel
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter registry"));

        cmbFilterStatus = new JComboBox<>();
        cmbFilterStatus.addItem("All Statuses");
        for (ComplaintStatus s : ComplaintStatus.values()) cmbFilterStatus.addItem(s.name());
        cmbFilterStatus.addActionListener(e -> applyAdminFilters());

        cmbFilterCategory = new JComboBox<>();
        cmbFilterCategory.addItem("All Categories");
        for (ComplaintCategory c : ComplaintCategory.values()) cmbFilterCategory.addItem(c.name());
        cmbFilterCategory.addActionListener(e -> applyAdminFilters());

        cmbFilterPriority = new JComboBox<>();
        cmbFilterPriority.addItem("All Priorities");
        for (ComplaintPriority p : ComplaintPriority.values()) cmbFilterPriority.addItem(p.name());
        cmbFilterPriority.addActionListener(e -> applyAdminFilters());

        JButton btnClearFilters = new JButton("Clear");
        btnClearFilters.addActionListener(e -> {
            cmbFilterStatus.setSelectedIndex(0);
            cmbFilterCategory.setSelectedIndex(0);
            cmbFilterPriority.setSelectedIndex(0);
            refreshAdminTable();
        });

        filtersPanel.add(cmbFilterStatus);
        filtersPanel.add(cmbFilterCategory);
        filtersPanel.add(cmbFilterPriority);
        filtersPanel.add(btnClearFilters);

        modelAdminComplaints = new DefaultTableModel(
                new Object[]{"ID", "Title", "Category", "Priority", "Status", "Assignee"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblAdminComplaints = new JTable(modelAdminComplaints);
        
        // Color row renderer based on priority/status
        tblAdminComplaints.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                if (!isSel) {
                    String status = table.getValueAt(r, 4).toString();
                    if (status.equalsIgnoreCase("Closed")) {
                        comp.setForeground(new Color(100, 116, 139)); // grayed out
                    } else if (status.equalsIgnoreCase("Resolved")) {
                        comp.setForeground(COLOR_SUCCESS);
                    } else if (status.equalsIgnoreCase("Rejected")) {
                        comp.setForeground(COLOR_DANGER);
                    } else {
                        comp.setForeground(Color.WHITE);
                    }
                }
                return comp;
            }
        });

        tblAdminComplaints.getSelectionModel().addListSelectionListener(e -> {
            int row = tblAdminComplaints.getSelectedRow();
            if (row >= 0) {
                Long id = (Long) tblAdminComplaints.getValueAt(row, 0);
                loadAdminComplaintDetails(id);
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblAdminComplaints);

        leftContainer.add(filtersPanel, BorderLayout.NORTH);
        leftContainer.add(scrollTable, BorderLayout.CENTER);

        // RIGHT SIDE: Details Sheet, Status controls & comments
        JPanel rightContainer = new JPanel(new BorderLayout(5, 5));
        rightContainer.setBorder(new EmptyBorder(15, 10, 15, 15));

        JPanel detailsSheet = new JPanel(new BorderLayout(5, 5));
        detailsSheet.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                "Triage Desk Panel", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), Color.WHITE
        ));

        // Metadata grid
        JPanel metaGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        metaGrid.setBorder(new EmptyBorder(8, 10, 8, 10));
        metaGrid.setBackground(COLOR_BG_PANEL);

        lblAdminId = new JLabel("ID: --");
        lblAdminCategory = new JLabel("Category: --");
        lblAdminPriority = new JLabel("Priority: --");
        lblAdminStatus = new JLabel("Status: --");
        lblAdminAssignee = new JLabel("Assignee: --");
        lblAdminDateFiled = new JLabel("Filed Date: --");

        metaGrid.add(lblAdminId);
        metaGrid.add(lblAdminCategory);
        metaGrid.add(lblAdminPriority);
        metaGrid.add(lblAdminStatus);
        metaGrid.add(lblAdminAssignee);
        metaGrid.add(lblAdminDateFiled);

        // Split: Description & Actions controls
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        txtAdminDescription = new JTextArea(4, 20);
        txtAdminDescription.setEditable(false);
        txtAdminDescription.setLineWrap(true);
        txtAdminDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtAdminDescription);
        scrollDesc.setBorder(BorderFactory.createTitledBorder("Ticket Description"));

        txtAdminTimeline = new JTextArea();
        txtAdminTimeline.setEditable(false);
        txtAdminTimeline.setLineWrap(true);
        txtAdminTimeline.setWrapStyleWord(true);
        JScrollPane scrollTimeline = new JScrollPane(txtAdminTimeline);
        scrollTimeline.setBorder(BorderFactory.createTitledBorder("Resolution Notes & Audit Logs"));

        JPanel docSplit = new JPanel(new GridLayout(2, 1, 5, 5));
        docSplit.add(scrollDesc);
        docSplit.add(scrollTimeline);

        // Control Panel actions drawer
        JPanel controlActions = new JPanel();
        controlActions.setLayout(new BoxLayout(controlActions, BoxLayout.Y_AXIS));
        controlActions.setBorder(BorderFactory.createTitledBorder("Manage Ticket Operations"));

        // Row 1: Assign
        JPanel assignRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        assignRow.add(new JLabel("Assign to:"));
        cmbAdminStaff = new JComboBox<>();
        cmbAdminStaff.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User u = (User) value;
                    setText(u.getName() + " (" + u.getEmail() + ")");
                }
                return this;
            }
        });
        btnAssign = new JButton("Assign");
        btnAssign.setEnabled(false);
        btnAssign.addActionListener(e -> handleAdminAssignAction());
        assignRow.add(cmbAdminStaff);
        assignRow.add(btnAssign);

        // Row 2: Set Priority
        JPanel priorityRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        priorityRow.add(new JLabel("Priority:"));
        cmbSetPriority = new JComboBox<>(ComplaintPriority.values());
        btnSetPriority = new JButton("Update");
        btnSetPriority.setEnabled(false);
        btnSetPriority.addActionListener(e -> handleAdminPriorityAction());
        priorityRow.add(cmbSetPriority);
        priorityRow.add(btnSetPriority);

        // Row 3: Status Triage buttons
        JPanel triageButtonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        btnTriageProgress = new JButton("Start Progress");
        btnTriageProgress.setBackground(COLOR_ACCENT);
        btnTriageProgress.setForeground(Color.WHITE);
        btnTriageProgress.setEnabled(false);
        btnTriageProgress.addActionListener(e -> handleAdminStatusUpdateAction(ComplaintStatus.IN_PROGRESS));

        btnTriageReject = new JButton("Reject Ticket");
        btnTriageReject.setBackground(COLOR_DANGER);
        btnTriageReject.setForeground(Color.WHITE);
        btnTriageReject.setEnabled(false);
        btnTriageReject.addActionListener(e -> handleAdminStatusUpdateAction(ComplaintStatus.REJECTED));

        triageButtonsRow.add(btnTriageProgress);
        triageButtonsRow.add(btnTriageReject);

        // Row 4: Resolve controls
        JPanel resolveRow = new JPanel(new BorderLayout(5, 2));
        resolveRow.setBorder(BorderFactory.createTitledBorder("Submit Resolution Fix Details"));
        txtAdminResolutionNote = new JTextArea(2, 20);
        txtAdminResolutionNote.setLineWrap(true);
        txtAdminResolutionNote.setWrapStyleWord(true);
        txtAdminResolutionNote.setEnabled(false);
        btnResolve = new JButton("Resolve Ticket");
        btnResolve.setBackground(COLOR_SUCCESS);
        btnResolve.setForeground(Color.WHITE);
        btnResolve.setEnabled(false);
        btnResolve.addActionListener(e -> handleAdminResolveAction());

        resolveRow.add(new JScrollPane(txtAdminResolutionNote), BorderLayout.CENTER);
        resolveRow.add(btnResolve, BorderLayout.EAST);

        // Row 5: Close Ticket
        JPanel closeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        btnClose = new JButton("Close Ticket (Archive)");
        btnClose.setBackground(COLOR_PRIMARY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setEnabled(false);
        btnClose.addActionListener(e -> handleAdminCloseAction());
        closeRow.add(btnClose);

        controlActions.add(assignRow);
        controlActions.add(priorityRow);
        controlActions.add(triageButtonsRow);
        controlActions.add(resolveRow);
        controlActions.add(closeRow);

        centerPanel.add(docSplit, BorderLayout.CENTER);
        centerPanel.add(controlActions, BorderLayout.EAST);

        // Admin Chat Box
        JPanel chatBox = new JPanel(new BorderLayout(5, 5));
        chatBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtAdminCommentInput = new JTextField();
        txtAdminCommentInput.setEnabled(false);
        btnAdminAddComment = new JButton("Send Comment");
        btnAdminAddComment.setEnabled(false);
        btnAdminAddComment.addActionListener(e -> handleAdminPostComment());

        chatBox.add(txtAdminCommentInput, BorderLayout.CENTER);
        chatBox.add(btnAdminAddComment, BorderLayout.EAST);

        detailsSheet.add(metaGrid, BorderLayout.NORTH);
        detailsSheet.add(centerPanel, BorderLayout.CENTER);
        detailsSheet.add(chatBox, BorderLayout.SOUTH);

        rightContainer.add(detailsSheet, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftContainer);
        splitPane.setRightComponent(rightContainer);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Populate Assignee Staff Combobox
        cmbAdminStaff.removeAllItems();
        List<User> admins = userService.getAllAdmins();
        for (User u : admins) {
            cmbAdminStaff.addItem(u);
        }

        return mainPanel;
    }

    private void refreshAdminTable() {
        modelAdminComplaints.setRowCount(0);
        List<Complaint> complaints = complaintService.getAllComplaints();
        for (Complaint c : complaints) {
            modelAdminComplaints.addRow(new Object[]{
                    c.getId(),
                    c.getTitle(),
                    c.getCategory().name(),
                    c.getPriority().name(),
                    c.getStatus().name(),
                    c.getAssignedPerson()
            });
        }
    }

    private void applyAdminFilters() {
        String selStatusStr = (String) cmbFilterStatus.getSelectedItem();
        String selCategoryStr = (String) cmbFilterCategory.getSelectedItem();
        String selPriorityStr = (String) cmbFilterPriority.getSelectedItem();

        ComplaintStatus filterStatus = (selStatusStr == null || selStatusStr.equals("All Statuses"))
                ? null : ComplaintStatus.valueOf(selStatusStr);
        ComplaintCategory filterCategory = (selCategoryStr == null || selCategoryStr.equals("All Categories"))
                ? null : ComplaintCategory.valueOf(selCategoryStr);
        ComplaintPriority filterPriority = (selPriorityStr == null || selPriorityStr.equals("All Priorities"))
                ? null : ComplaintPriority.valueOf(selPriorityStr);

        List<Complaint> filtered = complaintService.getComplaintsFiltered(null, filterStatus, filterCategory, filterPriority);
        
        modelAdminComplaints.setRowCount(0);
        for (Complaint c : filtered) {
            modelAdminComplaints.addRow(new Object[]{
                    c.getId(),
                    c.getTitle(),
                    c.getCategory().name(),
                    c.getPriority().name(),
                    c.getStatus().name(),
                    c.getAssignedPerson()
            });
        }
    }

    private void loadAdminComplaintDetails(Long id) {
        selectedComplaint = complaintService.searchByComplaintId(id);
        if (selectedComplaint == null) return;

        lblAdminId.setText("Ticket ID: #" + selectedComplaint.getId());
        lblAdminCategory.setText("Category: " + selectedComplaint.getCategory().getDisplayName());
        lblAdminPriority.setText("Priority: " + selectedComplaint.getPriority().getDisplayName());
        lblAdminStatus.setText("Status: " + selectedComplaint.getStatus().getDisplayName());
        lblAdminAssignee.setText("Assigned to: " + selectedComplaint.getAssignedPerson());
        lblAdminDateFiled.setText("Filed: " + selectedComplaint.getCreatedDate().format(DATE_FORMATTER));

        txtAdminDescription.setText(selectedComplaint.getDescription());
        txtAdminTimeline.setText(selectedComplaint.getResolution() == null || selectedComplaint.getResolution().equals("None")
                ? "No updates or comments yet." : selectedComplaint.getResolution());

        // Toggle Control Buttons state based on ticket status
        ComplaintStatus status = selectedComplaint.getStatus();
        boolean isClosed = status == ComplaintStatus.CLOSED;

        btnAssign.setEnabled(!isClosed);
        btnSetPriority.setEnabled(!isClosed);
        btnAdminAddComment.setEnabled(!isClosed);
        txtAdminCommentInput.setEnabled(!isClosed);

        btnTriageProgress.setEnabled(status == ComplaintStatus.OPEN || status == ComplaintStatus.ASSIGNED);
        btnTriageReject.setEnabled(status == ComplaintStatus.OPEN || status == ComplaintStatus.ASSIGNED);
        
        boolean canResolve = status == ComplaintStatus.ASSIGNED || status == ComplaintStatus.IN_PROGRESS;
        btnResolve.setEnabled(canResolve);
        txtAdminResolutionNote.setEnabled(canResolve);
        if (canResolve) {
            txtAdminResolutionNote.setText("");
        } else {
            txtAdminResolutionNote.setText("Unavailable in current status state.");
        }

        btnClose.setEnabled(status == ComplaintStatus.RESOLVED);
    }

    private void handleAdminAssignAction() {
        if (selectedComplaint == null) return;
        User assignee = (User) cmbAdminStaff.getSelectedItem();
        if (assignee == null) return;

        try {
            complaintService.assignComplaint(selectedComplaint.getId(), assignee.getName());
            loadAdminComplaintDetails(selectedComplaint.getId());
            applyAdminFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminPriorityAction() {
        if (selectedComplaint == null) return;
        ComplaintPriority priority = (ComplaintPriority) cmbSetPriority.getSelectedItem();
        if (priority == null) return;

        try {
            complaintService.updatePriority(selectedComplaint.getId(), priority);
            loadAdminComplaintDetails(selectedComplaint.getId());
            applyAdminFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminStatusUpdateAction(ComplaintStatus newStatus) {
        if (selectedComplaint == null) return;

        try {
            complaintService.updateStatus(selectedComplaint.getId(), newStatus);
            loadAdminComplaintDetails(selectedComplaint.getId());
            applyAdminFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminResolveAction() {
        if (selectedComplaint == null) return;
        String note = txtAdminResolutionNote.getText().trim();
        if (note.length() < 5) {
            JOptionPane.showMessageDialog(this,
                    "Resolution notes must explain the fix (at least 5 characters).",
                    "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            complaintService.resolveComplaint(selectedComplaint.getId(), note);
            loadAdminComplaintDetails(selectedComplaint.getId());
            applyAdminFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminCloseAction() {
        if (selectedComplaint == null) return;

        try {
            complaintService.closeComplaint(selectedComplaint.getId());
            loadAdminComplaintDetails(selectedComplaint.getId());
            applyAdminFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminPostComment() {
        if (selectedComplaint == null) return;

        String text = txtAdminCommentInput.getText().trim();
        if (text.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String threadPrefix = "\n[" + timestamp + " - " + currentUser.getName() + "]: ";

        String existingResolution = selectedComplaint.getResolution();
        if (existingResolution == null || existingResolution.equalsIgnoreCase("None")) {
            selectedComplaint.setResolution(threadPrefix + text);
        } else {
            selectedComplaint.setResolution(existingResolution + threadPrefix + text);
        }

        selectedComplaint.setUpdatedDate(LocalDateTime.now());
        complaintService.updateStatus(selectedComplaint.getId(), selectedComplaint.getStatus()); // Forces save

        txtAdminCommentInput.setText("");
        loadAdminComplaintDetails(selectedComplaint.getId());
    }

    // =========================================================================
    // ============================ LOG OUT HOOKS ==============================
    // =========================================================================

    private void handleLogOut() {
        currentUser = null;
        selectedComplaint = null;

        // Reset details panel user views
        lblUserId.setText("ID: --");
        lblUserCategory.setText("Category: --");
        lblUserPriority.setText("Priority: --");
        lblUserStatus.setText("Status: --");
        lblUserAssignee.setText("Assignee: --");
        txtUserDescription.setText("");
        txtUserTimeline.setText("");
        txtUserCommentInput.setText("");
        txtUserCommentInput.setEnabled(false);
        btnUserAddComment.setEnabled(false);

        // Reset details panel admin views
        lblAdminId.setText("ID: --");
        lblAdminCategory.setText("Category: --");
        lblAdminPriority.setText("Priority: --");
        lblAdminStatus.setText("Status: --");
        lblAdminAssignee.setText("Assignee: --");
        txtAdminDescription.setText("");
        txtAdminTimeline.setText("");
        txtAdminResolutionNote.setText("");
        txtAdminResolutionNote.setEnabled(false);
        txtAdminCommentInput.setText("");
        txtAdminCommentInput.setEnabled(false);
        btnAdminAddComment.setEnabled(false);
        btnAssign.setEnabled(false);
        btnSetPriority.setEnabled(false);
        btnTriageProgress.setEnabled(false);
        btnTriageReject.setEnabled(false);
        btnResolve.setEnabled(false);
        btnClose.setEnabled(false);

        cardLayout.show(mainContainer, "LOGIN");
    }
}
