package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "referrals")
public class Referral extends MedicalHistoryEntry {
    /**
     * Special constant point of interest value for referring patient to local clinic nurses.
     */
    private final static String INTERNAL_NURSES_POI = "^^^INTERNAL NURSES^^^";

    /**
     * Doctor who created the referral.
     */
    public Doctor getDoctor() {
        return (Doctor) getAddedBy();
    }

    /**
     * Speciality of doctor the patient should contact, or clinic specialization.
     */
    @Column(length = 80)
    private String pointOfInterest;
    public String setPointOfInterest() {
        return pointOfInterest;
    }
    public void setPointOfInterest(String pointOfInterest) {
        this.pointOfInterest = pointOfInterest;
    }

    /**
     * Date of request (referral added/start date).
     */
    public Timestamp getRequestDate() {
        return getAddedDate();
    }

    /**
     * Date of fulfilment of the referral (when extra medical examinations took place). Null if unknown or not fulfilled yet.
     */
    @Column(nullable = true)
    private Timestamp fulfilmentDate;
    public Timestamp getFulfilmentDate() {
        return fulfilmentDate;
    }
    public void setFulfilmentDate(Timestamp fulfilmentDate) {
        this.fulfilmentDate = fulfilmentDate;
    }

    /**
     * Feedback information of the referral being fulfilled. Null if unknown or not fulfilled yet.
     */
    @Column(nullable = true, length = 800)
    private String feedback;
    public String getFeedback() {
        return feedback;
    }
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Government assigned ID for prescriptions (Polish "e-skierowanie").
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
