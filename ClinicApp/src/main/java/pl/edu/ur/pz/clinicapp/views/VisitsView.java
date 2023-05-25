package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class VisitsView extends ChildControllerBase<MainWindowController> {
    @FXML protected Button newButton;
    @FXML protected ComboBox filter;
    @FXML protected TableView<Appointment> table;
    @FXML protected TableColumn<Appointment, Date> dateCol;
    @FXML protected TableColumn<Appointment, String> doctorCol;
    @FXML protected TableColumn<Appointment, String> specCol;
    @FXML protected TextField searchTextField;

    private boolean firstRun = true;

    protected ObservableList<Appointment> appointments = FXCollections.observableArrayList();


    /** Searching bar functionality it checks every column to find matching substring in values **/
    @FXML
    protected void searchEnterAction(ActionEvent event) {
        searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
                    appointmentFilteredList.setPredicate(appointment -> {
                        if (ClinicApplication.getUser().getRole() == User.Role.DOCTOR) {
                            return appointment.getDate().toString().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getPatient().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getNotes().toLowerCase().contains(searchTextField.getText().toLowerCase());
                        } else if (ClinicApplication.getUser().getRole() == User.Role.PATIENT) {
                            return appointment.getDate().toString().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getNotes().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getDoctor().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getDoctor().getSpeciality().toLowerCase().contains(searchTextField.getText().toLowerCase());
                        } else {
                            return appointment.getDate().toString().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getNotes().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getDoctor().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getDoctor().getSpeciality().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                    appointment.getPatient().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase());
                        }
                    });
                    table.setItems(appointmentFilteredList);
                }
            }
        });
    }

    @FXML
    protected void sortAction() {}

    /** Moving you to add view **/
    @FXML
    protected void newAction() {
        this.getParentController().goToView(MainWindowController.Views.VISIT_DETAILS,
                VisitsDetailsView.PrMode.CREATE, ClinicApplication.getUser());
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /** Returns appointments depending on role of user **/
    private List<Appointment> getAllVisits() {
        final var doctor = ClinicApplication.getUser().asDoctor();
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
        if (ClinicApplication.getUser().getRole() == User.Role.NURSE) {
            filter.setDisable(true);
            filter.setOpacity(0);
            newButton.setDisable(true);
        }
        else {
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            if (ClinicApplication.getUser().getRole() == User.Role.DOCTOR) {
                doctorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPatient().getDisplayName()));
                doctorCol.setText("Pacjent");
                specCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNotes()));
                specCol.setText("Notatki");
            } else {
                if (firstRun) {
                    TableColumn<Appointment, String> notesCol = new TableColumn<>();
                    notesCol.setText("Notatki");
                    notesCol.setMaxWidth(specCol.getMaxWidth());
                    table.getColumns().add(notesCol);
                    notesCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNotes()));
                }

                if (ClinicApplication.getUser().getRole() != User.Role.PATIENT) {
                    if (firstRun) {
                        TableColumn<Appointment, String> patientCol = new TableColumn<>();
                        patientCol.setText("Pacjent");
                        patientCol.setMaxWidth(specCol.getMaxWidth());
                        table.getColumns().add(patientCol);
                        patientCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPatient().getDisplayName()));
                    }
                }
                doctorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDoctor().getDisplayName()));
                specCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDoctor().getSpeciality()));

            }
            firstRun = false;
            appointments.clear();
            table.getItems().clear();
            table.refresh();

            table.setRowFactory(tv -> {
                TableRow<Appointment> localRow = new TableRow<>();
                localRow.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        if (mouseEvent.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                            this.getParentController().goToView(MainWindowController.Views.VISIT_DETAILS, VisitsDetailsView.PrMode.DETAILS, table.getSelectionModel().getSelectedItem());
                        }
                    }
                });
                return localRow;
            });
            try {
                table.getItems().addAll(getAllVisits());
            } catch (Exception e) {
            }

            filter.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
                    appointmentFilteredList.setPredicate(appointment -> {
                        return filter.getValue().equals("Nadchodzące wizyty") ?
                                appointment.getDate().compareTo(Timestamp.valueOf(LocalDateTime.now()).toInstant()) > 0
                                : appointment.getDate().compareTo(Timestamp.valueOf(LocalDateTime.now()).toInstant()) < 0;
                    });
                    table.setItems(appointmentFilteredList);
                }
            });
            appointments.addAll(getAllVisits());
        }
    }





    @Override
    public void refresh() { populate(); }

}
