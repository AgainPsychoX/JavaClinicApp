package pl.edu.ur.pz.clinicapp.utils;

import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Utility class to deal with formatting of durations (in milliseconds).
 */
public class DurationUtils {
    public enum Format {
        /**
         * Example: `1d 2h 3m 4s 5ms`
         */
        WORDS_SHORT,
        /**
         * Example: `1 day, 2 hours, 3 minutes, 4 seconds, 5 milliseconds`
         */
        WORDS_LONG,
        /**
         * Example: `26:03:04,005`
         */
        TIMER,
    }

    public enum Precision {
        MILLISECONDS,
        SECOND,
        MINUTE,
        HOUR,
        DAY,
    }

    // TODO: rounding specification?
//    public enum Rounding {
//        FLOOR,
//        NORMAL,
//        CEIL,
//    }

    private static final long MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1);
    private static final long MILLIS_IN_HOUR = TimeUnit.HOURS.toMillis(1);
    private static final long MILLIS_IN_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final long MILLIS_IN_SECOND = TimeUnit.SECONDS.toMillis(1);

    private static long[] destructDurationToParts(long duration) {
        final var parts = new long[5];
        parts[4] = duration / MILLIS_IN_DAY;
        duration %= MILLIS_IN_DAY;
        parts[3] = duration / MILLIS_IN_HOUR;
        duration %= MILLIS_IN_HOUR;
        parts[2] = duration / MILLIS_IN_MINUTE;
        duration %= MILLIS_IN_MINUTE;
        parts[1] = duration / MILLIS_IN_SECOND;
        duration %= MILLIS_IN_SECOND;
        parts[0] = duration;
        return parts;
    }

    public static String[] formatMillisecondsToShortAndLongWords(final int duration) {
        return formatMillisecondsToShortAndLongWords(duration, Precision.SECOND);
    }

    public static String[] formatMillisecondsToShortAndLongWords(final long duration, final Precision precision) {
        final var parts = destructDurationToParts(duration);

        final var ssb = new StringBuilder(24);
        final var lsb = new StringBuilder(56);

        // TODO: localization?

        if (parts[4] > 0) {
            ssb.append(parts[4]);
            ssb.append("d ");
            lsb.append(parts[4]);
            if (parts[4] == 1) {
                lsb.append(" dzień");
            }
            else {
                lsb.append(" dni");
            }
        }
        if (precision.compareTo(Precision.HOUR) <= 0) {
            if (parts[3] > 0) {
                ssb.append(parts[3]);
                ssb.append("h ");
                if (lsb.length() > 0) {
                    lsb.append(", ");
                }
                lsb.append(parts[3]);
                lsb.append(" godzin");
                if (parts[3] == 1) {
                    lsb.append('a');
                }
                else if (parts[3] <= 4) {
                    lsb.append('y');
                }
            }
            if (precision.compareTo(Precision.MINUTE) <= 0) {
                if (parts[2] > 0) {
                    ssb.append(parts[2]);
                    ssb.append("m ");
                    if (lsb.length() > 0) {
                        lsb.append(", ");
                    }
                    lsb.append(parts[2]);
                    lsb.append(" minut");
                    if (parts[2] == 1) {
                        lsb.append('a');
                    }
                    else if (parts[2] <= 4) {
                        lsb.append('y');
                    }
                }
                if (precision.compareTo(Precision.SECOND) <= 0) {
                    if (parts[1] > 0) {
                        ssb.append(parts[1]);
                        ssb.append("s ");
                        if (lsb.length() > 0) {
                            lsb.append(", ");
                        }
                        lsb.append(parts[1]);
                        lsb.append(" sekund");
                        if (parts[1] == 1) {
                            lsb.append('a');
                        }
                        else if (parts[1] <= 4) {
                            lsb.append('y');
                        }
                    }
                    if (precision.compareTo(Precision.MILLISECONDS) <= 0) {
                        if (parts[0] > 0) {
                            ssb.append(parts[0]);
                            ssb.append("ms ");
                            if (lsb.length() > 0) {
                                lsb.append(", ");
                            }
                            lsb.append(parts[0]);
                            lsb.append(" milisekund");
                            if (parts[0] == 1) {
                                lsb.append('a');
                            }
                            else if (parts[0] <= 4) {
                                lsb.append('y');
                            }
                        }
                    }
                }
            }
        }

        // Chop away space after last unit
        if (ssb.length() == 0) {
            if (duration == 0) {
                ssb.append("0");
                lsb.append("0");
            }
        }
        else {
            ssb.setLength(ssb.length() - 1);
        }

        return new String[] {
                ssb.toString(),
                lsb.toString(),
        };
    }

    public static String formatMillisecondsToTimer(final int duration, final Precision precision) {
        final var parts = destructDurationToParts(duration);
        switch (precision) {
            case MILLISECONDS -> {
                return String.format("%d:%02d:%02d,%03d", (parts[4] * 24 + parts[3]), parts[2], parts[1], parts[0]);
            }
            case SECOND -> {
                return String.format("%d:%02d:%02d", (parts[4] * 24 + parts[3]), parts[2], parts[1]);
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Regex pattern for parsing timer-like duration string.
     */
    private static final Pattern durationByTimerPattern = Pattern.compile("^(?:(\\d+):)?(\\d+):(\\d+):(\\d+)(?:[,.](\\d+))?");

    /**
     * Regex pattern for parsing words based duration description.
     */
    private static final Pattern durationByWordsPattern = Pattern.compile("^\\s*(?:(\\d+)\\s*d(?:ays?)?[\\s,]*)?(?:(\\d+)\s*h(?:ours?)?[\\s,]*)?(?:(\\d+)\\s*m(?:in(?:ute)?s?)?[\\s,]*)?(?:(\\d+)\\s*s(?:ec(?:ond)?s?)?[\\s,]*)?(?:(\\d+)\\s*(?:ms|milis)[\\s,]*)?", Pattern.CASE_INSENSITIVE);

    private static int parseIntegerOrZero(String string) {
        return string == null ? 0 : Integer.parseInt(string);
    }

    /**
     * Parses duration string to integer (milliseconds), detecting used pattern.
     * @param string String to parse.
     * @return Number of milliseconds for the duration.
     */
    private static int parseDurationFromStringSmart(String string) {
        final var pattern = string.contains(":") ? durationByTimerPattern : durationByWordsPattern;
        final var matcher = pattern.matcher(string);
        if (!matcher.find()) {
            return 0;
        }
        final var days = parseIntegerOrZero(matcher.group(1));
        final var hours = parseIntegerOrZero(matcher.group(2));
        final var minutes = parseIntegerOrZero(matcher.group(3));
        final var seconds = parseIntegerOrZero(matcher.group(4));
        final var millis = parseIntegerOrZero(matcher.group(5));
        return (((days * 24 + hours) * 60 + minutes) * 60 + seconds) * 1000 + millis;
    }

    /**
     * Prepares new string converter instance for displaying and reading duration in string format(s).
     *
     * Examples for supported formats:
     *      + `1d 2h 3m 4s 5ms`
     *      + `1day, 2 hours 3min 4seconds 005millis`
     *      + `26:03:04,005`
     *      + `1:02:03:04`
     *
     * Output format specified by `Format` enum, defaults to `TIMER` (example: `26:03:04`).
     */
    public static StringConverter<Integer> getStringConverterForDuration(final Format format, final Precision precision) {
        switch (format) {
            case WORDS_SHORT, WORDS_LONG -> {
                // TODO: localization?
                // TODO: should be separate impl instead reusing.
                return new StringConverter<>() {
                    @Override
                    public String toString(Integer integer) {
                        return formatMillisecondsToShortAndLongWords(integer, precision)[format == Format.WORDS_SHORT ? 0 : 1];
                    }
                    @Override
                    public Integer fromString(String string) {
                        return parseDurationFromStringSmart(string);
                    }
                };
            }
            case TIMER -> {
                return new StringConverter<>() {
                    @Override
                    public String toString(Integer integer) {
                        return formatMillisecondsToTimer(integer, precision);
                    }
                    @Override
                    public Integer fromString(String string) {
                        return parseDurationFromStringSmart(string);
                    }
                };
            }
        }
        throw new UnsupportedOperationException();
    }

    public static StringConverter<Integer> getStringConverterForDuration(final Format format) {
        return getStringConverterForDuration(format, Precision.MILLISECONDS);
    }

    public static StringConverter<Integer> getStringConverterForDuration() {
        return getStringConverterForDuration(Format.WORDS_SHORT);
    }

    public static <T extends Cell<? extends Integer>> void updateCellItemForDuration(T cell, Integer duration, boolean empty) {
        if (duration == null || empty) {
            cell.setText(null);
        }
        else {
            final String[] shortAndLong = formatMillisecondsToShortAndLongWords(duration, Precision.MILLISECONDS);
            cell.setText(shortAndLong[0]); // short
        }
        cell.setGraphic(null);
    }

    public static <T> TableCell<T, Integer> createTableCellForDuration() {
        return new TableCell<>() {
            @Override
            public void updateItem(Integer duration, boolean empty) {
                super.updateItem(duration, empty);
                updateCellItemForDuration(this, duration, empty);
            }
        };
    }
}
