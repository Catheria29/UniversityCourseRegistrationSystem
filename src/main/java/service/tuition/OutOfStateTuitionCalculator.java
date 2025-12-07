package service.tuition;

import model.Student;

public class OutOfStateTuitionCalculator implements TuitionCalculator {
    @Override
    public double calculate(Student student) {
        if (student == null) {
            return 3000.0;
        }

        // Doing a simple calculation using the number of current enrollments
        return student.getCurrentEnrollments().size() * 500.0 + 3000.0;
    }
}
