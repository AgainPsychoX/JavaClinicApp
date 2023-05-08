package pl.edu.ur.pz.clinicapp.models;

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
})
public class Appointment extends MedicalHistoryEntry implements Schedule.Entry {
    /**
     * Doctor who will receive the patient.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
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
