package ui;

import model.Admin;
import model.Instructor;
import model.Student;
import repository.Repository;
import service.AdminService;
import service.CatalogService;
import service.GradingService;
import service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class MainUI {

    private final Repository<Student, String> studentRepo;
    private final Repository<Instructor, String> instructorRepo;
    private final Repository<Admin, String> adminRepo;
    private final RegistrationService registrationService;
    private final CatalogService catalogService;
    private final GradingService gradingService;
    private final AdminService adminService;

    public MainUI(
            Repository<Student, String> studentRepo,
            Repository<Instructor, String> instructorRepo,
            Repository<Admin, String> adminRepo,
            RegistrationService registrationService,
            CatalogService catalogService,
            GradingService gradingService,
            AdminService adminService
    ) {
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.adminRepo = adminRepo;
        this.registrationService = registrationService;
        this.catalogService = catalogService;
        this.gradingService = gradingService;
        this.adminService = adminService;
    }

    public void start() {
        SwingUtilities.invokeLater(this::showLoginWindow);
    }

    private void showLoginWindow() {
        JFrame frame = new JFrame("University Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel roleLabel = new JLabel("Select role:");
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});

        JLabel idLabel = new JLabel("Enter ID:");
        JTextField idField = new JTextField();

        JButton loginButton = new JButton("Login");
        JLabel statusLabel = new JLabel("");

        frame.add(roleLabel);
        frame.add(roleBox);
        frame.add(idLabel);
        frame.add(idField);
        frame.add(new JLabel()); // empty placeholder
        frame.add(loginButton);
        frame.add(new JLabel()); // empty placeholder
        frame.add(statusLabel);

        loginButton.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            String id = idField.getText().trim();

            if (id.isEmpty()) {
                statusLabel.setText("ID cannot be empty!");
                return;
            }

            switch (role) {
                case "Student":
                    Optional<Student> student = studentRepo.findById(id);
                    if (student.isPresent()) {
                        frame.dispose();
                        new StudentUI(student.get(), registrationService, catalogService, frame).showUI();
                    } else {
                        statusLabel.setText("Student ID not found!");
                    }
                    break;
                case "Instructor":
                    Optional<Instructor> instructor = instructorRepo.findById(id);
                    if (instructor.isPresent()) {
                        frame.dispose();
                        new InstructorUI(instructor.get(), gradingService, catalogService, frame).showUI();
                    } else {
                        statusLabel.setText("Instructor ID not found!");
                    }
                    break;
                case "Admin":
                    Optional<Admin> admin = adminRepo.findById(id);
                    if (admin.isPresent()) {
                        frame.setVisible(false);  // hide main menu
                        new AdminUI(adminService, catalogService, instructorRepo, frame).showUI();
                    } else {
                        statusLabel.setText("Admin ID not found!");
                    }
                    break;
                default:
                    statusLabel.setText("Unknown role!");
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
