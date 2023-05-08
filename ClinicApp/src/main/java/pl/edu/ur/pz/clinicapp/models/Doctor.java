package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.utils.DurationMinutesConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Duration;

@Entity
@Table(name = "doctors")
public class Doctor extends User {
    @Column(nullable = true)
    private String speciality;
    public String getSpeciality() {
        return speciality;
    }
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Column(nullable = false, columnDefinition = "int default 30")
    @Convert(converter = DurationMinutesConverter.class)
    private Duration defaultVisitDuration;
    public Duration getDefaultVisitDuration() {
        return defaultVisitDuration;
    }
    public void setDefaultVisitDuration(Duration defaultVisitDuration) {
        this.defaultVisitDuration = defaultVisitDuration;
    }

    /**
     * Specifies how many days in advance can appointment be scheduled by patients & receptionist,
     * the doctor themselves can bypass this check.
     */
    @Column(nullable = false, columnDefinition = "int default 60")
    private int maxDaysInAdvance;
    public int getMaxDaysInAdvance() {
        return maxDaysInAdvance;
    }
    public void setMaxDaysInAdvance(int maxDaysInAdvance) {
        this.maxDaysInAdvance = maxDaysInAdvance;
    }
    
    // TODO: get doctor schedule
}
