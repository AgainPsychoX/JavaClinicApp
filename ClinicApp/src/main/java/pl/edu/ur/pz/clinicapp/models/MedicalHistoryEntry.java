package pl.edu.ur.pz.clinicapp.models;


import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@MappedSuperclass
public abstract class MedicalHistoryEntry {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public Integer getId() {
        return id;
    }

    /**
     * Notes about the medical entry.
     */
    @Column(nullable = false, length = 800)
    private String notes;
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Tags for the medical entry.
     */
    @Column(nullable = false, length = 255)
    private String tags;
    public String getStringTags(){return tags;}
    private List<String> getTags() {
        return List.of(tags.split(","));
    }
    public void setStringTags(String tags){
        this.tags = tags;
    }
    private void setTags(List<String> tags) {
        // TODO: Internally should be using some smart class acting like `List`, but assuring max length of underlying string.
        //       Would be nice to use converter `AttributeConverter` to avoid duplicate memory usage.
    }
    // TODO: https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/Hibernate_User_Guide.html#collections-as-basic

    /**
     * Patient the entry belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * User who added the entry.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
    @JoinColumn(name = "added_by_user_id", referencedColumnName = "id", nullable = false)
    private User addedBy;
    public User getAddedBy() {
        return addedBy;
    }
    public void setAddedBy(User user) {
        this.addedBy = user;
    }

    /**
     * Date when entry was added.
     */
    @Column(nullable = false)
    private Instant addedDate;
    public Instant getAddedDate() {
        return addedDate;
    }

    public LocalDate getAddedDateFormatted() {
        return Timestamp.from(this.getAddedDate()).toLocalDateTime().toLocalDate();
    }
    public void setAddedDate(Instant addedDate) {
        this.addedDate = addedDate;
    }
}
