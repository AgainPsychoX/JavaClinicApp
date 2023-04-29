package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor extends Patient {
    @Column(nullable = true)
    public String speciality;
    public String getSpeciality() {
        return speciality;
    }
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Column(nullable = false, columnDefinition = "int default 60")
    public int defaultVisitTime;
    public int getDefaultVisitTime() {
        return defaultVisitTime;
    }
    public void setDefaultVisitTime(int defaultVisitTime) {
        this.defaultVisitTime = defaultVisitTime;
    }

    // TODO: get doctor schedule
}
