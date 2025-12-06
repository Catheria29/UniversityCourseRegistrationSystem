package console.student;

import model.Section;
import model.Student;
import service.CatalogService;
import service.RegistrationService;

import java.util.List;
import java.util.Scanner;

public class StudentConsole {

    private final Student student;
    private final RegistrationService registrationService;
    private final CatalogService catalogService;

    public StudentConsole(Student student,
                          RegistrationService regService,
                          CatalogService catalogService) {
        this.student = student;
        this.registrationService = regService;
        this.catalogService = catalogService;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Student Menu (" + student.getName() + ") ===");
            System.out.println("\n=== Student Menu (" + student.getTranscript().size() + ") ===");
            System.out.println("1. View Catalog");
            System.out.println("2. Enroll in Section");
            System.out.println("3. Drop Section");
            System.out.println("4. View My Schedule");
            System.out.println("5. View Transcript");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewCatalog();
                case "2" -> enroll(sc);
                case "3" -> drop(sc);
                case "4" -> viewSchedule(sc);
                case "5" -> viewTranscript();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewCatalog() {
        catalogService.listCourses().forEach(course -> {
            System.out.println(course.getCode() + " - " + course.getTitle() + " (" + course.getCredits() + " credits)");

            // List all sections of this course
            List<Section> sections = catalogService.listSectionsByCourse(course.getCode());
            if (sections.isEmpty()) {
                System.out.println("  No sections available.");
            } else {
                for (Section sec : sections) {
                    String instructorName = sec.getInstructor() != null ? sec.getInstructor().getName() : "TBA";
                    System.out.println("  Section ID: " + sec.getId() +
                            ", Term: " + sec.getTerm() +
                            ", Capacity: " + sec.getCapacity() +
                            ", Instructor: " + instructorName);
                }
            }
            System.out.println();
        });
    }



    private void enroll(Scanner sc) {
        System.out.print("Section ID: ");
        var sectionId = sc.nextLine();

        var result = registrationService.enroll(student.getId(), sectionId);
        System.out.println(result.isOk() ? "Enrolled!" : result.getError());
    }

    private void drop(Scanner sc) {
        System.out.print("Section ID: ");
        var sectionId = sc.nextLine();

        var result = registrationService.drop(student.getId(), sectionId);
        System.out.println(result.isOk() ? "Dropped." : result.getError());
    }

    private void viewSchedule(Scanner sc) {
        System.out.print("Term: ");
        var term = sc.nextLine();

        var paged = registrationService.listSchedule(student.getId(), term, 1, 50);
        paged.getPageItems().forEach(e ->
                System.out.println(e.getSection().getCourse().getCode() +
                        " - " + e.getSection().getId()));
    }

    private void viewTranscript() {
        student.getTranscript().forEach(te ->
                System.out.println(
                        te.getSection().getCourse().getCode() + " | " +
                                te.getGrade()
                ));
    }
}
