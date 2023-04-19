package pl.edu.ur.pz.clinicapp.utils;

import java.util.Collections;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

public class DebugUtils {
    static public void printOutAllLoggers(String prefix) {
        var stream = Collections.list(LogManager.getLogManager().getLoggerNames()).stream();
        if (prefix != null) {
            stream = stream.filter(s -> s.startsWith(prefix));
        }
        System.out.println("Available loggers: \n+ " + stream.collect(Collectors.joining("+ \n")));
    }
}
