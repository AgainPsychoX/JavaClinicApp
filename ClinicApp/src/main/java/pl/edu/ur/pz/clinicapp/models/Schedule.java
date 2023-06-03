package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;

import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that eases manipulation of user schedule.
 */
public class Schedule {
    protected UserReference userReference;

    public UserReference getUserReference() {
        return userReference;
    }

    public User getUser() {
        if (userReference instanceof User user)
            return user;
        if (userReference instanceof Patient patient)
            return patient.asUser();
        if (userReference instanceof Doctor doctor)
            return doctor.asUser(); // might throw if unprivileged user want to get doctor as user

        return null;
    }

    protected Schedule() {}

    public static Schedule of(UserReference user) {
        final var instance = new Schedule();
        instance.userReference = user;
        return instance;
    }

    /**
     * Generates list of simple schedule entries describing open hours in specified timespan.
     * @param begin Beginning of the timespan to generate entries for.
     * @param end End of the timespan to generate entries for.
     * @return List of {@link Schedule.SimpleEntry} of type {@link Schedule.Entry.Type#OPEN},
     *  sorted chronologically (the oldest first).
     */
    public List<Entry> generateScheduleEntriesFromTimetables(ZonedDateTime begin, ZonedDateTime end) {
        // TODO: unit testing
        final var list = new ArrayList<Entry>(10);
        final var timetables = Timetable.forUser(userReference); // natural order

        ZonedDateTime currentTime = begin;
        while (currentTime.isBefore(end)) {
            // Find current & next effective timetable (in chronological order)
            Timetable effectiveTimetable = null;
            Timetable nextEffectiveTimetable = null;
            for (int i = 0; i < timetables.size(); i++) {
                final var timetable = timetables.get(i);
                if (timetable.isPossiblyEffective(currentTime)) {
                    effectiveTimetable = timetable;
                    if (i + 1 < timetables.size()) {
                        nextEffectiveTimetable = timetables.get(i + 1);
                    }
                    break;
                }
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

                ZonedDateTime entryEndTime = entry.calculatePotentialStartAtDate(currentTime);
                if (currentTime.isAfter(entryEndTime)) {
                    continue;
                }

                ZonedDateTime entryStartTime = entry.calculatePotentialEndAtDate(currentTime);
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
     * Finds schedule entries for the user between specified range of time (comparing start dates only).
     * Query might return partial data if no sufficient privileges. The {@link DoctorSchedule#findScheduleEntries}
     * override will also include vague information on entries that current user have no privileges to view.
     * @param from Begin timestamp of the time range (inclusive).
     * @param to End timestamp of the time range (inclusive).
     * @return List of schedule entries found for the user in selected period.
     */
    public List<Entry> findScheduleEntries(Instant from, Instant to) {
        final var list = new ArrayList<Entry>(80);
        list.addAll(SimpleEntry.forUserBetweenDates(userReference, from, to));
        list.addAll(Appointment.forUserBetweenDates(userReference, from, to));
        return list;
    }

    public List<WeekPane.Entry> generateWeekPaneEntriesForSchedule(LocalDate weekStartDate) {
        // TODO: unit testing
        final var zone = ZoneId.systemDefault();
        final var weekEndDate = weekStartDate.plusDays(7);
        final var resultList = new ArrayList<WeekPane.Entry>(80);

        final var scheduleEntries = findScheduleEntries(
                weekStartDate.atStartOfDay(zone).toInstant(),
                weekEndDate.atStartOfDay(zone).toInstant()
        );
        for (final var entry : scheduleEntries) {
            // Add original entry if it starts somewhere in the week
            final var beginDateTime = entry.getBeginTime().atZone(zone);
            if (!beginDateTime.toLocalDate().isBefore(weekStartDate)) {
                resultList.add(entry);
            }

            if (entry.doesCrossDays()) {
                var date = entry.getBeginTime().atZone(zone).toLocalDate();
                if (date.isBefore(weekStartDate)) {
                    date = weekStartDate;
                }

                final var dayBeforeEndDate = entry.getEndTime().atZone(zone).toLocalDate().minusDays(1);
                if (dayBeforeEndDate.isAfter(weekEndDate)) /* the original entry lasts outside the week */ {
                    // Add full days entries
                    while (date.isBefore(weekEndDate)) {
                        date = date.plusDays(1);
                        resultList.add(new ProxyWeekPaneEntry(entry, date.getDayOfWeek(), 0, 1440));
                    }
                } else /* the original entry ends inside the week */ {
                    // Add full days entries
                    while (date.isBefore(dayBeforeEndDate)) {
                        date = date.plusDays(1);
                        resultList.add(new ProxyWeekPaneEntry(entry, date.getDayOfWeek(), 0, 1440));
                    }

                    // Add last entry which might be not full day
                    final var endDateTime = entry.getEndTime().atZone(zone);
                    resultList.add(new ProxyWeekPaneEntry(entry, date.getDayOfWeek(),
                            0, endDateTime.getHour() * 60 + endDateTime.getMinute()));
                }

            }
        }

        return resultList;
    }

    /**
     * Proxy entries to represent schedule entries that doesn't fit in single weekday inside a week pane.
     */
    public static class ProxyWeekPaneEntry implements WeekPane.Entry {
        protected Entry original;
        public Entry getOriginal() {
            return original;
        }

        DayOfWeek dayOfWeek;
        @Override
        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        int startMinute;
        @Override
        public int getStartMinute() {
            return startMinute;
        }

        int endMinute;
        @Override
        public int getEndMinute() {
            return endMinute;
        }

        public ProxyWeekPaneEntry(Entry original, DayOfWeek dayOfWeek, int startMinute, int endMinute) {
            this.original = original;
            this.dayOfWeek = dayOfWeek;
            this.startMinute = startMinute;
            this.endMinute = endMinute;
        }
    }

    /**
     * Schedule entries representing allocated time pieces in the schedule.
     */
    public interface Entry extends WeekPane.Entry {
        enum Type {
            NONE,

            /**
             * Describes open hours. Entries with such type are not to be persisted in the database,
             * as they are provided by (effective) timetable (of the user) mechanism.
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

            public String localizedName() {
                return switch (this) {
                    case OPEN            -> "otwarte";
                    case CLOSED          -> "zamknięte";
                    case VACATION        -> "urlop";
                    case HOLIDAYS        -> "święto";
                    case SICK_LEAVE      -> "choroba";
                    case EMERGENCY_LEAVE -> "nagły wypadek";
                    case APPOINTMENT     -> "wizyta";
                    case EXTRA           -> "dodatkowe";
                    case OTHER           -> "inne";
                    default              -> "?";
                };
            }
        }

        Type getType();
        Instant getBeginTime();
        Instant getEndTime();
        Duration getDuration();

        @Override
        default DayOfWeek getDayOfWeek() {
            return getBeginTime().atZone(ZoneId.systemDefault()).getDayOfWeek();
        }
        @Override
        default int getStartMinute() {
            final var date = getBeginTime().atZone(ZoneId.systemDefault());
            return date.getHour() * 60 + date.getMinute();
        }

        /**
         * Warning: The value might be capped 24 * 60 = 1440 in case the entry crosses over multiple days.
         * Use of {@link ProxyWeekPaneEntry} might be required to display this entry properly on a {@link WeekPane}.
         * @return end minute of the entry on given day of week
         */
        @Override
        default int getEndMinute() {
            final var endMinute = getStartMinute() + (int) getDuration().toMinutes();
            return Math.min(endMinute, 24 * 60);
        }

        /**
         * @return true if the entry crosses over multiple days, false otherwise.
         */
        default boolean doesCrossDays() {
            final var startDay = getBeginTime().atZone(ZoneId.systemDefault()).getDayOfWeek();
            final var endDay = getEndTime().atZone(ZoneId.systemDefault()).getDayOfWeek();
            return startDay != endDay;
        }
    }

    /**
     * <p> Queries and returns all schedule busy entries for given user in given range of time,
     * as simple entries' data (without ID, readonly), including appointments (without details).
     *
     * @param from Begin timestamp of the time range (inclusive).
     * @param to End timestamp of the time range (inclusive).
     * @return List of the simple entries in natural order (by begin timestamp).
     */
    @SuppressWarnings("unchecked")
    List<Entry> getBusyEntries(Instant from, Instant to) {
        final var query = ClinicApplication.getEntityManager()
                .createNamedQuery("ScheduleBusyEntries.forUser.betweenDates", SimpleEntry.class);
        query.setParameter("user_id", userReference);
        query.setParameter("from", from);
        query.setParameter("to", to);
        return (List<Entry>) (List<? extends Entry>) query.getResultList();
    }

    /**
     * Simple schedule entries
     */
    @Entity
    @Table(name = "schedule_simple_entries")
    @NamedQueries({
            @NamedQuery(name = "ScheduleSimpleEntries.forUser.betweenDates", query = """
                    FROM Schedule$SimpleEntry e WHERE e.user.id = :user_id AND :from < e.endTime AND e.beginTime < :to
                    ORDER BY e.beginTime
                    """),
    })
    @NamedNativeQueries({
            @NamedNativeQuery(name = "ScheduleBusyEntries.forUser.betweenDates", query = """
                    SELECT * FROM schedule_busy_view WHERE user_id = :user_id AND :from < end_time AND begin_time < :to
                    """, // the view should be already ordered (`ORDER BY user_id, begin_time`)
                    resultSetMapping = "schedule_busy_view"),
    })
    @SqlResultSetMappings({
            @SqlResultSetMapping(name = "schedule_busy_view", classes = {
                    @ConstructorResult(targetClass = SimpleEntry.class, columns = {
                            @ColumnResult(name = "user_id",    type = Integer.class),
                            @ColumnResult(name = "begin_time", type = Instant.class),
                            @ColumnResult(name = "end_time",   type = Instant.class),
                            @ColumnResult(name = "type",       type = String.class), // custom enum
                    })
            }),
    })
    public static class SimpleEntry implements Entry {
        /**
         * Queries and returns all simple entries for given user in given range of time.
         * @param user Reference to user (user, patient or doctor).
         * @param from Begin timestamp of the time range (inclusive).
         * @param to End timestamp of the time range (inclusive).
         * @return List of the simple entries.
         */
        static List<SimpleEntry> forUserBetweenDates(UserReference user, Instant from, Instant to) {
            final var query = ClinicApplication.getEntityManager()
                    .createNamedQuery("ScheduleSimpleEntries.forUser.betweenDates", SimpleEntry.class);
            query.setParameter("user_id", user.getId());
            query.setParameter("from", from);
            query.setParameter("to", to);
            return query.getResultList();
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        public Integer getId() {
            return id;
        }

        @Column(nullable = false)
        protected Instant beginTime;
        @Override
        public Instant getBeginTime() {
            return beginTime;
        }
        public void setBeginTime(Instant beginTime) {
            this.beginTime = beginTime;
            // TODO: swap dates if ordering is invalid
        }

        @Column(nullable = false)
        protected Instant endTime;
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
        protected User user;
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, columnDefinition = "schedule_simple_entry_type") // custom enum type
        @org.hibernate.annotations.Type(type = "postgresql_enum")
        protected Entry.Type type;
        public Entry.Type getType() {
            return type;
        }

        // Empty constructor is required for JPA standard.
        public SimpleEntry() {}

        public SimpleEntry(Entry.Type type, Instant beginTime, Instant endTime) {
            this.type = type;
            this.beginTime = beginTime;
            this.endTime = endTime;
        }

        /**
         * Constructor for `schedule_busy_view` {@link SqlResultSetMapping}
         * (see annotations for the {@link Schedule.SimpleEntry})
         */
        @SuppressWarnings("unused")
        private SimpleEntry(Integer user_id, Instant begin_time, Instant end_time, String type) {
            this.type = Entry.Type.valueOf(type);
            this.beginTime = begin_time;
            this.endTime = end_time;
            // TODO: how to set use here? anyways, it's not like it's really needed anyways, as it's immutable entry
        }
    }
}
