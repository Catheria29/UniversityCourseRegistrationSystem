package service;

import model.Grade;
import utils.Result;

public interface GradingService {
    Result<Void> postGrade(String instructorId, String sectionId, String studentId, Grade grade);

    Result<Double> computeGPA(String studentId);
}
