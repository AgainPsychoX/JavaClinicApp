package pl.edu.ur.pz.clinicapp.models;

import org.jetbrains.annotations.NotNull;
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
        @NamedQuery(name = "Timetables.forUser", query = """
                FROM Timetable t LEFT JOIN FETCH t.entries
                WHERE t.user = :user
                ORDER BY t.effectiveDate
                """)
})
//@NamedNativeQueries({
//        @NamedNativeQuery(name = "Timetables.forUser", query = """
//                SELECT t.*, e.*
//                FROM timetables t
//                    LEFT JOIN timetable_entries e ON t.id = e.timetable_id
//                WHERE t.user_id = :id
//                ORDER BY t.effective_date, e.weekday, e.start_minute
//                """, readOnly = true),
//})
public class Timetable implements Comparable<Timetable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public Integer getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
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

    @OneToMany(mappedBy = "timetable", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Entry> entries;
    public Set<Entry> getEntries() {
        return entries;
    }

    /**
     * Adds (or merges in) new entry to the timetable.
     * @param newEntry entry to be added (or merged).
     * @return true if any entries were added or changed (due to merging),
     *  false otherwise - meaning the range already is covered by existing entries.
     */
    public boolean add(Timetable.Entry newEntry) {
        // TODO: unit testing & fix it because feels unreliable
        for (var existingEntry : entries) {
            if (existingEntry.getDayOfWeek() == newEntry.getDayOfWeek()) {
                if (newEntry.endMinute < existingEntry.startMinute) continue;
                if (existingEntry.endMinute < newEntry.startMinute) continue;

                int startMinute  = Math.min(newEntry.startMinute, existingEntry.startMinute);
                int endMinute    = Math.min(newEntry.endMinute,   existingEntry.endMinute);
                if (startMinute == existingEntry.startMinute && endMinute == existingEntry.endMinute) {
                    // New entry is covered by existing entry entirely
                    return false;
                }

                // Entries are immutable (start minute is part of composite key),
                // so we remove existing one and create new one.
                remove(existingEntry);
                return add(new Timetable.Entry(newEntry.getDayOfWeek(), startMinute, endMinute));
            }
        }
        newEntry.timetable = this;
        entries.add(newEntry);
        return true;
    }

    /**
     * Removing existing valid entry from the timetable.
     * @param entry entry to be removed
     */
    public void remove(Timetable.Entry entry) {
        if (entries.remove(entry)) {
            entry.timetable = null;
        }
    }

    public boolean isEmpty() {
        return entries.isEmpty();
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

    public int getTotalMinutesWeekly() {
        int total = 0;
        for (var entry : this.entries) {
            total += entry.getDurationMinutes();
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Timetable{effective=%s,entries=[%s]}",
                effectiveDate.toString(),
                entries.stream().map(Entry::toString).collect(Collectors.joining(",")));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Timetable that) {
            return getId() != null && getId().equals(that.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull Timetable o) {
        return this.effectiveDate.compareTo(o.effectiveDate);
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
            if (this == other) return true;
            if (other instanceof EntryId that) {
                return Objects.equals(this.timetable, that.timetable) &&
                        this.weekday == that.weekday &&
                        this.startMinute == that.startMinute;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timetable, weekday, startMinute);
        }

        @Override
        public String toString() {
            return String.format("Timetable.EntryId{timetable_id=%d,weekday=%d,start=%d}",
                    timetable.getId(), weekday, startMinute);
        }
    }

    @Entity
    @IdClass(EntryId.class)
    @Table(name = "timetable_entries")
    public static class Entry implements WeekPane.Entry {
        @Id
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
//        @JoinColumn(name = "timetable_id", referencedColumnName = "id", nullable = false)
        private Timetable timetable;
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
        private int weekday;
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

        public LocalTime startAsLocalTime() {
            return LocalTime.of(startMinute / 60, startMinute % 60);
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

        public LocalTime endAsLocalTime() {
            return LocalTime.of(endMinute / 60, endMinute % 60);
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
            this.startMinute = Math.min(startMinute, endMinute);
            this.endMinute = Math.max(startMinute, endMinute);
        }

        public Entry(DayOfWeek dayOfWeek, LocalTime start, LocalTime end) {
            this(dayOfWeek, start.getHour() * 60 + start.getMinute(), end.getHour() * 60 + end.getMinute());
        }

        @Override
        public String toString() {
            return String.format("Timetable.Entry{day=%s,start=%d,end=%d}",
                    this.getDayOfWeek().toString(), this.startMinute, this.endMinute);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other instanceof Entry that) {
                return Objects.equals(this.timetable, that.timetable) &&
                        this.weekday == that.weekday &&
                        this.startMinute == that.startMinute;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timetable, weekday, startMinute);
        }
    }
}
