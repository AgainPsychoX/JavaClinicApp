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
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.security.auth.login.LoginException;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.*;

/**
 * Dialog responsible for logging in user.
 */
public class LoginDialog extends Stage {
    @FXML protected VBox logInForm;
    final protected VBox loadingView;

    @FXML protected TextField identityTextField;
    @FXML protected PasswordField passwordField;
    @FXML protected Text errorText;

    protected BorderPane pane;

    public LoginDialog() {
        this(null, null);
    }

    public LoginDialog(String rememberedUser) {
        this(rememberedUser, null);
    }

    public LoginDialog(String rememberedUser, String rememberedPassword) {
        var fxml = ClinicApplication.class.getResource("dialogs/LoginDialog.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setController(this);

        try {
            pane = fxmlLoader.load();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        errorText.setVisible(false);
        errorText.setManaged(false);

        loadingView = new VBox(new Text("Logowanie..."));
        loadingView.setSpacing(8);
        // TODO: add generic loading spinner control

        if (!isStringNullOrBlank(rememberedUser)) {
            identityTextField.setText(rememberedUser);

            if (!isStringNullOrBlank(rememberedUser)) {
                runDelayed(200, () -> {
                    logInUsingRememberedData(rememberedUser, rememberedPassword);
                });
            }
        }

//        initModality(Modality.APPLICATION_MODAL);
        linkStageSizeToPane(this, pane);
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

        try {
            ClinicApplication.logIn(identity, password);
            super.close();
        } catch (LoginException e) {
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

        pane.setCenter(loadingView);

        try {
            if (identity == null || identity.isBlank() || password == null || password.isBlank()) {
                throw new LoginException("Nie wypełniono wymaganych pól!");
            }
            ClinicApplication.logIn(identity, password);
            super.close();
        } catch (LoginException e) {
            pane.setCenter(logInForm);
            passwordField.clear();
            errorText.setText(nullCoalesce(e.getMessage(), "Błąd logowania!"));
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
