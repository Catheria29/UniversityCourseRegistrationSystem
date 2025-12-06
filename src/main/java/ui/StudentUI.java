package ui;

import model.Enrollment;
import model.Section;
import model.Student;
import service.CatalogService;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton catalogBtn = new JButton("View Catalog");
        JButton scheduleBtn = new JButton("My Schedule");
        JButton enrollBtn = new JButton("Add / Drop Section");
        JButton transcriptBtn = new JButton("Transcript & GPA");
        JButton backBtn = new JButton("Back");

        buttonPanel.add(catalogBtn);
        buttonPanel.add(scheduleBtn);
        buttonPanel.add(enrollBtn);
        buttonPanel.add(transcriptBtn);
        buttonPanel.add(backBtn);

        frame.add(buttonPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
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

        JTable table = new JTable(model);
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

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void showEnrollDrop(JPanel panel) {
        panel.removeAll();

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Section ID:");
        JTextField sectionField = new JTextField(10);
        JButton addBtn = new JButton("Add");
        JButton dropBtn = new JButton("Drop");
        JLabel statusLabel = new JLabel();

        inputPanel.add(label);
        inputPanel.add(sectionField);
        inputPanel.add(addBtn);
        inputPanel.add(dropBtn);
        inputPanel.add(statusLabel);

        panel.add(inputPanel, BorderLayout.NORTH);

        addBtn.addActionListener(e -> {
            String secId = sectionField.getText().trim();
            String error = registrationService.enroll(student.getId(), secId).getError();
            statusLabel.setText(error != null ? "Error: " + error : "Enrolled successfully!");
            showSchedule(panel); // refresh
        });

        dropBtn.addActionListener(e -> {
            String secId = sectionField.getText().trim();
            String error = registrationService.drop(student.getId(), secId).getError();
            statusLabel.setText(error != null ? "Error: " + error : "Dropped successfully!");
            showSchedule(panel); // refresh
        });

        panel.revalidate();
        panel.repaint();
    }

    private void showTranscript(JPanel panel) {
        panel.removeAll();

        String[] columns = {"Course", "Section ID", "Term", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        student.getTranscript().forEach(te -> {
            model.addRow(new Object[]{
                    te.getSection().getCourse().getCode(),
                    te.getSection().getId(),
                    te.getSection().getTerm(),
                    te.getGrade() != null ? te.getGrade().name() : "-"
            });
        });

        JTable table = new JTable(model);
        JLabel gpaLabel = new JLabel("GPA: " + calculateGPA());

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(gpaLabel);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.revalidate();
        panel.repaint();
    }

    private double calculateGPA() {
        return student.getTranscript().stream()
                .filter(te -> te.getGrade() != null && te.getGrade().isCountedInGPA())
                .mapToDouble(te -> te.getGrade().getPoints())
                .average()
                .orElse(0.0);
    }
}
