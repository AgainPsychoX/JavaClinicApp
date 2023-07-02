package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.views.ViewControllerBase;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/*
 * TODO:
 *  + consider integrating it for adding user too
 *  + button to change password (and information if it was changed before saving in edit mode)
 *  + doctor information
 *  + allow doctor to promote themself to patient
 *  + show technical data (database username, created at, etc.) if viewing as admin
 *  + dirty changes logic (prevent cancel button, window close, refresh etc. if dirty state)
 *  + validation of the fields
 *  + cancel button?
 */

/**
 * View controller to display and edit account details.
 */
public class AccountDetailsView extends ViewControllerBase implements Initializable {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Elements and initialization
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML private TextField nameField;
    @FXML private TextField surnameField;

    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private TextField peselField;

    @FXML private TextField cityField;
    @FXML private TextField streetField;
    @FXML private TextField buildingField;
    @FXML private TextField postalCodeField;
    @FXML private TextField postalCityField;

    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button changePasswordButton;

    @FXML private GridPane doctorGridPane;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Text doctorText;

    @FXML private TextField specializationTextField;
    @FXML private TextField visitDurationTextField;
    @FXML private TextField maxDaysTextField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;

    private List<Node> patientOnlyThings;
    private List<Node> doctoryOnlyThings;


    Query createDBUser;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        patientOnlyThings = List.of(
                // Identification
                peselField,
                // Address:
                cityField,
                streetField,
                buildingField,
                postalCodeField,
                postalCityField,
                passwordField,
                repeatPasswordField
        );

        doctoryOnlyThings = List.of(
                visitDurationTextField,
                specializationTextField,
                maxDaysTextField
        );
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    protected User user;

    public enum Mode {
        VIEW,
        EDIT,
        CREATE
    }

    public ReadOnlyObjectProperty<Mode> modeProperty() {
        return mode;
    }
    private final ReadOnlyObjectWrapper<Mode> mode = new ReadOnlyObjectWrapper<>();
    public Mode getMode() {
        return mode.get();
    }

    public void setMode(Mode mode) {
        this.mode.set(mode);

        if(user != null) {
            final var patient = user.asPatient();
            final var doctor = user.asDoctor();
            if (patient != null) {
                // Make sure patient-only fields are shown
                for (final var node : patientOnlyThings) {
                    setNodeEnabledVisibleManaged(node, true);
                }
            } else {
                // Make sure patient-only fields are hidden, non-editable and cleared (just in case)
                for (final var node : patientOnlyThings) {
                    setNodeEnabledVisibleManaged(node, false);
                    if (node instanceof TextField field) {
                        field.setText(""); // just in case
                    }
                }
            }
            if (doctor != null){
                for(final var node : doctoryOnlyThings){
                    setNodeEnabledVisibleManaged(node, true);
                }
            }
            if(ClinicApplication.requireUser().getRole() != User.Role.ADMIN)
                roleComboBox.setDisable(true);
        switch (mode) {
            case VIEW -> {
                setNodeEnabledVisibleManaged(editButton, true);
                setNodeEnabledVisibleManaged(saveButton, false);
                changePasswordButton.setDisable(true);
                nameField.setEditable(false);
                surnameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);

                for (final var node : patientOnlyThings) {
                    if (node instanceof TextField field) {
                        field.setEditable(false);
                    }
                }
                for (final var node : doctoryOnlyThings) {
                    if (node instanceof TextField field) {
                        field.setEditable(false);
                    }
                }
            }
            case EDIT, CREATE -> {
                setNodeEnabledVisibleManaged(editButton, false);
                setNodeEnabledVisibleManaged(saveButton, true);
                changePasswordButton.setDisable(false);
                nameField.setEditable(true);
                surnameField.setEditable(true);
                emailField.setEditable(true);
                phoneField.setEditable(true);

                if (patient != null) {
                    for (final var node : patientOnlyThings) {
                        if (node instanceof TextField field) {
                            field.setEditable(true);
                        }
                    }
                }
                if (doctor != null){
                    for (final var node : doctoryOnlyThings){
                        if(node instanceof TextField field){
                            field.setEditable(true);
                        }
                    }
                }
            }
            }
        }
    }

    private void setNodeEnabledVisibleManaged(Node node, boolean show) {
        node.setDisable(!show);
        node.setVisible(show);
        node.setManaged(show);
    }

    /**
     * Populates the view for given context.
     *
     * If no argument is provided, the view will show current user information. Context arguments:
     * <ol>
     * <li>First argument can specify {@link User} whose info will is to be displayed (or edited).
     * <li>Second argument can specify {@link Mode}.
     * </ol>
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {
        var user = ClinicApplication.getUser();
        var mode = Mode.VIEW;

        if (context.length > 0) {
            if (context[0] instanceof User x) {
                user = x;
            } else if(context[0] instanceof Mode y){
                mode = y;
            }else {
                throw new IllegalArgumentException();
            }
            if (context.length > 1) {
                if (context[1] instanceof Mode y) {
                    mode = y;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        this.user = user;
        setMode(mode);

        if(user != null && mode != Mode.CREATE) {
            nameField.setText(user.getName());
            surnameField.setText(user.getSurname());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
        }
        final var patient = user.asPatient();
        if (patient != null) {
            peselField.setText(patient.getPESEL());
            cityField.setText(patient.getCity());
            streetField.setText(patient.getStreet());
            buildingField.setText(patient.getBuilding());
            postalCodeField.setText(patient.getPostCode());
            postalCityField.setText(patient.getPostCity());
        }

        final var doctor = user.asDoctor();
        if(doctor != null){
            doctorText.setVisible(true);
            specializationTextField.setText(doctor.getSpeciality());
            visitDurationTextField.setText(String.valueOf(doctor.getDefaultVisitDuration().toMinutes()));
            maxDaysTextField.setText(String.valueOf(doctor.getMaxDaysInAdvance()));
            doctorGridPane.setVisible(true);
        }

        if(ClinicApplication.requireUser().getRole() != User.Role.ADMIN){
            roleComboBox.setDisable(true);
        }
        createDBUser = session.getNamedQuery("createDatabaseUser");
    }

    @Override
    public void refresh() {
        if (getMode() == Mode.EDIT) {
            // TODO: ask only when dirty
            return;
        }

        final var entityManager = ClinicApplication.getEntityManager();
        entityManager.refresh(user);
        populate(user, getMode());
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Interaction handlers
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * Shows alert.
     * @param type {@link javafx.scene.control.Alert.AlertType}
     * @param title Alert title
     * @param header Alert header
     * @param text Alert text
     * @return true when OK button was pressed, false otherwise
     */
    private static boolean showAlert(Alert.AlertType type, String title, String header, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    protected boolean mightPreventNavigation() {
        // TODO: allow in edit if not dirty
        if (getMode() == Mode.EDIT) {
            final var dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Niezapisane zmiany");
            dialog.setHeaderText(null);
            dialog.setContentText("Musisz najpierw anulować lub zapisać zmiany.");
            dialog.showAndWait();
            return true;
        }
        return false;
    }

    public void editAction(ActionEvent actionEvent) {
        setMode(Mode.EDIT);
    }

    public void saveButton(ActionEvent actionEvent) {
//        I'm deeply sorry for this spaghetti :(
        //TODO - passwordField, validation, alert messages

        final var patient = user.asPatient();
        final var doctor = user.asDoctor();
        boolean validate = true;
        if(doctor != null) {
            if (specializationTextField.getText().trim().equals("") || specializationTextField.getText() == null
                    || maxDaysTextField.getText().trim().equals("") || maxDaysTextField.getText() == null
                    || visitDurationTextField.getText().trim().equals("")|| visitDurationTextField.getText() == null) {
                showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                        "Pola \"specjalizacji\", \"czasu wizyty\", \"dni\" są wymagane.");
                validate = false;
            }
            if (!nameSurnameSpecializationValidator(specializationTextField.getText().trim())) {
                showErrorAlert("Błąd zapisu", "Niepoprwana specjalizacja",
                        "Pole \"SPECIALIZACJA\" nie jest poprawną wartością");
                validate = false;
            }
            else if (!doctorFieldsValidator(maxDaysTextField.getText().trim())) {
                showErrorAlert("Błąd zapisu", "Niepoprawna liczba dni",
                        "Maksymalna liczba dni wynosi 999");
                validate = false;
            }
            else if (!doctorFieldsValidator(visitDurationTextField.getText().trim())) {
                showErrorAlert("Błąd zapisu", "Niepoprawna liczba minut",
                        "Maksymalna liczba minut wynosi 999");
                validate = false;
            }
        }
        if(validate) {
            if (nameField.getText() == null || nameField.getText().isBlank() || surnameField.getText() == null
                    || surnameField.getText().isBlank() || peselField.getText() == null
                    || peselField.getText().isBlank())
                showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                        "Pola \"imię\", \"nazwisko\", \"PESEL\" są wymagane.");
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
            } else if (!PESELValidator(peselField.getText().trim())) {
                showErrorAlert("Błąd zapisu", "Niepoprawny PESEL",
                        "Pole \"PESEL\" nie jest poprawnym numerem PESEL.");
            } else if (postalCodeField.getText() != null && !postalCodeField.getText().isBlank()
                    && !postCodeValidator(postalCodeField.getText().trim())) {
                showErrorAlert("Błąd zapisu", "Niepoprawny kod pocztowy",
                        "Pole \"kod pocztowy\" nie jest poprawnym kodem pocztowym.");
            } else {
                final var entityManager = ClinicApplication.getEntityManager();
                entityManager.getTransaction().begin();
                if (patient != null) {
                    patient.setName(nameField.getText().trim());
                    patient.setSurname(surnameField.getText().trim());
                    patient.setEmail(emailField.getText().trim());
                    patient.setPhone(phoneField.getText().trim());

                    patient.setPESEL(peselField.getText().trim());

                    patient.setCity(cityField.getText().trim());
                    patient.setStreet(streetField.getText().trim());
                    patient.setBuilding(buildingField.getText().trim());
                    patient.setPostCode(postalCodeField.getText().trim());
                    patient.setPostCity(postalCityField.getText().trim());
                } else {
                    user.setName(nameField.getText().trim());
                    user.setSurname(surnameField.getText().trim());
                    user.setEmail(emailField.getText().trim());
                    user.setPhone(phoneField.getText().trim());
                }
                if (doctor != null) {
                    doctor.setSpeciality(specializationTextField.getText());
                    doctor.setMaxDaysInAdvance(Integer.parseInt(maxDaysTextField.getText()));
                    String duration = visitDurationTextField.getText().trim();
                    int minutes = Integer.parseInt(duration);
                    doctor.setDefaultVisitDuration(Duration.ofMinutes(minutes));


                }
                entityManager.getTransaction().commit();
                showAlert(Alert.AlertType.INFORMATION, "Edycja danych", "Pomyślnie edytowano dane", "");
                setMode(Mode.VIEW);
            }
        }
        // TODO: check if dirty, if not - just ignore (maybe toast "nothing to save")
        // TODO: check if other user modified the timetables just before us?
        // TODO: lock UI (Mode.SAVING?) while saving?
        // TODO: validation!!! at least here, but while editing would be nice
    }


    @FXML
    private void updatePassword(){
        if(!passwordField.getText().equals(repeatPasswordField.getText()) || passwordField.getText() == null ||
        passwordField.getText().trim().equals("")){
            showErrorAlert("Niepoprawne hasło", "Złe hasło", "");
        }
        else {
            //TODO
            showAlert(Alert.AlertType.CONFIRMATION,"Zmiana hasła", "Zmieniono hasło", "");
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

    public static boolean nameSurnameSpecializationValidator(String text) {
        return text.matches("[a-zA-ZżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s-]+");
    }

    public static boolean doctorFieldsValidator(String days) {
        return days.matches("[0-9]{1,3}");
    }

    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
