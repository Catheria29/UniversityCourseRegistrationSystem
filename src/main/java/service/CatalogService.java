package service;

import model.Course;
import model.Section;

import java.util.List;
import java.util.Optional;

public interface CatalogService {
    Optional<Section> getSectionById(String sectionId);

    Optional<Course> getCourseById(String courseCode);

    List<Course> listCourses();

    List<Section> listSectionsByCourse(String courseCode);

    List<Section> getSectionsForInstructor(String instructorId);
}
