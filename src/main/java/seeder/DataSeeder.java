package seeder;

import model.*;
import repository.*;
import service.tuition.InStateTuitionCalculator;
import service.tuition.OutOfStateTuitionCalculator;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class DataSeeder {

    public static void seed(
            Repository<Student, String> studentRepo,
            Repository<Instructor, String> instructorRepo,
            Repository<Course, String> courseRepo,
            Repository<Section, String> sectionRepo,
            Repository<Admin, String> adminRepo) {

        // -----------------------------
        // 1. Courses
        // -----------------------------
        Course cs101 = new Course("CS101", "Intro to CS", 3);
        Course cs102 = new Course("CS102", "Data Structures", 3, List.of("CS101"));
        Course cs201 = new Course("CS201", "Algorithms", 3, List.of("CS102"));
        Course cs202 = new Course("CS202", "Operating Systems", 3, List.of("CS102"));
        Course cs301 = new Course("CS301", "Machine Learning", 3, List.of("CS201"));
        Course math101 = new Course("MATH101", "Calculus I", 4);
        Course math102 = new Course("MATH102", "Calculus II", 4, List.of("MATH101"));
        Course phys101 = new Course("PHYS101", "Physics I", 4);

        Arrays.asList(cs101, cs102, cs201, cs202, cs301, math101, math102, phys101)
                .forEach(courseRepo::save);

        // -----------------------------
        // 2. Instructors
        // -----------------------------
        Instructor inst1 = new Instructor("I1", "Dr. Stone", "stone@mail.com", "CS");
        Instructor inst2 = new Instructor("I2", "Dr. Lee", "lee@mail.com", "CS");
        Instructor inst3 = new Instructor("I3", "Dr. Johnson", "johnson@mail.com", "MATH");

        Arrays.asList(inst1, inst2, inst3).forEach(instructorRepo::save);

        // -----------------------------
        // 3. Sections
        // -----------------------------
        Section sec1 = new Section("SEC1", cs101, "Fall2025", 30, inst1);
        sec1.getMeetingTimes().add(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "R101"));

        Section sec2 = new Section("SEC2", cs101, "Spring2026", 30, inst1);
        sec2.getMeetingTimes().add(new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "R101"));

        Section sec3 = new Section("SEC3", cs102, "Fall2025", 25, inst1);
        sec3.getMeetingTimes().add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "R102"));

        Section sec4 = new Section("SEC4", cs102, "Spring2026", 25, inst2);
        sec4.getMeetingTimes().add(new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "R102"));

        Section sec5 = new Section("SEC5", cs201, "Fall2025", 20, inst2);
        sec5.getMeetingTimes().add(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "R103"));

        Section sec6 = new Section("SEC6", cs202, "Fall2025", 20, inst2);
        sec6.getMeetingTimes().add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "R104"));

        Section sec7 = new Section("SEC7", cs301, "Spring2026", 15, inst2);
        sec7.getMeetingTimes().add(new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 30), "R105"));

        Section sec8 = new Section("SEC8", math101, "Fall2025", 35, inst1);
        sec8.getMeetingTimes().add(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "M101"));

        Section sec9 = new Section("SEC9", math102, "Spring2026", 30, inst3);
        sec9.getMeetingTimes().add(new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "M101"));

        Section sec10 = new Section("SEC10", phys101, "Fall2025", 40, inst3);
        sec10.getMeetingTimes().add(new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "P101"));

        Section sec11 = new Section("SEC11", cs201, "Spring2026", 20, inst3);
        sec11.getMeetingTimes().add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "R103"));

        Section sec12 = new Section("SEC12", cs202, "Spring2026", 20, inst3);
        sec12.getMeetingTimes().add(new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "R104"));

        Arrays.asList(sec1, sec2, sec3, sec4, sec5, sec6, sec7, sec8, sec9, sec10, sec11, sec12)
                .forEach(sectionRepo::save);

        // -----------------------------
        // 4. Students
        // -----------------------------
        Student alice = new Student("S1", "Alice Kim", "alice@mail.com", "CS", new OutOfStateTuitionCalculator());
        Student bob = new Student("S2", "Bob Lee", "CS", "bob@mail.com", new InStateTuitionCalculator());
        Student carol = new Student("S3", "Carol Chan", "CS", "carol@mail.com", new OutOfStateTuitionCalculator());
        Student david = new Student("S4", "David Park", "MATH", "david@mail.com", new InStateTuitionCalculator());
        Student emma = new Student("S5", "Emma Li", "CS", "emma@mail.com", new OutOfStateTuitionCalculator());
        Student frank = new Student("S6", "Frank Wu", "PHYS", "frank@mail.com", new InStateTuitionCalculator());

        Arrays.asList(alice, bob, carol, david, emma, frank)
                .forEach(studentRepo::save);

        // -----------------------------
        // 5. Admin
        // -----------------------------
        Admin admin = new Admin("A1", "Super Admin", "admin@mail.com");
        adminRepo.save(admin);

        // -----------------------------
        // 6. Preload transcripts
        // -----------------------------
        alice.getTranscript().add(new TranscriptEntry(sec1, Grade.A)); // CS101
        bob.getTranscript().add(new TranscriptEntry(sec1, Grade.B));   // CS101
        carol.getTranscript().add(new TranscriptEntry(sec1, Grade.C)); // CS101
        david.getTranscript().add(new TranscriptEntry(sec8, Grade.A)); // MATH101
        emma.getTranscript().add(new TranscriptEntry(sec1, Grade.A));  // CS101
        emma.getTranscript().add(new TranscriptEntry(sec3, Grade.B));  // CS102
        frank.getTranscript().add(new TranscriptEntry(sec10, Grade.B)); // PHYS101
    }
}
