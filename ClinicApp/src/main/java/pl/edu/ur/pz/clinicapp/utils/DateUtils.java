package pl.edu.ur.pz.clinicapp.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Date utils for processing templates
 */
final public class DateUtils {
    /**
     * Converts
     * @param instant Date to convert
     * @return {@link java.time.LocalDateTime}
     */
    public LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
