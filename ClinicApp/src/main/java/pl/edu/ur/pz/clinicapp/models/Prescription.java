package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "prescriptions")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "findUserPrescriptions",
                query = "SELECT * FROM prescriptions Pr INNER JOIN patients Pat ON Pr.patient_id = Pat.id " +
                        "INNER JOIN users U on U.id = Pat.id WHERE U.internal_name = :uname",
                resultClass = Prescription.class
        ),
        @NamedNativeQuery(
                name = "editPrescription",
                query = "UPDATE prescriptions " +
                        "SET notes = :notes, " +
                        "tags = :tags, " +
                        "government_id = :governmentID " +
                        "WHERE id = :prId",
                resultClass = Prescription.class
        ),

        @NamedNativeQuery(
                name = "deletePrescription",
                query = "DELETE FROM prescriptions WHERE id =:id",
                resultClass = Prescription.class
        )
})

@NamedQueries({
            @NamedQuery(
                    name = "allPrescriptions",
                    query = "FROM Prescription"
            ),
            @NamedQuery(
                    name = "createdPrescriptions",
                    query = "FROM Prescription  WHERE addedBy = :user"
            ),
        })

public class Prescription extends MedicalHistoryEntry {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Government assigned ID for prescriptions (Polish "e-recepta").
     */
    @Column(length = 80, nullable = true)
    private String governmentId;


    public String getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    /**
     * Name and surname of the doctor who created the referral (displayed in referrals list).
     */
    public String getDoctorName() {
        return this.getAddedBy().getDisplayName();
    }

    public Integer getId(){
        return this.id;
    }

}