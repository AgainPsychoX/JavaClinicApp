package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Doctors are users
     */

    @Id
    private Integer id;

    @OneToOne(optional = false, orphanRemoval = true, cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @MapsId // same ID as user
    @JoinColumn(name = "id")
    private User user;
    public User asUser() {
        return user;
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Doctor data
     *
     * Some data is redundant in doctors table in order to properly
     * separate permissions. Alternative would be using custom SQL
     * views and queries - more complicated.
     *
     * We assume doctor e-mail & phone as user (users table) is private,
     * not to be read by any user (like patients), but in future
     * we could add functionality to have business e-mail 7 phone too.
     */

    @Column(nullable = false, length = 40)
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        asUser().setName(name);
        this.name = name;
    }

    @Column(nullable = false, length = 40)
    private String surname;
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        asUser().setSurname(surname);
        this.surname = name;
    }

    public String getDisplayName() {
        if (name != null) {
            if (surname != null)
                return name + " " + surname;
            else
                return name;
        }
        return null;
    }

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
