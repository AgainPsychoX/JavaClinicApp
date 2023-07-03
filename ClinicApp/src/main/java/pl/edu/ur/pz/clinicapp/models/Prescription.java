package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "prescriptions")
@NamedNativeQueries({
//        @NamedNativeQuery(
//                name = "findUserPrescriptions",
//                query = "SELECT * FROM prescriptions Pr INNER JOIN patients Pat ON Pr.patient_id = Pat.id " +
//                        "INNER JOIN users U on U.id = Pat.id WHERE U.internal_name = :uname",
//                resultClass = Prescription.class
//        ),
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
                    name = "findUsersPrescriptions",
                    query = "FROM Prescription P WHERE P.patient = :patient"
            ),
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
}