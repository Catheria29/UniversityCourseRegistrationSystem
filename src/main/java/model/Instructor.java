package model;

import lombok.Getter;
import utils.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Instructor extends Person {
    private final String department;
    private final List<String> assignedSectionIds = new ArrayList<>();

    public Instructor(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    @Override
    public String role() {
        return "INSTRUCTOR";
    }

    public List<String> getAssignedSectionIds() {
        return new ArrayList<>(assignedSectionIds);
    }

    public void assignSection(String sectionId) {
        assignedSectionIds.add(sectionId);
    }

    @Override
    public void displayProfile() {
        System.out.println("Instructor: " + getName() + ", Dept: " + department);
    }

    public Result<Void> assignGrade(Student student, Section section, Grade grade) {
        return student.postGrade(section, grade);
    }
}
