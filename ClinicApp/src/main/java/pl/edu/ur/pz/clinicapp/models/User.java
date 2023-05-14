package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.annotations.Type;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "users.current", query = "SELECT user FROM User user WHERE user.databaseUsername = FUNCTION('CURRENT_USER')"),
        @NamedQuery(name = "users",  query = "FROM User"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "login", query = "SELECT get_user_internal_name(:input) AS internal_name"),
        @NamedNativeQuery(
                name = "findFilteredUsers",
                query = "SELECT u.id, u.name, u.surname, u.phone, u.email FROM users u WHERE CAST(u.role AS varchar) = :role",
                resultClass = User.class
        ),
        @NamedNativeQuery(
                name = "findAllUsers",
                query = "SELECT id, name, surname, phone, email FROM users",
                resultClass = User.class
        ),
        @NamedNativeQuery(name = "findDatabaseUser", query = "SELECT FROM pg_catalog.pg_roles WHERE rolname = :rolname",
                resultClass = User.class),

})
public class User {
    public enum Role {
        ANONYMOUS,
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
    public Integer getId() {
        return id;
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
    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }
    static public String getDatabaseUsernameForInput(String emailOrPESEL) {
        final var em = ClinicApplication.getEntityManager();
        Query query = em.createNamedQuery("login");
        query.setParameter("input", emailOrPESEL.toLowerCase());
        return (String) query.getSingleResult();
    }



    static public User getCurrent() {
        return ClinicApplication.getEntityManager().createNamedQuery("users.current", User.class).getSingleResult();
    }


}