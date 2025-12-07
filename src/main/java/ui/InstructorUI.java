package ui;

import model.Enrollment;
import model.Grade;
import model.Instructor;
import model.Section;
import service.CatalogService;
import service.GradingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class InstructorUI {

    private final Instructor instructor;
    private final GradingService gradingService;
    private final CatalogService catalogService;

    // reference to main menu (AdminUI, MainUI, etc.)
    private final JFrame mainMenu;

    public InstructorUI(Instructor instructor,
                        GradingService gradingService,
                        CatalogService catalogService,
                        JFrame mainMenu) {
        this.instructor = instructor;
        this.gradingService = gradingService;
        this.catalogService = catalogService;
        this.mainMenu = mainMenu;
    }

    public void showUI() {
        JFrame frame = new JFrame("Instructor Portal - " + instructor.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Header panel with gradient
        JPanel headerPanel = UITheme.createHeaderPanel("Instructor Portal");
        frame.add(headerPanel, BorderLayout.NORTH);

        // === TOP BUTTON PANEL ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(UITheme.LIGHT_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton listSectionsBtn = UITheme.createPrimaryButton("My Sections");
        JButton viewRosterBtn = UITheme.createSecondaryButton("View Roster");
        JButton postGradeBtn = UITheme.createSuccessButton("Post Grade");
        JButton backBtn = UITheme.createDangerButton("Back");

        buttonPanel.add(listSectionsBtn);
        buttonPanel.add(viewRosterBtn);
        buttonPanel.add(postGradeBtn);
        buttonPanel.add(backBtn);

        frame.add(buttonPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(contentPanel, BorderLayout.CENTER);

        // === ACTIONS ===
        listSectionsBtn.addActionListener(e -> showSections(contentPanel));
        viewRosterBtn.addActionListener(e -> showRoster(contentPanel));
        postGradeBtn.addActionListener(e -> postGrade(contentPanel));

        backBtn.addActionListener(e -> {
            frame.dispose();        // Close InstructorUI
            if (mainMenu != null) {
                mainMenu.setVisible(true);  // return to main menu
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showSections(JPanel panel) {
        panel.removeAll();

        String[] columns = {"Section ID", "Course", "Term", "Capacity", "Remaining Seats"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<Section> sections = catalogService.getSectionsForInstructor(instructor.getId());
        for (Section sec : sections) {
            model.addRow(new Object[]{
                    sec.getId(),
                    sec.getCourse().getCode(),
                    sec.getTerm(),
                    sec.getCapacity(),
                    sec.getRemainingSeats()
            });
        }

        JTable table = styleTable(new JTable(model));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void showRoster(JPanel panel) {
        panel.removeAll();
        String sectionId = JOptionPane.showInputDialog(panel, "Enter Section ID:");
        if (sectionId == null || sectionId.isBlank()) return;

        Optional<Section> sectionOpt = catalogService.getSectionById(sectionId);
        if (sectionOpt.isEmpty() ||
                !sectionOpt.get().getInstructor().getId().equals(instructor.getId())) {
            JOptionPane.showMessageDialog(panel, "Invalid section or not assigned to you.");
            return;
        }

        Section section = sectionOpt.get();

        String[] columns = {"Student ID", "Student Name", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Enrollment e : section.getRoster()) {
            String grade = e.getGrade() != null ? e.getGrade().name() : "-";
            model.addRow(new Object[]{
                    e.getStudent().getId(),
                    e.getStudent().getName(),
                    grade
            });
        }

        JTable table = styleTable(new JTable(model));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void postGrade(JPanel panel) {
        panel.removeAll();
        String sectionId = JOptionPane.showInputDialog(panel, "Please Enter Section ID:");
        if (sectionId == null || sectionId.isBlank()) return;

        Optional<Section> sectionOpt = catalogService.getSectionById(sectionId);
        if (sectionOpt.isEmpty() ||
                !sectionOpt.get().getInstructor().getId().equals(instructor.getId())) {
            JOptionPane.showMessageDialog(panel, "Invalid section or not assigned to you.");
            return;
        }

        String studentId = JOptionPane.showInputDialog(panel, "Enter Student ID:");
        if (studentId == null || studentId.isBlank()) return;

        Grade[] grades = Grade.values();

        Grade selectedGrade = (Grade) JOptionPane.showInputDialog(
                panel,
                "Select Grade:",
                "Post Grade",
                JOptionPane.QUESTION_MESSAGE,
                null,
                grades,
                grades[0]
        );

        if (selectedGrade != null) {
            var result = gradingService.postGrade(instructor.getId(), sectionId, studentId, selectedGrade);

            if (result.getError() != null) {
                JOptionPane.showMessageDialog(panel, "Error: " + result.getError(), "Grade Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "✓ Grade posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
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
}
