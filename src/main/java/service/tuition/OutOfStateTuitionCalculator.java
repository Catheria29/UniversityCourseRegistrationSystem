package service.tuition;

import model.Student;

public class OutOfStateTuitionCalculator implements TuitionCalculator {
    @Override
    public double calculate(Student student) { return 3000.0; }
}
