package model.interfaces;

import model.TimeSlot;

import java.util.List;

public interface Schedulable {
    List<TimeSlot> getMeetingTimes();
}
