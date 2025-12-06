package console.admin;

import model.Admin;
import model.Course;
import model.Instructor;
import model.Section;
import repository.Repository;
import service.AdminService;

import java.util.Scanner;

public class AdminConsole {

    private final Admin admin;
    private final AdminService adminService;
    private final Repository<Course, String> courseRepository;
    private final Repository<Instructor, String> instructorRepository;

    public AdminConsole(Admin admin, AdminService adminService, Repository<Course, String> courseRepository, Repository<Instructor, String> instructorRepository ) {
        this.admin = admin;
        this.adminService = adminService;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Admin Menu (" + admin.getName() + ") ===");
            System.out.println("1. Create Course");
            System.out.println("2. Create Section");
            System.out.println("3. Assign Instructor");
            System.out.println("4. Override Capacity");
            System.out.println("5. Override Prerequisite");
            System.out.println("6. View Action Logs");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (sc.nextLine().trim()) {
                case "1" -> createCourse(sc);
                case "2" -> createSection(sc);
                case "3" -> assignInstructor(sc);
                case "4" -> overrideCapacity(sc);
                case "5" -> overridePrereq(sc);
                case "6" -> viewLogs();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createCourse(Scanner sc) {
        System.out.print("Code: ");
        String code = sc.nextLine();
        System.out.print("Title: ");
        String title = sc.nextLine();
        System.out.print("Credits: ");
        int credits = sc.nextInt();

        var result = adminService.createCourse(new Course(code, title, credits));
        System.out.println(result.isOk() ? "Course created." : result.getError());
    }

    private void createSection(Scanner sc) {
        System.out.print("Section ID: ");
        String id = sc.nextLine();
        System.out.print("Instructor ID: ");
        sc.nextLine();
        System.out.print("Course Code: ");
        String courseCode = sc.nextLine();
        System.out.print("Term: ");
        String term = sc.nextLine();
        System.out.print("Capacity: ");
        int cap = Integer.parseInt(sc.nextLine());

        Course course = courseRepository.findById(courseCode).get();
        Instructor instructor = instructorRepository.findById(courseCode).get();

        var section = new Section(id, course, term, cap, instructor);
        var result = adminService.createSection(section);
        System.out.println(result.isOk() ? "Section created." : result.getError());
    }

    private void assignInstructor(Scanner sc) {
        System.out.print("Section ID: ");
        String sec = sc.nextLine();
        System.out.print("Instructor ID: ");
        String inst = sc.nextLine();

        System.out.println(adminService.assignInstructor(sec, inst).isOk()
                ? "Instructor assigned."
                : "Error assigning.");
    }

    private void overrideCapacity(Scanner sc) {
        System.out.print("Section ID: ");
        String sec = sc.nextLine();
        System.out.print("New Capacity: ");
        int cap = Integer.parseInt(sc.nextLine());
        System.out.print("Reason: ");
        String reason = sc.nextLine();

        var result = adminService.overrideCapacity(sec, cap, reason);
        System.out.println(result.isOk() ? "Capacity overridden." : result.getError());
    }

    private void overridePrereq(Scanner sc) {
        System.out.print("Student ID: ");
        String stu = sc.nextLine();
        System.out.print("Course Code: ");
        String code = sc.nextLine();
        System.out.print("Reason: ");
        String reason = sc.nextLine();

        var result = adminService.overridePrerequisite(stu, code, reason);
        System.out.println(result.isOk() ? "Prerequisite overridden." : result.getError());
    }

    private void viewLogs() {
        adminService.getLogs().forEach(log ->
                System.out.println(log.getTimestamp() + " | " + log.getAction() + " | " + log.getReason()));
    }
}
