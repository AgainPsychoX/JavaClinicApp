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



    /* * * * * * * * * * * * * * * * * * * * *
     * Mocks for testing and development
     */

    public static Doctor MockDoctor = new Doctor() {{
        setRole(Role.DOCTOR);
        setEmail("k.malogrodzka@example.com");
        setName("Katarzyna");
        setSurname("Małogrodzka-Sylwester");
    }};
}
