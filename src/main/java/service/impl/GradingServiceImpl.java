package service.impl;

import model.*;
import repository.*;
import service.GradingService;
import utils.Result;


public class GradingServiceImpl implements GradingService {

    private final Repository<Student, String> studentRepo;
    private final Repository<Section, String> sectionRepo;
    private final Repository<Instructor, String> instructorRepo;

    public GradingServiceImpl(Repository<Student, String> studentRepo,
                              Repository<Section, String> sectionRepo,
                              Repository<Instructor, String> instructorRepo) {
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public Result<Void> postGrade(String instructorId, String sectionId, String studentId, Grade grade) {

        var instructorOpt = instructorRepo.findById(instructorId);
        if (instructorOpt.isEmpty()) return Result.fail("Instructor not found");

        var sectionOpt = sectionRepo.findById(sectionId);
        if (sectionOpt.isEmpty()) return Result.fail("Section not found");

        var studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found");

        Instructor instructor = instructorOpt.get();
        Section section = sectionOpt.get();
        Student student = studentOpt.get();

        // Ensure instructor actually teaches this section
        if (!section.getInstructor().getId().equals(instructorId)) {
            return Result.fail("Instructor is not assigned to this section");
        }

        // POLYMORPHISM: Instructor delegates to Student (Gradable)
        Result<Void> res = instructor.assignGrade(student, section, grade);

        if (res.isOk()) {
            studentRepo.save(student);  // persist updated transcript
        }

        return res;
    }

    @Override
    public Result<Double> computeGPA(String studentId) {
        var studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found");

        Student student = studentOpt.get();

        double totalPoints = 0;
        double totalCredits = 0;

        for (TranscriptEntry te : student.getTranscript()) {
            Grade g = te.getGrade();
            if (!g.isCountedInGPA()) continue;

            int credits = te.getSection().getCourse().getCredits();
            totalPoints += g.getPoints() * credits;
            totalCredits += credits;
        }

        if (totalCredits == 0) return Result.ok(0.0);

        return Result.ok(totalPoints / totalCredits);
    }
}
