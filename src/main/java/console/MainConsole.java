package console;

import model.*;
import repository.*;
import service.AdminService;
import service.CatalogService;
import service.GradingService;
import service.RegistrationService;

public class MainConsole {

    private final Repository<Student, String> studentRepo;
    private final Repository<Instructor, String> instructorRepo;
    private final Repository<Admin, String> adminRepo;

    private final RegistrationService registrationService;
    private final CatalogService catalogService;
    private final GradingService gradingService;
    private final AdminService adminService;
    private final Repository<Section, String> sectionRepo;
    private final Repository<Course, String> courseRepository;

    public MainConsole(
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
        new LoginConsole(
                studentRepo,
                instructorRepo,
                adminRepo,
                registrationService,
                catalogService,
                gradingService,
                adminService,
                sectionRepo,
                courseRepository
        ).start();
    }
}
