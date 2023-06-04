package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;
import java.time.Instant;

@NamedNativeQueries(
        {
//                @NamedNativeQuery(
//                        name = "findUsersReferrals",
//                        query = "SELECT * FROM referrals R INNER JOIN patients P ON R.patient_id=P.id "
//                                + "INNER JOIN users U on U.id = P.id WHERE U.internal_name = :uname",
//                        resultClass = Referral.class
//                ),
                @NamedNativeQuery(
                        name = "editReferral",
                        query = "UPDATE referrals "
                                + "SET added_date = :addedDate, "
                                + "fulfilment_date = :fulfilmentDate, "
                                + "point_of_interest = :pointOfInterest, "
                                + "notes = :notes, "
                                + "feedback = :feedback, "
                                + "tags = :tags, "
                                + "government_id = :governmentId "
                                + "WHERE id = :refId",
                        resultClass = Referral.class
                ),
                @NamedNativeQuery(
                        name = "deleteReferral",
                        query = "DELETE FROM referrals WHERE id = :refId",
                        resultClass = Referral.class
                )
        }
)

@NamedQueries({
        @NamedQuery(
                name = "findUsersReferrals",
                query = "FROM Referral R WHERE R.patient = :patient"
        ),
        @NamedQuery(
                name = "allReferrals",
                query = "FROM Referral"
        ),
        @NamedQuery(
                name = "nursesReferrals",
                query = "FROM Referral WHERE pointOfInterest = 'ZABIEG'"
        ),
        @NamedQuery(
                name = "createdReferrals",
                query = "FROM Referral WHERE addedBy = :user"
        ),
})

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
        return getAddedBy().asDoctor();
    }

    /**
     * Name and surname of the doctor who created the referral (displayed in referrals list).
     */
    public String getDoctorName() {
        return this.getAddedBy().getDisplayName();
    }

    /**
     * Speciality of doctor the patient should contact, or clinic specialization.
     */
    @Column(length = 80)
    private String pointOfInterest;

    public String getPointOfInterest() {
        return pointOfInterest;
    }

    public void setPointOfInterest(String pointOfInterest) {
        this.pointOfInterest = pointOfInterest;
    }

    /**
     * Date of request (referral added/start date).
     */
    public Instant getRequestDate() {
        return getAddedDate();
    }

    /**
     * Date of fulfilment of the referral (when extra medical examinations took place). Null if unknown or not fulfilled yet.
     */
    @Column(nullable = true)
    private Instant fulfilmentDate;

    public Instant getFulfilmentDate() {
        return fulfilmentDate;
    }

    public void setFulfilmentDate(Instant fulfilmentDate) {
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

    public String getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    public final static String forNurses = "ZABIEG";
}
