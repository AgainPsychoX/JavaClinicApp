package pl.edu.ur.pz.clinicapp.models;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Utility class that eases manipulation of doctor schedule (including timetables).
 */
public class DoctorSchedule extends Schedule {
    protected Doctor getDoctor() {
        return (Doctor) user;
    }

    protected DoctorSchedule() {}

    public static DoctorSchedule of(Doctor doctor) {
        final var instance = new DoctorSchedule();
        instance.user = doctor;
        // TODO: prefetch?
        return instance;
    }

    /**
     * Checks for avaliable time in doctor schedule.
     * @param beginTime Begin time of the potential slot in the schedule.
     * @param duration Requested duration.
     * @return True if there is avaliable time, false otherwise.
     */
    public boolean checkFreeSlot(ZonedDateTime beginTime, Duration duration) {
        // TODO: call SQL?
        return false;
    }
}
