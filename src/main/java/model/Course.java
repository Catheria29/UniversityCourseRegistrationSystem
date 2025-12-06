package model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Course {
    @NonNull private String code;
    @NonNull private String title;
    private int credits;

    private List<String> prerequisites = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();

    public Course(String code, String title, int credits, List<String> prerequisites) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        if (prerequisites != null) this.prerequisites = new ArrayList<>(prerequisites);
    }

    public Course(String code, String title, int credits) {
        this.code = code;
        this.title = title;
        this.credits = credits;
    }


}

