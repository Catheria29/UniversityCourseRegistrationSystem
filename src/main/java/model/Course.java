package model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import model.interfaces.Searchable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Course implements Searchable {
    @NonNull
    private String code;
    @NonNull
    private String title;
    private int credits;

    private List<String> prerequisites = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();

    public Course(@NonNull String code, @NonNull String title, int credits, List<String> prerequisites) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        if (prerequisites != null) this.prerequisites = new ArrayList<>(prerequisites);
    }

    public Course(@NonNull String code, @NonNull String title, int credits) {
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    @Override
    public boolean matches(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return code.toLowerCase().contains(q) || title.toLowerCase().contains(q);
    }
}

