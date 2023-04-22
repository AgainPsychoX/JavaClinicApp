package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor extends User {
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "speciality_id", referencedColumnName = "id", nullable = false)
    public DoctorSpecialty speciality;
    public DoctorSpecialty getSpeciality() {
        return speciality;
    }
    public void setSpeciality(DoctorSpecialty speciality) {
        this.speciality = speciality;
    }

    @Embedded
    private WeeklyTimetable weeklyTimetable;
    public WeeklyTimetable getWeeklyTimetable() {
        return weeklyTimetable;
    }

    // TODO: get doctor schedule
}
