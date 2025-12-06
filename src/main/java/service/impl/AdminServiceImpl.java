package service.impl;

import model.*;
import repository.*;
import service.AdminService;
import model.AdminActionLog;
import utils.Result;

import java.util.List;
import java.util.UUID;

public class AdminServiceImpl implements AdminService {

    private final Repository<Course, String> courseRepo;
    private final Repository<Section, String> sectionRepo;
    private final Repository<Instructor, String> instructorRepo;
    private final Repository<AdminActionLog, String> logRepo;

    private final String adminId;  // the admin performing the actions

    public AdminServiceImpl(Repository<Course, String> courseRepo,
                            Repository<Section, String> sectionRepo,
                            Repository<Instructor, String> instructorRepo,
                            Repository<AdminActionLog, String> logRepo,
                            String adminId) {
        this.courseRepo = courseRepo;
        this.sectionRepo = sectionRepo;
        this.instructorRepo = instructorRepo;
        this.logRepo = logRepo;
        this.adminId = adminId;
    }

    @Override
    public Result<Course> createCourse(Course course) {
        if (courseRepo.findById(course.getCode()).isPresent()) {
            return Result.fail("Course already exists: " + course.getCode());
        }

        courseRepo.save(course);
        log("CREATE_COURSE", "Created course " + course.getCode(), null);
        return Result.ok(course);
    }

    @Override
    public Result<Section> createSection(Section section) {
        if (sectionRepo.findById(section.getId()).isPresent()) {
            return Result.fail("Section already exists: " + section.getId());
        }

        if (courseRepo.findById(section.getCourse().getCode()).isEmpty()) {
            return Result.fail("Course not found: " + section.getCourse().getCode());
        }

        sectionRepo.save(section);
        log("CREATE_SECTION", "Created section " + section.getId(), null);
        return Result.ok(section);
    }

    @Override
    public Result<Void> assignInstructor(String sectionId, String instructorId) {
        var sectionOpt = sectionRepo.findById(sectionId);
        if (sectionOpt.isEmpty()) return Result.fail("Section not found");

        var instOpt = instructorRepo.findById(instructorId);
        if (instOpt.isEmpty()) return Result.fail("Instructor not found");

        Section section = sectionOpt.get();
        Instructor instructor = instOpt.get();

        section.setInstructor(instructor);
        sectionRepo.save(section);

        log("ASSIGN_INSTRUCTOR",
                "Assigned instructor " + instructorId + " to section " + sectionId,
                null);

        return Result.ok(null);
    }

    @Override
    public Result<Void> overrideCapacity(String sectionId, int newCapacity, String reason) {
        var sectionOpt = sectionRepo.findById(sectionId);
        if (sectionOpt.isEmpty()) return Result.fail("Section not found");

        Section section = sectionOpt.get();
        int oldCapacity = section.getCapacity();

        section.setCapacity(newCapacity);
        sectionRepo.save(section);

        log("OVERRIDE_CAPACITY",
                "Capacity override " + oldCapacity + " → " + newCapacity + " for section " + sectionId,
                reason);

        return Result.ok(null);
    }

    @Override
    public Result<Void> overridePrerequisite(String studentId, String courseCode, String reason) {
        // The validator will interpret this later
        log("PREREQ_OVERRIDE",
                "Prerequisite override for student " + studentId + " on course " + courseCode,
                reason);
        return Result.ok(null);
    }

    @Override
    public List<AdminActionLog> getLogs() {
        return logRepo.findAll();
    }

    private void log(String actionType, String action, String reason) {
        AdminActionLog log = new AdminActionLog(
                UUID.randomUUID().toString(),
                adminId,
                action,
                actionType,
                reason
        );

        logRepo.save(log);
    }
}
