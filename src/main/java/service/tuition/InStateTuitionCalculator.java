package service.tuition;

import model.Student;

public class InStateTuitionCalculator implements TuitionCalculator {
    @Override
    public double calculate(Student student) { return 1000.0; }
}
