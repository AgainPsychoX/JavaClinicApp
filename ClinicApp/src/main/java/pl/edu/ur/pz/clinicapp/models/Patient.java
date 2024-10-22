package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.isStringNullOrBlank;

@Entity
@Table(name = "patients")
@NamedQueries({
        @NamedQuery(name = "patients",  query = "FROM Patient p LEFT JOIN FETCH p.user")
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "createPatient", query = "INSERT INTO public.patients "
                +"(building, city, pesel, post_city, post_code, street, id) "
                +"VALUES (:building, :city, :pesel, :post_city, :post_code, :street, :id)",
                resultClass = Patient.class)
})
public class Patient implements UserReference {
    // Empty constructor is required for JPA standard.
    protected Patient() {}


    public Patient(User user, String pesel) {
        this.id = user.getId();
        this.user = user;
        this.setPESEL(pesel);
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Patients are users
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
        return String.format("Patient{id=%d,pesel=%s,name=%s,surname=%s}",
                id, getPESEL(), getName(), getSurname());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Patient that) {
            return getId() != null && getId().equals(that.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : super.hashCode();
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Patient data
     */

    public String getEmail() {
        return asUser().getEmail();
    }
    public void setEmail(String email) {
        asUser().setEmail(email);
    }

    public String getPhone() {
        return asUser().getPhone();
    }
    public void setPhone(String phone) {
        asUser().setPhone(phone);
    }


    public String getName() {
        return asUser().getName();
    }
    public void setName(String name) {
        asUser().setName(name);
    }

    public String getSurname() {
        return asUser().getSurname();
    }
    public void setSurname(String surname) {
        asUser().setSurname(surname);
    }

    public String getDisplayName() {
        return asUser().getDisplayName();
    }


    @Column(nullable = false, length = 11, unique = true)
    private String pesel;
    public String getPESEL() {
        return pesel;
    }
    public void setPESEL(String pesel) {
        this.pesel = pesel;
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Address
     *
     * TODO: Separate it out, even if it's the only place we use it.
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
        if (!isStringNullOrBlank(street)) {
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
        if (!isStringNullOrBlank(street)) {
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
}
