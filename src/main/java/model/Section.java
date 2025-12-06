package model;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class Section {
    @NonNull private String id;
    @NonNull private Course course;
    @NonNull private String term;

    private Instructor instructor;
    private int capacity;

    private List<TimeSlot> meetingTimes = new ArrayList<>();
    private List<Enrollment> roster = new ArrayList<>();


    public Section(String id, Course course, String term, int capacity, Instructor instructor) {
        this.id = id;
        this.course = course;
        this.term = term;
        this.capacity = capacity;
        this.instructor = instructor;
    }

    public int getRemainingSeats() {
        return capacity - roster.size();
    }
}
