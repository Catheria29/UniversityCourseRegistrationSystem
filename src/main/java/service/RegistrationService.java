package service;

import model.Enrollment;
import utils.PagedList;
import utils.Result;

public interface RegistrationService {
    Result<Enrollment> enroll(String studentId, String sectionId);
    Result<Void> drop(String studentId, String sectionId);
    PagedList<Enrollment> listSchedule(String studentId, String term, int page, int pageSize);
}
