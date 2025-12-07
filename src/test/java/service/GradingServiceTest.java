package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.impl.GradingServiceImpl;
import utils.Result;

import static org.junit.jupiter.api.Assertions.*;

class GradingServiceTest {

    private Repository<Student, String> studentRepo;
    private GradingService gradingService;

    private Student student;

    @BeforeEach
    void setUp() {
        studentRepo = new InMemoryRepository<>() {
            @Override
            protected String getId(Student entity) { return entity.getId(); }
        };
        Repository<Section, String> sectionRepo = new InMemoryRepository<>() {
            @Override
            protected String getId(Section entity) {
                return entity.getId();
            }
        };
        Repository<Instructor, String> instructorRepo = new InMemoryRepository<>() {
            @Override
            protected String getId(Instructor entity) {
                return entity.getId();
            }
        };

        // Create instructor
        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
        instructorRepo.save(instructor);

        gradingService = new GradingServiceImpl(studentRepo, sectionRepo, instructorRepo);

        student = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 0);
        studentRepo.save(student);

        Course c1 = new Course("CS101", "Intro CS", 3);
        Course c2 = new Course("CS102", "Data Structures", 3);
        Course c3 = new Course("CS103", "Algorithms", 3);

        Section section1 = new Section("SEC1", c1, "Fall2025", 30, instructor);
        Section section2 = new Section("SEC2", c2, "Fall2025", 30, instructor);
        Section section3 = new Section("SEC3", c3, "Fall2025", 30, instructor);

        sectionRepo.save(section1);
        sectionRepo.save(section2);
        sectionRepo.save(section3);

        // Post grades directly
        gradingService.postGrade("I1", section1.getId(), student.getId(), Grade.A);
        gradingService.postGrade("I1", section2.getId(), student.getId(), Grade.B);
        gradingService.postGrade("I1", section3.getId(), student.getId(), Grade.C);
    }

    @Test
    void computeGPA_correctly() {
        Result<Double> result = gradingService.computeGPA(student.getId());
        assertTrue(result.isOk());
        double gpa = result.getValue();

        // GPA = (4 + 3 + 2)/3 = 3.0
        assertEquals(3.0, gpa, 0.01);
    }

    @Test
    void computeGPA_noGrades_returnsZero() {
        Student emptyStudent = new Student("SM", "Bob", "bob@mail.com", "CS", s -> 0);
        studentRepo.save(emptyStudent);

        Result<Double> result = gradingService.computeGPA(emptyStudent.getId());
        assertTrue(result.isOk());
        assertEquals(0.0, result.getValue(), 0.01);
    }
}
