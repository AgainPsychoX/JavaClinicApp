package pl.edu.ur.pz.clinicapp.views;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrescriptionsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML
    protected VBox vBox;
    @FXML
    protected TableView<Prescription> table;
    @FXML
    protected TableColumn<Prescription, String> patientCol;
    @FXML
    protected TableColumn<Prescription, String> dateCol;
    @FXML
    protected TableColumn<Prescription, String> doctorCol;
    @FXML
    protected TableColumn<Prescription, Integer> codeCol;
    @FXML
    protected TableColumn<Prescription, String> tagsCol;
    @FXML
    protected Button addButton;
    @FXML
    protected Button moveButton;
    @FXML
    protected Button detailsButton;
    @FXML
    protected TextField searchTextField;

    protected ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
    protected FilteredList<Prescription> filteredPrescriptions = new FilteredList<>(prescriptions, b -> true);
    protected PauseTransition searchDebounce;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query query = session.getNamedQuery("findUserPrescriptions").setParameter("uname",
            ClinicApplication.getUser().getDatabaseUsername());

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Initializes table cells and adds listener which enables or disables the edit/save button.
     */
    @Override
    public void populate(Object... context) {
        table.getSelectionModel().clearSelection();
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));

        dateCol.setCellValueFactory(new PropertyValueFactory<>("addedDate"));

        table.getSelectionModel().selectedItemProperty().addListener(observable ->
                detailsButton.setDisable(table.getSelectionModel().getSelectedItem() == null));

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

        refresh();

        // if search field is not empty, perform search again - for user's convenience (no need to hit enter/type again)
        if (searchTextField.getText() != null && !searchTextField.getText().trim().equals("")) {
            searchAction(new ActionEvent());
        }

//        doctorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorName()));
//        doctorCol.setCellFactory(column -> new TableCell<>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null)
//                    setText(null);
//                else
//                    setText(item);
//            }

        //Testing
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        prescriptions2.addAll(query.getResultList());
//        table2.setItems(prescriptions2);
        //
    }

    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        List results = query.getResultList();
        for (Object presElem : results) {
            ClinicApplication.getEntityManager().refresh(presElem);
        }
        prescriptions.setAll(query.getResultList());
        table.setItems(prescriptions);
    }

    /**
     * Initialize responsive table view
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (ClinicApplication.getUser().getRole() == User.Role.PATIENT) {
            patientCol.setVisible(false);
            addButton.setVisible(false);
            vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                double tableWidth = newVal.doubleValue() - 50;
                dateCol.setPrefWidth(tableWidth * 0.2);
                doctorCol.setPrefWidth(tableWidth * 0.2);
                codeCol.setPrefWidth(tableWidth * 0.2);
            });
        } else {
            vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                double tableWidth = newVal.doubleValue() - 50;
                patientCol.setPrefWidth(tableWidth * 0.2);
                dateCol.setPrefWidth(tableWidth * 0.2);
                doctorCol.setPrefWidth(tableWidth * 0.2);
                codeCol.setPrefWidth(tableWidth * 0.2);
            });
        }
    }

    @FXML
    public void searchAction(ActionEvent event) {
        searchDebounce.stop();
        table.getSelectionModel().clearSelection();
        final var text = searchTextField.getText().toLowerCase();
        filteredPrescriptions.setPredicate(referral -> {
            if (text.isBlank()) return true;
            if (referral.getAddedDate().toString().contains(text.trim())) return true;
            if (referral.getDoctorName().toLowerCase().contains(text.trim())) return true;
            if (referral.getNotes().toLowerCase().contains(text.trim())) return true;
            if (referral.getStringTags().toLowerCase().contains(text.trim())) return true;
            return referral.getGovernmentId() != null && referral.getGovernmentId().contains(text.trim());
        });

        SortedList<Prescription> sortedReferrals = new SortedList<>(filteredPrescriptions);
        sortedReferrals.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedReferrals);
    }

    /**
     * Opens details view of the chosen referral in DETAILS mode.
     */
    @FXML
    public void displayDetails() {
        this.getParentController().goToView(MainWindowController.Views.PRESCRIPTION_DETAILS,
                PrescriptionDetailsView.PrMode.DETAILS, table.getSelectionModel().getSelectedItem());
    }

    //Insert for testing purposes - adds a prescription for current user instead of patient
    @FXML
    protected void addPrescription() {
        this.getParentController().goToView(MainWindowController.Views.PRESCRIPTION_DETAILS,
                PrescriptionDetailsView.PrMode.CREATE, ClinicApplication.getUser());
    }

    /**
     * Opens government's website for patients.
     */
    @FXML
    protected void moveToIKP() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.pacjent.gov.pl"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
