package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.StringNVarcharType;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.edu.ur.pz.clinicapp.models.Doctor.MockDoctor;
import static pl.edu.ur.pz.clinicapp.models.Patient.MockPatient;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "current", query="SELECT user FROM User user WHERE user.databaseUsername = FUNCTION('CURRENT_USER')"),
})
public class User {
    public enum Role {
        ANONYMOUS,
        PATIENT,
        RECEPTIONIST,
        NURSE,
        DOCTOR,
        ADMIN;
    
        public boolean isGroupUser() {
            return this == RECEPTIONIST || this == NURSE;
        }

        public String toString() {
            // TODO: when we have localization it will look much nicer
            if (this == PATIENT)      return "Pacjent";
            if (this == RECEPTIONIST) return "Recepcja";
            if (this == NURSE)        return "Pielęgniarka";
            if (this == DOCTOR)       return "Lekarz";
            if (this == ADMIN)        return "Administrator";
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
    @Column(nullable = false)
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
        this.email = email;
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
        if (emailOrPESEL.matches("^[a-zA-Z0-9.@]+$")) {

            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("anon");
            EntityManager entityManager = entityManagerFactory.createEntityManager();


            entityManager.getTransaction().begin();

            Query query = entityManager.createNativeQuery("SELECT public.get_login(:param)");
            query.setParameter("param", emailOrPESEL.toLowerCase());
            List<String> l = query.getResultList();


            entityManager.getTransaction().commit();



            entityManager.close();
            return l.get(0);

        }
        //TODO error message caused by wrong input (prevent sql injection)
        return "error";
    }



    static public User getCurrent() {
        return ClinicApplication.getEntityManager().createNamedQuery("current", User.class).getSingleResult();
    }

    static public User getMockUser(String emailOrPESEL, String password) {
        // TODO: actual authentication
        if (password.equals("asdf1234")) {
            if (emailOrPESEL.equals(MockPatient.getEmail()) || emailOrPESEL.equals(MockPatient.getPESEL())) return MockPatient;
            if (emailOrPESEL.equals(MockReceptionist.email)) return MockReceptionist;
            if (emailOrPESEL.equals(MockNurses.email)) return MockNurses;
            if (emailOrPESEL.equals(MockDoctor.getEmail())) return MockDoctor;
            if (emailOrPESEL.equals(MockAdmin.email)) return MockAdmin;
        }
        return null;
    }



    /* * * * * * * * * * * * * * * * * * * * *
     * Mocks for testing and development
     */

    public static User MockReceptionist = new User() {{
        setRole(Role.RECEPTIONIST);
        setEmail("reception@example.com");
        // No name as it's shared account (at least for now)
    }};
    public static User MockNurses = new User() {{
        setRole(Role.NURSE);
        setEmail("nurses@example.com");
        // No name as it's shared account (at least for now)
    }};
    public static User MockAdmin = new User() {{
        setRole(Role.ADMIN);
        setEmail("admin@example.com");
        setName("Jan");
        setSurname("Kowalski");
    }};
}
