package service.impl;

import model.*;
import repository.*;
import service.RegistrationService;
import service.validators.CapacityValidator;
import service.validators.PrerequisiteValidator;
import service.validators.ScheduleConflictChecker;
import utils.PagedList;
import utils.Result;

import java.util.List;
import java.util.stream.Collectors;

public class RegistrationServiceImpl implements RegistrationService {

    private final Repository<Student, String> studentRepo;
    private final Repository<Section, String> sectionRepo;
    private final Repository<Enrollment, String> enrollmentRepo;

    private final CapacityValidator capacityValidator;
    private final PrerequisiteValidator prerequisiteValidator;
    private final ScheduleConflictChecker conflictChecker;

    public RegistrationServiceImpl(Repository<Student, String> studentRepo,
                                   Repository<Section, String> sectionRepo,
                                   Repository<Enrollment, String> enrollmentRepo,
                                   CapacityValidator capacityValidator,
                                   PrerequisiteValidator prerequisiteValidator,
                                   ScheduleConflictChecker conflictChecker) {
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.capacityValidator = capacityValidator;
        this.prerequisiteValidator = prerequisiteValidator;
        this.conflictChecker = conflictChecker;
    }

    @Override
    public Result<Enrollment> enroll(String studentId, String sectionId) {
        var studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found");
        var sectionOpt = sectionRepo.findById(sectionId);
        if (sectionOpt.isEmpty()) return Result.fail("Section not found");

        Student student = studentOpt.get();
        Section section = sectionOpt.get();


        var result = student.enroll(
                section,
                enrollmentRepo,
                capacityValidator,
                prerequisiteValidator,
                conflictChecker
        );

        if (result.isOk()) {
            section.getRoster().add(result.getValue());
            sectionRepo.save(section);
        }

        return result;
    }

    @Override
    public Result<Void> drop(String studentId, String sectionId) {
        var studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found.");
        var sectionOpt = sectionRepo.findById(sectionId);
        if (sectionOpt.isEmpty()) return Result.fail("Section not found");

        Student student = studentOpt.get();
        Section section = sectionOpt.get();

        // Delegate drop logic to Student
        Result<Void> result = student.drop(section, enrollmentRepo);

        if (result.isOk()) {
            studentRepo.save(student);
        }

        return result;
    }

    @Override
    public PagedList<Enrollment> listSchedule(String studentId, String term, int page, int pageSize) {
        var studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) return new PagedList<>(List.of(), page, pageSize, 0);

        List<Enrollment> termEnrollments = studentOpt.get().getCurrentEnrollments()
                .stream()
                .filter(e -> e.getSection().getTerm().equals(term))
                .collect(Collectors.toList());

        return new PagedList<>(termEnrollments, page, pageSize, termEnrollments.size());
    }
}
