package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.utils.DurationMinutesConverter;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Model representing patient registered visit to clinic to see selected doctor.
 */
@Entity
@Table(name = "appointments")
@NamedQueries({
        @NamedQuery(name = "Appointments.forPatient.betweenDates", query = """
                FROM Appointment a LEFT JOIN FETCH a.doctor
                WHERE a.patient = :patient AND a.date BETWEEN :from AND :to
                ORDER BY a.date
                """),
        @NamedQuery(name = "Appointments.forDoctor.betweenDates", query = """
                FROM Appointment a LEFT JOIN FETCH a.patient
                WHERE a.doctor = :doctor AND a.date BETWEEN :from AND :to
                ORDER BY a.date
                """),
        @NamedQuery(name = "Appointments.forUser.betweenDates", query = """
                FROM Appointment a LEFT JOIN FETCH a.doctor LEFT JOIN FETCH a.patient
                WHERE (a.doctor.id = :user_id OR a.patient.id = :user_id) AND a.date BETWEEN :from AND :to
                ORDER BY a.date
                """),
        /* Other */
        // TODO: check if required
        @NamedQuery(name = "appointments",  query = "FROM Appointment"),
        @NamedQuery(name = "appointmentsDoctor",  query = "FROM Appointment WHERE doctor = :doctor"),
        @NamedQuery(
                name = "allAppointmentsForDoctor",
                query = "FROM Appointment WHERE doctor = :doctor AND id != :id"
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "validate_new_appointment",
                query = "SELECT validate_new_appointment(:patient_id, :doctor_id, :begin\\:\\:timestamp, :duration) AS code",
                resultSetMapping = "validate_new_appointment"
        ),
        // TODO: use Hibernate `persist` (or `merge`) to insert/update and `remove` to delete
        @NamedNativeQuery(
                name = "editAppointment",
                query = "UPDATE appointments SET date = :date, notes = :notes WHERE id = :id",
                resultClass = Appointment.class
        ),
        @NamedNativeQuery(
                name = "deleteAppointment",
                query = "DELETE FROM appointments WHERE id =:id",
                resultClass = Appointment.class
        )
})
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "validate_new_appointment", columns = {
                @ColumnResult(name = "code", type = Integer.class),
        }),
})
public class Appointment extends MedicalHistoryEntry implements Schedule.Entry {
    /**
     * Prepares query to select all appointments for given user in given range of time.
     * Note: Might return partial data (logged-in user permissions might limit viewing other users' data).
     * @param userReference Reference to user (user, patient or doctor).
     * @param from Begin timestamp of the time range (inclusive).
     * @param to End timestamp of the time range (inclusive).
     * @return Query for the appointments  (in natural order by date).
     */
    public static TypedQuery<Appointment> queryForUserBetweenDates(UserReference userReference, Instant from, Instant to) {
        final var query = ClinicApplication.getEntityManager()
                .createNamedQuery("Appointments.forUser.betweenDates", Appointment.class);
        query.setParameter("user_id", userReference.getId());
        query.setParameter("from", from);
        query.setParameter("to", to);
        return query;
    }

    public enum NewAppointmentValidationStatus {
        GOOD, INVALID_DURATION, TOO_FAR_IN_ADVANCE, TIMETABLE_CROSSED, OUTSIDE_TIMETABLE, DOCTOR_BUSY
    }

    /**
     * Validates schedule-sensitive fields for potential new appointment.
     * @param patient related patient (if null, tries to validate patient-independently)
     * @param doctor related doctor
     * @param begin timestamp of beginning of the potential slot in the schedule
     * @param duration requested (expected) duration
     * @return 0 if good, non-zero error code otherwise.
     */
    public static NewAppointmentValidationStatus validateNewAppointment(
            Patient patient, Doctor doctor, Instant begin, Duration duration) {
        final var query = ClinicApplication.getEntityManager()
                .createNamedQuery("validate_new_appointment", Integer.class);
        query.setParameter("patient_id", patient == null ? 0 : patient.getId());
        query.setParameter("doctor_id", doctor.getId());
        query.setParameter("begin", begin);
        query.setParameter("duration", (int) duration.toMinutes());
        return NewAppointmentValidationStatus.values()[query.getSingleResult()];
    }



    /**
     * Doctor who will receive the patient.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", nullable = false)
    private Doctor doctor;
    public Doctor getDoctor() {
        return doctor;
    }
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Expected date of the visit.
     */
    @Column(nullable = false)
    private Instant date;
    public Instant getDate() {
        return date;
    }
    public void setDate(Instant date) {
        this.date = date;
    }

    /**
     * Expected duration of the visit in minutes.
     */
    @Column(nullable = false)
    @Convert(converter = DurationMinutesConverter.class)
    private Duration duration;
    @Override
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Instant getBeginInstant() {
        return getDate();
    }
    @Override
    public Instant getEndInstant() {
        return getDate().plus(getDuration());
    }

    @Override
    public Type getType() {
        return Schedule.Entry.Type.APPOINTMENT;
    }

    @Override
    public boolean doesCrossDays(ZoneId zone) {
        return false;
    }



    @Override
    public String toString() {
        return String.format("Appointment{id=%d,date=%s,duration=%s,patient_id=%d,doctor_id=%d}",
                getId(), getDate(), getDuration(), getPatient().getId(), getDoctor().getId());
    }
}
