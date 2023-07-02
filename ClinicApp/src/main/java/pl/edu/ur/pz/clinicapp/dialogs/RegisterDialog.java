package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.views.ViewController;
import pl.edu.ur.pz.clinicapp.utils.views.ViewControllerBase;
import pl.edu.ur.pz.clinicapp.views.AccountsView;
import pl.edu.ur.pz.clinicapp.views.PatientsView;

import java.util.ArrayList;
import java.util.Optional;

public class RegisterDialog extends ViewControllerBase {

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
    Query createDatabaseUserQuery = session.getNamedQuery("createDatabaseUser");
    Query createDoctorQuery = session.getNamedQuery("createDoctor");
    Query findDatabaseUserQuery = session.getNamedQuery("findDatabaseUser");

    @FXML protected HBox banner;
    @FXML protected PasswordField passwordField;
    @FXML protected PasswordField repPasswordField;
    @FXML protected BorderPane BPane;
    @FXML protected TextField PESELField;
    @FXML protected TextField buildingField;
    @FXML protected TextField cityField;
    @FXML protected TextField emailField;
    @FXML protected VBox logInForm;
    @FXML protected TextField nameField;
    @FXML protected TextField phoneField;
    @FXML protected TextField postCityField;
    @FXML protected TextField postCodeField;
    @FXML protected Button registerButton;
    @FXML protected TextField streetField;
    @FXML protected TextField surnameField;
    @FXML protected Text backText;
    @FXML protected Text roleText;
    @FXML protected ComboBox<String> roleComboBox;
    @FXML protected GridPane doctorGridPane;
    @FXML protected TextField visitDurationTextField;
    @FXML protected TextField maxDaysTextField;
    @FXML protected TextField specializationTextField;

    public enum Mode{PATIENT, ACCOUNT}

    /**
     * List of all fields in the view.
     */
    private ArrayList<TextField> allFields = new ArrayList<TextField>();

    /**
     * Number of edited fields.
     */
    private IntegerProperty fieldsEdited = new SimpleIntegerProperty(0);

    private final ReadOnlyObjectWrapper<RegisterDialog.Mode> mode = new ReadOnlyObjectWrapper<>();

    public Mode getMode() {return mode.get();}
    public User.Role selectedRole;

    public void setMode (Mode mode){
        this.mode.set(mode);
        switch (mode){
            case ACCOUNT -> {
                doctorGridPane.setVisible(true);
                doctorGridPane.setDisable(false);
                backText.setText("< Powrót do listy użytkowników");
                roleText.setVisible(true);
                roleComboBox.setVisible(true);
                roleComboBox.setDisable(false);
                roleComboBox.getItems().setAll("Administrator", "Lekarz", "Pacjent");
                roleComboBox.setValue("Pacjent");
            }
            case PATIENT -> {
                selectedRole = User.Role.PATIENT;
                doctorGridPane.setVisible(false);
                doctorGridPane.setDisable(true);
                backText.setText("< Powrót do listy pacjentów");
                roleText.setVisible(false);
                roleComboBox.setVisible(false);
                roleComboBox.setDisable(true);
            }
        }
    }

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

    @Override
    public boolean onNavigation(Class<? extends ViewController> which, Object... context) {
        return !getEditState() || exitConfirm();
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
        selectedRole = User.Role.PATIENT;
        if(getMode() == Mode.ACCOUNT) {
            String roleName = roleComboBox.getSelectionModel().getSelectedItem();
            if (roleName.equals("Lekarz")){
                if(specializationTextField.getText().isBlank() || specializationTextField.getText() == null
                        || maxDaysTextField.getText().isBlank() || maxDaysTextField.getText() == null
                        || visitDurationTextField.getText().isBlank() || visitDurationTextField.getText() == null){
                    showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                            "Pola \"specjalizacji\", \"czasu wizyty\", \"dni\" są wymagane.");
                 }
            }
            if(roleName.equals("Pacjent")) selectedRole = User.Role.PATIENT;
            else if(roleName.equals("Lekarz")){
                selectedRole = User.Role.DOCTOR;
                if (!nameSurnameSpecializationValidator(specializationTextField.getText().trim()))
                    showErrorAlert("Błąd zapisu", "Niepoprwana specjalizacja",
                            "Pole \"SPECIALIZACJA\" nie jest poprawną wartością");
                else if (!doctorFieldsValidator(maxDaysTextField.getText().trim()))
                    showErrorAlert("Błąd zapisu", "Niepoprawna liczba dni",
                            "Maksymalna liczba dni wynosi 999");
                else if (!doctorFieldsValidator(visitDurationTextField.getText().trim()))
                    showErrorAlert("Błąd zapisu", "Niepoprawna liczba minut",
                            "Maksymalna liczba minut wynosi 999");
            }
            else selectedRole = User.Role.ADMIN;
        }
        if (nameField.getText() == null || nameField.getText().isBlank() || surnameField.getText() == null
                || surnameField.getText().isBlank() || PESELField.getText() == null
                || PESELField.getText().isBlank() || passwordField.getText() == null
                || passwordField.getText().isBlank() || repPasswordField.getText() == null
                || repPasswordField.getText().isBlank()) {
            showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                    "Pola \"imię\", \"nazwisko\", \"PESEL\", \"hasło\" i \"powtórz hasło\" są wymagane.");
        }
        else if (!nameSurnameSpecializationValidator(nameField.getText().trim())) {
            showErrorAlert("Błąd zapisu", "Niepoprawne imię",
                    "Pole \"imię\" nie jest poprawną wartością.");
        } else if (!nameSurnameSpecializationValidator(surnameField.getText().trim())) {
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

                // FIXME: use hibernate ffs

                if(selectedRole == User.Role.DOCTOR){
                    createDoctorQuery.setParameter("id", newUser.getId());
                    createDoctorQuery.setParameter("visitDuration",
                            Integer.parseInt(visitDurationTextField.getText().trim()));
                    createDoctorQuery.setParameter("maxDays", Integer.parseInt(maxDaysTextField.getText().trim()));
                    createDoctorQuery.setParameter("name", newUser.getName());
                    createDoctorQuery.setParameter("surname", newUser.getSurname());
                    createDoctorQuery.setParameter("speciality", specializationTextField.getText().trim());
                    createDoctorQuery.executeUpdate();
                }
                transaction.commit();
                Alert exit = new Alert(Alert.AlertType.INFORMATION);
                exit.setTitle("Rejestracja");
                exit.setHeaderText("Rejestracja zakończona pomyślnie");
                exit.setContentText("Zarejestrowano nową osobę.");
                exit.showAndWait();

                fieldsEdited.setValue(0);
                if(getMode() == Mode.PATIENT)
                    this.getParentController().goToViewRaw(PatientsView.class); //WrongClassException?
                else
                    this.getParentController().goToViewRaw(AccountsView.class);
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
        var mode = RegisterDialog.Mode.PATIENT;
        backText.setText("< Powrót do listy pacjentów");

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

        if(context.length > 1){
            if(context[1] instanceof Mode m){
                mode = m;
            }else{
                throw new IllegalArgumentException();
            }
        }

        setMode(mode);

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
        if (editState && !exitConfirm()) {
            return;
        }
        editState = false;
        if (getMode() == Mode.PATIENT)
            this.getParentController().goToViewRaw(PatientsView.class);
        else
            this.getParentController().goToViewRaw(AccountsView.class);
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

    public static boolean nameSurnameSpecializationValidator(String text) {
        return text.matches("[a-zA-ZżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s-]+");
    }

    public static boolean doctorFieldsValidator(String days) {
        return days.matches("[0-9]{1,3}");
    }

}
