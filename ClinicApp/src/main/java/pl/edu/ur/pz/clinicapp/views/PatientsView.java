package pl.edu.ur.pz.clinicapp.views;

import freemarker.template.TemplateModelException;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.dialogs.RegisterDialog;
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.utils.views.ViewControllerBase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PatientsView extends ViewControllerBase implements Initializable {

    @FXML
    protected Button registerButton;
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
    @FXML
    protected Button detailsButton;

    protected ObservableList<Patient> patients = FXCollections.observableArrayList();
    protected FilteredList<Patient> filteredPatients = new FilteredList<>(patients, b -> true);

    protected PauseTransition searchDebounce;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make double click to jump to patient details
        table.setRowFactory(tv -> {
            final var row = new TableRow<Patient>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    this.getParentController().goToView(
                            PatientDetailsView.class, PatientDetailsView.RefMode.DETAILS, row.getItem());
                }
            });
            return row;
        });

        nameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
        surnameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSurname()));
        peselCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPESEL()));

        // Show phone number like multiple 3-digits parts separated by space
        phoneCol.setCellValueFactory(features -> {
            final var phoneNumber = features.getValue().getPhone();
            if (phoneNumber == null) {
                return new ReadOnlyObjectWrapper<>(null);
            } else {
                return new ReadOnlyObjectWrapper<>(String.join(" ", phoneNumber.split("(?<=\\G.{3})")));
            }
        });

        emailCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getEmail()));
        addressCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(
                features.getValue().getAddressDisplayShort()));

        // Debounce for search action on key typed to avoid lag
        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);

        table.getSelectionModel().selectedItemProperty().addListener(observable ->
                detailsButton.setDisable(table.getSelectionModel().getSelectedItem() == null));

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());
    }

    private List<Patient> getAllPatients() {
        return ClinicApplication.getEntityManager().createNamedQuery("patients", Patient.class).getResultList();
    }

    @Override
    public void populate(Object... context) {
        patients.setAll(getAllPatients());
        table.setItems(patients);
        table.getSelectionModel().clearSelection();

        // if search field is not empty, perform search again - for user's convenience (no need to hit enter/type again)
        if (searchTextField.getText() != null && !searchTextField.getText().trim().equals("")) {
            searchAction(new ActionEvent());
        }
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
            return patients.getEmail() != null && patients.getEmail().toLowerCase().contains(text);
        });

        SortedList<Patient> sortedPatients = new SortedList<>(filteredPatients);
        sortedPatients.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedPatients);
        table.refresh();
    }


    /**
     * Opens details view of the chosen referral in DETAILS mode.
     */

    @FXML
    protected void detailsAction(ActionEvent event){
        this.getParentController().goToView(PatientDetailsView.class,
                PatientDetailsView.RefMode.DETAILS, table.getSelectionModel().getSelectedItem());

    }

    public void register() {
        this.getParentController().goToView(RegisterDialog.class,
                "INDIRECT");
    }


    /**
     *
     * @throws TemplateModelException when unwrapping template fails or data can't be retrieved
     * @throws IOException when there is a file missing
     * @throws URISyntaxException when string couldn't be passed as {@link URI} reference
     */
    public void report() throws TemplateModelException, IOException, URISyntaxException {
        ReportDialog rd = new ReportDialog();
        ReportDialog.createConfig();
        rd.initialize(null, null);
        rd.patientsReport(patients.subList(patients.size()-20, patients.size()));
    }
}
