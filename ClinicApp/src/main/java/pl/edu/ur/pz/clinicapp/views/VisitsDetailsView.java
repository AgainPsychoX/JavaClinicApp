package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.Doctor;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Available window modes (details of existing referral or creation of a new one).
 */


public class VisitsDetailsView extends ChildControllerBase<MainWindowController> {

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);
    @FXML
    protected ComboBox<Integer> hourPicker;
    @FXML
    protected DatePicker datePicker;
    @FXML
    protected HBox buttonBox;
    @FXML
    protected ComboBox<Patient> patientCombo;
    @FXML
    protected ComboBox<Doctor> doctorCombo;
    @FXML
    protected TextArea notesTextField;
    @FXML
    protected Button editButton;
    @FXML
    protected Button deleteButton;


    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editAppointment");
    Query deleteQuery = session.getNamedQuery("deleteAppointment");

    /**
     * Current view mode.
     */
    private PrMode currMode;
    private Appointment appointment;

    private final ObservableList<Integer> hours = FXCollections.observableArrayList();

    /** Listener for datePicker which calls function to update list of hours **/

    private final ChangeListener<LocalDate> listener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
            hourPicker.getItems().clear();
            if (currMode == PrMode.CREATE) {
                refreshHours(t1, null);
            } else {
                refreshHours(t1, appointment);
            }

            hourPicker.getItems().addAll(hours);
            hourPicker.setValue(null);
        }
    };


    /**
     * Displays alert about unsaved changes and returns whether user wants to discard them or not.
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
     */
    @Override
    public void populate(Object... context) {
        final var entityManger = ClinicApplication.getEntityManager();

        datePicker.valueProperty().removeListener(listener);
        editState.addListener((observableValue, before, after) -> {
            if (after) {
                editButton.setText("Zapisz");
            } else {
                editButton.setText("Przełóż");
            }
        });
        User.Role role = ClinicApplication.getUser().getRole();
        currMode = (PrMode) context[0];

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
        hourPicker.setButtonCell(cellHourFactory.call(null));
        hourPicker.setCellFactory(cellHourFactory);

        datePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);

            }
        });

        if (currMode == PrMode.DETAILS) {
            appointment = (Appointment) context[1];
            patientCombo.getItems().clear();
            doctorCombo.getItems().clear();
            hourPicker.getItems().clear();
            patientCombo.getItems().add(appointment.getPatient());
            patientCombo.setValue(appointment.getPatient());
            doctorCombo.getItems().add(appointment.getDoctor());
            doctorCombo.setValue(appointment.getDoctor());
            datePicker.setValue(appointment.getDate().atZone(ZoneId.systemDefault()).toLocalDate());
            datePicker.setDisable(true);
            datePicker.setOpacity(1);
            datePicker.getEditor().setOpacity(1);

            LocalDateTime hourLDT = appointment.getDate().atZone(ZoneId.systemDefault()).toLocalDateTime();
            int hour = hourLDT.getHour()*60 + hourLDT.getMinute();
            hourPicker.getItems().add(hour);
            hourPicker.setValue(hour);
            hourPicker.getSelectionModel().select(0);
            notesTextField.setEditable(false);
            notesTextField.setText(appointment.getNotes());
            // in case the referral was edited while app is running
            ClinicApplication.getEntityManager().refresh(appointment);

            if (role != User.Role.ADMIN && appointment.getAddedBy() != ClinicApplication.getUser() && role != User.Role.RECEPTION) {
                buttonBox.getChildren().remove(editButton);
                buttonBox.getChildren().remove(deleteButton);
            } else {
                if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);
                if (!buttonBox.getChildren().contains(deleteButton)) buttonBox.getChildren().add(deleteButton);
            }
            refresh();
        }
        else if (currMode == PrMode.CREATE) {
            doctorCombo.getItems().clear();
            patientCombo.getItems().clear();
            hourPicker.getItems().clear();
            datePicker.setDisable(false);
            datePicker.setValue(null);
            buttonBox.getChildren().remove(deleteButton);
            notesTextField.setEditable(true);
            if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);

            List<Doctor> doctors = new ArrayList<>();

            if (ClinicApplication.getUser().getRole() == User.Role.DOCTOR) {
                doctors.add(ClinicApplication.getUser().asDoctor());
                doctorCombo.getItems().addAll(doctors);
                doctorCombo.setValue(ClinicApplication.getUser().asDoctor());
            } else {
                doctors = entityManger.createNamedQuery("patients", Doctor.class).getResultList();
                doctorCombo.getItems().addAll(doctors);
            }
            doctors.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
            notesTextField.setText(null);
            editState.setValue(true);

            List<Patient> patients = new ArrayList<>();

            if (ClinicApplication.getUser().getRole() == User.Role.PATIENT) {
                patients.add(ClinicApplication.getUser().asPatient());
                patientCombo.getItems().add(ClinicApplication.getUser().asPatient());
                patientCombo.setValue(ClinicApplication.getUser().asPatient());
            } else {
                patients = entityManger.createNamedQuery("patients", Patient.class).getResultList();
            }
            doctors.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
            patientCombo.getItems().addAll(patients);

            datePicker.valueProperty().addListener(listener);
        }
    }


    /** Function refreshes hours of selected doctor to match his timetable and exclude hours that are occupied **/
    private void refreshHours(LocalDate newValue, Appointment localAppointment) {
        if(newValue == null) newValue = LocalDate.now();
        final int[] dayHours = {-1, -1};
        hours.clear();
        hourPicker.getItems().clear();
        // TODO: resolve this cluster-fuck
//        Doctor doctor = doctorCombo.getValue();
//        switch (newValue.getDayOfWeek()) {
//            case MONDAY -> {
//                try {
//                    dayHours[0] = doctor.getWeeklyTimetable().getMondayStart();
//                    dayHours[1] = doctor.getWeeklyTimetable().getMondayEnd();
//                } catch (Exception ignored){}
//            }
//            case TUESDAY -> {
//                try {
//                    dayHours[0] = doctor.getWeeklyTimetable().getTuesdayStart();
//                    dayHours[1] = doctor.getWeeklyTimetable().getTuesdayEnd();
//                } catch (Exception ignored){}
//            }
//            case WEDNESDAY -> {
//                try {
//                    dayHours[0] = doctor.getWeeklyTimetable().getWednesdayStart();
//                    dayHours[1] = doctor.getWeeklyTimetable().getWednesdayEnd();
//                } catch (Exception ignored){}
//            }
//            case THURSDAY -> {
//                try {
//                    dayHours[0] = doctor.getWeeklyTimetable().getThursdayStart();
//                    dayHours[1] = doctor.getWeeklyTimetable().getThursdayEnd();
//                } catch (Exception ignored){}
//            }
//            case FRIDAY -> {
//                try {
//                    dayHours[0] = doctor.getWeeklyTimetable().getFridayStart();
//                    dayHours[1] = doctor.getWeeklyTimetable().getFridayEnd();
//                } catch (Exception ignored){}
//            }
//        }
//        ArrayList<Integer> occupiedHours = new ArrayList<>();
//        if(dayHours[0] != -1){
//            if (localAppointment != null) {
//                List<Appointment> appointments = ClinicApplication.getEntityManager().createNamedQuery("allAppointmentsForDoctor", Appointment.class)
//                        .setParameter("doctor", localAppointment.getDoctor())
//                        .setParameter("id", localAppointment.getId())
//                        .getResultList();
//                for (Appointment local : appointments) {
//                    occupiedHours.add(local.getDate().toLocalDateTime().getHour() * 60 +
//                            local.getDate().toLocalDateTime().getMinute());
//                }
//            } else {
//                List<Appointment> appointments = ClinicApplication.getEntityManager().createNamedQuery("allAppointmentsForDoctor", Appointment.class)
//                        .setParameter("doctor", doctorCombo.getValue())
//                        .setParameter("id", -1)
//                        .getResultList();
//                for (Appointment local : appointments) {
//                    occupiedHours.add(local.getDate().toLocalDateTime().getHour() * 60 +
//                            local.getDate().toLocalDateTime().getMinute());
//                }
//            }
//        }
//
//        while (dayHours[0] != -1 && dayHours[0] <= dayHours[1]) {
//            if(localAppointment != null) {
//                if(occupiedHours.contains(dayHours[0]) && dayHours[0] != localAppointment.getDate().toLocalDateTime().getHour() * 60 + localAppointment.getDate().toLocalDateTime().getMinute()) {
//                    dayHours[0] += localAppointment.getDoctor().getSpeciality().defaultVisitTime;
//                    continue;
//                }
//                hours.add(dayHours[0]);
//                dayHours[0] += localAppointment.getDoctor().getSpeciality().defaultVisitTime;
//            } else {
//                if(occupiedHours.contains(dayHours[0])) {
//                    dayHours[0] += doctorCombo.getValue().getSpeciality().defaultVisitTime;
//                    continue;
//                }
//                hours.add(dayHours[0]);
//                dayHours[0] += doctorCombo.getValue().getSpeciality().defaultVisitTime;
//            }
//        }
        hourPicker.setDisable(hours.isEmpty());
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {}


    /** Creating transaction and execute queries depending on type of action **/
    @FXML
    public void editSave() {
        Transaction transaction;
        try {
            if (currMode == PrMode.DETAILS) {
                datePicker.valueProperty().addListener(listener);
                datePicker.setDisable(false);
                int temp = hourPicker.getValue();
                refreshHours(datePicker.getValue(), appointment);
                hourPicker.getItems().clear();
                hourPicker.getItems().addAll(hours);
                hourPicker.setValue(temp);
                if (editState.getValue()) {
                    if (datePicker.getValue() == null || hourPicker.getValue() == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd zapisu");
                        alert.setHeaderText("Nie wypełniono wymaganych pól");
                        alert.setContentText("Wszytkie pola są wymagane.");
                        alert.showAndWait();
                        editState.setValue(!editState.getValue());
                    } else {
                        int hour = (int)Math.floor((double) hourPicker.getValue() /60);
                        int minutes = hourPicker.getValue() - hour * 60;
                        editQuery.setParameter("date", datePicker.getValue().atTime(hour, minutes));
                        transaction = session.beginTransaction();
                        editQuery.executeUpdate();
                        transaction.commit();
                        ClinicApplication.getEntityManager().refresh(appointment);
                        datePicker.setDisable(true);
                        temp = hourPicker.getValue();
                        hourPicker.getItems().clear();
                        hourPicker.getItems().add(temp);
                        hourPicker.setValue(temp);
                    }
                }
            }
            else {
                if (notesTextField.getText() == null || patientCombo.getValue() == null ||
                        doctorCombo.getValue() == null || datePicker.getValue() == null || hourPicker.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd zapisu");
                    alert.setHeaderText("Nie wypełniono wymaganych pól");
                    alert.setContentText("Wszystkie pola są wymagane");
                    alert.showAndWait();
                    editState.setValue(!editState.getValue());
                } else {
                    transaction = session.beginTransaction();
                    Appointment newVisit = new Appointment();
                    newVisit.setAddedDate(Timestamp.valueOf(LocalDateTime.now()).toInstant());
                    newVisit.setAddedBy(ClinicApplication.getUser());
                    newVisit.setNotes((notesTextField.getText() == null)
                            ? null : notesTextField.getText().trim());
                    newVisit.setPatient(patientCombo.getValue());
                    newVisit.setDuration(doctorCombo.getValue().getDefaultVisitDuration());
                    newVisit.setDoctor(doctorCombo.getValue());
                    newVisit.setStringTags(" ");
                    int hour = (int) Math.floor((double) hourPicker.getValue() /60);
                    int minutes = hourPicker.getValue() - (hour * 60);
                    newVisit.setDate(Timestamp.valueOf(datePicker.getValue().atTime(hour, minutes)).toInstant());
                    session.persist(newVisit);
                    transaction.commit();
                    editState.setValue(!editState.getValue());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Dodawanie Wizyty");
                    alert.setHeaderText("Pomyślnie dodano Wizyte");
                    alert.showAndWait();
                    this.getParentController().goBack();
                    return;
                }
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
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

    public enum PrMode {DETAILS, CREATE}

    /** Converts hour(integer) to text form used in details view**/
    private String minutesToHour(int minutes) {
        int hours = (int) Math.floor((double) minutes /60);
        int remainingMinutes = minutes - hours*60;
        if (remainingMinutes == 0)
            return hours + ":" + remainingMinutes + "0";
        return hours + ":" + remainingMinutes;
    }
}
