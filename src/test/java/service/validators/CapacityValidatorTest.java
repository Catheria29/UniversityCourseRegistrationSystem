package service.validators;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Result;

import static org.junit.jupiter.api.Assertions.*;

class CapacityValidatorTest {

    private CapacityValidator validator;
    private Section section;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        validator = new CapacityValidator();
        Instructor instructor = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
        Course course = new Course("CS101", "Intro to CS", 3);
        section = new Section("SEC1", course, "Fall2025", 1, instructor); // capacity = 1

        student1 = new Student("S1", "Alice", "alice@mail.com", "CS", s -> 0);
        student2 = new Student("S2", "Bob", "bob@mail.com", "CS", s -> 0);
    }

    @Test
    void testSectionNotFull() {
        // No students enrolled yet, capacity = 1
        Result<Void> result = validator.validate(section);
        assertTrue(result.isOk(), "Section should have available seats");

        // Add one enrolled student, still within cap acity
        section.getRoster().add(new Enrollment(student1, section, Enrollment.Status.ENROLLED));

        result = validator.validate(section);
        assertFalse(result.isOk(), "Section not have available seats");
    }


    @Test
    void testSectionFull() {
        // Add one student, then test capacity
        section.getRoster().add(new Enrollment(student1, section, Enrollment.Status.ENROLLED));
        section.getRoster().add(new Enrollment(student2, section, Enrollment.Status.ENROLLED)); // over capacity

        Result<Void> result = validator.validate(section);
        assertFalse(result.isOk(), "Section should be full");
        assertEquals("Section is full", result.getError());
    }

    @Test
    void testDroppedStudentDoesNotCount() {
        // Add a student who dropped
        section.getRoster().add(new Enrollment(student1, section, Enrollment.Status.DROPPED));

        Result<Void> result = validator.validate(section);
        assertTrue(result.isOk(), "Dropped student should not count toward capacity");
    }
}
