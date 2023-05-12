package pl.edu.ur.pz.clinicapp.utils;

import javafx.scene.control.Alert;

public class ProblemHandlingUtils {
    /**
     * Util to get cause chain string (by `getMessage`) from exceptions.
     *
     * Adapted from https://stackoverflow.com/a/17963553/4880243
     * @param root Exception to create string for.
     * @return Stringified exception cause chain.
     */
    public static String getCauseChainString(Throwable root, boolean onlyChildren) {
        final var sb = new StringBuilder(200);
        if (!onlyChildren) {
            sb.append(root.getClass()).append(": ").append(root.getLocalizedMessage());
        }
        Throwable next = root.getCause();
        while (next != null) {
            sb.append("Caused by: ").append(next.getClass()).append(": ").append(next.getLocalizedMessage()).append("\n");
            next = next.getCause();
        }
        if (sb.length() > 0 && root.getCause() != null) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String getCauseChainString(Throwable e) {
        return getCauseChainString(e, false);
    }

    public static void reportExceptionNicely(final String alertTitle, Throwable exception) {
        final var causeChainString = getCauseChainString(exception, true);
        Alert alert = new Alert(Alert.AlertType.ERROR) {{
            setTitle(alertTitle);
            setHeaderText(alertTitle);
            setContentText(exception.getLocalizedMessage() + (causeChainString.isEmpty() ? "" : ("\n\nSzczegóły:\n" + causeChainString)));
            setResizable(true);
            getDialogPane().setPrefWidth(480);
        }};
        alert.showAndWait();
        exception.printStackTrace();
    }
}
