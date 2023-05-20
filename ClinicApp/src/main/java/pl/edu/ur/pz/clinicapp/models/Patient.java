package pl.edu.ur.pz.clinicapp.models;

import org.hibernate.Session;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javafx.collections.ObservableList;

import javax.persistence.*;

import java.util.Comparator;
import java.util.List;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.isStringNullOrEmpty;

@Entity
@Table(name = "patients")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "patients",  query = "FROM Patient"),
        @NamedQuery(name = "patients.current", query = "SELECT patient FROM Patient patient WHERE patient.databaseUsername = FUNCTION('CURRENT_USER')")
})

@NamedNativeQueries({
        @NamedNativeQuery(name = "createPatient", query = "INSERT INTO public.patients "
                +"(building, city, pesel, post_city, post_code, street, id) "
                +"VALUES (:building, :city, :pesel, :post_city, :post_code, :street, :id)",
                resultClass = Patient.class)
})

public class Patient extends User{
    @Column(nullable = false, length = 11, unique = true)
    private String pesel;
    public String getPESEL() {
        return pesel;
    }
    public void setPESEL(String pesel) {
        this.pesel = pesel;
    }

    /* * * * * * * * * * * * * * * * * * * * *
     * Address
     */

    @Column(length = 40)
    private String city;
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    @Column(length = 40)
    private String street;
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    @Column(length = 16)
    private String building;
    public String getBuilding() {
        return building;
    }
    public void setBuilding(String building) {
        this.building = building;
    }

    @Column(length = 6)
    private String postCode;
    public String getPostCode() {
        return postCode;
    }
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Column(length = 40)
    private String postCity;
    public String getPostCity() {
        return postCity;
    }
    public void setPostCity(String postCity) {
        this.postCity = postCity;
    }

    /**
     * Returns address for displaying in short form (without post code or post city).
     * Example: Some city ul. Some street 123a
     */
    public String getAddressDisplayShort() {
        final var builder = new StringBuilder(80);
        builder.append(city);
        if (!isStringNullOrEmpty(street)) {
            builder.append(" ul. ");
            builder.append(street);
        }
        builder.append(' ');
        builder.append(building);
        return builder.toString();
    }

    /**
     * Returns address for displaying in long form (with post code or post city).
     * Example: Some city ul. Some street 123a\n12-345 Some city
     */
    public String getAddressDisplayLong() {
        final var builder = new StringBuilder(80);
        builder.append(city);
        if (!isStringNullOrEmpty(street)) {
            builder.append(" ul. ");
            builder.append(street);
        }
        builder.append(' ');
        builder.append(building);
        builder.append('\n');
        builder.append(postCode);
        builder.append(' ');
        builder.append(postCity);
        return builder.toString();
    }

    static public Patient getCurrent() {
        return ClinicApplication.getEntityManager().createNamedQuery("patients.current", Patient.class).getSingleResult();
    }

    public static List getAll(Class c) {
        Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
        return session.createCriteria(c).list();
    }

    public static Comparator<Patient> patientNameComparator = new Comparator<Patient>() {
        public int compare(Patient patient1, Patient patient2) {

            String patientName1 = patient1.getDisplayName().toUpperCase();
            String patientName2 = patient2.getDisplayName().toUpperCase();
            return patientName1.compareTo(patientName2);
        }

    };
}
