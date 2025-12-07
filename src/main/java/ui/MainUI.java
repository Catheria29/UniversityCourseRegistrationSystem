package ui;

import model.Admin;
import model.Instructor;
import model.Person;
import model.Student;
import repository.Repository;
import service.AdminService;
import service.CatalogService;
import service.GradingService;
import service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MainUI {

    private final Repository<Student, String> studentRepo;
    private final Repository<Instructor, String> instructorRepo;
    private final Repository<Admin, String> adminRepo;
    private final Repository<Person, String> personRepo;
    private final RegistrationService registrationService;
    private final CatalogService catalogService;
    private final GradingService gradingService;
    private final AdminService adminService;

    public MainUI(
            Repository<Student, String> studentRepo,
            Repository<Instructor, String> instructorRepo,
            Repository<Admin, String> adminRepo,
            Repository<Person, String> personRepo,
            RegistrationService registrationService,
            CatalogService catalogService,
            GradingService gradingService,
            AdminService adminService
    ) {
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.adminRepo = adminRepo;
        this.personRepo = personRepo;
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
        frame.setSize(500, 700);
        frame.setResizable(false);

        // Create main gradient panel
        JPanel mainPanel = UITheme.createGradientPanel(UITheme.PRIMARY_BLUE, UITheme.SECONDARY_PURPLE);
        mainPanel.setLayout(new BorderLayout());

        // Header
        JLabel titleLabel = new JLabel("University Portal");
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(UITheme.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(null);
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(UITheme.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Role selection
        JLabel roleLabel = UITheme.createHeadingLabel("Select Your Role:");
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(roleLabel);
        contentPanel.add(Box.createVerticalStrut(12));

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});
        roleBox.setFont(UITheme.REGULAR_FONT);
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        roleBox.setBackground(UITheme.LIGHT_BG);
        contentPanel.add(roleBox);
        contentPanel.add(Box.createVerticalStrut(25));

        // ID input
        JLabel idLabel = UITheme.createHeadingLabel("Enter Your ID:");
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(idLabel);
        contentPanel.add(Box.createVerticalStrut(12));

        JTextField idField = UITheme.createStyledTextField(20);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        contentPanel.add(idField);
        contentPanel.add(Box.createVerticalStrut(30));

        // Login Button
        JButton loginButton = UITheme.createPrimaryButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(loginButton);

        contentPanel.add(Box.createVerticalStrut(20));

        // Search Button
        JButton searchButton = UITheme.createSuccessButton("Search User");
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(searchButton);

        contentPanel.add(Box.createVerticalStrut(10));

        // Status Label
        JLabel statusLabel = new JLabel("");
        statusLabel.setFont(UITheme.SMALL_FONT);
        statusLabel.setForeground(UITheme.ERROR_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(statusLabel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        frame.add(mainPanel);

        // Login button action
        loginButton.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            String id = idField.getText().trim();

            if (id.isEmpty()) {
                statusLabel.setText("ID cannot be empty!");
                statusLabel.setForeground(UITheme.ERROR_RED);
                return;
            }

            switch (role != null ? role : "anonymous") {
                case "Student":
                    Optional<Student> student = studentRepo.findById(id);
                    if (student.isPresent()) {
                        frame.dispose();
                        new StudentUI(student.get(), registrationService, catalogService, frame).showUI();
                    } else {
                        statusLabel.setText("Student ID not found!");
                        statusLabel.setForeground(UITheme.ERROR_RED);
                    }
                    break;
                case "Instructor":
                    Optional<Instructor> instructor = instructorRepo.findById(id);
                    if (instructor.isPresent()) {
                        frame.dispose();
                        new InstructorUI(instructor.get(), gradingService, catalogService, frame).showUI();
                    } else {
                        statusLabel.setText("Instructor ID not found!");
                        statusLabel.setForeground(UITheme.ERROR_RED);
                    }
                    break;
                case "Admin":
                    Optional<Admin> admin = adminRepo.findById(id);
                    if (admin.isPresent()) {
                        frame.setVisible(false);
                        new AdminUI(adminService, catalogService, instructorRepo, frame).showUI();
                    } else {
                        statusLabel.setText("Admin ID not found!");
                        statusLabel.setForeground(UITheme.ERROR_RED);
                    }
                    break;
                default:
                    statusLabel.setText("Unknown role!");
                    statusLabel.setForeground(UITheme.ERROR_RED);
            }
        });

        // Search button action
        searchButton.addActionListener(e -> {
            frame.setVisible(false);
            showUserSearchWindow(frame);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showUserSearchWindow(JFrame mainFrame) {
        JFrame searchFrame = new JFrame("User Directory - Search");
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        searchFrame.setSize(900, 650);
        searchFrame.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = UITheme.createHeaderPanel("User Directory Search");
        searchFrame.add(headerPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(UITheme.LIGHT_BG);
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel searchLabel = UITheme.createHeadingLabel("Search by Name, ID, or Email:");
        JTextField searchField = UITheme.createStyledTextField(30);
        searchField.setPreferredSize(new Dimension(300, 40));
        JButton searchBtn = UITheme.createPrimaryButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        searchFrame.add(searchPanel, BorderLayout.NORTH);

        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(UITheme.WHITE);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea resultsArea = new JTextArea();
        resultsArea.setFont(UITheme.REGULAR_FONT);
        resultsArea.setEditable(false);
        resultsArea.setBackground(UITheme.WHITE);
        resultsArea.setForeground(UITheme.TEXT_DARK);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setText("Enter a search query and click 'Search' to find users...");

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_BLUE, 1));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        searchFrame.add(resultsPanel, BorderLayout.CENTER);

        // Bottom panel with back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        bottomPanel.setBackground(UITheme.LIGHT_BG);
        JButton backBtn = UITheme.createDangerButton("Back to Main");
        backBtn.addActionListener(e -> {
            searchFrame.dispose();
            mainFrame.setVisible(true);
        });
        bottomPanel.add(backBtn);
        searchFrame.add(bottomPanel, BorderLayout.SOUTH);

        // Search action
        searchBtn.addActionListener(e -> performSearch(searchField.getText().trim(), resultsArea));

        // Allow Enter key to search
        searchField.addActionListener(e -> searchBtn.doClick());

        searchFrame.setLocationRelativeTo(null);
        searchFrame.setVisible(true);
    }

    private void performSearch(String query, JTextArea resultsArea) {
        if (query.isEmpty()) {
            resultsArea.setText("Please enter a search query.");
            resultsArea.setForeground(UITheme.ERROR_RED);
            return;
        }

        // Search in person repository
        List<Person> results = personRepo.findAll().stream()
                .filter(p -> p.matches(query))
                .toList();

        if (results.isEmpty()) {
            resultsArea.setText("No users found matching: " + query);
            resultsArea.setForeground(UITheme.ERROR_RED);
        } else {
            StringBuilder resultsText = new StringBuilder();
            resultsText.append("Found ").append(results.size()).append(" user(s):\n\n");

            for (Person person : results) {
                resultsText.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                resultsText.append("ID: ").append(person.getId()).append("\n");
                resultsText.append("Name: ").append(person.getName()).append("\n");
                resultsText.append("Email: ").append(person.getEmail()).append("\n");
                resultsText.append("Role: ").append(person.role()).append("\n");

                // Display additional profile info based on type
                switch (person) {
                    case Student student -> {
                        resultsText.append("Major: ").append(student.getMajor()).append("\n");
                        resultsText.append("Current Enrollments: ").append(student.getCurrentEnrollments().size()).append("\n");
                        resultsText.append("GPA: ").append(String.format("%.2f", calculateGPA(student))).append("\n");
                    }
                    case Instructor instructor ->
                            resultsText.append("Department: ").append(instructor.getDepartment()).append("\n");
                    case Admin ignored -> resultsText.append("Admin Account\n");
                    default -> {
                    }
                }
            }
            resultsArea.setText(resultsText.toString());
            resultsArea.setForeground(UITheme.TEXT_DARK);
            resultsArea.setCaretPosition(0);
        }
    }

    private double calculateGPA(Student student) {
        return student.getTranscript().stream()
                .filter(te -> te.getGrade() != null && te.getGrade().isCountedInGPA())
                .mapToDouble(te -> te.getGrade().getPoints())
                .average()
                .orElse(0.0);
    }
}
