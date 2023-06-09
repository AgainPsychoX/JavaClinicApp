package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    private final ObservableList<Integer> hours = FXCollections.observableArrayList();


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
     * @param context contains current mode of window and appointment which is currently displaying/editing.
     */
    @Override
    public void populate(Object... context) {
        User.Role role = ClinicApplication.requireUser().getRole();
        currMode = (Mode) context[0];


        if (currMode == Mode.DETAILS) {
            populateDetails((Appointment) context[1], role);
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

        LocalDateTime hourLDT = appointment.getDate().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int hour = hourLDT.getHour()*60 + hourLDT.getMinute();
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

//    /** Function refreshes hours of selected doctor to match his timetable and exclude hours that are occupied
//     * @param newValue date for which hours are being refreshed
//     *                 if null, current date is used
//     *                 if not null, hours for this date are refreshed
//     *                 (so that doctor can't be assigned to two appointments at the same time)
//     * @param localAppointment appointment which is currently displaying/editing
//     *                         if null, no hours are excluded
//     *                         if not null, hours of this appointment are excluded
//     *                         (so that doctor can't be assigned to two appointments at the same time)
//     * **/
//    private void refreshHours(LocalDate newValue, Appointment localAppointment) {
//        if(newValue == null) newValue = LocalDate.now();
//        final int[] dayHours = {-1, -1};
//        hours.clear();
//        List<Timetable> timetables = doctorCombo.getValue().asUser().getTimetables();
//        for (Timetable timetable : timetables) {
//            if (timetable.getEffectiveDate().isEqual(ChronoZonedDateTime.from(newValue))) {
//                for (Timetable.Entry entry : timetable.getEntries()) {
//                    dayHours[0] = entry.getStartMinute();
//                    dayHours[1] = entry.getEndMinute();
//                    ArrayList<Integer> occupiedHours = getOccupiedHours(dayHours[0], localAppointment);
//                    while (dayHours[0] != -1 && dayHours[0] <= dayHours[1]) {
//                        if(localAppointment != null) {
//                            if(occupiedHours.contains(dayHours[0]) &&
//                                    dayHours[0] != localAppointment.getDate().atZone(ZoneOffset.UTC).getHour() * 60
//                                            + localAppointment.getDate().atZone(ZoneOffset.UTC).getMinute()) {
//                                dayHours[0] += entry.getDurationMinutes();
//                                continue;
//                            }
//                        } else {
//                            if(occupiedHours.contains(dayHours[0])) {
//                                dayHours[0] += entry.getDurationMinutes();
//                                continue;
//                            }
//                        }
//                        hours.add(dayHours[0]);
//                        dayHours[0] += entry.getDurationMinutes();
//                    }
//                }
//            }
//        }
//    }

    private void getFirstDate() {
        LocalDate firstDate = LocalDate.now();
        Schedule schedule = Schedule.of(doctorCombo.getValue().asUser());
    }

    /**
     * Function returns list of hours that are occupied by appointments of selected doctor
     * @param startHour hour from which hours are being checked
     * @param localAppointment appointment which is currently displaying/editing
     *                         if null, no hours are excluded
     *                         if not null, hours of this appointment are excluded
     *                         (so that doctor can't be assigned to two appointments at the same time)
     * @return list of occupied hours
     */
    private ArrayList<Integer> getOccupiedHours(Integer startHour, Appointment localAppointment) {
        ArrayList<Integer> occupiedHours = new ArrayList<>();
        if(startHour != -1){
            if (localAppointment != null) {
                List<Appointment> appointments = ClinicApplication.getEntityManager()
                        .createNamedQuery("allAppointmentsForDoctor", Appointment.class)
                        .setParameter("doctor", localAppointment.getDoctor())
                        .setParameter("id", localAppointment.getId())
                        .getResultList();
                for (Appointment local : appointments) {
                    occupiedHours.add(local.getDate().atZone(ZoneOffset.UTC).getHour() * 60 +
                            local.getDate().atZone(ZoneOffset.UTC).getMinute());
                }
            } else {
                List<Appointment> appointments = ClinicApplication.getEntityManager()
                        .createNamedQuery("allAppointmentsForDoctor", Appointment.class)
                        .setParameter("doctor", doctorCombo.getValue())
                        .setParameter("id", -1)
                        .getResultList();
                for (Appointment local : appointments) {
                    occupiedHours.add(local.getDate().atZone(ZoneOffset.UTC).getHour() * 60 +
                            local.getDate().atZone(ZoneOffset.UTC).getMinute());
                }
            }
        }
        return occupiedHours;
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {}


    /** Creating transaction and execute queries depending on type of action **/
    @FXML
    public void editSave() {
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
            return;
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
        Callback<ListView<Integer>, ListCell<Integer>> cellHourFactory = new Callback<>() {

            @Override
            public ListCell<Integer> call(ListView<Integer> l) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(minutesToHour(item));
                        }
                    }
                };
            }
        };
        pickedDate.setEditable(false);
    }

    public enum Mode {DETAILS, CREATE}

    /** Converts hour(integer) to text form used in details view
     * @param minutes - minutes to convert
     * @return String in format HH:MM
     * **/
    private String minutesToHour(int minutes) {
        int hours = (int) Math.floor((double) minutes /60);
        int remainingMinutes = minutes - hours*60;
        if (remainingMinutes == 0)
            return hours + ":" + remainingMinutes + "0";
        return hours + ":" + remainingMinutes;
    }


    /** Function for picking date and time of appointment **/
    public void pickDate() {
        Schedule schedule = Schedule.of(doctorCombo.getValue());
        final var dialog = new ScheduleSlotPickerDialog(
                schedule, nullCoalesce(LocalDateTime.now()));
        dialog.showAndWait();
        pickedDate.setText(dialog.getResultDateTime().get().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        timestamp = Timestamp.valueOf(dialog.getResultDateTime().get());
        System.out.println(timestamp);
    }
}
