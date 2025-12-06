package ui;

import model.AdminActionLog;
import model.Course;
import model.Section;
import model.Instructor;
import repository.InstructorRepository;
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
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1, 5, 5));

        JButton createCourseBtn = new JButton("Create Course");
        JButton createSectionBtn = new JButton("Create Section");
        JButton assignInstructorBtn = new JButton("Assign Instructor");
        JButton overrideCapacityBtn = new JButton("Override Capacity");
        JButton overridePrereqBtn = new JButton("Override Prerequisite");
        JButton viewLogsBtn = new JButton("View Admin Logs");
        JButton backBtn = new JButton("Back"); // <--- new back button

        add(createCourseBtn);
        add(createSectionBtn);
        add(assignInstructorBtn);
        add(overrideCapacityBtn);
        add(overridePrereqBtn);
        add(viewLogsBtn);
        add(backBtn); // <--- add to layout

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
        String title = JOptionPane.showInputDialog(this, "Enter course title:");
        int credits = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter credits:"));
        Course course = new Course(code, title, credits);

        Result<Course> result = adminService.createCourse(course);
        showResult(result);
    }

    private void createSection() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter course code:");
        Optional<Course> courseOpt = catalogService.getCourseById(courseCode);

        if (courseOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course not found: " + courseCode,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String instructorId = JOptionPane.showInputDialog(this, "Enter instructor id:");
        Optional<Instructor> instructorOpt = instructorRepo.findById(instructorId);

        if (instructorOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Instructor not found: " + courseCode,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Course course = courseOpt.get();

        String term = JOptionPane.showInputDialog(this, "Enter term (e.g., Fall2025):");

        int capacity;
        try {
            capacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter capacity:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid capacity entered",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Section section = new Section("SEC" + System.currentTimeMillis(), course, term, capacity, instructorOpt.get());

        Result<Section> result = adminService.createSection(section);
        showResult(result);
    }


    private void assignInstructor() {
        String sectionId = JOptionPane.showInputDialog(this, "Enter section ID:");
        String instructorId = JOptionPane.showInputDialog(this, "Enter instructor ID:");

        Result<Void> result = adminService.assignInstructor(sectionId, instructorId);
        showResult(result);
    }

    private void overrideCapacity() {
        String sectionId = JOptionPane.showInputDialog(this, "Enter section ID:");
        int newCapacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new capacity:"));
        String reason = JOptionPane.showInputDialog(this, "Reason for override:");

        Result<Void> result = adminService.overrideCapacity(sectionId, newCapacity, reason);
        showResult(result);
    }

    private void overridePrerequisite() {
        String studentId = JOptionPane.showInputDialog(this, "Enter student ID:");
        String courseCode = JOptionPane.showInputDialog(this, "Enter course code:");
        String reason = JOptionPane.showInputDialog(this, "Reason for override:");

        Result<Void> result = adminService.overridePrerequisite(studentId, courseCode, reason);
        showResult(result);
    }

    private void viewLogs() {
        // 1. Fetch the list of logs from the AdminService
        List<AdminActionLog> logs = adminService.getLogs();

        // 2. Convert the logs list to a readable string
        StringBuilder logText = new StringBuilder();
        for (AdminActionLog log : logs) {
            logText.append("ID: ").append(log.getId())
                    .append(", Admin: ").append(log.getAdminId())
                    .append(", Action: ").append(log.getAction())
                    .append(", Type: ").append(log.getActionType())
                    .append(", Reason: ").append(log.getReason() != null ? log.getReason() : "-")
                    .append("\n");
        }

        // 3. Show the logs in a message dialog
        JOptionPane.showMessageDialog(
                this,
                !logText.isEmpty() ? logText.toString() : "No logs available.",
                "Admin Logs",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void showResult(Result<?> result) {
        if (result.isOk()) {
            JOptionPane.showMessageDialog(this, "Success!");
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + result.getError(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
