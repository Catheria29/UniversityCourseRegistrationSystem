package console;

import console.admin.AdminConsole;
import console.instructor.InstructorConsole;
import console.student.StudentConsole;
import model.*;
import repository.*;
import service.AdminService;
import service.CatalogService;
import service.GradingService;
import service.RegistrationService;

import java.util.Scanner;

public class LoginConsole {

    private final Repository<Student, String> studentRepo;
    private final Repository<Instructor, String> instructorRepo;
    private final Repository<Admin, String> adminRepo;

    private final RegistrationService registrationService;
    private final CatalogService catalogService;
    private final GradingService gradingService;
    private final AdminService adminService;
    private final Repository<Section, String> sectionRepo;
    private final Repository<Course, String> courseRepository;

    public LoginConsole(
            Repository<Student, String> studentRepo,
            Repository<Instructor, String> instructorRepo,
            Repository<Admin, String> adminRepo,
            RegistrationService registrationService,
            CatalogService catalogService,
            GradingService gradingService,
            AdminService adminService,
            Repository<Section, String> sectionRepo,
            Repository<Course, String> courseRepository
    ) {
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.adminRepo = adminRepo;
        this.registrationService = registrationService;
        this.catalogService = catalogService;
        this.gradingService = gradingService;
        this.adminService = adminService;
        this.sectionRepo = sectionRepo;
        this.courseRepository = courseRepository;
    }

    public void start() {
        var scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== University System =====");
            System.out.println("1. Login as Student");
            System.out.println("2. Login as Instructor");
            System.out.println("3. Login as Admin");
            System.out.println("0. Exit");
            System.out.print("> ");

            switch (scanner.nextLine().trim()) {
                case "1" -> loginStudent(scanner);
                case "2" -> loginInstructor(scanner);
                case "3" -> loginAdmin(scanner);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void loginStudent(Scanner scanner) {
        System.out.print("Enter student ID: ");
        var id = scanner.nextLine();
        studentRepo.findById(id).ifPresentOrElse(
                s -> {
                    System.out.println("Welcome, " + s.getName() + "!");
                    System.out.println("Welcome, " + s.getTranscript().size() + "!");
                    new StudentConsole(s, registrationService, catalogService).start();
                },
                () -> System.out.println("Student not found.")
        );
    }

    private void loginInstructor(Scanner scanner) {
        System.out.print("Enter instructor ID: ");
        var id = scanner.nextLine();
        instructorRepo.findById(id).ifPresentOrElse(
                i -> new InstructorConsole(i, gradingService, sectionRepo).start(),
                () -> System.out.println("Instructor not found.")
        );
    }

    private void loginAdmin(Scanner scanner) {
        System.out.print("Enter admin ID: ");
        var id = scanner.nextLine();
        adminRepo.findById(id).ifPresentOrElse(
                a -> new AdminConsole(a, adminService, courseRepository, instructorRepo).start(),
                () -> System.out.println("Admin not found.")
        );
    }
}
