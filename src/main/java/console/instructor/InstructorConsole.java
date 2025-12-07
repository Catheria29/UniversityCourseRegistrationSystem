package console.instructor;

import model.Grade;
import model.Instructor;
import model.Section;
import repository.Repository;
import service.GradingService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class InstructorConsole {

    private final Instructor instructor;
    private final GradingService gradingService;
    private final Repository<Section, String> sectionRepository;

    public InstructorConsole(Instructor instructor, GradingService gradingService, Repository<Section, String> sectionRepository) {
        this.instructor = instructor;
        this.gradingService = gradingService;
        this.sectionRepository = sectionRepository;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Instructor Menu (" + instructor.getName() + ") ===");
            System.out.println("1. List My Sections");
            System.out.println("2. Enter Grade");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (sc.nextLine().trim()) {
                case "1" -> listSections();
                case "2" -> enterGrade(sc);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void listSections() {
        List<String> sectionIds = instructor.getAssignedSectionIds();
        sectionIds.stream().map(sectionRepository::findById).filter(Optional::isPresent).forEach(sec ->
                System.out.println(sec.get().getId() + " - " + sec.get().getCourse().getCode()));
    }

    private void enterGrade(Scanner sc) {
        System.out.print("Student ID: ");
        String studentId = sc.nextLine();
        System.out.print("Section ID: ");
        String sectionId = sc.nextLine();
        System.out.print("Grade (A-F): ");
        String grade = sc.nextLine();

        var result = gradingService.postGrade(instructor.getId(), studentId, sectionId, Grade.valueOf(grade));
        System.out.println(result.isOk() ? "Grade posted." : result.getError());
    }
}
