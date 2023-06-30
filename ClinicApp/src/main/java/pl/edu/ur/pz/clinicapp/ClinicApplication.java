package pl.edu.ur.pz.clinicapp;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.dialogs.LoginDialog;
import pl.edu.ur.pz.clinicapp.dialogs.RegisterDialog;
import pl.edu.ur.pz.clinicapp.localization.JavaFxBuiltInsLocalizationFix;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.views.PrescriptionDetailsView;
import pl.edu.ur.pz.clinicapp.views.ReferralDetailsView;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.isStringNullOrBlank;
import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.linkStageSizeToPane;

public class ClinicApplication extends Application {
    private static final Logger logger = Logger.getLogger(ClinicApplication.class.getName());

    @Inject
    static ApplicationContext context;

    /**
     * Provides access to the entity manager that allows operating persistable data,
     * connected with login details of current user, or anonymous if no one logged-in.
     * @return Entity manager.
     */
    public static EntityManager getEntityManager() {
        return context.getEntityManager();
    }

    /**
     * Provides access to currently logged-in user. Makes sure the instance is managed by the entity manager.
     * @return Currently logged-in user, or null if no one logged-in.
     */
    public static User getUser() {
        return context.getUser();
    }

    /**
     * Provides access to currently logged-in user or throws if no one logged-in.
     * @throws IllegalStateException When no user is logged-in.
     * @return Currently logged-in user
     */
    public static User requireUser() throws IllegalStateException {
        return context.requireUser();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load logging custom properties if any
        try (FileInputStream loggingPropertiesFile = new FileInputStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(loggingPropertiesFile);
        }
        catch (FileNotFoundException e) {
            // Ignore, system defaults will be used, most likely INFO level only to console.
            LogManager.getLogManager().readConfiguration();
        }
        logger.finest("Hello!");

        final var injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ApplicationContext.class).to(DefaultApplicationContext.class);
            }
        });
        context = injector.getInstance(ApplicationContext.class);

        final var locale = context.getLocale();
        JavaFxBuiltInsLocalizationFix.injectLocalizationForJavaFxBuiltInControls(locale);
        Locale.setDefault(locale);

        if (isSeedingAvailable()) {
            if (showConfirmSeedingDialog()) {
                seedDatabase();
            }
        }

        connectToDatabaseAnonymously();
        while (waitForLogin()) {
            spawnMainWindow();
        }
    }

    static private boolean tryRememberedLogin = true;

    private boolean waitForLogin() {
        final var dialog = tryRememberedLogin
                ? new LoginDialog(
                    context.getProperties().getProperty("login.remember.identity"),
                    context.getProperties().getProperty("login.remember.password"))
                : new LoginDialog(); // without prefill with remembered stuff
        dialog.showAndWait();
        if (getUser() == null) {
            Platform.exit();
            return false;
        }
        return true;
    }

    static private void handleUnexpectedDatabaseConnectionError(Exception e, String extraMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Wystąpił błąd w czasie łączenia do bazy danych.\n\n" +
                (extraMessage == null ? "" : (extraMessage + "\n\n"))
                + "Szczegóły:\n" + e.getLocalizedMessage());
        alert.setTitle("Błąd w czasie łączenia do bazy danych");
        alert.setHeaderText(null);
        alert.showAndWait();
        System.exit(1);
    }

    private boolean isSeedingAvailable() {
        return !isStringNullOrBlank(context.getProperties().getProperty("seeding.username"));
    }

    private boolean showConfirmSeedingDialog() {
        final var dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Reinitializacja bazy danych");
        dialog.setHeaderText(null);
        dialog.setContentText("Podano parametry dla reinicjalizacji bazy danych.\n" +
                "Czy chcesz kontynuować? Wszystkie dane zostaną utracone, " +
                "schemat zostanie utworzony na nowo i wypełniony przykładowymi danymi.");
        dialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Avoid seeding (which resets data) being default option in the dialog
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton((false));
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton((true));

        return dialog.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    static private void seedDatabase() {
        // TODO: allow migration only (minimalist/structure only seeding - no example data)
        try {
            context.seedDatabase();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error seeding database!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wystąpił błąd w trakcie reinicjalizacji bazy danych.");
            alert.setTitle("Błąd w trakcie reinicjalizacji do bazy danych");
            alert.setHeaderText(null);
            alert.showAndWait();
            System.exit(1);
        }
    }

    static private void connectToDatabaseAnonymously() {
        try {
            context.connectToDatabaseAnonymously();
        }
        catch (PersistenceException e) {
            handleUnexpectedDatabaseConnectionError(e, "Sprawdź, czy dla zadanych ustawień aplikacji " +
                    "baza danych jest poprawnie skonfigurowana, uruchomiona i dostępna.");
        }
    }

    static private void connectToDatabaseAsUser(String emailOrPESEL, String password) throws LoginException {
        try {
            context.connectToDatabaseAsUser(emailOrPESEL, password);
        }
        catch (PersistenceException e) {
            handleUnexpectedDatabaseConnectionError(e, null);
        }
    }

    static public void logOut() {
        connectToDatabaseAnonymously();
        logger.info("Logged out");
        tryRememberedLogin = false;
    }

    static public void logIn(String emailOrPESEL, String password) throws LoginException {
        logger.info("Logging in as `%s`".formatted(emailOrPESEL));
        connectToDatabaseAsUser(emailOrPESEL, password);
        logger.info("Logged in");
    }

    private void spawnMainWindow() throws IOException {
        final var stage = new Stage();
        final var loader = new FXMLLoader(ClinicApplication.class.getResource("MainWindow.fxml"));
        final BorderPane pane = loader.load();
        final var scene = new Scene(pane);
        stage.setTitle("ClinicApp");
        stage.setScene(scene);
        linkStageSizeToPane(stage, pane);
//        mainWindowController = loader.getController();
//        stage.setWidth(pane.getMinWidth());
//        stage.setHeight(pane.getMinHeight());
        stage.setOnCloseRequest(we -> {
            if((ReferralDetailsView.getEditState() && !ReferralDetailsView.exitConfirm())
            || (PrescriptionDetailsView.getEditState() && !PrescriptionDetailsView.exitConfirm())
            || (RegisterDialog.getEditState() && !RegisterDialog.exitConfirm())){
                we.consume();
            }else {
                logOut();
            }
        });
        stage.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}