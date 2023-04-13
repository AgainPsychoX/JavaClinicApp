package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Utility class that eases manipulation of user schedule.
 */
public class Schedule {
    protected Schedule() {}

    protected User user;

    public static Schedule of(User user) {
        final var instance = new Schedule();
        instance.user = user;
        // TODO: prefetch?
        return instance;
    }

    // TODO: get all entries for given user in given time range (i.e. week, month)
    //       include medical appointments (for user)

    /**
     * Schedule entries represent already allocated time in the schedule.
     */
    @Entity
    @Table(name = "schedule_entries")
    public static class Entry {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        public Integer getId() {
            return id;
        }

        @Column(nullable = false)
        private Timestamp dateBegin;
        public Timestamp getDateBegin() {
            return dateBegin;
        }
        public void setDateBegin(Timestamp dateBegin) {
            this.dateBegin = dateBegin;
            // TODO: swap dates if ordering is invalid
        }

        @Column(nullable = false)
        private Timestamp dateEnd;
        public Timestamp getDateEnd() {
            return dateEnd;
        }
        public void setDateEnd(Timestamp dateEnd) {
            this.dateEnd = dateEnd;
            // TODO: swap dates if ordering is invalid
        }

        /**
         * User that the schedule belongs to.
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
        private User user;
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }

        public enum Type {
            NONE,
            VACATION,
            HOLIDAYS,
            SICK_LEAVE,
            EMERGENCY_LEAVE,
            CLOSED;
        }

        @Column(nullable = false, insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private Type type;
        public Type getType() {
            return type;
        }
    }
}
