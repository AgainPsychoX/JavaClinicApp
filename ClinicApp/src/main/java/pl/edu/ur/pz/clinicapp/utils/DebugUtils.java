package pl.edu.ur.pz.clinicapp.utils;

import java.util.Collections;
import java.util.logging.*;
import java.util.stream.Collectors;

public class DebugUtils {
    static private Level getLoggerEffectiveLevel(Logger logger) {
        Level level;
        do {
            level = logger.getLevel();
            logger = logger.getParent();
        }
        while (level == null);
        return level;
    }

    static public void printOutAllLoggers(String prefix) {
        final var lm = LogManager.getLogManager();
        var stream = Collections.list(lm.getLoggerNames()).stream();
        if (prefix != null) {
            stream = stream.filter(s -> s.startsWith(prefix));
        }
        stream = stream.map(s -> (s + "\twith level = " + getLoggerEffectiveLevel(lm.getLogger(s))));
        System.out.println("Available loggers: \n+ " + stream.collect(Collectors.joining("+ \n")));
    }
}
