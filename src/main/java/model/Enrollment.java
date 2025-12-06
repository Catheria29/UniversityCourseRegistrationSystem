package model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Enrollment {
    private String id;

    @NonNull private transient Student student;
    @NonNull private Section section;

    private Status status = Status.ENROLLED;
    private Grade grade;

    public enum Status { ENROLLED, DROPPED, WAITLISTED }

    public String getCourseCode() {
        return section.getCourse().getCode();
    }

    public Enrollment(@NonNull Student student, @NonNull Section section, Status status) {
        this.id = java.util.UUID.randomUUID().toString();
        this.student = student;
        this.section = section;
        this.status = status;
    }
}
