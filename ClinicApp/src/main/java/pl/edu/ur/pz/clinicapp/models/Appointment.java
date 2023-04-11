package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Model representing patient registered visit to clinic to see selected doctor.
 */
@Entity
@Table(name = "appointments")
public class Appointment extends MedicalHistoryEntry {
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
