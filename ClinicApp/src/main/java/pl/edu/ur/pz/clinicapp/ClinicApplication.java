package pl.edu.ur.pz.clinicapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.hibernate.service.spi.ServiceException;
import pl.edu.ur.pz.clinicapp.dialogs.LoginDialog;
import pl.edu.ur.pz.clinicapp.models.Settings;
import pl.edu.ur.pz.clinicapp.models.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class ClinicApplication extends Application {
    private Settings settings;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private User user;
    private MainWindowController mainWindowController;

    static private ClinicApplication instance;

    public static Settings getSettings() {
        return instance.settings;
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

        settings = Settings.Defaults;
        final var logger = Logger.getGlobal();
        final var consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(settings.loggingLevel);
        logger.addHandler(consoleHandler);
        logger.setLevel(settings.loggingLevel);
        logger.config("Logging level set to " + settings.loggingLevel);

        connectToDatabaseAnonymously();
        while (waitForLogin()) {
            spawnMainWindow();
        }
    }

    private boolean waitForLogin() {
//        final var dialog = new LoginDialog("anna.nowak.123@example.com", "asdf1234");
        final var dialog = new LoginDialog();
        dialog.showAndWait();
        if (user == null) {
            Platform.exit();
            return false;
        }
        return true;
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wystąpił błąd w czasie łączenia do bazy danych.\n\nSprawdź, czy dla zadanych ustawień aplikacji baza danych jest poprawnie skonfigurowana i uruchomiona.\n\nSzczegóły:\n" + e.getLocalizedMessage());
            alert.setTitle("Błąd w czasie łączenia do bazy danych");
            alert.setHeaderText(null);
            alert.showAndWait();
            System.exit(1);
        }
    }

    private void connectToDatabaseAsUser(String emailOrPESEL, String password) {
        disconnectFromDatabase();
        try {
            // We shadow default (anonymous) login details from `persistence.xml` with user specific ones.
//            TODO fix this facory
            entityManagerFactory = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                    Map.entry("hibernate.connection.username", User.getDatabaseUsernameForInput(emailOrPESEL)),
                    Map.entry("hibernate.connection.password", password)
            ));
            entityManager = entityManagerFactory.createEntityManager();

        }
        // TODO: catch security specific exceptions to show "bad password" message.
        catch (ServiceException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wystąpił błąd w czasie łączenia do bazy danych.\n\nSprawdź, czy dla zadanych ustawień aplikacji baza danych jest poprawnie skonfigurowana i uruchomiona.\n\nSzczegóły:\n" + e.getLocalizedMessage());
            alert.setTitle("Błąd w czasie łączenia do bazy danych");
            alert.setHeaderText(null);
            alert.showAndWait();
            System.exit(1);
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
        stage.setOnCloseRequest(we -> logOut());
        stage.showAndWait();
    }

    static public void logOut() {
        instance.user = null;
        // TODO: close database session
    }

    static public void logIn(String emailOrPESEL, String password) {
        instance.connectToDatabaseAsUser(emailOrPESEL, password);
        instance.user = User.getCurrent();
    }

    public static void main(String[] args) {
        launch();
    }
}