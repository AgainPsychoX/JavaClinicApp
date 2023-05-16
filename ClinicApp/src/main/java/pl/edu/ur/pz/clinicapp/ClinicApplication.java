package pl.edu.ur.pz.clinicapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.service.spi.ServiceException;
import pl.edu.ur.pz.clinicapp.dialogs.LoginDialog;
import pl.edu.ur.pz.clinicapp.dialogs.RegisterDialog;
import pl.edu.ur.pz.clinicapp.localization.JavaFxBuiltInsLocalizationFix;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.views.MyAccount;
import pl.edu.ur.pz.clinicapp.views.PrescriptionDetailsView;
import pl.edu.ur.pz.clinicapp.views.ReferralDetailsView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.isStringNullOrEmpty;

public class ClinicApplication extends Application {
    private static final Logger logger = Logger.getLogger(ClinicApplication.class.getName());

    private Properties properties;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private User user;
    private MainWindowController mainWindowController;

    static private ClinicApplication instance;

    public static Properties getProperties() {
        return instance.properties;
    }

    public static EntityManager getEntityManager() {
        return instance.entityManager;
    }

    public static User getUser() {
        return instance.user;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;

        // Load logging custom properties if any
        try (FileInputStream loggingPropertiesFile = new FileInputStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(loggingPropertiesFile);
        }
        catch (FileNotFoundException e) {
            // Ignore, system defaults will be used, most likely INFO level only to console.
            LogManager.getLogManager().readConfiguration();
        }
        logger.finest("Hello!");

        // Load app properties
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("app.default.properties")) {
            final var defaults = new Properties() {{
                load(inputStream);
            }};
            properties = new Properties(defaults);
        }
        try (FileInputStream appPropertiesFile = new FileInputStream("app.properties")) {
            properties.load(appPropertiesFile);
        }
        catch (FileNotFoundException e) {
            // Ignore, defaults will be used.
        }

        final var locale = new Locale(properties.getProperty("locale"));
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

    private boolean tryRememberedLogin = true;

    private boolean waitForLogin() {
        final var dialog = tryRememberedLogin
                ? new LoginDialog(
                    properties.getProperty("login.remember.identity"),
                    properties.getProperty("login.remember.password"))
                : new LoginDialog(); // without prefill with remembered stuff
        dialog.showAndWait();
        if (user == null) {
            Platform.exit();
            return false;
        }
        return true;
    }

    private void handleUnexpectedDatabaseConnectionError(Exception e, String extraMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Wystąpił błąd w czasie łączenia do bazy danych.\n\n" +
                (extraMessage == null ? "" : (extraMessage + "\n\n"))
                + "Szczegóły:\n" + e.getLocalizedMessage());
        alert.setTitle("Błąd w czasie łączenia do bazy danych");
        alert.setHeaderText(null);
        alert.showAndWait();
        System.exit(1);
    }

    private void disconnectFromDatabase() {
        // TODO: gracefully disconnect from the database (if connected)
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    private boolean isSeedingAvailable() {
        return !isStringNullOrEmpty(properties.getProperty("seeding.username"));
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

    private void seedDatabase() {
        disconnectFromDatabase();
        logger.fine("----------------------------------------------------------------");
        logger.info("Seeding database...");
        try {
            // For seeding we need superuser and special setting, so we shadow default settings from `persistence.xml`.
            entityManagerFactory = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                    Map.entry("hibernate.connection.username", properties.getProperty("seeding.username")),
                    Map.entry("hibernate.connection.password", properties.getProperty("seeding.password")),
                    Map.entry("hibernate.hbm2ddl.auto", "create")
            ));
            entityManager = entityManagerFactory.createEntityManager();
            logger.info("Finished seeding!");
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
        finally {
            logger.fine("----------------------------------------------------------------");
        }
    }

    /**
     * Connects to database anonymously, in order to fetch user details while logging in or view public data as guest.
     */
    private void connectToDatabaseAnonymously() {
        disconnectFromDatabase();
        try {
            // For anonymous connect, the login details are used from  `persistence.xml` along other settings.
            entityManagerFactory = Persistence.createEntityManagerFactory("default");
            entityManager = entityManagerFactory.createEntityManager();
        }
        catch (ServiceException e) {
            handleUnexpectedDatabaseConnectionError(e, "Sprawdź, czy dla zadanych ustawień aplikacji " +
                    "baza danych jest poprawnie skonfigurowana, uruchomiona i dostępna.");
        }
    }

    private void connectToDatabaseAsUser(String emailOrPESEL, String password) throws LoginException {
        try {
            final var username = User.getDatabaseUsernameForInput(emailOrPESEL);
            logger.fine("Database username for '%s' is '%s'".formatted(emailOrPESEL, username));

            // We shadow default (anonymous) login details from `persistence.xml` with user specific ones.
            final var emf = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                    Map.entry("hibernate.connection.username", username),
                    Map.entry("hibernate.connection.password", password)
            ));
            final var em = emf.createEntityManager();

            // Late replace to prevent reconnecting as anonymous on login failures and allow database username fetching
            disconnectFromDatabase();
            entityManagerFactory = emf;
            entityManager = em;
        }
        catch (ServiceException e) {
            if (e.getCause() instanceof GenericJDBCException genericJDBCException) {
                final var text = genericJDBCException.getSQLException().toString();
                if (text.contains("password") && text.contains("fail")) {
                    throw new LoginException("Authentication failed");
                }
            }
            handleUnexpectedDatabaseConnectionError(e, null);
        }
    }

    private void spawnMainWindow() throws IOException {
        final var stage = new Stage();
        final var loader = new FXMLLoader(ClinicApplication.class.getResource("MainWindow.fxml"));
        final BorderPane pane = loader.load();
        final var scene = new Scene(pane);
        stage.setTitle("ClinicApp");
        stage.setScene(scene);
        stage.minWidthProperty().bind(pane.minWidthProperty());
        stage.maxWidthProperty().bind(pane.maxWidthProperty());
        stage.minHeightProperty().bind(pane.minHeightProperty());
        stage.maxHeightProperty().bind(pane.maxHeightProperty());
        mainWindowController = loader.getController();
//        stage.setWidth(pane.getMinWidth());
//        stage.setHeight(pane.getMinHeight());
        stage.setOnCloseRequest(we -> {
            if((ReferralDetailsView.getEditState() && !ReferralDetailsView.exitConfirm())
            || (PrescriptionDetailsView.getEditState() && !PrescriptionDetailsView.exitConfirm())
            || (RegisterDialog.getEditState() && !RegisterDialog.exitConfirm())
                    || (MyAccount.getEditState() && !MyAccount.exitConfirm())){
                we.consume();
            }else {
                logOut();
            }
        });
        stage.showAndWait();
    }

    static public void logOut() {
        instance.user = null;
        instance.connectToDatabaseAnonymously();
        logger.info("Logged out");
        instance.tryRememberedLogin = false;
    }

    static public void logIn(String emailOrPESEL, String password) throws LoginException {
        logger.info("Logging in as `%s`".formatted(emailOrPESEL));
        instance.connectToDatabaseAsUser(emailOrPESEL, password);
        instance.user = User.getCurrent();
        logger.info("Logged in");
    }

    public static void main(String[] args) {
        launch();
    }
}