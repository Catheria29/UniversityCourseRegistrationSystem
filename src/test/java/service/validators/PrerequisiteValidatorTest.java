package service.validators;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Result;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrerequisiteValidatorTest {

    private PrerequisiteValidator validator;
    private Student student;
    private Section targetSection;
    private Course prereqCourse;

    @BeforeEach
    void setUp() {
        validator = new PrerequisiteValidator();

        // Setup student with empty transcript
        student = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 0);

        // Prerequisite course
        prereqCourse = new Course("CS101", "Intro to CS", 3);

        // Target course with prerequisite
        Course mainCourse = new Course("CS102", "Data Structures", 3);
        mainCourse.setPrerequisites(List.of(prereqCourse.getCode()));
        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
        targetSection = new Section("SEC1", mainCourse, "Fall2025", 30, instructor);
    }

    @Test
    void testMissingPrerequisite() {
        Result<Void> result = validator.validate(student, targetSection);
        assertFalse(result.isOk());
        assertEquals("Missing prerequisite: CS101", result.getError());
    }

    @Test
    void testPrerequisitePassed() {
        // Add transcript entry with passing grade
        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
        Section prereqSection = new Section("SEC0", prereqCourse, "Spring2025", 30, instructor);
        student.postGrade(prereqSection, Grade.B);

        Result<Void> result = validator.validate(student, targetSection);
        assertTrue(result.isOk());
        assertNull(result.getError());
    }

    @Test
    void testAdminOverride() {
        // Simulate admin override by manually adding transcript entry as passed
        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
        Section prereqSection = new Section("SEC0", prereqCourse, "Spring2025", 30, instructor);
        student.getTranscript().add(new TranscriptEntry(prereqSection, Grade.A));

        Result<Void> result = validator.validate(student, targetSection);
        assertTrue(result.isOk());
        assertNull(result.getError());
    }

    @Test
    void testPrerequisiteFailedGrade() {
        // Add transcript entry with failing grade
        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
        Section prereqSection = new Section("SEC0", prereqCourse, "Spring2025", 30, instructor);
        student.postGrade(prereqSection, Grade.F);

        Result<Void> result = validator.validate(student, targetSection);
        assertFalse(result.isOk());
        assertEquals("Missing prerequisite: CS101", result.getError());
    }
}
