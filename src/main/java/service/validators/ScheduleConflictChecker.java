package service.validators;

import model.Enrollment;
import model.Section;
import model.Student;
import utils.Result;

public record ScheduleConflictChecker() {

    public Result<Void> validate(Student student, Section newSection) {

        boolean conflict = student.getCurrentEnrollments().stream()
                .filter(e -> e.getStatus() == Enrollment.Status.ENROLLED)
                .flatMap(e -> e.getSection().getMeetingTimes().stream())
                .anyMatch(existingMT ->
                        newSection.getMeetingTimes().stream()
                                .anyMatch(existingMT::hasOverlap)
                );

        if (conflict) {
            return Result.fail("Schedule conflict detected with existing enrollment.");
        }

        return Result.ok(null);
    }
}
