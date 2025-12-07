package service.impl;

import model.Course;
import model.Section;
import repository.Repository;
import service.CatalogService;

import java.util.List;
import java.util.Optional;

public class CatalogServiceImpl implements CatalogService {

    private final Repository<Course, String> courseRepo;
    private final Repository<Section, String> sectionRepo;

    public CatalogServiceImpl(Repository<Course, String> courseRepo,
                              Repository<Section, String> sectionRepo) {
        this.courseRepo = courseRepo;
        this.sectionRepo = sectionRepo;
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
