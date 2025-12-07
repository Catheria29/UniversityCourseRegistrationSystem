package model;

import lombok.Getter;

@Getter
public enum Grade {
    A(4.0), B(3.0), C(2.0), D(1.0), F(0.0), I(null), W(null);

    private final Double points;

    Grade(Double points) {
        this.points = points;
    }

    public boolean isPassing() {
        return points != null && points > 0.0;
    }

    public boolean isCountedInGPA() {
        return this == A || this == B || this == C || this == D || this == F;
    }
}
