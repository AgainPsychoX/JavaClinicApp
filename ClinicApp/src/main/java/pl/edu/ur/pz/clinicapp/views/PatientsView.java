package pl.edu.ur.pz.clinicapp.views;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PatientsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML
    protected TableView<Patient> table;
    @FXML
    protected TableColumn<Patient, String> nameCol;
    @FXML
    protected TableColumn<Patient, String> surnameCol;
    @FXML
    protected TableColumn<Patient, String> peselCol;
    @FXML
    protected TableColumn<Patient, String> phoneCol;
    @FXML
    protected TableColumn<Patient, String> emailCol;
    @FXML
    protected TableColumn<Patient, String> addressCol;
    @FXML
    protected TextField searchTextField;

    protected ObservableList<Patient> patients = FXCollections.observableArrayList();
    protected FilteredList<Patient> filteredPatients = new FilteredList<>(patients, b -> true);

    protected PauseTransition searchDebounce;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
        surnameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSurname()));
        peselCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPESEL()));
        phoneCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPhone()));
        emailCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getEmail()));
        addressCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getAddressDisplayShort()));

        // Debounce for search action on key typed to avoid lag
        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());
    }

    private List<Patient> getAllPatients() {
        return ClinicApplication.getEntityManager().createNamedQuery("patients", Patient.class).getResultList();
    }

    @Override
    public void populate(Object... context) {
        // Testing
        final var q = ClinicApplication.getEntityManager().createNamedQuery("patient34", Patient.class);
        final var r = q.getSingleResult();
        System.out.println(r);

        patients.setAll(getAllPatients());
        table.getItems().setAll(patients);
    }

    @Override
    public void refresh() {
        populate();
    }

    @FXML
    protected void searchAction(ActionEvent event) {
        searchDebounce.stop();

        final var text = searchTextField.getText().toLowerCase();
        filteredPatients.setPredicate(patients -> {
            if (text.isBlank()) return true;
            if (patients.getName().toLowerCase().contains(text)) return true;
            if (patients.getSurname().toLowerCase().contains(text)) return true;
            if (patients.getPhone() != null && patients.getPhone().toLowerCase().contains(text)) return true;
            if (patients.getPESEL().toLowerCase().contains(text)) return true;
            if (patients.getEmail() != null && patients.getEmail().toLowerCase().contains(text)) return true;
            return false;
        });

        SortedList<Patient> sortedPatients = new SortedList<>(filteredPatients);
        sortedPatients.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedPatients);
        table.refresh();
    }
}
