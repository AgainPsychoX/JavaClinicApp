package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
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
    @FXML protected VBox vBox;
    @FXML protected TableView<Appointment> table;
    @FXML protected TableColumn<Appointment, Date> dateCol;
    @FXML protected TableColumn<Appointment, String> doctorCol;
    @FXML protected TableColumn<Appointment, String> specCol;
    @FXML protected TextField searchTextField;

    private boolean firstRun = true;

    protected ObservableList<Appointment> appointments = FXCollections.observableArrayList();


    /** Searching bar functionality it checks every column to find matching substring in values **/
    @FXML
    protected void searchEnterAction() {
        searchTextField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
                appointmentFilteredList.setPredicate(appointment -> {
                    if(ClinicApplication.getUser().getRole().name().equals("DOCTOR")) {
                        return appointment.getDate().toString().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getPatient().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getNotes().toLowerCase().contains(searchTextField.getText().toLowerCase());
                    } else if(ClinicApplication.getUser().getRole().name().toLowerCase().equals("PATIENT")) {
                        return appointment.getDate().toString().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getNotes().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getDoctor().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getDoctor().getSpeciality().getName().toLowerCase().contains(searchTextField.getText().toLowerCase());
                    } else {
                        return appointment.getDate().toString().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getNotes().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getDoctor().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getDoctor().getSpeciality().getName().toLowerCase().contains(searchTextField.getText().toLowerCase()) ||
                                appointment.getPatient().getDisplayName().toLowerCase().contains(searchTextField.getText().toLowerCase());
                    }
                });
                table.getItems().clear();
                table.getItems().addAll(appointmentFilteredList);
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
        return ClinicApplication.getUser().getRole().name().equals("DOCTOR") ?
                ClinicApplication.getEntityManager().createNamedQuery("appointmentsDoctor", Appointment.class).setParameter("doctor", ClinicApplication.getUser()).getResultList()
                : ClinicApplication.getEntityManager().createNamedQuery("appointments", Appointment.class).getResultList();
    }

    /** Populates the table with adding cell factories **/
    @Override
    public void populate(Object... context) {

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        if(ClinicApplication.getUser().getRole().name().equals("DOCTOR")){
            doctorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPatient().getDisplayName()));
            doctorCol.setText("Pacjent");
            specCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNotes()));
            specCol.setText("Notatki");
        }
        else {
            if(firstRun) {
                TableColumn<Appointment, String> notesCol = new TableColumn<>();
                notesCol.setText("Notatki");
                notesCol.setMaxWidth(specCol.getMaxWidth());
                table.getColumns().add(notesCol);
                notesCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNotes()));
            }

            if (!ClinicApplication.getUser().getRole().name().equals("PATIENT")) {
                if(firstRun) {
                    TableColumn<Appointment, String> patientCol = new TableColumn<>();
                    patientCol.setText("Pacjent");
                    patientCol.setMaxWidth(specCol.getMaxWidth());
                    table.getColumns().add(patientCol);
                    patientCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPatient().getDisplayName()));
                }
            }
            doctorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDoctor().getDisplayName()));
            specCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDoctor().getSpeciality().getName()));

        }
        firstRun = false;
        appointments.clear();
        table.getItems().clear();
        table.refresh();

        table.setRowFactory(tv -> {
            TableRow<Appointment> localRow = new TableRow<>();
            localRow.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if (mouseEvent.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                        this.getParentController().goToView(MainWindowController.Views.VISIT_DETAILS, VisitsDetailsView.PrMode.DETAILS, table.getSelectionModel().getSelectedItem());
                    }
                }
            });
            return localRow;
        });

        try {
            table.getItems().addAll(getAllVisits());
        } catch (Exception ignored) {}

        filter.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(appointments);
            appointmentFilteredList.setPredicate(appointment -> filter.getValue().equals("Nadchodzące wizyty") ?
                    appointment.getDate().compareTo(Timestamp.valueOf(LocalDateTime.now())) > 0
                    : appointment.getDate().compareTo(Timestamp.valueOf(LocalDateTime.now())) < 0);
            table.setItems(appointmentFilteredList);
        });
        if (ClinicApplication.getUser().getRole().equals(User.Role.NURSE))
            newButton.setDisable(true);
    }





    @Override
    public void refresh() { populate(); }

}
