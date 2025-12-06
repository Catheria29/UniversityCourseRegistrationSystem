package service.validators;

import model.Section;
import model.Student;
import utils.Result;

import java.util.List;

public record PrerequisiteValidator() {
    public Result<Void> validate(Student student, Section targetSection) {
        List<String> prerequisites = targetSection.getCourse().getPrerequisites();

        for (String prereq : prerequisites) {
            boolean hasCompleted = student.getTranscript().stream()
                    .anyMatch(entry ->
                            entry.getSection().getCourse().getCode().equals(prereq)
                                    && entry.getGrade() != null
                                    && entry.getGrade().isPassing()
                    );

            if (!hasCompleted) {
                return Result.fail("Missing prerequisite: " + prereq);
            }
        }

        return Result.ok(null);
    }
}
