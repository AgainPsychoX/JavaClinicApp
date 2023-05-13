package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;


@Entity
@Table(name = "doctors")
@NamedQueries({
        @NamedQuery(name = "doctors.fromUser", query = "SELECT doctor FROM Doctor doctor WHERE doctor.databaseUsername = :uname")
})
public class Doctor extends Patient {
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "speciality_id", referencedColumnName = "id", nullable = false)
    public DoctorSpecialty speciality;

    public DoctorSpecialty getSpeciality() {
        return speciality;
    }
    public void setSpeciality(DoctorSpecialty speciality) {
        this.speciality = speciality;
    }

    static public Doctor getDoctor(String uname){
        return ClinicApplication.getEntityManager().createNamedQuery("doctors.fromUser", Doctor.class).setParameter("uname", uname).getSingleResult();

    }

    @Embedded
    private WeeklyTimetable weeklyTimetable;
    public WeeklyTimetable getWeeklyTimetable() {
        return weeklyTimetable;
    }

    // TODO: get doctor schedule
}
