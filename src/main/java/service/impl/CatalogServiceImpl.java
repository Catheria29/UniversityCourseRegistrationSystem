package service.impl;

import model.Course;
import model.Instructor;
import model.Section;
import repository.Repository;
import service.CatalogService;
import utils.CourseQuery;
import utils.PagedList;
import utils.Result;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CatalogServiceImpl implements CatalogService {

    private final Repository<Course, String> courseRepo;
    private final Repository<Section, String> sectionRepo;
    private final Repository<Instructor, String> instructorRepo;

    public CatalogServiceImpl(Repository<Course, String> courseRepo,
                              Repository<Section, String> sectionRepo,
                              Repository<Instructor, String> instructorRepo) {
        this.courseRepo = courseRepo;
        this.sectionRepo = sectionRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public PagedList<Course> search(CourseQuery query, int page, int pageSize) {
        var allCourses = courseRepo.findAll(0, Integer.MAX_VALUE).getPageItems();
        var filtered = allCourses.stream()
                .filter(query::matches)
                .collect(Collectors.toList());
        return new PagedList<>(filtered, page, pageSize, filtered.size());
    }

    @Override
    public Result<Course> createCourse(Course course) {
        courseRepo.save(course);
        return Result.ok(course);
    }

    @Override
    public Result<Section> createSection(Section section) {
        if (courseRepo.findById(section.getCourse().getCode()).isEmpty()) {
            return Result.fail("Course does not exist");
        }
        sectionRepo.save(section);
        return Result.ok(section);
    }

    @Override
    public Optional<Section> getSectionById(String sectionId) {
        return sectionRepo.findById(sectionId);
    }

    @Override
    public Optional<Course> getCourseById(String courseCode) {
        return courseRepo.findById(courseCode);
    }

    @Override
    public Result<Void> assignInstructor(String sectionId, String instructorId) {
        var secOpt = sectionRepo.findById(sectionId);
        var instructorOpt = instructorRepo.findById(instructorId);
        if (secOpt.isEmpty()) return Result.fail("Section not found");

        if (instructorOpt.isEmpty()) return Result.fail("Instructor not found");

        Section section = secOpt.get();
        section.setInstructor(instructorOpt.get());
        sectionRepo.save(section);
        return Result.ok(null);
    }

    @Override
    public List<Course> listCourses() {
        return courseRepo.findAll();
    }

    @Override
    public List<Section> listSectionsByCourse(String courseCode) {
        return sectionRepo.findAll().stream().filter(section -> section.getCourse().getCode().equals(courseCode)).toList();
    }

    @Override
    public List<Section> getSectionsForInstructor(String instructorId) {
        return sectionRepo.findAll().stream().filter(section -> section.getInstructor().getId().equals(instructorId)).toList();
    }
}
