package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;

/**
 * Model representing patient registered visit to clinic to see selected doctor.
 */
@Entity
@Table(name = "appointments")
@NamedQueries({
        /* Patient */
        @NamedQuery(name = "appointments_as_patient",
                query = "FROM Appointment a WHERE a.patient = :patient"),
        @NamedQuery(name = "appointments_as_patient_from_date",
                query = "FROM Appointment a WHERE a.patient = :patient AND :date <= a.date"),
        @NamedQuery(name = "appointments_as_patient_between_dates",
                query = "FROM Appointment a WHERE a.patient = :patient AND a.date BETWEEN :from AND :to"),
        /* Doctor */
        @NamedQuery(name = "appointments_as_doctor_between_dates",
                query = "FROM Appointment a WHERE a.doctor = :doctor AND a.date BETWEEN :from AND :to"),
        /* Any */
        @NamedQuery(name = "appointments_as_user_between_dates",
                query = "FROM Appointment a WHERE (a.doctor = :user OR a.patient = :user) AND a.date BETWEEN :from AND :to")
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
     * Doctor who will receive the patient.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
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
}
