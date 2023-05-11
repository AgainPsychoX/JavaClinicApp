package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "users.current", query = "FROM User u WHERE u.databaseUsername = FUNCTION('CURRENT_USER')"),
        @NamedQuery(name = "users.get_by_login", query = "FROM User u WHERE u.databaseUsername = FUNCTION('get_user_internal_name', :input)"),
        @NamedQuery(name = "User.clearTimetables", query = "DELETE FROM Timetable t WHERE t.user.id = :id")
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "login", query = "SELECT get_user_internal_name(:input) AS internal_name"),
})
public final class User {
    public enum Role {
        ANONYMOUS,
        // TODO: rethink role field (as users can be both patients & doctors at the same time, no?
        PATIENT,
        RECEPTION,
        NURSE,
        DOCTOR,
        ADMIN;

        public boolean isGroupUser() {
            return this == RECEPTION || this == NURSE;
        }

        public String toString() {
            // TODO: when we have localization it will look much nicer
            if (this == PATIENT)   return "Pacjent";
            if (this == RECEPTION) return "Recepcja";
            if (this == NURSE)     return "Pielęgniarka";
            if (this == DOCTOR)    return "Lekarz";
            if (this == ADMIN)     return "Administrator";
            return this.name();
        }
    }

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "user_role") // custom enum type
    @Type(type = "postgresql_enum")
    private Role role;
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    @Column(nullable = true, length = 64, unique = true)
    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    @Column(nullable = true, length = 12)
    private String phone;
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(nullable = false, length = 40)
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = 40)
    private String surname;
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
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

    @Column(name = "internal_name", nullable = false, length = 40, unique = true)
    private String databaseUsername;
    public String getDatabaseUsername() {
        return databaseUsername;
    }

    static public String getDatabaseUsernameForInput(String emailOrPESEL) {
        final var em = ClinicApplication.getEntityManager();
        final var query = em.createNamedQuery("login");
        query.setParameter("input", emailOrPESEL.toLowerCase());
        return (String) query.getSingleResult();
    }



    static public User getCurrent() {
        return ClinicApplication.getEntityManager().createNamedQuery("users.current", User.class).getSingleResult();
    }

    static public User getByLogin(String emailOrPESEL) {
        final var em = ClinicApplication.getEntityManager();
        final var query = em.createNamedQuery("users.get_by_login", User.class);
        query.setParameter("input", emailOrPESEL.toLowerCase());
        return query.getSingleResult();
    }



    @Override
    public String toString() {
        return String.format("User{role=%s,name=%s,surname=%s,email=%s,internal=%s}",
                role.toString(), name, surname, email, databaseUsername);
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Users might be patients
     *
     * Why not use inheritance? Hibernate handles it *not-that-well*,
     * requiring large tables or unnecessarily joining tables for each query,
     * and on top of that, it seems impossible to user SQL permissions
     * and rules/policies system without custom queries... So, we simplify
     * everything by having some one-to-one relations and proxy accessors.
     *
     * Problems when using parent-side association with lazy loading,
     * causing N+1 queries problem. Two solutions:
     * 1) compile time instrumentation, or
     * 2) a bit stupid (but working) find on demand.
     * See {@link https://stackoverflow.com/questions/1444227/how-can-i-make-a-jpa-onetoone-relation-lazy}.
     */

    @Transient
    private Patient patient;

    /**
     * Provides access to the patient details of the user.
     * @return patient model or null if the user isn't a patient
     */
    public Patient asPatient() {
        if (patient == null) {
            patient = ClinicApplication.getEntityManager().find(Patient.class, id);
        }
        return patient;
    }

    /**
     * Checks whenever the user is a patient.
     * @return true if the user is a patient, false otherwise.
     */
    public boolean isPatient() {
        return asPatient() != null;
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Users might be doctors
     */

    @Transient
    private Doctor doctor;

    /**
     * Provides access to the doctor details of the user.
     * @return doctor model or null if the user isn't a doctor
     */
    public Doctor asDoctor() {
        if (doctor == null) {
            doctor = ClinicApplication.getEntityManager().find(Doctor.class, id);
        }
        return doctor;
    }

    /**
     * Checks whenever the user is a doctor.
     * @return true if the user is a doctor, false otherwise.
     */
    public boolean isDoctor() {
        return asDoctor() != null;
    }



    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @OrderBy("effective_date DESC")
    private Collection<Timetable> timetables;

    /**
     * Provides access to timetables for given user.
     *
     * @return Collection of all timetables objects related to the user,
     * the latest effective date first as unmodifiable collection.
     */
    public Collection<Timetable> getTimetables() {
        // TODO: use custom AbstractPersistentCollection to avoid get/set/clearXyz etc. in owner class...?
        return Collections.unmodifiableCollection(timetables);
    }

    /**
     * Adds specified timetable to the user, persisting it the database.
     *
     * @param timetable timetable to add
     */
    public void addTimetable(Timetable timetable) {
        if (Hibernate.isInitialized(timetables)) {
            timetables.add(timetable);
        }
        timetable.setUser(this);
        ClinicApplication.getEntityManager().persist(timetable);
    }

    /**
     * Removes specified timetable to the user, removing it from the database too.
     *
     * If timetable is not related to the user, nothing happens (not even removed).
     *
     * @param timetable timetable to remove
     */
    public void removeTimetable(Timetable timetable) {
        if (Hibernate.isInitialized(timetables)) {
            timetables.remove(timetable);
        }
        if (timetable.getUser() != this) {
            return; // not our concern
        }
        ClinicApplication.getEntityManager().remove(timetable);
        timetable.setUser(null); // just in case
    }

    /**
     * Removes all timetables, removing it from the database too.
     */
    public void clearTimetables() {
        final var query = ClinicApplication.getEntityManager().createNamedQuery("User.clearTimetables");
        query.setParameter("id", id);
        query.executeUpdate();
        if (Hibernate.isInitialized(timetables)) {
            for (final var timetable : timetables) {
                timetable.setUser(null); // just in case
            }
            timetables.clear();
        }
    }



    public Schedule getSchedule() {
        return Schedule.of(this);
    }
}
