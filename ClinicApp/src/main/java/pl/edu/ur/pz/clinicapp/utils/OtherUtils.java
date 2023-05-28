package pl.edu.ur.pz.clinicapp.utils;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class OtherUtils {
    public static boolean isStringNullOrBlank(String string) {
        return string == null || string.isBlank();
    }

    @SafeVarargs
    public static <T> T nullCoalesce(@Nullable T... params) {
        for (T param : params) {
            if (param != null) {
                return param;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static void doNothing(Object... params) {
        // This function really does nothing, but good luck removing it without introducing errors or warnings :)
    }

    public static Stage getStageFromEvent(Event event) {
        return (Stage)((Node) event.getSource()).getScene().getWindow();
    }

    public static Stage getStageFromNode(Node node) {
        final var scene = node.getScene();
        if (scene == null) return null;
        return (Stage) scene.getWindow();
    }

    /**
     * Helper method to ask user for confirmation by boolean dialog.
     * False is always default, not only when false button is pressed, but also on dialog close.
     * @param title the title of the alert dialog
     * @param content the content of the alert dialog
     * @param falseButton the button type for the false button,
     *                    required to avoid "cancel" on cancel confirmation (UX anti-pattern)
     * @return true if the user clicks the true button, false otherwise (default).
     */
    public static boolean requireConfirmation(String title, String content, ButtonType falseButton) {
        return requireConfirmation(Alert.AlertType.WARNING, title, content, ButtonType.YES, falseButton);
    }

    /**
     * Helper method to ask user for confirmation by boolean dialog.
     * False is always default, not only when false button is pressed, but also on dialog close.
     * @param type the type of the alert dialog
     * @param title the title of the alert dialog
     * @param content the content of the alert dialog
     * @param trueButton the button type for the true button
     * @param falseButton the button type for the false button
     * @return true if the user clicks the true button, false otherwise (default).
     */
    public static boolean requireConfirmation(Alert.AlertType type, String title, String content,
                                          ButtonType trueButton, ButtonType falseButton) {
        final var dialog = new Alert(type);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        dialog.getButtonTypes().setAll(trueButton, falseButton);
        return dialog.showAndWait().orElse(ButtonType.CLOSE) == trueButton;
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
