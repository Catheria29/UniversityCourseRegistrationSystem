package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import service.validators.CapacityValidator;
import service.validators.PrerequisiteValidator;
import service.validators.ScheduleConflictChecker;
import utils.Result;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    private Student student;
    private Section sectionA, sectionB;
    private InMemoryRepository<Enrollment, String> enrollmentRepo;
    private CapacityValidator capacityValidator;
    private PrerequisiteValidator prereqValidator;
    private ScheduleConflictChecker conflictChecker;

    @BeforeEach
    void setUp() {
        // Minimal TuitionCalculator stub
        student = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 1000.0);

        enrollmentRepo = new InMemoryRepository<>() {
            @Override
            protected String getId(Enrollment entity) {
                return entity.getId();
            }
        };

        capacityValidator = new CapacityValidator();
        prereqValidator = new PrerequisiteValidator();
        conflictChecker = new ScheduleConflictChecker();

        Course course1 = new Course("CS101", "Intro CS", 3);
        Course course2 = new Course("CS102", "Data Structures", 3);
        course2.getPrerequisites().add("CS101");

        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");

        sectionA = new Section("SEC-A", course1, "Fall2025", 2, instructor);
        sectionA.getMeetingTimes().add(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9,0), LocalTime.of(10,0), "R1"));

        sectionB = new Section("SEC-B", course2, "Fall2025", 2, instructor);
        sectionB.getMeetingTimes().add(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9,30), LocalTime.of(10,30), "R2"));
    }

    @Test
    void enroll_successful() {
        Result<Enrollment> result = student.enroll(sectionA, enrollmentRepo, capacityValidator, prereqValidator, conflictChecker);
        assertTrue(result.isOk());
        assertEquals(1, student.getCurrentEnrollments().size());
    }

    @Test
    void enroll_scheduleConflict() {
        // Enroll in sectionA first
        student.enroll(sectionA, enrollmentRepo, capacityValidator, prereqValidator, conflictChecker);
        // sectionB overlaps with sectionA
        Result<Enrollment> result = student.enroll(sectionB, enrollmentRepo, capacityValidator, prereqValidator, conflictChecker);
        assertFalse(result.isOk());
    }

    @Test
    void dropSection_successful() {
        student.enroll(sectionA, enrollmentRepo, capacityValidator, prereqValidator, conflictChecker);
        Result<Void> dropResult = student.drop(sectionA, enrollmentRepo);
        assertTrue(dropResult.isOk());
        assertEquals(0, student.getCurrentEnrollments().size());
    }

    @Test
    void dropSection_notEnrolled() {
        Result<Void> dropResult = student.drop(sectionA, enrollmentRepo);
        assertFalse(dropResult.isOk());
        assertEquals("Student not enrolled in section", dropResult.getError());
    }

    @Test
    void postGrade_updatesTranscriptAndEnrollment() {
        student.enroll(sectionA, enrollmentRepo, capacityValidator, prereqValidator, conflictChecker);
        student.postGrade(sectionA, Grade.A);
        assertEquals(1, student.getTranscript().size());
        assertEquals(Grade.A, student.getTranscript().get(0).getGrade());
        assertEquals(Grade.A, student.getCurrentEnrollments().get(0).getGrade());
    }

    @Test
    void calculateTuition_returnsExpected() {
        double tuition = student.calculateTuition();
        assertEquals(1000.0, tuition);
    }
}
