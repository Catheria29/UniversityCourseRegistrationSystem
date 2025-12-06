package service.validators;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.tuition.TuitionCalculator;
import utils.Result;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleConflictCheckerTest {

    private Student student;
    private ScheduleConflictChecker checker;

    private Course dummyCourse1;
    private Course dummyCourse2;

    private Instructor instructor1;

    @BeforeEach
    void setup() {
        // Tuition calculator stub
        TuitionCalculator calc = s -> 0.0;

        student = new Student("S1", "Alice", "a@x.com", "CS", calc);
        checker = new ScheduleConflictChecker();

        dummyCourse1 = new Course("C101", "Intro", 4);
        dummyCourse2 = new Course("C102", "Data Structures", 4);
        instructor1 = new Instructor("I1", "Dr. Smith", "smith@mail.com", "CS");
    }

    private TimeSlot ts(DayOfWeek day, int startHour, int endHour) {
        return new TimeSlot(
                day,
                LocalTime.of(startHour, 0),
                LocalTime.of(endHour, 0),
                "ROOM-101"
        );
    }

    private Section sectionWithTimes(String id, Course c, TimeSlot... slots) {
        Section s = new Section(id, c, "Fall2025", 30, instructor1);
        s.getMeetingTimes().addAll(List.of(slots));
        return s;
    }

    private void enroll(Student student, Section section) {
        Enrollment e = new Enrollment(student, section, Enrollment.Status.ENROLLED);
        student.getCurrentEnrollments().add(e);
    }

    @Test
    void noConflictWhenSchedulesDontOverlap() {
        Section existing = sectionWithTimes("SEC1", dummyCourse1,
                ts(DayOfWeek.MONDAY, 10, 11));

        Section newSec = sectionWithTimes("SEC2", dummyCourse2,
                ts(DayOfWeek.MONDAY, 12, 13));

        enroll(student, existing);

        Result<Void> result = checker.validate(student, newSec);

        assertTrue(result.isOk());
    }

    @Test
    void conflictWhenTimesOverlap() {
        Section existing = sectionWithTimes("SEC1", dummyCourse1,
                ts(DayOfWeek.MONDAY, 10, 12));

        Section newSec = sectionWithTimes("SEC2", dummyCourse2,
                ts(DayOfWeek.MONDAY, 11, 13));

        enroll(student, existing);

        Result<Void> result = checker.validate(student, newSec);

        assertFalse(result.isOk());
        assertEquals("Schedule conflict detected with existing enrollment.", result.getError());
    }

    @Test
    void noConflictWhenDaysAreDifferent() {
        Section existing = sectionWithTimes("SEC1", dummyCourse1,
                ts(DayOfWeek.MONDAY, 10, 12));

        Section newSec = sectionWithTimes("SEC2", dummyCourse2,
                ts(DayOfWeek.TUESDAY, 10, 12));

        enroll(student, existing);

        Result<Void> result = checker.validate(student, newSec);

        assertTrue(result.isOk());
    }

    @Test
    void conflictWhenAnySlotOverlaps() {
        Section existing = sectionWithTimes("SEC1", dummyCourse1,
                ts(DayOfWeek.MONDAY, 9, 10),
                ts(DayOfWeek.WEDNESDAY, 14, 16));

        Section newSec = sectionWithTimes("SEC2", dummyCourse2,
                ts(DayOfWeek.WEDNESDAY, 15, 17));

        enroll(student, existing);

        Result<Void> result = checker.validate(student, newSec);

        assertFalse(result.isOk());
    }

    @Test
    void noConflictIfStudentHasNoEnrollments() {
        Section newSec = sectionWithTimes("SEC2", dummyCourse2,
                ts(DayOfWeek.MONDAY, 10, 11));

        Result<Void> result = checker.validate(student, newSec);

        assertTrue(result.isOk());
    }
}
