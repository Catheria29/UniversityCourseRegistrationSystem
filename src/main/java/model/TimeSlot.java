package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
public class TimeSlot implements Comparable<TimeSlot> {
    @NonNull private DayOfWeek day;
    @NonNull private LocalTime startTime;
    @NonNull private LocalTime endTime;
    @NonNull private String room;


    @Override
    public int compareTo(TimeSlot o) {
        int cmp = day.compareTo(o.day);
        if (cmp != 0) return cmp;
        return startTime.compareTo(o.startTime);
    }

    public boolean hasOverlap(TimeSlot other) {
        if (!this.day.equals(other.day)) return false;
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }
}
