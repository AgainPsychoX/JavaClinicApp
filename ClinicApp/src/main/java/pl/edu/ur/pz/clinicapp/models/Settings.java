package pl.edu.ur.pz.clinicapp.models;

import java.util.Locale;
import java.util.logging.Level;

public class Settings {
    public Level loggingLevel;

    public Locale locale;

    static public Settings Defaults = new Settings() {{
        loggingLevel = Level.ALL;
        locale = new Locale("pl", "PL");
    }};
}
