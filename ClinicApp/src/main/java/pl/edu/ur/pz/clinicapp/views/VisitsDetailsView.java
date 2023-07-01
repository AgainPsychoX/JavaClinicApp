package pl.edu.ur.pz.clinicapp.views;

import freemarker.template.SimpleDate;
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
import pl.edu.ur.pz.clinicapp.dialogs.AppointmentSlotPickerDialog;
import pl.edu.ur.pz.clinicapp.dialogs.ScheduleSlotPickerDialog;
import pl.edu.ur.pz.clinicapp.models.*;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                    .withZone(ZoneId.systemDefault());

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
        if (getEditState()) {
            if (exitConfirm()) {
                setEditState(false);
                this.getParentController().goBack();
            }
        } else {
            this.getParentController().goBack();
        }


    }

    /**
     * Checks current window mode and user's identity and accordingly removes forbidden activities (edit and deletion
     * for non-creators of the referral or deletion if mode is set to CREATE).
     * @param context contains current mode of window and {@link pl.edu.ur.pz.clinicapp.models.Appointment} which is currently displaying/editing
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
            populateCreate((Patient) context[2]);
        }

    }

    /**
     * Part of populate which only executes when current mode is set to Details.
     * @param appointment {@link pl.edu.ur.pz.clinicapp.models.Appointment} which is currently displaying/editing.
     * @param role Role of current user.
     */

    private void populateDetails(Appointment appointment, User.Role role) {
        patientCombo.getItems().clear();
        doctorCombo.getItems().clear();
        patientCombo.getItems().add(appointment.getPatient());
        patientCombo.setValue(appointment.getPatient());
        doctorCombo.getItems().add(appointment.getDoctor());
        doctorCombo.setValue(appointment.getDoctor());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        pickedDate.setText(dateFormat.format(Date.from(appointment.getDate())));
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
    private void populateCreate(Patient patient) {
        final var entityManger = ClinicApplication.getEntityManager();
        doctorCombo.getItems().clear();
        patientCombo.getItems().clear();
        buttonBox.getChildren().remove(deleteButton);
        notesTextField.setEditable(true);
        if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);

        List<Doctor> doctors = entityManger.createNamedQuery("doctors", Doctor.class).getResultList();
        doctorCombo.getItems().addAll(doctors);
        doctors.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        notesTextField.setText(null);
        setEditState(true);

        List<Patient> patients = new ArrayList<>();

        if (ClinicApplication.requireUser().getRole() == User.Role.PATIENT) {
            patients.add(ClinicApplication.requireUser().asPatient());
        } else if(patient != null) {
            patients.add(patient);
        } else {
            patients = entityManger.createNamedQuery("patients", Patient.class).getResultList();
        }
        patients.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        patientCombo.getItems().addAll(patients);
        patientCombo.setValue(patients.get(0));
        doctors.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {}


    /** Creating transaction and execute queries depending on type of action **/
    @FXML
    public void editSave() {
        setEditState(true);
        datePicker.setDisable(false);
        try {
            if (currMode == Mode.DETAILS) {
                editSaveDetails();
            }
            else {
                editSaveCreate();
            }
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
        setEditState(false);
    }

    /** Function executes query for editing {@link pl.edu.ur.pz.clinicapp.models.Appointment}. **/
    private void editSaveDetails() {
        if (getEditState()) {
            if (pickedDate.getText() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd zapisu");
                alert.setHeaderText("Nie wypełniono wymaganych pól");
                alert.setContentText("Wszytkie pola są wymagane.");
                alert.showAndWait();
            } else {
                Transaction transaction;
                editQuery.setParameter("date", timestamp);
                transaction = session.beginTransaction();
                editQuery.executeUpdate();
                transaction.commit();
                ClinicApplication.getEntityManager().refresh(appointment);
                datePicker.setDisable(true);
                createNotif(doctorCombo.getValue().asUser(), patientCombo.getValue().asUser(),
                        "Zmieniono datę wizyty na: " + formatter.format(timestamp.toInstant()) + ".");
            }
        }
    }

    /** Function executes query for creating {@link pl.edu.ur.pz.clinicapp.models.Appointment}. **/
    private void editSaveCreate() {
        if (notesTextField.getText() == null || patientCombo.getValue() == null ||
                doctorCombo.getValue() == null || pickedDate.getText() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Wszystkie pola są wymagane");
            alert.showAndWait();
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
            pickedDate.setText(null);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Dodawanie wizyty");
            alert.setHeaderText("Pomyślnie dodano wizytę.");
            alert.showAndWait();
            createNotif(doctorCombo.getValue().asUser(), patientCombo.getValue().asUser(),
                    "Stworzono wizytę wizytę na dzień: " + formatter.format(timestamp.toInstant()) +'.');
            this.getParentController().goBack();

        }
    }
    /**
     * Deletes selected {@link pl.edu.ur.pz.clinicapp.models.Appointment}.
     */
    @FXML
    public void deleteAppointment() {
        Instant tempDate;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Odwoływanie wizyty");
        alert.setHeaderText("Czy na pewno chcesz odwołać wizytę?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteQuery.setParameter("id", appointment.getId());
            tempDate = appointment.getDate();
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();
            createNotif(doctorCombo.getValue().asUser(), patientCombo.getValue().asUser(),
                    "Odwołano wizytę z dnia: " + formatter.format(tempDate) +"." );
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
        if (doctorCombo.getValue() != null) {
            Schedule schedule = Schedule.of(doctorCombo.getValue());
            final var dialog = new AppointmentSlotPickerDialog(
                    schedule, nullCoalesce(LocalDateTime.now()));
            dialog.showAndWait();
            final var selection = dialog.getResult();
            if (selection.isPresent()) {
                final var begin = selection.get().getBeginInstant().atZone(ZoneId.systemDefault());
                // TODO: use one picker for both point and duration
                pickedDate.setText(begin.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                timestamp = Timestamp.valueOf(begin.toLocalDateTime());
                // TODO: ditch the Timestamp class for Instant or ZonedDateTime
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Nie wybrano lekarza.").showAndWait();
        }
    }

    /**
     * Function for creating notifications
     * @param destinationUser user that notification is sent to
     * @param text content of notification
     * @param sourceUser user that sends notification
     **/
    public void createNotif(User destinationUser, User sourceUser, String text) {
        Notification notif = new Notification();
        notif.setSourceUser(sourceUser);
        notif.setDestinationUser(destinationUser);
        notif.setSentDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        notif.setContent(text);
        Transaction transaction = session.beginTransaction();
        session.persist(notif);
        transaction.commit();
        System.out.println("Utworzone");

    }
}
