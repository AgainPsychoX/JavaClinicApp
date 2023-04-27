package pl.edu.ur.pz.clinicapp.views;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import javax.persistence.Query;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PatientsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML
    protected VBox vBox;
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


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        Query query = ClinicApplication.getEntityManager().createNamedQuery("findAllPatients", Patient.class);
        patients.setAll(query.getResultList());
        table.getItems().setAll(patients);

    }

    @Override
    public void refresh() {
        populate();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
        surnameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSurname()));
        peselCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPESEL()));
        phoneCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPhone()));
        emailCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getEmail()));
        addressCol.setCellValueFactory(features -> {

            String address =  features.getValue().getCity() + " ul." + features.getValue().getStreet() + " " + features.getValue().getBuilding();
            return new ReadOnlyObjectWrapper<>(address);
        });

    }

    public void searchAction(ActionEvent event) {

        var newValue = searchTextField.getText();
        filteredPatients.setPredicate(patients -> {

            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            if (patients.getName().toLowerCase().contains(lowerCaseFilter)) return true;
            if (patients.getSurname().toLowerCase().contains(lowerCaseFilter)) return true;
            if (patients.getPhone() != null && patients.getPhone().toLowerCase().contains(lowerCaseFilter)) return true;
            if (patients.getPESEL().toLowerCase().contains(lowerCaseFilter)) return true;
            if (patients.getEmail() != null && patients.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;
            return false;
        });

        SortedList<Patient> sortedPatients = new SortedList<>(filteredPatients);

        sortedPatients.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedPatients);

        table.refresh();

    }
}
