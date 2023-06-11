package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.dialogs.ScheduleSlotPickerDialog;
import pl.edu.ur.pz.clinicapp.models.*;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.nullCoalesce;


/**
 * Available window modes (details of existing referral or creation of a new one).
 */


public class VisitsDetailsView extends ChildControllerBase<MainWindowController> implements Initializable {

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);

    @FXML protected Button datePicker;
    @FXML protected HBox buttonBox;
    @FXML protected ComboBox<Patient> patientCombo;
    @FXML protected ComboBox<Doctor> doctorCombo;
    @FXML protected TextArea notesTextField;
    @FXML protected Button editButton;
    @FXML protected Button deleteButton;
    @FXML protected TextField pickedDate;


    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editAppointment");
    Query deleteQuery = session.getNamedQuery("deleteAppointment");

    private Timestamp timestamp;

    /**
     * Get current edit state (fields editable or non-editable).
     * @return Current edit state.
     */
    public static boolean getEditState() {
        return editState.getValue();
    }

    /**
     * Set current edit state (fields editable or non-editable).
     * @param editState New edit state.
     */
    public static void setEditState(boolean editState) {
        VisitsDetailsView.editState.set(editState);
    }

    /**
     * Current view mode.
     */
    private Mode currMode;
    private Appointment appointment;


    /**
     * Displays alert about unsaved changes and returns whether user wants to discard them or not.
     * @return True if user wants to discard changes, false otherwise.
     */
    public static Boolean exitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Checks if window is in edit state and accordingly displays alert and/or changes view to previous one.
     */
    public void onBackClick() {
        if (editState.getValue()) {
            if (exitConfirm()) {
                editState.setValue(!editState.getValue());
                this.getParentController().goBack();
            }
        } else {
            this.getParentController().goBack();
        }


    }

    /**
     * Checks current window mode and user's identity and accordingly removes forbidden activities (edit and deletion
     * for non-creators of the referral or deletion if mode is set to CREATE).
     * @param context contains current mode of window and appointment which is currently displaying/editing
     *                or User which is creating new appointment.
     */
    @Override
    public void populate(Object... context) {
        User.Role role = ClinicApplication.requireUser().getRole();
        currMode = (Mode) context[0];

        if (currMode == Mode.DETAILS) {
            appointment = (Appointment) context[1];
            populateDetails(appointment, role);
        }
        else if (currMode == Mode.CREATE) {
            populateCreate();
        }

    }

    /**
     * Part of populate which only executes when current mode is set to Details.
     * @param appointment Appointment which is currently displaying/editing.
     * @param role Role of current user.
     */

    private void populateDetails(Appointment appointment, User.Role role) {
        patientCombo.getItems().clear();
        doctorCombo.getItems().clear();
        patientCombo.getItems().add(appointment.getPatient());
        patientCombo.setValue(appointment.getPatient());
        doctorCombo.getItems().add(appointment.getDoctor());
        doctorCombo.setValue(appointment.getDoctor());
        datePicker.setDisable(true);

        notesTextField.setEditable(false);
        notesTextField.setText(appointment.getNotes());
        // in case the referral was edited while app is running
        ClinicApplication.getEntityManager().refresh(appointment);

        if (role != User.Role.ADMIN && appointment.getAddedBy() != ClinicApplication.requireUser() && role != User.Role.RECEPTION) {
            buttonBox.getChildren().remove(editButton);
            buttonBox.getChildren().remove(deleteButton);
        } else {
            if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);
            if (!buttonBox.getChildren().contains(deleteButton)) buttonBox.getChildren().add(deleteButton);
        }
    }

    /**
     * Part of populate which only executes when current mode is set to Create.
     */
    private void populateCreate() {
        final var entityManger = ClinicApplication.getEntityManager();
        doctorCombo.getItems().clear();
        patientCombo.getItems().clear();
        buttonBox.getChildren().remove(deleteButton);
        notesTextField.setEditable(true);
        if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);

        List<Doctor> doctors = new ArrayList<>();

        if (ClinicApplication.requireUser().getRole() == User.Role.DOCTOR) {
            doctors.add(ClinicApplication.requireUser().asDoctor());
            doctorCombo.getItems().addAll(doctors);
            doctorCombo.setValue(ClinicApplication.requireUser().asDoctor());
        } else {
            doctors = entityManger.createNamedQuery("doctors", Doctor.class).getResultList();
            doctorCombo.getItems().addAll(doctors);
        }
        doctors.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        notesTextField.setText(null);
        editState.setValue(true);

        List<Patient> patients = new ArrayList<>();

        if (ClinicApplication.requireUser().getRole() == User.Role.PATIENT) {
            patients.add(ClinicApplication.requireUser().asPatient());
            patientCombo.getItems().add(ClinicApplication.requireUser().asPatient());
            patientCombo.setValue(ClinicApplication.requireUser().asPatient());
        } else {
            patients = entityManger.createNamedQuery("patients", Patient.class).getResultList();
        }
        doctors.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        patientCombo.getItems().addAll(patients);

    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {}


    /** Creating transaction and execute queries depending on type of action **/
    @FXML
    public void editSave() {
        datePicker.setDisable(false);
        try {
            if (currMode == Mode.DETAILS) {
                editSaveDetails();
            }
            else {
                editSaveCreate();
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    /** Function executes query for editing appointment **/
    private void editSaveDetails() {
        if (editState.getValue()) {
            if (pickedDate.getText() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd zapisu");
                alert.setHeaderText("Nie wypełniono wymaganych pól");
                alert.setContentText("Wszytkie pola są wymagane.");
                alert.showAndWait();
                editState.setValue(!editState.getValue());
            } else {
                Transaction transaction;
                editQuery.setParameter("date", timestamp);
                transaction = session.beginTransaction();
                editQuery.executeUpdate();
                transaction.commit();
                ClinicApplication.getEntityManager().refresh(appointment);
                datePicker.setDisable(true);
            }
        }
    }

    /** Function executes query for creating appointment **/
    private void editSaveCreate() {
        if (notesTextField.getText() == null || patientCombo.getValue() == null ||
                doctorCombo.getValue() == null || pickedDate.getText() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Wszystkie pola są wymagane");
            alert.showAndWait();
            editState.setValue(!editState.getValue());
        } else {
            Transaction transaction;
            transaction = session.beginTransaction();
            Appointment newVisit = new Appointment();
            newVisit.setAddedDate(Timestamp.valueOf(LocalDateTime.now()).toInstant());
            newVisit.setAddedBy(ClinicApplication.requireUser());
            newVisit.setNotes((notesTextField.getText() == null)
                    ? null : notesTextField.getText().trim());
            newVisit.setPatient(patientCombo.getValue());
            newVisit.setDuration(doctorCombo.getValue().getDefaultVisitDuration());
            newVisit.setDoctor(doctorCombo.getValue());
            newVisit.setStringTags(" ");
            newVisit.setDate(timestamp.toInstant());
            session.persist(newVisit);
            transaction.commit();
            editState.setValue(!editState.getValue());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Dodawanie wizyty");
            alert.setHeaderText("Pomyślnie dodano wizytę.");
            alert.showAndWait();
            this.getParentController().goBack();
        }
    }
    /**
     * Deletes selected visit.
     */
    @FXML
    public void deleteAppointment() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Odwoływanie wizyty");
        alert.setHeaderText("Czy na pewno chcesz odwołać wizytę?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteQuery.setParameter("id", appointment.getId());
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();
            this.getParentController().goBack();
        } else {
            alert.close();
        }
    }

    /**
     * Initialize all factories on start.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editState.addListener((observableValue, before, after) -> {
            if (after) {
                editButton.setText("Zapisz");
            } else {
                editButton.setText("Przełóż");
            }
        });
        Callback<ListView<Patient>, ListCell<Patient>> cellPatientFactory = new Callback<>() {

            @Override
            public ListCell<Patient> call(ListView<Patient> l) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(Patient item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getDisplayName());
                        }
                    }
                };
            }
        };
        patientCombo.setButtonCell(cellPatientFactory.call(null));
        patientCombo.setCellFactory(cellPatientFactory);
        Callback<ListView<Doctor>, ListCell<Doctor>> cellDoctorFactory = new Callback<>() {

            @Override
            public ListCell<Doctor> call(ListView<Doctor> l) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(Doctor item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getDisplayName() + ", " + item.getSpeciality());
                        }
                    }
                };
            }
        };
        doctorCombo.setButtonCell(cellDoctorFactory.call(null));
        doctorCombo.setCellFactory(cellDoctorFactory);
        pickedDate.setEditable(false);
    }

    public enum Mode {DETAILS, CREATE}


    /** Function for picking date and time of appointment **/
    public void pickDate() {
        Schedule schedule = Schedule.of(doctorCombo.getValue());
        final var dialog = new ScheduleSlotPickerDialog(
                schedule, nullCoalesce(LocalDateTime.now()));
        dialog.showAndWait();
        final var selection = dialog.getResult();
        if (selection.isPresent()) {
            final var begin = selection.get().getBeginTime().atZone(ZoneId.systemDefault());
            // TODO: use one picker for both point and duration
            pickedDate.setText(begin.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            timestamp = Timestamp.valueOf(begin.toLocalDateTime());
            // TODO: ditch the Timestamp class for Instant or ZonedDateTime
        }
    }
}
