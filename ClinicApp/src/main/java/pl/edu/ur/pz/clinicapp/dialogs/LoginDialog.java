package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.runDelayed;

/**
 * Dialog responsible for logging in user.
 */
public class LoginDialog extends Stage {
    @FXML protected VBox logInForm;
    final protected VBox loadingView;

    @FXML protected TextField identityTextField;
    @FXML protected PasswordField passwordField;
    @FXML protected Text errorText;

    final protected BorderPane pane;

    public LoginDialog() {
        this(null, null);
    }

    public LoginDialog(String rememberedUser) {
        this(rememberedUser, null);
    }

    public LoginDialog(String rememberedUser, String rememberedPassword) {
        pane = new BorderPane();
        var fxml = ClinicApplication.class.getResource("dialogs/LoginDialog.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(pane);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException exception) {
            Logger.getGlobal().log(
                    Level.SEVERE,
                    "Error creating login dialog!" +
                            "\n\tFXML: " + fxml
            );
            throw new RuntimeException(exception);
        }

        errorText.setVisible(false);
        errorText.setManaged(false);

        loadingView = new VBox(new Text("Logowanie...")) {{
            setSpacing(8);
            // TODO: add generic loading spinner control
            // TODO: make it separate control as well
        }};

        if (rememberedUser != null) {
            identityTextField.setText(rememberedUser);

            if (rememberedPassword != null) {
                runDelayed(200, () -> {
                    logInUsingRememberedData(rememberedUser, rememberedPassword);
                });
            }
        }

        initModality(Modality.APPLICATION_MODAL);
        minWidthProperty().bind(pane.minWidthProperty());
        maxWidthProperty().bind(pane.maxWidthProperty());
        minHeightProperty().bind(pane.minHeightProperty());
        maxHeightProperty().bind(pane.maxHeightProperty());
//        setWidth(pane.getMinWidth());
//        setHeight(pane.getMinHeight());
        setScene(new Scene(pane));
    }

    @Override
    public void close() {
        // TODO: ask for confirmation, as it would most likely exit the application entirely
        super.close();
    }

    private void logInUsingRememberedData(String identity, String password) {
        pane.setCenter(loadingView);
        final boolean success = ClinicApplication.logIn(identity, password);

        if (success) {
            super.close();
        }
        else {
            pane.setCenter(logInForm);
            passwordField.clear();
            errorText.setText("Zapamiętane dane logowania są nieprawidłowe. Zaloguj się ponownie.");
            errorText.setVisible(true);
            errorText.setManaged(true);
        }
    }

    private void logInUsingFormData() {
        final var identity = identityTextField.getText();
        final var password = passwordField.getText();

        // TODO: remember me checkbox

        // TODO: simple validation before even trying to log-in

        pane.setCenter(loadingView);
        final boolean success = ClinicApplication.logIn(identity, password);

        if (success) {
            super.close();
        }
        else {
            pane.setCenter(logInForm);
            passwordField.clear();
            errorText.setText("Nieprawidłowe dane logowania!");
            errorText.setVisible(true);
            errorText.setManaged(true);
        }
    }

    @FXML
    protected void logInAction(ActionEvent event) {
        logInUsingFormData();
    }

    @FXML
    protected void identityEnterAction(ActionEvent event) {
        passwordField.requestFocus();
    }

    @FXML
    protected void passwordEnterAction(ActionEvent event) {
        logInUsingFormData();
    }
}
