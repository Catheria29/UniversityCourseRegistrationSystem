package ui;

import model.Enrollment;
import model.Section;
import model.Student;
import model.TranscriptEntry;
import service.CatalogService;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class StudentUI {

    private final Student student;
    private final RegistrationService registrationService;
    private final CatalogService catalogService;

    private final JFrame mainMenu; // reference to caller menu

    public StudentUI(Student student,
                     RegistrationService registrationService,
                     CatalogService catalogService,
                     JFrame mainMenu) {
        this.student = student;
        this.registrationService = registrationService;
        this.catalogService = catalogService;
        this.mainMenu = mainMenu;
    }

    public void showUI() {
        JFrame frame = new JFrame("Student Portal - " + student.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Header panel with gradient
        JPanel headerPanel = UITheme.createHeaderPanel("Student Portal");
        frame.add(headerPanel, BorderLayout.NORTH);

        // Button panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(UITheme.LIGHT_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton catalogBtn = UITheme.createPrimaryButton("View Catalog");
        JButton scheduleBtn = UITheme.createSecondaryButton("My Schedule");
        JButton enrollBtn = UITheme.createSuccessButton("Add / Drop");
        JButton transcriptBtn = UITheme.createPrimaryButton("Transcript & GPA");
        JButton backBtn = UITheme.createDangerButton("Back");

        buttonPanel.add(catalogBtn);
        buttonPanel.add(scheduleBtn);
        buttonPanel.add(enrollBtn);
        buttonPanel.add(transcriptBtn);
        buttonPanel.add(backBtn);

        frame.add(buttonPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(contentPanel, BorderLayout.CENTER);

        // Button actions
        catalogBtn.addActionListener(e -> showCatalog(contentPanel));
        scheduleBtn.addActionListener(e -> showSchedule(contentPanel));
        enrollBtn.addActionListener(e -> showEnrollDrop(contentPanel));
        transcriptBtn.addActionListener(e -> showTranscript(contentPanel));

        backBtn.addActionListener(e -> {
            frame.dispose();                // close StudentUI window
            if (mainMenu != null)
                mainMenu.setVisible(true);  // return to main menu
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showCatalog(JPanel panel) {
        panel.removeAll();

        String[] columns = {"Course Code", "Title", "Credits", "Section ID", "Term", "Instructor", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        catalogService.listCourses().forEach(course -> {
            List<Section> sections = catalogService.listSectionsByCourse(course.getCode());
            if (sections.isEmpty()) {
                model.addRow(new Object[]{course.getCode(), course.getTitle(), course.getCredits(),
                        "-", "-", "-", "-"});
            } else {
                for (Section sec : sections) {
                    String instructorName = sec.getInstructor() != null ? sec.getInstructor().getName() : "TBA";
                    model.addRow(new Object[]{
                            course.getCode(), course.getTitle(), course.getCredits(),
                            sec.getId(), sec.getTerm(), instructorName, sec.getCapacity()
                    });
                }
            }
        });

        JTable table = styleTable(new JTable(model));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void showSchedule(JPanel panel) {
        panel.removeAll();

        String[] columns = {"Section ID", "Course", "Term", "Instructor", "Grade", "Schedule"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Enrollment e : student.getCurrentEnrollments()) {
            Section sec = e.getSection();
            String instructorName = sec.getInstructor() != null ? sec.getInstructor().getName() : "TBA";
            String grade = e.getGrade() != null ? e.getGrade().name() : "-";

            StringBuilder scheduleStr = new StringBuilder();
            for (var ts : sec.getMeetingTimes()) {
                scheduleStr.append(ts.getDay())
                        .append(" ")
                        .append(ts.getStartTime())
                        .append("-")
                        .append(ts.getEndTime())
                        .append(" @")
                        .append(ts.getRoom())
                        .append("; ");
            }

            model.addRow(new Object[]{
                    sec.getId(),
                    sec.getCourse().getCode(),
                    sec.getTerm(),
                    instructorName,
                    grade,
                    scheduleStr.toString()
            });
        }

        JTable table = styleTable(new JTable(model));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void showEnrollDrop(JPanel panel) {
        panel.removeAll();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(UITheme.LIGHT_BG);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = UITheme.createHeadingLabel("Enter Section ID:");
        JTextField sectionField = UITheme.createStyledTextField(15);
        sectionField.setMaximumSize(new Dimension(300, 40));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonRow.setBackground(UITheme.LIGHT_BG);
        JButton addBtn = UITheme.createSuccessButton("Add Section");
        JButton dropBtn = UITheme.createDangerButton("Drop Section");
        buttonRow.add(addBtn);
        buttonRow.add(dropBtn);

        JLabel statusLabel = new JLabel("");
        statusLabel.setFont(UITheme.REGULAR_FONT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(label);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(sectionField);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(buttonRow);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(statusLabel);

        panel.add(inputPanel, BorderLayout.NORTH);

        addBtn.addActionListener(e -> {
            String secId = sectionField.getText().trim();
            if (secId.isEmpty()) {
                statusLabel.setText("Please enter a section ID");
                statusLabel.setForeground(UITheme.ERROR_RED);
                return;
            }
            var result = registrationService.enroll(student.getId(), secId);
            if (result.getError() != null) {
                statusLabel.setText("Error: " + result.getError());
                statusLabel.setForeground(UITheme.ERROR_RED);
            } else {
                statusLabel.setText("✓ Enrolled successfully!");
                statusLabel.setForeground(UITheme.SUCCESS_GREEN);
                sectionField.setText("");
                showSchedule(panel); // refresh
            }
        });

        dropBtn.addActionListener(e -> {
            String secId = sectionField.getText().trim();
            if (secId.isEmpty()) {
                statusLabel.setText("Please enter a section ID");
                statusLabel.setForeground(UITheme.ERROR_RED);
                return;
            }
            var result = registrationService.drop(student.getId(), secId);
            if (result.getError() != null) {
                statusLabel.setText("Error: " + result.getError());
                statusLabel.setForeground(UITheme.ERROR_RED);
            } else {
                statusLabel.setText("✓ Dropped successfully!");
                statusLabel.setForeground(UITheme.SUCCESS_GREEN);
                sectionField.setText("");
                showSchedule(panel); // refresh
            }
        });

        panel.revalidate();
        panel.repaint();
    }

    private void showTranscript(JPanel panel) {
        panel.removeAll();

        String[] columns = {"Course", "Section ID", "Term", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (TranscriptEntry te : student.getTranscript()) {
            model.addRow(new Object[]{
                    te.getSection().getCourse().getCode(),
                    te.getSection().getId(),
                    te.getSection().getTerm(),
                    te.getGrade() != null ? te.getGrade().name() : "-"
            });
        }

        JTable table = styleTable(new JTable(model));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UITheme.LIGHT_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel gpaLabel = UITheme.createHeadingLabel("GPA: " + String.format("%.2f", calculateGPA()));
        bottomPanel.add(gpaLabel);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.revalidate();
        panel.repaint();
    }

    private JTable styleTable(JTable table) {
        table.setFont(UITheme.REGULAR_FONT);
        table.setRowHeight(25);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(UITheme.PRIMARY_BLUE);
        table.setSelectionForeground(UITheme.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.PRIMARY_BLUE);
        header.setForeground(UITheme.WHITE);
        header.setFont(UITheme.BUTTON_FONT);

        return table;
    }

    private double calculateGPA() {
        return student.getTranscript().stream()
                .filter(te -> te.getGrade() != null && te.getGrade().isCountedInGPA())
                .mapToDouble(te -> te.getGrade().getPoints())
                .average()
                .orElse(0.0);
    }
}
