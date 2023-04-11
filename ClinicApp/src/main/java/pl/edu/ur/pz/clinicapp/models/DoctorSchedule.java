package pl.edu.ur.pz.clinicapp.models;

public class DoctorSchedule extends Schedule {
    public static DoctorSchedule of(Doctor doctor) {
        final var instance = new DoctorSchedule();
        instance.user = doctor;
        // TODO: prefetch?
        return instance;
    }

    // TODO: SQL to check availability on given date (based on schedule entries and timetable)
    // TODO: return availability for dates range (based on schedule entries and timetable)
}
