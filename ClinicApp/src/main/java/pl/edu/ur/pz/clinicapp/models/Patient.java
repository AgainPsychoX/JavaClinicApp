package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "patients")
public class Patient extends User {
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
    public String setStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    @Column(length = 16)
    private String building;
    public String setBuilding() {
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
    public String setPostCity() {
        return postCity;
    }
    public void setPostCity(String postCity) {
        this.postCity = postCity;
    }



    /* * * * * * * * * * * * * * * * * * * * *
     * Mocks for testing and development
     */

    public static Patient MockPatient = new Patient() {{
        setRole(Role.PATIENT);
        setEmail("anna.nowak.123@example.com");
        setPESEL("99032301234");
        setName("Anna");
        setSurname("Nowak");
    }};
}
