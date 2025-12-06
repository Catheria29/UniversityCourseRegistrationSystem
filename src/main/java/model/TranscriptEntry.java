package model;

import lombok.Data;
import lombok.NonNull;

@Data
public class TranscriptEntry {
    @NonNull private Section section;
    private int credits;
    private Grade grade;

    public TranscriptEntry(Section section, Grade grade) {
        this.section = section;
        this.grade = grade;
    }
}
