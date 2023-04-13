package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Entity
@Table(name = "prescriptions")
public class Prescription extends MedicalHistoryEntry {
    /**
     * Government assigned ID for prescriptions (Polish "e-recepta").
     */
    @Column(length = 80, nullable = true)
    private String governmentId;
    public String setGovernmentId() {
        return governmentId;
    }
    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }
}
