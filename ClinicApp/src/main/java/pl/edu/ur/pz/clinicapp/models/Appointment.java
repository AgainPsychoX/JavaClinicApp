package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Model representing patient registered visit to clinic to see selected doctor.
 */
@Entity
@Table(name = "appointments")
@NamedQueries({
        @NamedQuery(name = "appointments",  query = "FROM Appointment"),
        @NamedQuery(name = "appointmentsDoctor",  query = "FROM Appointment WHERE doctor = :doctor"),
        @NamedQuery(
                name = "allAppointmentsForDoctor",
                query = "FROM Appointment WHERE doctor = :doctor AND id != :id"
        )
})
@NamedNativeQueries({
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

public class Appointment extends MedicalHistoryEntry {
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
    private Timestamp date;
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp date) {
        this.date = date;
    }

    /**
     * Expected duration of the visit.
     */
    @Column(nullable = false)
    private int duration;
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
