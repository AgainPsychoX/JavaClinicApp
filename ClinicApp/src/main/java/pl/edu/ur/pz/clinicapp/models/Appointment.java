package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
        // TODO: use Hibernate `persist` (or `merge`) to insert/update and `remove` to delete
        @NamedNativeQuery(
                name = "editAppointment",
                query = "UPDATE appointments SET date = :date",
                resultClass = Appointment.class
        ),
        @NamedNativeQuery(
                name = "deleteAppointment",
                query = "DELETE FROM appointments WHERE id =:id",
                resultClass = Appointment.class
        )
})
public class Appointment extends MedicalHistoryEntry implements Schedule.Entry {
    /**
     * Queries and returns all appointments for given user in given range of time.
     * Note: Might return partial data (logged-in user permissions might limit viewing other users' data).
     * @param userReference Reference to user (user, patient or doctor).
     * @param from Begin timestamp of the time range (inclusive).
     * @param to End timestamp of the time range (inclusive).
     * @return List of the appointments.
     */
    public static List<Appointment> forUserBetweenDates(UserReference userReference, Instant from, Instant to) {
        final var query = ClinicApplication.getEntityManager()
                .createNamedQuery("Appointments.forUser.betweenDates", Appointment.class);
        query.setParameter("user_id", userReference.getId());
        query.setParameter("from", from);
        query.setParameter("to", to);
        return query.getResultList();
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
    private Duration duration;
    @Override
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Instant getBeginTime() {
        return getDate();
    }
    @Override
    public Instant getEndTime() {
        return getDate().plus(getDuration());
    }

    @Override
    public Type getType() {
        return Schedule.Entry.Type.APPOINTMENT;
    }

    @Override
    public boolean doesCrossDays() {
        return false;
    }
}
