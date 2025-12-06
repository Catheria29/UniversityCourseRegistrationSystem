package service.validators;

import model.Enrollment;
import model.Section;
import utils.Result;

public record CapacityValidator () {

    public Result<Void> validate(Section section) {

        long count = section.getRoster().stream()
                .filter(e -> e.getStatus() == Enrollment.Status.ENROLLED)
                .count();

        if (count >= section.getCapacity()) {
            return Result.fail("Section is full");
        }

        return Result.ok(null);
    }
}
