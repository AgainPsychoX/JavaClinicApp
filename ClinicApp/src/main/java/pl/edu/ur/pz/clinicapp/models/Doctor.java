package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.utils.DurationMinutesConverter;

import javax.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "doctors")
@NamedQueries({
        @NamedQuery(name = "doctors",  query = "FROM Doctor d LEFT JOIN FETCH d.user")
})
public class Doctor {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Doctors are users
     */

    @Id
    private Integer id;

    @OneToOne(optional = false, orphanRemoval = true,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
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
