package utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import model.Course;


@Getter
@Builder
@AllArgsConstructor
public class CourseQuery {
    private String code;
    private String title;
    private Integer minCredits;
    private Integer maxCredits;
    private String instructorId;

    public boolean matches(Course course) {
        if (code != null && !course.getCode().equalsIgnoreCase(code)) return false;
        if (title != null && !course.getTitle().toLowerCase().contains(title.toLowerCase())) return false;
        if (minCredits != null && course.getCredits() < minCredits) return false;
        if (maxCredits != null && course.getCredits() > maxCredits) return false;
        return true;
    }
}
