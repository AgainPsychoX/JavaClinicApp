package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class VisitsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML protected Button newButton;
    @FXML protected ComboBox<String> filter;
    @FXML protected TableView<Appointment> table;
    @FXML protected TableColumn<Appointment, String> dateCol;
    @FXML protected TableColumn<Appointment, String> doctorCol;
    @FXML protected TableColumn<Appointment, String> specCol;
    @FXML protected TextField searchTextField;
    protected ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private Patient patientGlobal;


    /** Searching bar functionality it checks every column to find matching substring in values **/
    @FXML
    protected void searchEnterAction() {
        searchTextField.setOnKeyPressed(keyEvent -> {
                FilteredList<Appointment> appointmentFilteredList = filterList();
                table.setItems(appointmentFilteredList);
        });
    }

    private FilteredList<Appointment> filterList() {
        patientGlobal = null;
        FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
        appointmentFilteredList.setPredicate(appointment -> {
            if (ClinicApplication.requireUser().getRole() == User.Role.DOCTOR) {
                return appointment.getDate().toString().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getPatient().getDisplayName().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getNotes().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase());
            } else if (ClinicApplication.requireUser().getRole() == User.Role.PATIENT) {
                return appointment.getDate().toString().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getNotes().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getDoctor().getDisplayName().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getDoctor().getSpeciality().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase());
            } else {
                return appointment.getDate().toString().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getNotes().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getDoctor().getDisplayName().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getDoctor().getSpeciality().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase()) ||
                        appointment.getPatient().getDisplayName().toLowerCase()
                                .contains(searchTextField.getText().toLowerCase());
            }
    });
        return appointmentFilteredList;
    }

    private FilteredList<Appointment> patientAppointments() {
        FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
        appointmentFilteredList.setPredicate(appointment -> appointment.getPatient().getDisplayName().toLowerCase()
                .contains(searchTextField.getText().toLowerCase()));
        return appointmentFilteredList;
    }

    @FXML
    protected void sortAction() {}

    /** Moving you to add view **/
    @FXML
    protected void newAction() {
        this.getParentController().goToView(MainWindowController.Views.VISIT_DETAILS,
                VisitsDetailsView.Mode.CREATE, ClinicApplication.getUser(), patientGlobal);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /** Returns appointments depending on role of user **/
    private List<Appointment> getAllVisits() {
        final var doctor = ClinicApplication.requireUser().asDoctor();
        if (doctor != null) {
            return ClinicApplication.getEntityManager().createNamedQuery("appointmentsDoctor", Appointment.class)
                    .setParameter("doctor", doctor)
                    .getResultList();
        }
        else {
            return ClinicApplication.getEntityManager().createNamedQuery("appointments", Appointment.class)
                    .getResultList();
        }
    }

    /** Populates the table with adding cell factories **/
    @Override
    public void populate(Object... context) {
        if (ClinicApplication.requireUser().getRole() == User.Role.NURSE) {
            filter.setDisable(true);
            filter.setOpacity(0);
            newButton.setDisable(true);
        }
        else {
            appointments.clear();
            table.getItems().clear();
            table.refresh();
            appointments.addAll(getAllVisits());
            if (ClinicApplication.requireUser().getRole() != User.Role.PATIENT && context.length > 0) {
                Patient patient = (Patient) context[0];
                searchTextField.setText(patient.getDisplayName());
                FilteredList<Appointment> appointmentFilteredList = patientAppointments();
                table.setItems(appointmentFilteredList);
                patientGlobal = patient;
                searchTextField.setEditable(ClinicApplication.requireUser().getRole() != User.Role.RECEPTION);
            } else {
                searchTextField.setText("");
                patientGlobal = null;
                table.setItems(appointments);
            }
        }
    }

    @Override
    public void refresh() { populate(); }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateCol.setCellValueFactory(cellData -> {
            Date date = Date.from(cellData.getValue().getDate());
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            return new ReadOnlyStringWrapper(df.format(date));
        });
        if (ClinicApplication.requireUser().getRole() == User.Role.DOCTOR) {
            doctorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPatient().getDisplayName()));
            doctorCol.setText("Pacjent");
            specCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNotes()));
            specCol.setText("Notatki");
        }
        else {
            TableColumn<Appointment, String> notesCol = new TableColumn<>();
            notesCol.setText("Notatki");
            notesCol.setMaxWidth(specCol.getMaxWidth());
            table.getColumns().add(notesCol);
            notesCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNotes()));
            if (ClinicApplication.requireUser().getRole() != User.Role.PATIENT) {
                    TableColumn<Appointment, String> patientCol = new TableColumn<>();
                    patientCol.setText("Pacjent");
                    patientCol.setMaxWidth(specCol.getMaxWidth());
                    table.getColumns().add(patientCol);
                    patientCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPatient().getDisplayName()));
            }
            doctorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDoctor().getDisplayName()));
            specCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDoctor().getSpeciality()));
        }

        table.setRowFactory(tv -> {
            TableRow<Appointment> localRow = new TableRow<>();
            localRow.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if (mouseEvent.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                        this.getParentController().goToView(MainWindowController.Views.VISIT_DETAILS, VisitsDetailsView.Mode.DETAILS, table.getSelectionModel().getSelectedItem());
                    }
                }
            });
            return localRow;
        });
        filter.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
            appointmentFilteredList.setPredicate(appointment -> filter.getValue().equals("Nadchodzące wizyty") ?
                    appointment.getDate().compareTo(Timestamp.valueOf(LocalDateTime.now()).toInstant()) > 0
                    : appointment.getDate().compareTo(Timestamp.valueOf(LocalDateTime.now()).toInstant()) < 0);
            table.setItems(appointmentFilteredList);
        });
    }
}
