package pl.edu.ur.pz.clinicapp.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

public class ScheduleTest {
    @Nested
    @DisplayName("Tests for Schedule.generateWeekPaneEntriesForScheduleEntries")
    class WeekPaneEntriesGeneration {
        final static ZoneId zone = ZoneId.systemDefault();

        static Schedule.Entry entry(LocalDateTime begin, LocalDateTime end) {
            return new Schedule.SimpleEntry(begin.atZone(zone).toInstant(), end.atZone(zone).toInstant());
        }
        static ZonedDateTime zonedBeginOf(Schedule.Entry entry) {
            return entry.getBeginInstant().atZone(zone);
        }
        static ZonedDateTime zonedEndOf(Schedule.Entry entry) {
            return entry.getEndInstant().atZone(zone);
        }

        static LocalDate validWeekStart(LocalDate date) {
            Assertions.assertEquals(date.getDayOfWeek(), DayOfWeek.MONDAY);
            return date;
        }

        @Test
        void insideDay() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 17, 10, 30),
                    LocalDateTime.of(2023, 5, 17, 10, 45));
            Assertions.assertFalse(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 15)), List.of(scheduleEntry));
            Assertions.assertEquals(1, entries.size());
            Assertions.assertEquals(zonedBeginOf(scheduleEntry).getDayOfWeek(), entries.get(0).getDayOfWeek());
            Assertions.assertEquals(zonedBeginOf(scheduleEntry).toLocalTime(), entries.get(0).getStartAsLocalTime());
            Assertions.assertEquals(zonedEndOf(scheduleEntry).toLocalTime(), entries.get(0).getEndAsLocalTime());
            Assertions.assertEquals(scheduleEntry, entries.get(0).getScheduleEntry());
        }

        @Test
        void wholeDay() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 17, 0, 0),
                    LocalDateTime.of(2023, 5, 18, 0, 0));
            Assertions.assertFalse(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 15)), List.of(scheduleEntry));
            Assertions.assertEquals(1, entries.size());
            Assertions.assertEquals(zonedBeginOf(scheduleEntry).getDayOfWeek(), entries.get(0).getDayOfWeek());
            Assertions.assertEquals(0, entries.get(0).getStartMinute());
            Assertions.assertEquals(1440, entries.get(0).getEndMinute());
        }

        @Test
        void crossingDay() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 19, 19, 0),
                    LocalDateTime.of(2023, 5, 20, 2, 0));
            Assertions.assertTrue(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 15)), List.of(scheduleEntry));
            Assertions.assertEquals(2, entries.size());
            Assertions.assertEquals(zonedBeginOf(scheduleEntry).getDayOfWeek(), entries.get(0).getDayOfWeek());
            Assertions.assertEquals(zonedEndOf(scheduleEntry).getDayOfWeek(), entries.get(1).getDayOfWeek());
            Assertions.assertEquals(zonedBeginOf(scheduleEntry).toLocalTime(), entries.get(0).getStartAsLocalTime());
            Assertions.assertEquals(zonedEndOf(scheduleEntry).toLocalTime(), entries.get(1).getEndAsLocalTime());
        }

        @Test
        void startOutsideWeek() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 18, 13, 0), // Thursday
                    LocalDateTime.of(2023, 5, 25, 0, 0)); // Thursday (Wednesday end)
            Assertions.assertTrue(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 22)), List.of(scheduleEntry));

            Assertions.assertEquals(3, entries.size()); // Monday, Tuesday, Wednesday
            Assertions.assertEquals(DayOfWeek.MONDAY,    entries.get(0).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.TUESDAY,   entries.get(1).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.WEDNESDAY, entries.get(2).getDayOfWeek());

            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(0, entries.get(i).getStartMinute());
            }
            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(1440, entries.get(i).getEndMinute());
            }
        }

        @Test
        void endOutsideWeek() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 18, 13, 0), // Thursday
                    LocalDateTime.of(2023, 5, 25, 0, 0)); // Thursday (Wednesday end)
            Assertions.assertTrue(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 15)), List.of(scheduleEntry));

            Assertions.assertEquals(4, entries.size());
            Assertions.assertEquals(DayOfWeek.THURSDAY, entries.get(0).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.FRIDAY,   entries.get(1).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.SATURDAY, entries.get(2).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.SUNDAY,   entries.get(3).getDayOfWeek());

            Assertions.assertEquals(zonedBeginOf(scheduleEntry).toLocalTime(), entries.get(0).getStartAsLocalTime());
            for (int i = 1; i <= 3; i++) {
                Assertions.assertEquals(0, entries.get(i).getStartMinute());
            }
            for (int i = 0; i <= 3; i++) {
                Assertions.assertEquals(1440, entries.get(i).getEndMinute());
            }
        }

        @Test
        void wholeWeek() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 10, 16, 20),
                    LocalDateTime.of(2023, 5, 24, 16, 20));
            Assertions.assertTrue(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 15)), List.of(scheduleEntry));

            Assertions.assertEquals(7, entries.size());
            Assertions.assertEquals(DayOfWeek.MONDAY,    entries.get(0).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.TUESDAY,   entries.get(1).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.WEDNESDAY, entries.get(2).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.THURSDAY,  entries.get(3).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.FRIDAY,    entries.get(4).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.SATURDAY,  entries.get(5).getDayOfWeek());
            Assertions.assertEquals(DayOfWeek.SUNDAY,    entries.get(6).getDayOfWeek());

            for (int i = 0; i <= 3; i++) {
                Assertions.assertEquals(0, entries.get(i).getStartMinute());
                Assertions.assertEquals(1440, entries.get(i).getEndMinute());
            }
        }

        @Test
        void fullyOutsideRightBeforeWeekStart() {
            final var scheduleEntry = entry(
                    LocalDateTime.of(2023, 5, 8, 0, 0),
                    LocalDateTime.of(2023, 5, 15, 0, 0));
            Assertions.assertTrue(scheduleEntry.doesCrossDays(zone));

            final var entries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    validWeekStart(LocalDate.of(2023, 5, 15)), List.of(scheduleEntry));

            Assertions.assertEquals(0, entries.size());
        }
    }
}
