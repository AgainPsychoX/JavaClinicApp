package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.controls.WeekPane;

import javax.persistence.*;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "timetables")
@NamedQueries({
        @NamedQuery(name = "timetables_for_user", query = "SELECT t FROM Timetable t WHERE t.user = :user")
})
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public Integer getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Effective timestamp should be keep with zone,
     * as hours in entries might refer to ambiguous local time.
     */
    @Column(nullable = false)
    private ZonedDateTime effectiveDate;
    public ZonedDateTime getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(ZonedDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @OneToMany(mappedBy = "timetable", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("weekday, start_minute")
    private Set<Entry> entries;
    public Set<Entry> getEntries() {
        return entries;
    }

    public void add(Timetable.Entry entry) {
        entry.timetable = this;
        // TODO: detect overlapping and throw error
        entries.add(entry);
    }

    public Timetable() {
        this(ZonedDateTime.now());
    }

    public Timetable(ZonedDateTime effectiveDate) {
        this(effectiveDate, new HashSet<>(7));
    }

    public Timetable(ZonedDateTime effectiveDate, Set<Entry> entries) {
        this.effectiveDate = effectiveDate;
        this.entries = entries;
    }

    @Override
    public String toString() {
        return String.format("Timetable{effective=%s,entries=[%s]}",
                effectiveDate.toString(),
                entries.stream().map(Entry::toString).collect(Collectors.joining(",")));
    }

    /**
     * Composite primary key for timetable entries, consisting of both timetable ID and weekday.
     */
    public static final class EntryId implements Serializable {
        private final Timetable timetable;
        private final int weekday;
        private final int startMinute;

        public Timetable timetable() {
            return timetable;
        }

        public int weekday() {
            return weekday;
        }

        public DayOfWeek dayOfWeek() {
            return DayOfWeek.of(weekday + 1);
        }

        public int startMinute() {
            return startMinute;
        }

        // Empty constructor is required for JPA standard.
        public EntryId() {
            this(null, 0, 0); // invalid, but required for `record`
        }

        public EntryId(Timetable timetable, int weekday, int startMinute) {
            this.timetable = timetable;
            this.weekday = weekday;
            this.startMinute = startMinute;
        }

        public EntryId(Timetable timetable, DayOfWeek dayOfWeek, int startMinute) {
            this(timetable, dayOfWeek.getValue() - 1, startMinute);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) return true;
            if (other == null || other.getClass() != this.getClass()) return false;
            var that = (EntryId) other;
            return Objects.equals(this.timetable, that.timetable) &&
                    this.weekday == that.weekday &&
                    this.startMinute == that.startMinute;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timetable, weekday, startMinute);
        }
    }

    @Entity
    @IdClass(EntryId.class)
    @Table(name = "timetable_entries")
    public static class Entry implements WeekPane.Entry {
        @Id
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "timetable_id", referencedColumnName = "id", nullable = false)
        protected Timetable timetable;
        public Timetable getTimetable() {
            return timetable;
        }

        /**
         * Weekday in SQL is integer as Sunday (0) to Saturday (6).
         *
         * See <a href="https://www.postgresql.org/docs/current/functions-datetime.html">PostgreSQL docs about datetime functions</a>.
         */
        @Id
        @Column(name = "weekday", nullable = false)
        protected int weekday;
        public DayOfWeek getDayOfWeek() {
            return DayOfWeek.of(weekday + 1);
        }

        /**
         * Minute mark when the timetable entry starts in the weekday. Example: 13:49 = 13*60+49 = 829.
         */
        @Column(nullable = false)
        private int startMinute;
        public int getStartMinute() {
            return startMinute;
        }
        public void setStartMinute(int minute) {
            if (minute < endMinute) {
                startMinute = minute;
            }
            else {
                startMinute = endMinute;
                endMinute = minute;
            }
        }
        public void setStartTime(ZonedDateTime time) {
            setStartTime(time.toLocalTime());
        }
        public void setStartTime(LocalTime time) {
            setStartMinute(time.getHour() * 60 + time.getMinute());
        }

        /**
         * Calculates potential entry start moment as zoned date time.
         * @param date Date & zone to be used.
         * @return Zoned date time for potential entry start.
         */
        public ZonedDateTime startAsZonedDateTime(ZonedDateTime date) {
            assert date.getDayOfWeek() == getDayOfWeek();
            return date.toLocalDate()
                    .atTime(startMinute / 60, startMinute % 60)
                    .atZone(date.getZone());
        }

        /**
         * Minute mark when the timetable entry ends in the weekday. Example: 13:49 = 13*60+49 = 829.
         */
        @Column(nullable = false)
        private int endMinute;
        public int getEndMinute() {
            return endMinute;
        }
        public void setEndMinute(int minute) {
            if (minute < startMinute) {
                endMinute = startMinute;
                startMinute = minute;
            }
            else {
                endMinute = minute;
            }
        }
        public void setEndTime(ZonedDateTime time) {
            setEndTime(time.toLocalTime());
        }
        public void setEndTime(LocalTime time) {
            setEndMinute(time.getHour() * 60 + time.getMinute());
        }

        /**
         * Calculates potential entry end moment as zoned date time.
         * @param date Date & zone to be used.
         * @return Zoned date time for potential entry end.
         */
        public ZonedDateTime endAsZonedDateTime(ZonedDateTime date) {
            assert date.getDayOfWeek() == getDayOfWeek();
            return date.toLocalDate()
                    .atTime(endMinute / 60, endMinute % 60)
                    .atZone(date.getZone());
        }

        // Empty constructor is required for JPA standard.
        public Entry() {}

        public Entry(DayOfWeek dayOfWeek, int startMinute, int endMinute) {
            this.weekday = dayOfWeek.getValue() - 1;
            this.startMinute = startMinute;
            this.endMinute = endMinute;
        }

        @Override
        public String toString() {
            return String.format("Timetable.Entry{day=%s,start=%d,end=%d}",
                    this.getDayOfWeek().toString(), this.startMinute, this.endMinute);
        }
    }
}
