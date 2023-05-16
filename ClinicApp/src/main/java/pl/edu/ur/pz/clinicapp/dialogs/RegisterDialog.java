package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RegisterDialog extends ChildControllerBase<MainWindowController> {

    private static boolean editState = false;

    public static boolean getEditState() {
        return editState;
    }

    public static void setEditState(boolean editState) {
        RegisterDialog.editState = editState;
    }

    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query createPatientQuery = session.getNamedQuery("createPatient");
    Query createDatabaseUserQuery = session.getNamedQuery("createDatabaseUser");
    Query findDatabaseUserQuery = session.getNamedQuery("findDatabaseUser");
    @FXML
    protected HBox banner;
    @FXML
    protected PasswordField passwordField;
    @FXML
    protected PasswordField repPasswordField;
    @FXML
    protected BorderPane BPane;
    @FXML
    protected TextField PESELField;
    @FXML
    protected TextField buildingField;
    @FXML
    protected TextField cityField;
    @FXML
    protected TextField emailField;
    @FXML
    protected VBox logInForm;
    @FXML
    protected TextField nameField;
    @FXML
    protected TextField phoneField;
    @FXML
    protected TextField postCityField;
    @FXML
    protected TextField postCodeField;
    @FXML
    protected Button registerButton;
    @FXML
    protected TextField streetField;
    @FXML
    protected TextField surnameField;

    private ArrayList<TextField> allFields = new ArrayList<TextField>();


    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static Boolean exitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Czy na pewno chcesz opuścić ten widok? Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    @FXML
    void register() {
        Transaction transaction;
//        TEST ONLY
        nameField.setText("Adam");
        surnameField.setText("Nowak");
        PESELField.setText("12345678901");
        passwordField.setText("pass");
        repPasswordField.setText("pass");

        if (nameField.getText() == null || nameField.getText().isBlank() || surnameField.getText() == null
                || surnameField.getText().isBlank() || PESELField.getText() == null
                || PESELField.getText().isBlank() || passwordField.getText() == null
                || passwordField.getText().isBlank() || repPasswordField.getText() == null
                || repPasswordField.getText().isBlank()) {
            showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                    "Pola \"imię\", \"nazwisko\", \"PESEL\", \"hasło\" i \"powtórz hasło\" są wymagane.");
        } else if (!passwordField.getText().trim().equals(repPasswordField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niezgodne hasła",
                    "Pola \"hasło\" i \"powtórz hasło\" muszą być takie same.");
        } else {
            try {
                transaction = session.beginTransaction();
                String internalName;

                // XXX
                while (true) {
                    int random = ThreadLocalRandom.current().nextInt(1000, 10000);
                    internalName = "u" + Character.toLowerCase(nameField.getText().charAt(0))
                            + Character.toLowerCase(surnameField.getText().charAt(0)) + random;
                    findDatabaseUserQuery.setParameter("rolname", internalName);
                    if (findDatabaseUserQuery.getResultList().size() == 0) break;
                }

                createDatabaseUserQuery.setParameter("userName", internalName);
                createDatabaseUserQuery.setParameter("password", passwordField.getText().trim());

                User newUser = new User();
                newUser.setDatabaseUsername(internalName);
                newUser.setEmail((emailField.getText() == null || emailField.getText().isBlank()) ? null : emailField.getText().trim());
                newUser.setName(nameField.getText().trim());
                newUser.setPhone((phoneField.getText() == null || phoneField.getText().isBlank()) ? null : phoneField.getText().trim());
                newUser.setRole(User.Role.PATIENT);
                newUser.setSurname(surnameField.getText().trim());

                session.persist(newUser);

                createPatientQuery.setParameter("building", (buildingField.getText() == null
                        || buildingField.getText().isBlank())
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : buildingField.getText().trim());
                createPatientQuery.setParameter("city", (cityField.getText() == null
                        || cityField.getText().isBlank())
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : cityField.getText().trim());
                createPatientQuery.setParameter("pesel", PESELField.getText().trim());
                createPatientQuery.setParameter("post_city", (postCityField.getText() == null
                        || postCityField.getText().isBlank())
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : postCityField.getText().trim());
                createPatientQuery.setParameter("post_code", (postCodeField.getText() == null
                        || postCodeField.getText().isBlank())
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : postCodeField.getText().trim());
                createPatientQuery.setParameter("street", (streetField.getText() == null
                        || streetField.getText().isBlank())
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : streetField.getText().trim());
                createPatientQuery.setParameter("id", newUser.getId());

                createDatabaseUserQuery.getSingleResult();
                createPatientQuery.executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction = session.getTransaction();
                if (transaction.isActive()) transaction.rollback();
                System.err.println(e.getMessage());
                if (e.getMessage().contains("ConstraintViolationException")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd zapisu");
                    alert.setHeaderText("Podane dane istnieją już w bazie.");
                    alert.setContentText("Wprowadzony PESEL lub email jest już przypisany do istniejącego użytkownika.");
                    alert.showAndWait();
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {

        allFields.add(PESELField);
        allFields.add(buildingField);
        allFields.add(cityField);
        allFields.add(emailField);
        allFields.add(nameField);
        allFields.add(phoneField);
        allFields.add(postCityField);
        allFields.add(postCodeField);
        allFields.add(streetField);
        allFields.add(surnameField);
        allFields.add(passwordField);
        allFields.add(repPasswordField);

        if (context.length != 0) {
            BPane.getChildren().remove(banner);
            registerButton.setText("Zarejestruj");
        }

        for (TextField field : allFields) {
            field.setText("");
        }
    }

    @Override
    public void refresh() {

    }

    public void onBackClick(MouseEvent mouseEvent) {
        for (TextField field : allFields) {
            if (field.getText() != null && !field.getText().isBlank()) {
                editState = true;
                break;
            }
        }

        if (editState) {
            if (exitConfirm()) {
                editState = false;
                this.getParentController().goBack();
            }
        } else {
            this.getParentController().goBack();
        }
    }
}
