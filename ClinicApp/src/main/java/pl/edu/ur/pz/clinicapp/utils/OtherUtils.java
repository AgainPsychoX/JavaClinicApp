package pl.edu.ur.pz.clinicapp.utils;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class OtherUtils {
    public static boolean isStringNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static Stage getStageFromEvent(Event event) {
        return (Stage)((Node) event.getSource()).getScene().getWindow();
    }

    public static Stage getStageFromNode(Node node) {
        final var scene = node.getScene();
        if (scene == null) return null;
        return (Stage) scene.getWindow();
    }

    public enum OS {
        Windows,
        Linux,
        Mac,
        Other
    };

    private static OS detectedOS = null;

    public static OS getDetectedOS() {
        if (detectedOS == null) {
            final String name = System.getProperty("os.name").toLowerCase();
            if (name.contains("win")) {
                detectedOS = OS.Windows;
            }
            else if (name.contains("nix") || name.contains("nux")  || name.contains("aix")) {
                detectedOS = OS.Linux;
            }
            else if (name.contains("mac") || name.contains("darwin")) {
                detectedOS = OS.Mac;
            }
            else {
                detectedOS = OS.Other;
            }
        }
        return detectedOS;
    }

    public static void openWebPage(String url) {
        // TODO: prevent RCE xD
        try {
            Runtime rt = Runtime.getRuntime();
            switch (getDetectedOS()) {
                case Windows -> {
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                }
                case Linux -> {
                    rt.exec(new String[] { "sh", "-c", "URL=\"" + url + "\"; xdg-open $URL || sensible-browser $URL || x-www-browser $URL || gnome-open $URL"});
                }
                case Mac -> {
                    rt.exec("open " + url);
                }
                case Other -> {
                    System.err.println("Couldn't open web page in browser, unsupported operating system.");
                }
            }
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void runDelayed(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try { Thread.sleep(millis); }
                catch (InterruptedException ignored) {}
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }
}
