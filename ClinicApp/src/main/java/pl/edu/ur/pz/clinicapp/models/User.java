package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.getCurrent", query = "FROM User u WHERE u.databaseUsername = FUNCTION('CURRENT_USER')"),
        @NamedQuery(name = "User.getByLogin", query = "FROM User u WHERE u.databaseUsername = FUNCTION('get_user_internal_name', :input)"),
        @NamedQuery(name = "User.byRole", query = "FROM User u WHERE u.role = :role"),
        @NamedQuery(name = "User.clearTimetables", query = "DELETE FROM Timetable t WHERE t.user.id = :id"),
        // TODO: clean up queries (again)
        @NamedQuery(name = "allWorkers", query = "FROM User WHERE role = 'RECEPTION' OR role ='NURSE' or role = 'ADMIN'")
        @NamedQuery(name = "allUsers", query = "FROM User"),
        @NamedQuery(name = "allDoctors", query = "FROM User WHERE role = 'DOCTOR'"),
        @NamedQuery(name = "allPatients", query = "FROM User WHERE role = 'PATIENT'"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "login", query = "SELECT get_user_internal_name(:input) AS internal_name"),
        @NamedNativeQuery(name = "changePassword", query = "CALL change_password(:uname, :passwd)"),
        // TODO: clean up queries (again)
//        @NamedNativeQuery(name = "createUser", query = "INSERT INTO users "
//                +"(internal_name, email, name, phone, role, surname) VALUES "
//                +"(:internalName, :email, :name, :phone, :role, :surname)",
//                resultClass = User.class),
        @NamedNativeQuery(name = "createUser", query = "SELECT 1 FROM create_user(:internalName, :email, :name, :phone, :role, :surname)"),
//        @NamedNativeQuery(name = "createDatabaseUser", query = "CREATE USER :userName LOGIN ENCRYPTED "
//                +"PASSWORD :password IN ROLE gp_patients",
//                resultClass = User.class),
        @NamedNativeQuery(name = "createDatabaseUser", query = "SELECT 1 FROM create_database_user(:userName, :password, :role)"),
        @NamedNativeQuery(name = "findDatabaseUser", query = "SELECT FROM pg_catalog.pg_roles WHERE rolname = :rolname",
                resultClass = User.class),
})
public class User implements UserReference {
    @Override
    public User asUser() {
        return this;
    }

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

        public String localizedName() {
            return switch (this) {
                case ANONYMOUS  -> "Niezalogowany";
                case PATIENT    -> "Pacjent";
                case RECEPTION  -> "Recepcja";
                case NURSE      -> "Pielęgniarka";
                case DOCTOR     -> "Lekarz";
                case ADMIN      -> "Administrator";
            };
        }
    }

    @Id
    @Access(AccessType.PROPERTY)
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Override
    public Integer getId() {
        return id;
    }
    @SuppressWarnings("unused") // required by @Access(AccessType.PROPERTY)
    protected void setId(Integer id) {
        this.id = id;
    }

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

    /**
     * Prepares query to select all user by given role.
     * @param role Role of users to query.
     * @return Query for the users of given role.
     */
    static public TypedQuery<User> queryByRole(Role role) {
        final var em = ClinicApplication.getEntityManager();
        final var query = em.createNamedQuery("User.byRole", User.class);
        query.setParameter("role", role);
        return query;
    }

    @Column(nullable = true, length = 64, unique = true)
    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = (email == null) ? null : email.toLowerCase();
    }

    @Column(nullable = true, length = 12)
    private String phone;
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone.replaceAll("\\s", "");
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



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Users have database accounts
     */

    /**
     * Special value for database username, to mark for insert trigger to have the database username generated.
     */
    final private static String DATABASE_USERNAME_AUTOGENERATE = "!";

    @Column(name = "internal_name", nullable = false, length = 40, unique = true)
    @GeneratedValue()
    private String databaseUsername = DATABASE_USERNAME_AUTOGENERATE;
    public String getDatabaseUsername() {
        if (databaseUsername.equals(DATABASE_USERNAME_AUTOGENERATE)) {
            final var em = ClinicApplication.getEntityManager();
            if (em.contains(this)) {
                em.flush();
                em.refresh(this);
            }
        }
        return databaseUsername;
    }
    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    /**
     * Used in anonymous database session to get internal database username, to connect as given user.
     * @param emailOrPESEL login input string, email or PESEL identifying the user
     * @return actual database username, or similarly looking random one (to avoid users enumeration).
     */
    static public String getDatabaseUsernameForInput(String emailOrPESEL) {
        final var em = ClinicApplication.getEntityManager();
        final var query = em.createNamedQuery("login");
        query.setParameter("input", emailOrPESEL.toLowerCase());
        return (String) query.getSingleResult();
    }

    /**
     * Finds entity of user that currently connected (whose privileges are effective in this session).
     * Prefer using {@link ClinicApplication#getUser()} to check for logged-in user,
     * or even better: {@link ClinicApplication#requireUser()} in places where user must be logged-in.
     * @return Entity of the user currently connected.
     */
    static public User getCurrentFromConnection() {
        return ClinicApplication.getEntityManager().createNamedQuery("User.getCurrent", User.class).getSingleResult();
    }

    /**
     * Finds entity of user with given login string (email or PESEL).
     * @param emailOrPESEL login string
     * @return entity of found user
     */
    static public User getByLogin(String emailOrPESEL) {
        final var em = ClinicApplication.getEntityManager();
        final var query = em.createNamedQuery("User.getByLogin", User.class);
        query.setParameter("input", emailOrPESEL.toLowerCase());
        return query.getSingleResult();
    }

    /**
     * Sets new password for the user.
     * @param newPassword new password to use
     */
    public void changePassword(String newPassword) {
        final var em = ClinicApplication.getEntityManager();
        final var query = em.createNamedQuery("changePassword");
        query.setParameter("uname", getDatabaseUsername());
        query.setParameter("passwd", newPassword);
        query.executeUpdate();
    }

    // TODO: allow changing other users (patients) passwords by doctor/reception (see security.sql change_password)



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * General object operators
     */

    @Override
    public String toString() {
        return String.format("User{id=%d,role=%s,name=%s,surname=%s,email=%s,internal=%s}",
                id, role.toString(), name, surname, email, databaseUsername);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof User that) {
            return getId() != null && getId().equals(that.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : super.hashCode();
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



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Other users related entities
     */

    @OneToMany(mappedBy = "sourceUser", fetch = FetchType.LAZY)
    @OrderBy("sentDate DESC")
    private List<Notification> allSentNotifications;
    public List<Notification> getAllSentNotifications() {
        return allSentNotifications;
    }

    @OneToMany(mappedBy = "destinationUser", fetch = FetchType.LAZY)
    @OrderBy("sentDate DESC")
    private List<Notification> allReceivedNotifications;
    public List<Notification> getAllReceivedNotifications() {
        return allReceivedNotifications;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("effective_date")
    private List<Timetable> timetables = new ArrayList<>(7);

    /**
     * Provides access to timetables for given user. Upon persisting/merging,
     * the list will be persisted/merged as well, incl. deletion of removed elements.
     *
     * @return List of all timetables objects related to the user
     * in natural order (the oldest first).
     */
    public List<Timetable> getTimetables() {
        // TODO: custom PersistentCollection (to avoid add/remove/clear-Timetable in User)
        //  and custom CollectionPersister (to avoid N+1 more properly somehow)...
        if (!Hibernate.isInitialized(timetables)) {
            Timetable.forUser(this); // to prefetch both timetables and their entries avoiding N+1
        }
        return timetables;
    }

    /**
     * Adds specified timetable to the user, persisting it the database.
     *
     * @param timetable timetable to add
     */
    public void addTimetable(Timetable timetable) {
        final var entityManager = ClinicApplication.getEntityManager();
        timetable.setUser(this);
        if (Hibernate.isInitialized(timetables)) {
            timetables.add(timetable);
            if (!entityManager.contains(timetable)) {
                entityManager.persist(timetable);
            }
        }
        else {
            entityManager.persist(timetable);
        }
    }

    /**
     * Removes specified timetable to the user, removing it from the database too.
     *
     * If timetable is not related to the user, nothing happens (not even removed).
     *
     * @param timetable timetable to remove
     */
    public void removeTimetable(Timetable timetable) {
        final var entityManager = ClinicApplication.getEntityManager();
        if (Hibernate.isInitialized(timetables)) {
            timetables.remove(timetable);
            if (entityManager.contains(timetable)) {
                entityManager.remove(timetable);
            }
        }
        else {
            entityManager.remove(timetable);
        }
        if (timetable.getUser() != this) {
            return; // not our concern
        }
        timetable.setUser(null); // just in case
    }

    /**
     * Removes all timetables, removing it from the database too.
     */
    public void clearTimetables() {
        if (Hibernate.isInitialized(timetables)) {
            for (final var timetable : timetables) {
                timetable.setUser(null); // just in case
            }
            timetables.clear();
        }
        else {
            final var query = ClinicApplication.getEntityManager().createNamedQuery("User.clearTimetables");
            query.setParameter("id", id);
            query.executeUpdate();
        }
    }
}
