package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that eases manipulation of user schedule.
 */
public class Schedule {
    protected User user;

    protected Schedule() {}

    public static Schedule of(User user) {
        final var instance = new Schedule();
        instance.user = user;
        return instance;
    }

    /**
     * Generates list of simple schedule entries describing open hours in specified timespan.
     * @param begin Beginning of the timespan to generate entries for.
     * @param end End of the timespan to generate entries for.
     * @return List of {@link Schedule.SimpleEntry} of type {@link Schedule.Entry.Type#OPEN},
     *  sorted chronologically (oldest first).
     */
    public List<Entry> generateScheduleEntriesFromTimetables(ZonedDateTime begin, ZonedDateTime end) {
        // TODO: unit testing
        final var list = new ArrayList<Entry>(10);
        final var timetables = user.getTimetables(); // latest first
        // FIXME: timetables are kept in natural order for effective dates, earliest first, latest last

        ZonedDateTime currentTime = begin;
        while (currentTime.isBefore(end)) {
            // Find current & next effective timetable (in chronological order)
            Timetable effectiveTimetable = null;
            Timetable nextEffectiveTimetable = null;
            for (final var timetable : timetables) {
                if (!timetable.getEffectiveDate().isAfter(currentTime)) {
                    effectiveTimetable = timetable;
                    break;
                }
                nextEffectiveTimetable = timetable;
            }
            if (effectiveTimetable == null) {
                throw new IllegalStateException();
            }
            assert (nextEffectiveTimetable == null ||
                    effectiveTimetable.getEffectiveDate().isBefore(
                            nextEffectiveTimetable.getEffectiveDate()));

            // Looking through timetable entries in chronological order
            for (final Timetable.Entry entry : effectiveTimetable.getEntries()) {
                if (currentTime.getDayOfWeek() != entry.getDayOfWeek()) {
                    continue;
                }

                ZonedDateTime entryEndTime = entry.endAsZonedDateTime(currentTime);
                if (currentTime.isAfter(entryEndTime)) {
                    continue;
                }

                ZonedDateTime entryStartTime = entry.startAsZonedDateTime(currentTime);
                if (currentTime.isAfter(entryStartTime)) {
                    entryStartTime = currentTime;
                }

                if (nextEffectiveTimetable != null) {
                    if (!entryStartTime.isBefore(nextEffectiveTimetable.getEffectiveDate())) {
                        // Go next timetable
                        currentTime = nextEffectiveTimetable.getEffectiveDate();
                        break;
                    }
                    if (entryEndTime.isAfter(nextEffectiveTimetable.getEffectiveDate())) {
                        // Add cutoff entry and go next timetable
                        currentTime = nextEffectiveTimetable.getEffectiveDate();
                        list.add(new SimpleEntry(Entry.Type.OPEN,
                                entryStartTime.toInstant(),
                                nextEffectiveTimetable.getEffectiveDate().toInstant()));
                        break;
                    }
                    // Or add full entry and look for more
                    currentTime = entryEndTime;
                    list.add(new SimpleEntry(Entry.Type.OPEN,
                            entryStartTime.toInstant(),
                            entryEndTime.toInstant()));
                }
            }
        }

        return list;
    }

    /**
     * Finds schedule entries for the user limited by begin and end timestamps.
     * Note: It compares start dates only.
     * @param beginTime Begin timestamp (inclusive).
     * @param endTime End timestamp (inclusive).
     * @return List of schedule entries found for the user in selected period.
     */
    public List<Entry> findScheduleEntries(Instant beginTime, Instant endTime) {
        final var em = ClinicApplication.getEntityManager();
        final var list = new ArrayList<Entry>(80);

        {
            TypedQuery<SimpleEntry> query = em.createNamedQuery(
                    "schedule_simple_entries_as_user_between_dates", SimpleEntry.class);
            query.setParameter("user", this.user);
            query.setParameter("from", beginTime);
            query.setParameter("to", endTime);
            query.getResultStream().forEach(list::add);
        }
        {
            TypedQuery<Appointment> query = em.createNamedQuery(
                    "appointments_as_user_between_dates", Appointment.class);
            query.setParameter("user", this.user);
            query.setParameter("from", beginTime);
            query.setParameter("to", endTime);
            query.getResultStream().forEach(list::add);
        }

        return list;
    }

    /**
     * Schedule entries representing allocated time pieces in the schedule.
     */
    public interface Entry {
        enum Type {
            NONE,

            /**
             * Describes open hours.
             */
            OPEN,

            /**
             * Describes clinic or doctor being closed for other reason.
             */
            CLOSED,

            /**
             * Describes doctor taking planned vacations.
             */
            VACATION,
            /**
             * Describes whole clinic (or doctor visits) being closed due to some holiday(s).
             */
            HOLIDAYS,
            /**
             * Describes doctor being unavailable due to health issues.
             */
            SICK_LEAVE,
            /**
             * Describes doctor being unavailable due to emergency issue.
             */
            EMERGENCY_LEAVE,

            /**
             * Describes hours taken by appointment(s).
             */
            APPOINTMENT,

            /**
             * Describes extra hours (when appointments can take place) for any reason.
             */
            EXTRA,

            OTHER;
        }

        Type getType();
        Instant getBeginTime();
        Instant getEndTime();
        Duration getDuration();
    }

    /**
     * Simple schedule entries
     */
    @Entity
    @Table(name = "schedule_simple_entries")
    @NamedQueries({
            @NamedQuery(name = "schedule_simple_entries_as_user",
                    query = "FROM Schedule$SimpleEntry e WHERE e.user = :user"),
            @NamedQuery(name = "schedule_simple_entries_as_user_between_dates",
                    query = "FROM Schedule$SimpleEntry e WHERE e.user = :user AND :from < e.endTime AND e.beginTime < :to"),
    })
    public static class SimpleEntry implements Entry {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        public Integer getId() {
            return id;
        }

        @Column(nullable = false)
        private Instant beginTime;
        @Override
        public Instant getBeginTime() {
            return beginTime;
        }
        public void setBeginTime(Instant beginTime) {
            this.beginTime = beginTime;
            // TODO: swap dates if ordering is invalid
        }

        @Column(nullable = false)
        private Instant endTime;
        @Override
        public Instant getEndTime() {
            return endTime;
        }
        public void setEndTime(Instant endTime) {
            this.endTime = endTime;
            // TODO: swap dates if ordering is invalid
        }

        @Override
        public Duration getDuration() {
            return Duration.between(beginTime, endTime);
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

        @Column(nullable = false, insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private Type type;
        public Type getType() {
            return type;
        }

        // Empty constructor is required for JPA standard.
        public SimpleEntry() {}

        public SimpleEntry(Type type, Instant beginTime, Instant endTime) {
            this.type = type;
            this.beginTime = beginTime;
            this.endTime = endTime;
        }
    }
}
