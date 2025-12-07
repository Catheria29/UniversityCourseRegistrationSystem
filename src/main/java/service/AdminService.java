package service;

import model.Course;
import model.Section;
import model.AdminActionLog;
import utils.Result;

import java.util.List;


public interface AdminService {
    Result<Course> createCourse(Course course);

    Result<Section> createSection(Section section);

    Result<Void> assignInstructor(String sectionId, String instructorId);

    Result<Void> overrideCapacity(String sectionId, int newCapacity, String reason);

    Result<Void> overridePrerequisite(String studentId, String courseCode, String reason);

    List<AdminActionLog> getLogs();
}
