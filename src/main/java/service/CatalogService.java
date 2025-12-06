package service;

import model.Course;
import model.Section;
import utils.CourseQuery;
import utils.PagedList;
import utils.Result;

import java.util.List;
import java.util.Optional;

public interface CatalogService {
    PagedList<Course> search(CourseQuery query, int page, int pageSize);
    Result<Course> createCourse(Course course);
    Result<Section> createSection(Section section);
    Optional<Section> getSectionById(String sectionId);
    Optional<Course> getCourseById(String courseCode);
    Result<Void> assignInstructor(String sectionId, String instructorId);

    List<Course> listCourses();
    List<Section> listSectionsByCourse(String courseCode);
    List<Section> getSectionsForInstructor(String instructorId);
}
