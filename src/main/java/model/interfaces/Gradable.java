package model.interfaces;

import model.Grade;
import model.Section;
import utils.Result;

public interface Gradable {
    Result<Void> postGrade(Section section, Grade grade);
}
