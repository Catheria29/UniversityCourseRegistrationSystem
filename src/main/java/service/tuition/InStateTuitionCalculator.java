package service.tuition;

import model.Student;

public class InStateTuitionCalculator implements TuitionCalculator {
    @Override
    public double calculate(Student student) {
        if (student == null) {return 1000;}
        // Doing a simple calculation using the number of current enrollments

        return student.getCurrentEnrollments().size() * 500.0 + 1000.0;
    }
}
