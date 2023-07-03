package pl.edu.ur.pz.clinicapp.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class TemporalUtils {
    public static LocalDate alignDateToWeekStart(LocalDate date) {
        assert DayOfWeek.MONDAY.ordinal() == 0; // always true
        return date.minusDays(date.getDayOfWeek().ordinal());
    }

    public static LocalDate alignDateToWeekEnd(LocalDate date) {
        assert DayOfWeek.SUNDAY.ordinal() == 6; // always true
        return date.plusDays(7 - date.getDayOfWeek().ordinal());
    }
}
