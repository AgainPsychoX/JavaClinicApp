package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.util.ArrayList;
import java.util.Optional;

public class RegisterDialog extends ChildControllerBase<MainWindowController> {

    /**
     * Tells if any fields have been edited.
     */
    private static boolean editState = false;

    public static boolean getEditState() {
        return editState;
    }

    public static void setEditState(boolean editState) {
        RegisterDialog.editState = editState;
    }

    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query createPatientQuery = session.getNamedQuery("createPatient");
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

    /**
     * List of all fields in the view.
     */
    private ArrayList<TextField> allFields = new ArrayList<TextField>();

    /**
     * Number of edited fields.
     */
    private IntegerProperty fieldsEdited = new SimpleIntegerProperty(0);


    /**
     * Shows a simple error alert.
     */
    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Displays alert about unsaved changes and returns whether user wants to discard them or not.
     */
    public static Boolean exitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Czy na pewno chcesz opuścić ten widok? Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    /**
     * Checks if necessary fields aren't empty and registers a new patient by adding new rows in 'users' and 'patients'
     * tables and a new database user.
     */
    @FXML
    void register() {
        Transaction transaction;

        if (nameField.getText() == null || nameField.getText().isBlank() || surnameField.getText() == null
                || surnameField.getText().isBlank() || PESELField.getText() == null
                || PESELField.getText().isBlank() || passwordField.getText() == null
                || passwordField.getText().isBlank() || repPasswordField.getText() == null
                || repPasswordField.getText().isBlank()) {
            showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                    "Pola \"imię\", \"nazwisko\", \"PESEL\", \"hasło\" i \"powtórz hasło\" są wymagane.");
        } else if (!nameSurnameValidator(nameField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawne imię",
                    "Pole \"imię\" nie jest poprawną wartością.");
        } else if (!nameSurnameValidator(surnameField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawne nazwisko",
                    "Pole \"nazwisko\" nie jest poprawną wartością.");
        } else if (emailField.getText() != null && !emailField.getText().isBlank()
                && !emailValidator(emailField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawny email",
                    "Pole \"email\" nie jest poprawnym adresem email.");
        } else if (phoneField.getText() != null && !phoneField.getText().isBlank()
                && !phoneValidator(phoneField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawny numer telefonu",
                    "Pole \"numer telefonu\" nie jest poprawnym numerem telefonu.");
        } else if (!PESELValidator(PESELField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawny PESEL",
                    "Pole \"PESEL\" nie jest poprawnym numerem PESEL.");
        } else if (postCodeField.getText() != null && !postCodeField.getText().isBlank()
                && !postCodeValidator(postCodeField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawny kod pocztowy",
                    "Pole \"kod pocztowy\" nie jest poprawnym kodem pocztowym.");
        } else if (!passwordField.getText().trim().equals(repPasswordField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niezgodne hasła",
                    "Pola \"hasło\" i \"powtórz hasło\" muszą być takie same.");
        } else {
            try {
                transaction = session.beginTransaction();

                User newUser = new User();
                newUser.setEmail((emailField.getText() == null || emailField.getText().isBlank()) ? null : emailField.getText().trim());
                newUser.setName(nameField.getText().trim());
                newUser.setPhone((phoneField.getText() == null || phoneField.getText().isBlank()) ? null : phoneField.getText().trim());
                newUser.setRole(User.Role.PATIENT);
                newUser.setSurname(surnameField.getText().trim());

                session.persist(newUser);
                newUser.changePassword(passwordField.getText());
                // FIXME: instead of using query to create patient data, let's use hibernate

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

                createPatientQuery.executeUpdate();
                transaction.commit();

                Alert exit = new Alert(Alert.AlertType.INFORMATION);
                exit.setTitle("Rejestracja");
                exit.setHeaderText("Rejestracja zakończona pomyślnie");
                exit.setContentText("Zarejestrowano nowego pacjenta.");
                exit.showAndWait();

                fieldsEdited.setValue(0);
                this.getParentController().goToViewRaw(MainWindowController.Views.PATIENTS); //WrongClassException?
            } catch (Exception e) {
                transaction = session.getTransaction();
                if (transaction.isActive()) transaction.rollback();
                if (e.getMessage().contains("ConstraintViolationException")) {
                    showErrorAlert("Błąd zapisu", "Podane dane istnieją już w bazie.",
                            "Wprowadzony PESEL lub email jest już przypisany do istniejącego użytkownika.");
                }
            }
        }
    }

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Populates the list of fields, empties them and adds listeners to them. Adds listener to the fieldsEdited
     * property, which will change editState accordingly if there are any fields edited or not.
     */
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

        for (TextField field : allFields) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                int oldFieldsVal = fieldsEdited.getValue();
                if ((oldValue == null || oldValue.isBlank()) && newValue != null && !newValue.isBlank()) {
                    fieldsEdited.setValue(oldFieldsVal + 1);
                }
                if ((oldValue != null && !oldValue.isBlank()) && (newValue == null || newValue.isBlank())) {
                    fieldsEdited.setValue(oldFieldsVal - 1);
                }
            });
        }

        fieldsEdited.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number before, Number after) {
                editState = (after.intValue() != 0);
            }
        });
    }

    /**
     * Default refresh method.
     */
    @Override
    public void refresh() {

    }

    /**
     * Checks for edited fields and accordingly displays exit alert. Goes to the previous view.
     */
    public void onBackClick(MouseEvent mouseEvent) {
        if (editState) {
            if (exitConfirm()) {
                editState = false;
                this.getParentController().goToViewRaw(MainWindowController.Views.PATIENTS);
            }
        } else {
            this.getParentController().goToViewRaw(MainWindowController.Views.PATIENTS);
        }
    }

    public static boolean emailValidator(String email) {
        if (!email.contains("@")) return false;
        String prev = email.substring(0, email.indexOf('@'));
        String past = email.substring(email.indexOf('@'));

        if (prev.length() == 0) return false;
        return past.contains(".");
    }

    public static boolean PESELValidator(String pesel) {
        return pesel.matches("[0-9]{11}");
    }

    public static boolean phoneValidator(String phone) {
        return phone.matches("[0-9]{9}");
    }

    public static boolean postCodeValidator(String code) {
        return code.matches("[0-9]{2}-[0-9]{3}");
    }

    public static boolean nameSurnameValidator(String text) {
        return text.matches("[a-zA-ZżźćńółęąśŻŹĆĄŚĘŁÓŃ]+");
    }

}
