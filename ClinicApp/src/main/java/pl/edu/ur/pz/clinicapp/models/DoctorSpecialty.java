package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "doctor_specialities")
public class DoctorSpecialty {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public Integer getId() {
        return id;
    }

    @Column(nullable = false, unique = true)
    public String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = this.name;
    }

    @Column(nullable = false, columnDefinition = "int default 60")
    public int defaultVisitTime;
    public int setDefaultVisitTime() {
        return defaultVisitTime;
    }
    public void setDefaultVisitTime(int defaultVisitTime) {
        this.defaultVisitTime = this.defaultVisitTime;
    }
}
