package model;

import lombok.Getter;
import model.interfaces.Gradable;
import utils.Result;

import java.util.*;

@Getter
public class Instructor extends Person {
    private final String department;
    private final Set<String> assignedSectionIds = new HashSet<>();

    public Instructor(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    public List<String> getAssignedSectionIds() {
        return new ArrayList<>(assignedSectionIds);
    }

    public void addAssignedSectionId(String sectionId) {
        assignedSectionIds.add(sectionId);
    }

    @Override
    public String role() {
        return "INSTRUCTOR";
    }

    @Override
    public void displayProfile() {
        System.out.println("Instructor: " + getName() + ", Dept: " + department);
    }

    public Result<Void> assignGrade(Gradable student, Section section, Grade grade) {
        return student.postGrade(section, grade);
    }
}
