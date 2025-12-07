package ui;

import model.AdminActionLog;
import model.Course;
import model.Section;
import model.Instructor;
import repository.Repository;
import service.AdminService;
import service.CatalogService;
import utils.Result;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class AdminUI extends JFrame {

    private final AdminService adminService;
    private final CatalogService catalogService;
    private final Repository<Instructor, String> instructorRepo;
    private final JFrame mainMenu;

    public AdminUI(AdminService adminService,
                   CatalogService catalogService,
                   Repository<Instructor, String> instructorRepo,
                   JFrame mainMenu) {
        this.adminService = adminService;
        this.catalogService = catalogService;
        this.instructorRepo = instructorRepo;
        this.mainMenu = mainMenu;
    }

    public void showUI() {
        setTitle("Admin Dashboard");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header panel with gradient
        JPanel headerPanel = UITheme.createHeaderPanel("Admin Dashboard");
        add(headerPanel, BorderLayout.NORTH);

        // Button panel with modern styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2, 15, 15));
        buttonPanel.setBackground(UITheme.LIGHT_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton createCourseBtn = UITheme.createPrimaryButton("Create Course");
        JButton createSectionBtn = UITheme.createPrimaryButton("Create Section");
        JButton assignInstructorBtn = UITheme.createSecondaryButton("Assign Instructor");
        JButton overrideCapacityBtn = UITheme.createSecondaryButton("Override Capacity");
        JButton overridePrereqBtn = UITheme.createSecondaryButton("Override Prerequisite");
        JButton viewLogsBtn = UITheme.createSuccessButton("View Admin Logs");
        JButton backBtn = UITheme.createDangerButton("Back");

        // Spacer to fill grid
        JPanel spacer = new JPanel();
        spacer.setBackground(UITheme.LIGHT_BG);

        buttonPanel.add(createCourseBtn);
        buttonPanel.add(createSectionBtn);
        buttonPanel.add(assignInstructorBtn);
        buttonPanel.add(overrideCapacityBtn);
        buttonPanel.add(overridePrereqBtn);
        buttonPanel.add(viewLogsBtn);
        buttonPanel.add(backBtn);
        buttonPanel.add(spacer);

        add(buttonPanel, BorderLayout.CENTER);

        // --- Actions ---
        createCourseBtn.addActionListener(e -> createCourse());
        createSectionBtn.addActionListener(e -> createSection());
        assignInstructorBtn.addActionListener(e -> assignInstructor());
        overrideCapacityBtn.addActionListener(e -> overrideCapacity());
        overridePrereqBtn.addActionListener(e -> overridePrerequisite());
        viewLogsBtn.addActionListener(e -> viewLogs());

        // --- Back button action ---
        backBtn.addActionListener(e -> {
            this.dispose(); // close AdminUI
            if (mainMenu != null) mainMenu.setVisible(true); // show main menu
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createCourse() {
        String code = JOptionPane.showInputDialog(this, "Enter course code:");
        if (code == null || code.isBlank()) return;
        String title = JOptionPane.showInputDialog(this, "Enter course title:");
        if (title == null || title.isBlank()) return;

        int credits;
        try {
            credits = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter credits:"));
        } catch (NumberFormatException e) {
            showError("Invalid credits entered");
            return;
        }

        Course course = new Course(code, title, credits);
        Result<Course> result = adminService.createCourse(course);
        showResult(result);
    }

    private void createSection() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter course code:");
        if (courseCode == null || courseCode.isBlank()) return;

        Optional<Course> courseOpt = catalogService.getCourseById(courseCode);

        if (courseOpt.isEmpty()) {
            showError("Course not found: " + courseCode);
            return;
        }

        String instructorId = JOptionPane.showInputDialog(this, "Enter instructor id:");
        if (instructorId == null || instructorId.isBlank()) return;

        Optional<Instructor> instructorOpt = instructorRepo.findById(instructorId);

        if (instructorOpt.isEmpty()) {
            showError("Instructor not found: " + instructorId);
            return;
        }

        Course course = courseOpt.get();

        String term = JOptionPane.showInputDialog(this, "Enter term (e.g., Fall2025):");
        if (term == null || term.isBlank()) return;

        int capacity;
        try {
            capacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter capacity:"));
        } catch (NumberFormatException e) {
            showError("Invalid capacity entered");
            return;
        }

        Section section = new Section("SEC" + System.currentTimeMillis(), course, term, capacity, instructorOpt.get());

        Result<Section> result = adminService.createSection(section);
        showResult(result);
    }


    private void assignInstructor() {
        String sectionId = JOptionPane.showInputDialog(this, "Enter section ID:");
        if (sectionId == null || sectionId.isBlank()) return;

        String instructorId = JOptionPane.showInputDialog(this, "Enter instructor ID:");
        if (instructorId == null || instructorId.isBlank()) return;

        Result<Void> result = adminService.assignInstructor(sectionId, instructorId);
        showResult(result);
    }

    private void overrideCapacity() {
        String sectionId = JOptionPane.showInputDialog(this, "Enter section ID:");
        if (sectionId == null || sectionId.isBlank()) return;

        int newCapacity;
        try {
            newCapacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new capacity:"));
        } catch (NumberFormatException e) {
            showError("Invalid capacity entered");
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Reason for override:");
        if (reason == null || reason.isBlank()) reason = "No reason provided";

        Result<Void> result = adminService.overrideCapacity(sectionId, newCapacity, reason);
        showResult(result);
    }

    private void overridePrerequisite() {
        String studentId = JOptionPane.showInputDialog(this, "Enter student ID:");
        if (studentId == null || studentId.isBlank()) return;

        String courseCode = JOptionPane.showInputDialog(this, "Enter course code:");
        if (courseCode == null || courseCode.isBlank()) return;

        String reason = JOptionPane.showInputDialog(this, "Reason for override:");
        if (reason == null || reason.isBlank()) reason = "No reason provided";

        Result<Void> result = adminService.overridePrerequisite(studentId, courseCode, reason);
        showResult(result);
    }

    private void viewLogs() {
        // Fetch and display logs
        List<AdminActionLog> logs = adminService.getLogs();

        // Create a more visually appealing logs display
        JFrame logsFrame = new JFrame("Admin Logs");
        logsFrame.setSize(800, 500);
        logsFrame.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = UITheme.createHeaderPanel("Admin Action Logs");
        logsFrame.add(headerPanel, BorderLayout.NORTH);

        // Logs text area
        JTextArea textArea = new JTextArea();
        textArea.setFont(UITheme.REGULAR_FONT);
        textArea.setEditable(false);
        textArea.setBackground(UITheme.WHITE);
        textArea.setForeground(UITheme.TEXT_DARK);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        if (logs.isEmpty()) {
            textArea.setText("No admin logs available.");
        } else {
            StringBuilder logText = new StringBuilder();
            for (AdminActionLog log : logs) {
                logText.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                logText.append("ID: ").append(log.getId()).append("\n");
                logText.append("Admin: ").append(log.getAdminId()).append("\n");
                logText.append("Action: ").append(log.getAction()).append("\n");
                logText.append("Type: ").append(log.getActionType()).append("\n");
                logText.append("Reason: ").append(log.getReason() != null ? log.getReason() : "-").append("\n");
            }
            textArea.setText(logText.toString());
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        logsFrame.add(scrollPane, BorderLayout.CENTER);

        logsFrame.setLocationRelativeTo(this);
        logsFrame.setVisible(true);
    }

    private void showResult(Result<?> result) {
        if (result.isOk()) {
            JOptionPane.showMessageDialog(this, "✓ Operation completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError(result.getError());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, "Error: " + message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
