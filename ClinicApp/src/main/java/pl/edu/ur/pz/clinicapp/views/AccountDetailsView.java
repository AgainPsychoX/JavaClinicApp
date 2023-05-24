package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.List;
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
public class AccountDetailsView extends ChildControllerBase<MainWindowController> implements Initializable {
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

    @FXML private Button goToPrescriptionsButton;
    @FXML private Button goToReferralsButton;
    @FXML private Button goToVisitsButton;
    @FXML private Button editButton;
    @FXML private Button saveButton;

    private List<Node> patientOnlyThings;

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
                postalCityField
        );
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    protected User user;

    public enum Mode {
        VIEW,
        EDIT,
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

        final var patient = user.asPatient();
        if (patient != null) {
            // Make sure patient-only fields are shown
            for (final var node : patientOnlyThings) {
                setNodeEnabledVisibleManaged(node, true);
            }
        }
        else {
            // Make sure patient-only fields are hidden, non-editable and cleared (just in case)
            for (final var node : patientOnlyThings) {
                setNodeEnabledVisibleManaged(node, false);
                if (node instanceof TextField field) {
                    field.setText(""); // just in case
                }
            }
        }

        switch (mode) {
            case VIEW -> {
                setNodeEnabledVisibleManaged(editButton, true);
                setNodeEnabledVisibleManaged(saveButton, false);

                nameField.setEditable(false);
                surnameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);

                for (final var node : patientOnlyThings) {
                    if (node instanceof TextField field) {
                        field.setEditable(false);
                    }
                }
            }
            case EDIT -> {
                setNodeEnabledVisibleManaged(editButton, false);
                setNodeEnabledVisibleManaged(saveButton, true);

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

        if (context.length >= 1) {
            if (context[0] instanceof User x) {
                user = x;
            } else {
                throw new IllegalArgumentException();
            }

            if (context.length >= 2) {
                if (context[1] instanceof Mode y) {
                    mode = y;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        this.user = user;
        setMode(mode);

        nameField.setText(user.getName());
        surnameField.setText(user.getSurname());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());

        final var patient = user.asPatient();
        if (patient != null) {
            peselField.setText(patient.getPESEL());
            cityField.setText(patient.getCity());
            streetField.setText(patient.getStreet());
            buildingField.setText(patient.getBuilding());
            postalCodeField.setText(patient.getPostCode());
            postalCityField.setText(patient.getPostCity());
        }
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

    public void goToVisitsAction(ActionEvent actionEvent) {
        if (mightPreventNavigation()) return;
        getParentController().goToView(MainWindowController.Views.VISITS, user);
    }

    public void goToPrescriptionsAction(ActionEvent actionEvent) {
        if (mightPreventNavigation()) return;
        getParentController().goToView(MainWindowController.Views.PRESCRIPTIONS, user);
    }

    public void goToReferralsAction(ActionEvent actionEvent) {
        if (mightPreventNavigation()) return;
        getParentController().goToView(MainWindowController.Views.REFERRALS, user);
    }

    public void editAction(ActionEvent actionEvent) {
        setMode(Mode.EDIT);
    }

    public void saveButton(ActionEvent actionEvent) {
        // TODO: check if dirty, if not - just ignore (maybe toast "nothing to save")
        // TODO: check if other user modified the timetables just before us?
        // TODO: lock UI (Mode.SAVING?) while saving?
        // TODO: validation!!! at least here, but while editing would be nice

        final var entityManager = ClinicApplication.getEntityManager();
        entityManager.getTransaction().begin();

        final var patient = user.asPatient();
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
        }
        else {
            user.setName(nameField.getText().trim());
            user.setSurname(surnameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());
        }

        entityManager.getTransaction().commit();

        // TODO: toast?

        setMode(Mode.VIEW);
    }
}
