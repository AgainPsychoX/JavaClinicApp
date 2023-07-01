package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.utils.DurationMinutesConverter;

import javax.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "doctors")
@NamedQueries({
        @NamedQuery(name = "doctors",  query = "FROM Doctor d LEFT JOIN FETCH d.user")
})
public class Doctor implements UserReference {
    // Empty constructor is required for JPA standard.
    private Doctor() {}


    public Doctor(User user) {
        this.id = user.getId();
        this.user = user;
        this.name = user.getName();
        this.surname = user.getSurname();
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Doctors are users
     */

    @Id
    private Integer id;
    @Override
    public Integer getId() {
        return id;
    }

    @OneToOne(optional = false, orphanRemoval = true,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @MapsId // same ID as user
    @JoinColumn(name = "id")
    private User user;
    public User asUser() {
        return user;
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * General object operators
     */

    @Override
    public String toString() {
        return String.format("Doctor{id=%d,name=%s,surname=%s,speciality=%s}",
                id, getName(), getSurname(), getSpeciality());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Doctor that) {
            return getId() != null && getId().equals(that.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : super.hashCode();
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

    @Override
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
    private Duration defaultVisitDuration = Duration.ofMinutes(30);
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
    private int maxDaysInAdvance = 60;
    public int getMaxDaysInAdvance() {
        return maxDaysInAdvance;
    }
    public void setMaxDaysInAdvance(int maxDaysInAdvance) {
        this.maxDaysInAdvance = maxDaysInAdvance;
    }
}
