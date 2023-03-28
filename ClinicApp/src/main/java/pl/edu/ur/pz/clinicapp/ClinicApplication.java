package pl.edu.ur.pz.clinicapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.dialogs.LoginDialog;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.OtherUtils;

import java.io.IOException;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.getStageFromNode;

public class ClinicApplication extends Application {
    public static User user;
    public static MainWindowController mainWindowController;

    @Override
    public void start(Stage stage) throws Exception {
//        while (true) {
        waitForLogin();
        spawnMainWindow(stage); // TODO: make separate stage so it can be `showAndWait`ed for
//        }
    }

    private void waitForLogin() {
//        final var dialog = new LoginDialog("anna.nowak.123@example.com", "JKB&PZ123");
        final var dialog = new LoginDialog();
        dialog.showAndWait();
        if (user == null) {
            Platform.exit();
        }
    }

    private void spawnMainWindow(Stage stage) throws IOException {
        final var loader = new FXMLLoader(ClinicApplication.class.getResource("MainWindow.fxml"));
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setTitle("ClinicApp");
        stage.setScene(scene);
        stage.minWidthProperty().bind(pane.minWidthProperty());
        stage.maxWidthProperty().bind(pane.maxWidthProperty());
        stage.minHeightProperty().bind(pane.minHeightProperty());
        stage.maxHeightProperty().bind(pane.maxHeightProperty());
        mainWindowController = loader.getController();
//        stage.setWidth(pane.getMinWidth());
//        stage.setHeight(pane.getMinHeight());
//        stage.showAndWait();
        stage.show();
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