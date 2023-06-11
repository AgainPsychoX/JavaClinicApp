package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;

import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static pl.edu.ur.pz.clinicapp.utils.TemporalUtils.alignDateToWeekStart;

/**
 * Utility class that eases manipulation of user schedule & timetables.
 */
public class Schedule {
    protected UserReference userReference;

    public UserReference getUserReference() {
        return userReference;
    }

    protected Schedule() {}

    public static Schedule of(UserReference user) {
        if (user == null) throw new NullPointerException();
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

        // Find possibly effective timetable for the beginning
        Iterator<Timetable> timetablesIterator = timetables.iterator();
        if (!timetablesIterator.hasNext()) {
            assert false; // throw only in debug
            return List.of();
        }
        Timetable effectiveTimetable = timetablesIterator.next();
        if (!effectiveTimetable.isPossiblyEffective(currentTime)) {
            // Begin date is of before any timetable - let's try start from the first timetable
            currentTime = effectiveTimetable.getEffectiveDate();
        }

        // Make sure the selected timetable is effective (not shadowed) & find next effective timetable
        Timetable nextEffectiveTimetable = null;
        while (timetablesIterator.hasNext()) {
            final var timetable = timetablesIterator.next();
            if (timetable.isPossiblyEffective(currentTime)) {
                // Next timetable shadows current one, use it
                effectiveTimetable = timetable;
            } else {
                nextEffectiveTimetable = timetable;
                break;
            }
        }

        final var zone = currentTime.getZone();
        var weekDate = alignDateToWeekStart(currentTime.toLocalDate()).atStartOfDay(zone);

        while (true) {
            for (final Timetable.Entry entry : effectiveTimetable.getEntries()) {
                final var entryDate = weekDate.plusDays(entry.getDayOfWeek().ordinal());
                final var entryEnd = entryDate.plusMinutes(entry.getEndMinute());
                if (!currentTime.isBefore(entryEnd)) {
                    continue;
                }
                ZonedDateTime entryStart = entryDate.plusMinutes(entry.getStartMinute());
                if (currentTime.isAfter(entryStart)) {
                    entryStart = currentTime;
                }

                // Now one of 5 things happen:

                if (!entryStart.isBefore(end)) {
                    // 1. End reached between entries - return
                    return list;
                }
                if (entryEnd.isAfter(end)) {
                    // 2. End reached in middle of entry, add cutoff entry, then return
                    list.add(new SimpleEntry(Entry.Type.OPEN,
                            entryStart.toInstant(),
                            end.toInstant()));
                    return list;
                }

                if (nextEffectiveTimetable != null) {
                    if (nextEffectiveTimetable.isPossiblyEffective(entryStart)) {
                        // 3. Go next timetable
                        currentTime = null; // reuse the current time variable as flag for next timetable
                        break;
                    }
                    if (entryEnd.isAfter(nextEffectiveTimetable.getEffectiveDate())) {
                        // 4. Add cutoff entry and go next timetable
                        currentTime = null; // reuse the current time variable as flag for next timetable
                        list.add(new SimpleEntry(Entry.Type.OPEN,
                                entryStart.toInstant(),
                                nextEffectiveTimetable.getEffectiveDate().toInstant()));
                        break;
                    }
                }

                // 5. Add full entry and look for more with current timetable
                list.add(new SimpleEntry(Entry.Type.OPEN,
                        entryStart.toInstant(),
                        entryEnd.toInstant()));
            }

            if (currentTime == null) /* next timetable */ {
                assert nextEffectiveTimetable != null;
                currentTime = nextEffectiveTimetable.getEffectiveDate();
                effectiveTimetable = nextEffectiveTimetable;
                nextEffectiveTimetable = timetablesIterator.hasNext() ? timetablesIterator.next() : null;
            }
            else /* next week of the same timetable */ {
                weekDate = weekDate.plusDays(7);
            }
        }
    }

    /**
     * Finds schedule entries for the user between specified range of time (comparing start dates only).
     * Query might return partial data if no sufficient privileges. The {@link PublicSchedule#findScheduleEntries}
     * override will also include vague information on entries that current user have no privileges to view.
     * @param from Begin timestamp of the time range (inclusive).
     * @param to End timestamp of the time range (inclusive).
     * @return List of schedule entries found for the user in selected period.
     */
    public Stream<Entry> findScheduleEntries(Instant from, Instant to) {
        return Stream.concat(
                SimpleEntry.queryForUserBetweenDates(userReference, from, to).getResultStream(),
                Appointment.queryForUserBetweenDates(userReference, from, to).getResultStream());
    }

    /**
     * Generate {@link WeekPane.Entry}ies, which might include {@link ProxyWeekPaneEntry}ies for multi-day,
     * to represent given schedule entries for selected week.
     * @param weekStartDate week start date
     * @return list of week pane entries, ready to be set on display on the week pane
     */
    public List<WeekPane.Entry> generateWeekPaneEntriesForSchedule(LocalDate weekStartDate) {
        final var zone = ZoneId.systemDefault();
        final var weekEndDate = weekStartDate.plusDays(7);

        final var weekStart = weekStartDate.atStartOfDay(zone);
        final var weekEnd = weekEndDate.atStartOfDay(zone);

        final var results = new ArrayList<WeekPane.Entry>(80);
        results.addAll(generateScheduleEntriesFromTimetables(weekStart, weekEnd)); // the background
        results.addAll(generateWeekPaneEntriesForScheduleEntries(weekStartDate, // the content
                findScheduleEntries(weekStart.toInstant(), weekEnd.toInstant()).toList()));
        return results;
    }

    /**
     * Generate {@link WeekPane.Entry}ies, which might include {@link ProxyWeekPaneEntry}ies for multi-day,
     * to represent given {@link Schedule.Entry}ies properly. Order is kept natural if the passed schedule entries list
     * also were kept in natural order.
     * @param weekStartDate week start date
     * @param entries entries to be represented on the week pane
     * @return list of week pane entries, ready to be set on display on the week pane
     */
    public List<WeekPane.Entry> generateWeekPaneEntriesForScheduleEntries(LocalDate weekStartDate, List<Schedule.Entry> entries) {
        // TODO: unit testing
        final var zone = ZoneId.systemDefault();
        final var weekEndDate = weekStartDate.plusDays(7);
        final var resultList = new ArrayList<WeekPane.Entry>(80);

        for (final var entry : entries) {
            // Add original entry if it starts somewhere in the week
            final var beginDateTime = entry.getBeginTime().atZone(zone);
            if (!beginDateTime.toLocalDate().isBefore(weekStartDate)) {
                resultList.add(entry);
            }

            if (entry.doesCrossDays()) {
                var date = beginDateTime.toLocalDate();
                if (date.isBefore(weekStartDate)) {
                    date = weekStartDate;
                }

                final var endDateTime = entry.getEndTime().atZone(zone);
                final var dayBeforeEndDate = endDateTime.toLocalDate().minusDays(1);
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
                    final var endMinute = endDateTime.getHour() * 60 + endDateTime.getMinute();
                    resultList.add(new ProxyWeekPaneEntry(entry, endDateTime.getDayOfWeek(), 0, endMinute));
                }

            }
        }

        return resultList;
    }

    /**
     * Proxy entries to represent schedule entries that doesn't fit in single weekday inside a week pane.
     *
     * TODO: maybe refactor? shouldn't be Schedule.Entry? Currently every schedule-like weekPane
     *  uses WeekPane.Entry and multiple instanceof which looks wierd...
     */
    public static class ProxyWeekPaneEntry implements WeekPane.Entry {
        protected Entry original;
        public Entry getOriginal() {
            return original;
        }

        public DayOfWeek dayOfWeek;
        @Override
        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public int startMinute;
        @Override
        public int getStartMinute() {
            return startMinute;
        }

        public int endMinute;
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

            public boolean isBusy() {
                return switch (this) {
                    case NONE, OPEN, EXTRA -> false;
                    default -> true;
                };
            }

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

        default Duration getDuration() {
            return Duration.between(getBeginTime(), getEndTime());
        }

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

        /**
         * @param other other entry to test with
         * @return true if time of this and other entry overlaps (end time exclusive), false if no second is common
         */
        default boolean overlaps(Entry other) {
            return this.getEndTime().isAfter(other.getBeginTime())
                && this.getBeginTime().isBefore(other.getEndTime());
        }

        /**
         * @param other other entry to test with
         * @return true if this entry contains the other entry (by time range; types are omitted), false otherwise
         */
        default boolean contains(Entry other) {
            return !this.getBeginTime().isAfter(other.getBeginTime())
                && !this.getEndTime().isBefore(other.getEndTime());
        }
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
         * Prepares query to select all simple entries for given user in given range of time.
         * @param user Reference to user (user, patient or doctor).
         * @param from Begin timestamp of the time range (inclusive).
         * @param to End timestamp of the time range (inclusive).
         * @return Typed query for the simple entries (in natural order by begin timestamp)
         */
        static TypedQuery<SimpleEntry> queryForUserBetweenDates(UserReference user, Instant from, Instant to) {
            final var query = ClinicApplication.getEntityManager()
                    .createNamedQuery("ScheduleSimpleEntries.forUser.betweenDates", SimpleEntry.class);
            query.setParameter("user_id", user.getId());
            query.setParameter("from", from);
            query.setParameter("to", to);
            return query;
        }

        /**
         * Prepares query to select all schedule busy entries for given user in given range of time,
         * as simple entries' data (without ID, readonly), including appointments (without details).
         * @param from Begin timestamp of the time range (inclusive).
         * @param to End timestamp of the time range (inclusive).
         * @return Typed query for the busy entries (in natural order by begin timestamp).
         */
        @SuppressWarnings("unchecked")
        static TypedQuery<Entry> queryForBusyEntries(UserReference userReference, Instant from, Instant to) {
            final var query = ClinicApplication.getEntityManager()
                    .createNamedQuery("ScheduleBusyEntries.forUser.betweenDates", SimpleEntry.class);
            query.setParameter("user_id", userReference);
            query.setParameter("from", from);
            query.setParameter("to", to);
            return (TypedQuery<Entry>) (TypedQuery<? extends Entry>) query;
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
        public void setType(Entry.Type type) {
            assert type != Type.APPOINTMENT; // should be Appointment class instead simple entry
            this.type = type;
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
