package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;

import javax.persistence.*;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import static pl.edu.ur.pz.clinicapp.utils.TemporalUtils.alignDateToWeekStart;

/**
 * Utility class that eases manipulation of user schedule and timetables.
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

    protected List<Timetable> cachedTimetables;

    public List<Timetable> getCachedTimetables() {
        if (cachedTimetables == null) {
            cachedTimetables = Timetable.forUser(userReference); // natural order
        }
        return Collections.unmodifiableList(cachedTimetables);
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
                    list.add(new SimpleEntry(Entry.Type.OPEN, entryStart.toInstant(), end.toInstant()));
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
                                entryStart.toInstant(), nextEffectiveTimetable.getEffectiveDate().toInstant()));
                        break;
                    }
                }

                // 5. Add full entry and look for more with current timetable
                list.add(new SimpleEntry(Entry.Type.OPEN, entryStart.toInstant(), entryEnd.toInstant()));
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
     * Generate {@link WeekPane.Entry}ies, which might include {@link ScheduleWeekPaneEntry}ies for multi-day,
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
        // TODO: consider passing timetable week pane entries directly instead making them schedule entries
        results.addAll(generateWeekPaneEntriesForScheduleEntries(weekStartDate, // the background
                generateScheduleEntriesFromTimetables(weekStart, weekEnd)));
        results.addAll(generateWeekPaneEntriesForScheduleEntries(weekStartDate, // the foreground
                findScheduleEntries(weekStart.toInstant(), weekEnd.toInstant()).toList()));
        return results;
    }

    /**
     * Generate {@link WeekPane.Entry}ies (as {@link ScheduleWeekPaneEntry}ies; multiple for multi-day entries),
     * to represent given {@link Schedule.Entry}ies properly. Order is kept natural if the passed schedule entries list
     * also were kept in natural order.
     * @param weekStartDate week start date (monday).
     * @param scheduleEntries entries to be represented on the week pane
     * @return list of week pane entries, ready to be set on display on the week pane
     */
    static public List<ScheduleWeekPaneEntry> generateWeekPaneEntriesForScheduleEntries(
            LocalDate weekStartDate, List<Schedule.Entry> scheduleEntries) {
        final var zone = ZoneId.systemDefault();
        final var weekEndDate = weekStartDate.plusDays(7); // exclusive
        final var resultList = new ArrayList<ScheduleWeekPaneEntry>(80);

        for (final var scheduleEntry : scheduleEntries) {
            final var beginDateTime = scheduleEntry.getBeginInstant().atZone(zone);
            if (!beginDateTime.toLocalDate().isBefore(weekEndDate)) {
                continue;
            }

            final var endDateTime = scheduleEntry.getEndInstant().atZone(zone);
            if (endDateTime.toLocalDate().isBefore(weekStartDate)) {
                continue;
            }

            if (scheduleEntry.doesCrossDays(zone)) {
                var date = beginDateTime.toLocalDate();

                if (date.isBefore(weekStartDate)) {
                    date = weekStartDate;
                }
                else {
                    // Add first entry if starts inside the week
                    final var startMinute = beginDateTime.getHour() * 60 + beginDateTime.getMinute();
                    resultList.add(new ScheduleWeekPaneEntry(scheduleEntry, date.getDayOfWeek(), startMinute, 1440));

                    date = date.plusDays(1);
                }

                if (endDateTime.toLocalDate().isBefore(weekEndDate)) /* the original entry ends inside the week */ {
                    // Add full days entries, except last one
                    while (date.isBefore(endDateTime.toLocalDate())) {
                        resultList.add(new ScheduleWeekPaneEntry(scheduleEntry, date.getDayOfWeek(), 0, 1440));
                        date = date.plusDays(1);
                    }

                    // Add last entry which might be not full day
                    var end = endDateTime.getHour() * 60 + endDateTime.getMinute();
                    if (end != 0) {
                        resultList.add(new ScheduleWeekPaneEntry(scheduleEntry, date.getDayOfWeek(), 0, end));
                    }
                }
                else /* the original entry lasts after the weekend */ {
                    // Add full days entries
                    while (date.isBefore(weekEndDate)) {
                        resultList.add(new ScheduleWeekPaneEntry(scheduleEntry, date.getDayOfWeek(), 0, 1440));
                        date = date.plusDays(1);
                    }
                }
            }
            else {
                // Add the entry as singular one
                final var start = beginDateTime.getHour() * 60 + beginDateTime.getMinute();
                var end = endDateTime.getHour() * 60 + endDateTime.getMinute();
                if (end == 0) {
                    assert beginDateTime.toLocalDate().plusDays(1).isEqual(endDateTime.toLocalDate());
                    end = 1440; // whole day entry
                }
                resultList.add(new ScheduleWeekPaneEntry(scheduleEntry, beginDateTime.getDayOfWeek(), start, end));
            }
        }

        return resultList;
    }

    /**
     * Puppet {@link WeekPane.Entry} implementation, used to represent {@link Schedule.Entry} inside a {@link WeekPane}.
     */
    public static class ScheduleWeekPaneEntry implements WeekPane.Entry {
        protected Schedule.Entry scheduleEntry;
        public Schedule.Entry getScheduleEntry() {
            return scheduleEntry;
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

        public ScheduleWeekPaneEntry(Entry scheduleEntry) {
            final var zone = ZoneId.systemDefault();
            assert !scheduleEntry.doesCrossDays(zone);
            final var beginDateTime = scheduleEntry.getBeginInstant().atZone(zone);
            final var endDateTime = scheduleEntry.getEndInstant().atZone(zone);
            this.scheduleEntry = scheduleEntry;
            this.dayOfWeek = beginDateTime.getDayOfWeek();
            this.startMinute = beginDateTime.getHour() * 60 + beginDateTime.getMinute();
            this.endMinute = endDateTime.getHour() * 60 + endDateTime.getMinute();
        }

        public ScheduleWeekPaneEntry(Entry scheduleEntry, DayOfWeek dayOfWeek, int startMinute, int endMinute) {
            this.scheduleEntry = scheduleEntry;
            this.dayOfWeek = dayOfWeek;
            this.startMinute = startMinute;
            this.endMinute = endMinute;
        }
    }

    /**
     * Schedule entries representing allocated time pieces in the schedule.
     */
    public interface Entry {
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
        Instant getBeginInstant();
        Instant getEndInstant();

        default Duration getDuration() {
            return Duration.between(getBeginInstant(), getEndInstant());
        }

        /**
         * @return true if the entry crosses over multiple days, false otherwise.
         */
        default boolean doesCrossDays(ZoneId zone) {
            final var endZoned = getEndInstant().atZone(zone);
            final var startDay = getBeginInstant().atZone(zone).toLocalDate();
            final var endDay = endZoned.toLocalDate();
            if (startDay.equals(endDay)) {
                return false;
            }
            if (startDay.plusDays(1).isEqual(endDay)) {
                return endZoned.toLocalTime().toSecondOfDay() > 0; // zero means not crossing, as time of 24:00
            }
            return true;
        }

        /**
         * @param other other entry to test with
         * @return true if time of this and other entry overlaps (end time exclusive), false if no second is common
         */
        default boolean overlaps(Entry other) {
            return this.getEndInstant().isAfter(other.getBeginInstant())
                && this.getBeginInstant().isBefore(other.getEndInstant());
        }

        /**
         * @param other other entry to test with
         * @return true if this entry contains the other entry (by time range; types are omitted), false otherwise
         */
        default boolean contains(Entry other) {
            return !this.getBeginInstant().isAfter(other.getBeginInstant())
                && !this.getEndInstant().isBefore(other.getEndInstant());
        }
    }

    /**
     * Simple schedule entries
     */
    @Entity
    @Table(name = "schedule_simple_entries")
    @NamedQueries({
            @NamedQuery(name = "ScheduleSimpleEntries.forUser.betweenDates", query = """
                    FROM Schedule$SimpleEntry e WHERE e.user.id = :user_id AND :from < e.end AND e.begin < :to
                    ORDER BY e.begin
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
                            @ColumnResult(name = "begin_time", type = ZonedDateTime.class),
                            @ColumnResult(name = "end_time",   type = ZonedDateTime.class),
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
        protected Instant begin;
        @Override
        public Instant getBeginInstant() {
            return begin;
        }
        public void setBegin(Instant begin) {
            this.begin = begin;
            // TODO: swap dates if ordering is invalid
        }

        @Column(nullable = false)
        protected Instant end;
        @Override
        public Instant getEndInstant() {
            return end;
        }
        public void setEndInstant(Instant end) {
            this.end = end;
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

        public SimpleEntry(Instant begin, Instant end) {
            this.type = Type.NONE;
            this.begin = begin;
            this.end = end;
        }

        public SimpleEntry(Entry.Type type, Instant begin, Instant end) {
            this.type = type;
            this.begin = begin;
            this.end = end;
        }

        /**
         * Constructor for `schedule_busy_view` {@link SqlResultSetMapping}
         * (see annotations for the {@link Schedule.SimpleEntry})
         */
        @SuppressWarnings("unused")
        private SimpleEntry(Integer user_id, Instant begin_time, Instant end_time, String type) {
            this.type = Entry.Type.valueOf(type);
            this.begin = begin_time;
            this.end = end_time;
            // TODO: how to set use here? anyways, it's not like it's really needed anyways, as it's immutable entry
        }
    }
}
