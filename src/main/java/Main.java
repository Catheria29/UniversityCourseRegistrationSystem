import model.*;
import repository.*;
import seeder.DataSeeder;
import service.*;
import service.impl.*;
import service.validators.CapacityValidator;
import service.validators.PrerequisiteValidator;
import service.validators.ScheduleConflictChecker;
import ui.MainUI;

import javax.swing.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // -----------------------------
        // 1. Repositories
        // -----------------------------
        Repository<Student, String> studentRepo = new StudentRepository();
        Repository<Instructor, String> instructorRepo = new InstructorRepository();
        Repository<Course, String> courseRepo = new CourseRepository();
        Repository<Section, String> sectionRepo = new SectionRepository();
        Repository<Admin, String> adminRepo = new AdminRepository();
        Repository<AdminActionLog, String> adminLogRepo = new AdminActionLogRepository();
        Repository<Enrollment, String> enrollmentRepo = new EnrollmentRepository();
        Repository<Person, String> personRepo = new PersonRepository();

        // -----------------------------
        // 2. Seed data
        // -----------------------------
        DataSeeder.seed(studentRepo, instructorRepo, courseRepo, sectionRepo, adminRepo);

        // Populate PersonRepository with all users
        studentRepo.findAll().forEach(personRepo::save);
        instructorRepo.findAll().forEach(personRepo::save);
        adminRepo.findAll().forEach(personRepo::save);

        // -----------------------------
        // 3. Services
        // -----------------------------
        AdminService adminService = new AdminServiceImpl(
                courseRepo, sectionRepo, instructorRepo, adminLogRepo, "A1");

        RegistrationService registrationService = new RegistrationServiceImpl(
                studentRepo, sectionRepo, enrollmentRepo, new CapacityValidator(),
                new PrerequisiteValidator(),
                new ScheduleConflictChecker()
        );

        GradingService gradingService = new GradingServiceImpl(
                studentRepo, sectionRepo, instructorRepo);

        CatalogService catalogService = new CatalogServiceImpl(
                courseRepo, sectionRepo);

        // -----------------------------
        // 4. Assign instructors to all sections
        // -----------------------------
        List<Section> allSections = sectionRepo.findAll();
        List<Instructor> instructors = instructorRepo.findAll();
        for (int i = 0; i < allSections.size(); i++) {
            Section sec = allSections.get(i);
            Instructor inst = instructors.get(i % instructors.size());
            String error = adminService.assignInstructor(sec.getId(), inst.getId()).getError();
            if (error != null) System.out.println("Instructor assignment error: " + error);
        }

        // -----------------------------
        // 5. Enroll students with proper validators
        // -----------------------------
        List<Student> students = studentRepo.findAll();
        for (Student s : students) {
            for (Section sec : allSections) {
                String error = registrationService.enroll(
                        s.getId(),
                        sec.getId()
                ).getError();

                if (error != null) {
                    System.out.println("Enrollment failed for " + s.getName() + " in " +
                            sec.getCourse().getCode() + ": " + error);
                }
            }

            studentRepo.save(s);
        }

        // -----------------------------
        // 6. Post some grades for demo even items, to simulate randomness
        // -----------------------------
        int i = 0;
        for (Student s : students) {
            for (Enrollment e : s.getCurrentEnrollments()) {
                if (i % 2 == 0) {
                    i++;
                    continue;
                }
                if (e.getGrade() == null) {
                    System.out.println("Posting grade A for " + s.getName() + " in " +
                            e.getSection().getCourse().getCode());
                }
            }
        }

        // -----------------------------
        // 7. Display student transcripts and tuition
        // -----------------------------
        for (Student s : students) {
            System.out.println("--- Transcript for " + s.getName() + " ---");
            s.getTranscript().forEach(t -> System.out.println(t.getSection().getCourse().getCode() + " → " + t.getGrade()));
            System.out.println("Tuition: $" + s.calculateTuition());
            System.out.println();
        }

        // -----------------------------
        // 8. Launch console
        // -----------------------------
        //
        // This launches the Console. I commented it out for now, since I'm now using the UI.
//        new MainConsole(
//                studentRepo, instructorRepo, adminRepo,
//                registrationService, catalogService, gradingService, adminService,
//                sectionRepo, courseRepo
//        ).start();

// This launches the UI.
        SwingUtilities.invokeLater(() -> new MainUI(studentRepo, instructorRepo, adminRepo, personRepo,
                registrationService, catalogService, gradingService, adminService).start());
    }
}
