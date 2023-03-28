package pl.edu.ur.pz.clinicapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.edu.ur.pz.clinicapp.dialogs.LoginDialog;
import pl.edu.ur.pz.clinicapp.models.User;

import java.io.IOException;

public class ClinicApplication extends Application {
    public static User user;
    public static MainWindowController mainWindowController;

    @Override
    public void start(Stage stage) throws Exception {
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
        user = null;
        // TODO: close database session
    }

    static public boolean logIn(String emailOrPESEL, String password) {
        user = User.authorize(emailOrPESEL, password);
        // TODO: open database connection
        return user != null;
    }

    public static void main(String[] args) {
        launch();
    }
}