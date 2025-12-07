package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;
import model.interfaces.Gradable;
import model.interfaces.Payable;
import repository.Repository;
import service.tuition.TuitionCalculator;
import service.validators.CapacityValidator;
import service.validators.PrerequisiteValidator;
import service.validators.ScheduleConflictChecker;
import utils.Result;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Student extends Person implements Payable, Gradable {
    private final String major;
    private final List<Enrollment> currentEnrollments = new ArrayList<>();
    private final List<TranscriptEntry> transcript = new ArrayList<>();
    @JsonIgnore
    private final TuitionCalculator tuitionCalculator;


    public Student(String id, String name, String email, String major, TuitionCalculator calculator) {
        super(id, name, email);
        this.major = major;
        this.tuitionCalculator = calculator;
    }

    @Override
    public String role() {
        return "STUDENT";
    }

    @Override
    public void displayProfile() {
        System.out.println("Student: " + getName() + " (" + getId() + ")");
    }

    @Override
    public double calculateTuition() {
        return tuitionCalculator.calculate(this);
    }

    @Override
    public Result<Void> postGrade(Section section, Grade grade) {
        transcript.removeIf(te ->
                te.getSection().getCourse().getCode().equals(section.getCourse().getCode())
                        && te.getSection().getTerm().equals(section.getTerm())
        );

        transcript.add(new TranscriptEntry(
                section,
                grade
        ));

        // Update enrollment if exists
        currentEnrollments.stream()
                .filter(e -> e.getSection().getId().equals(section.getId()))
                .findFirst()
                .ifPresent(e -> {
                    e.setGrade(grade);
                    e.setStatus(Enrollment.Status.ENROLLED);
                });

        return Result.ok(null);
    }

    /**
     * Enroll in a section with validators and enrollment repository
     */
    public Result<Enrollment> enroll(Section section, Repository<Enrollment, String> enrollmentRepo,
                                     CapacityValidator capacityValidator,
                                     PrerequisiteValidator prerequisiteValidator,
                                     ScheduleConflictChecker conflictValidator) {

        // Validate capacity
        Result<Void> capResult = capacityValidator.validate(section);
        if (!capResult.isOk()) return Result.fail(capResult.getError());

        // Validate prerequisites
        Result<Void> prereqResult = prerequisiteValidator.validate(this, section);
        if (!prereqResult.isOk()) return Result.fail(prereqResult.getError());

        // Validate schedule conflicts
        Result<Void> conflictResult = conflictValidator.validate(this, section);
        if (!conflictResult.isOk()) return Result.fail(conflictResult.getError());

        // Create and save enrollment
        Enrollment enrollment = new Enrollment(this, section, Enrollment.Status.ENROLLED);
        enrollmentRepo.save(enrollment);

        currentEnrollments.add(enrollment);

        return Result.ok(enrollment);
    }

    /**
     * Drop a section
     */
    public Result<Void> drop(Section section, Repository<Enrollment, String> enrollmentRepo) {
        Enrollment toRemove = currentEnrollments.stream()
                .filter(e -> e.getSection().getId().equals(section.getId()))
                .findFirst()
                .orElse(null);

        if (toRemove == null) return Result.fail("Student not enrolled in section");

        toRemove.setStatus(Enrollment.Status.DROPPED);
        enrollmentRepo.save(toRemove);

        currentEnrollments.remove(toRemove);

        return Result.ok(null);
    }

}
