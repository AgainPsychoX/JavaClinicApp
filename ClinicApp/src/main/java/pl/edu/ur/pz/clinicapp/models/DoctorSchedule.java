package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Utility class that eases manipulation of doctor schedule & timetables.
 */
public class DoctorSchedule extends Schedule {
    protected Doctor getDoctor() {
        if (userReference instanceof Doctor doctor)
            return doctor;

        return getUser().asDoctor();
    }

    protected DoctorSchedule() {}

    public static DoctorSchedule of(Doctor doctor) {
        final var instance = new DoctorSchedule();
        instance.userReference = doctor;
        // TODO: prefetch?
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Entry> findScheduleEntries(Instant from, Instant to) {
        // If it's owner viewing their schedule, they always have access to everything
        final var currentUser = ClinicApplication.getUser();
        if (currentUser != null && currentUser.getId().equals(userReference.getId())) {
            return super.findScheduleEntries(from, to);
        }

        /* Iterating over base busy entries filling with details if avaliable. Both vague and detailed entries
         * are already in sorted in natural order (by begin timestamp). We assume there cannot be multiple detailed
         * entries starting on the same time (but there can be the vague ones).
         */
        List<Entry> entries = getBusyEntries(from, to);
        List<Entry> details = super.findScheduleEntries(from, to);
        for (int i = 0, j = 0; i < entries.size(); i++) {
            final var vague = entries.get(i);
            final var detailed = details.get(j);
            if (vague.getBeginTime().equals(detailed.getBeginTime())
                    && vague.getEndTime().equals(detailed.getEndTime())
                    && vague.getType().equals(detailed.getType())) {
                entries.set(i, detailed);
            }
        }
        return entries;
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
