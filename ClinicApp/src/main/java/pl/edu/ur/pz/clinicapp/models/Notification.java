package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notifications")

@NamedQueries({
        @NamedQuery(name = "getAllNotifications", query = "FROM Notification "),
})

public class Notification {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public Integer getId() {
        return id;
    }

    /**
     * Source user of the notification, i.e. doctor who changed the appointment date.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "source_user_id", referencedColumnName = "id", nullable = false)
    private User sourceUser;
    public User getSourceUser() {
        return sourceUser;
    }
    public void setSourceUser(User user) {
        this.sourceUser = user;
    }

    /**
     * Destination user of the notification, i.e. doctor who changed the appointment date.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_user_id", referencedColumnName = "id", nullable = false)
    private User destinationUser;
    public User getDestinationUser() {
        return destinationUser;
    }
    public void setDestinationUser(User user) {
        this.destinationUser = user;
    }

    /**
     * Date the notification was sent.
     */
    @Column(nullable = false)
    private Timestamp sentDate;
    public Timestamp getSentDate() {
        return sentDate;
    }
    public void setSentDate(Timestamp sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * Date the notification was read.
     */
    @Column(nullable = true)
    private Timestamp readDate;
    public Timestamp getReadDate() {
        return readDate;
    }
    public void setReadDate(Timestamp readDate) {
        this.readDate = readDate;
    }

    /**
     * Checks whenever notification was read.
     * @return True if already read, false if pending.
     */
    public boolean wasRead() {
        return this.readDate != null;
    }

    /**
     * Content of the notification.
     */
    @Column(nullable = false, length = 800)
    private String content;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
