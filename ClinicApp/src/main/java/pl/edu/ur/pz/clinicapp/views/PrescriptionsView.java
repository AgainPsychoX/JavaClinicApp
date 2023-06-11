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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.ResourceBundle;

public class PrescriptionsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML protected VBox vBox;
    @FXML protected TableView<Prescription> table;
    @FXML protected TableColumn<Prescription, String> patientCol;
    @FXML protected TableColumn<Prescription, String> dateCol;
    @FXML protected TableColumn<Prescription, String> doctorCol;
    @FXML protected TableColumn<Prescription, String> codeCol;
    @FXML protected TableColumn<Prescription, String> tagsCol;
    @FXML protected Button addButton;
    @FXML protected Button moveButton;
    @FXML protected Button detailsButton;
    @FXML protected TextField searchTextField;
    @FXML protected ComboBox filter;
    @FXML protected HBox buttonBox;
    @FXML protected Text backText;

    protected ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
    protected FilteredList<Prescription> filteredPrescriptions = new FilteredList<>(prescriptions, b -> true);
    Session session;
    protected PauseTransition searchDebounce;

    Query currQuery;
    Query findUsersPrescriptions;
    Query allPrescriptions;
    Query createdPrescriptions;
    Query findTargetUsersPrescriptions;

    User.Role currUserRole;
    private Patient targetPatient;

    private enum filterMode{OWN, CREATED, ALL}
    private static final EnumMap<filterMode, String> filteredModeToString = new EnumMap<>(filterMode.class);

    public void setCurrQuery(User.Role role){
        if(currQuery == null){
            if(role == User.Role.PATIENT){
                currQuery = findUsersPrescriptions;
            }else if(role == User.Role.DOCTOR){
                currQuery = createdPrescriptions;
            }else{
                currQuery = allPrescriptions;
            }
        }
    }


    /**
     * Sets available combobox options for filtering {@link Prescription}s according to given
     * {@link pl.edu.ur.pz.clinicapp.models.User.Role}.
     *
     * @param role {@link  pl.edu.ur.pz.clinicapp.models.User.Role} of current user.
     */
    public void setFilterVals(User.Role role){
        if(role == User.Role.PATIENT || targetPatient != null)
            filter.setVisible(false);
        else{
            filter.setItems(FXCollections.observableArrayList(
                    filteredModeToString.get(filterMode.ALL),
                    filteredModeToString.get(filterMode.CREATED),
                    filteredModeToString.get(filterMode.OWN)
            ));
            filter.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (ClinicApplication.requireUser().getRole() == User.Role.PATIENT) {
            patientCol.setVisible(false);
            addButton.setVisible(false);
        }

        doctorCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getDoctorName()));
        patientCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPatientName()));
        codeCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getGovernmentId()));
        tagsCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getTags()));
        dateCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getAddedDate().toString()));

        table.getSelectionModel().selectedItemProperty().addListener(observable ->
                detailsButton.setDisable(table.getSelectionModel().getSelectedItem() == null));

    }

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
        if (currQuery == findTargetUsersPrescriptions) currQuery = null;
        targetPatient = ((context.length > 0) ? ((Patient) context[0]) : null);
        buttonBox.getChildren().clear();
        if (targetPatient != null){
            buttonBox.getChildren().add(detailsButton);
            if (!(currUserRole == User.Role.RECEPTION)) buttonBox.getChildren().add(addButton);
            if (!vBox.getChildren().contains(backText)) vBox.getChildren().add(1, backText);
            backText.setText("< Powrót do pacjenta " + targetPatient.getDisplayName());
        }else {
            buttonBox.getChildren().add(detailsButton);
            vBox.getChildren().remove(backText);
        }

        session = ClinicApplication.getEntityManager().unwrap(Session.class);
        findUsersPrescriptions = session.getNamedQuery("findUsersPrescriptions").setParameter("patient",
            ClinicApplication.requireUser().asPatient());
        findTargetUsersPrescriptions = session.getNamedQuery("findUsersPrescriptions").setParameter("patient",
                targetPatient);
        allPrescriptions = session.getNamedQuery("allPrescriptions");
        createdPrescriptions = session.getNamedQuery("createdPrescriptions").setParameter("user",
                ClinicApplication.requireUser());

        table.getSelectionModel().clearSelection();

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

        filteredModeToString.put(filterMode.OWN, "Moje recepty");
        filteredModeToString.put(filterMode.CREATED, "Utworzone przeze mnie");
        filteredModeToString.put(filterMode.ALL, "Wszystkie");

        User.Role currUserRole = ClinicApplication.requireUser().getRole();
        if(targetPatient != null){
            currQuery = findTargetUsersPrescriptions;
        }else setCurrQuery(currUserRole);
        setFilterVals(currUserRole);


        if(currQuery == allPrescriptions) filter.setValue(filteredModeToString.get(filterMode.ALL));
        else if(currQuery == createdPrescriptions) filter.setValue(filteredModeToString.get(filterMode.CREATED));
        else if(currQuery == findUsersPrescriptions) filter.setValue(filteredModeToString.get(filterMode.OWN));

        refresh();
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        List results = currQuery.getResultList();
        for (Object presElem : results) {
            ClinicApplication.getEntityManager().refresh(presElem);
        }
        prescriptions.setAll(currQuery.getResultList());
        table.setItems(prescriptions);

        // if search field is not empty, perform search again - for user's convenience (no need to hit enter/type again)
        if (searchTextField.getText() != null && !searchTextField.getText().trim().equals("")) {
            searchAction(new ActionEvent());
        }
    }

    @FXML
    public void changeFilter(){
        if (filter.getSelectionModel().getSelectedItem() == null || targetPatient != null) return;
        if (filter.getSelectionModel().getSelectedItem() == filteredModeToString.get(filterMode.ALL))
            currQuery = allPrescriptions;
        else if (filter.getSelectionModel().getSelectedItem() == filteredModeToString.get(filterMode.CREATED))
            currQuery = createdPrescriptions;
        else currQuery = findUsersPrescriptions;

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
            if (referral.getTags().toLowerCase().contains(text.trim())) return true;
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
                PrescriptionDetailsView.Mode.DETAILS, table.getSelectionModel().getSelectedItem(), targetPatient);

    }

    /**
     * Opens details view in CREATE mode.
     */
    @FXML
    protected void addPrescription() {
        this.getParentController().goToView(MainWindowController.Views.PRESCRIPTION_DETAILS,
                PrescriptionDetailsView.Mode.CREATE, targetPatient);
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

    @FXML
    public void onBackClick(){
        this.getParentController().goToView(MainWindowController.Views.PATIENT_DETAILS,
        PatientDetailsView.RefMode.DETAILS, targetPatient); // tu chyba pres mode
    }

    @FXML
    protected void printPrescriptions() {
        this.getParentController().goToView(MainWindowController.Views.REPORTS, ReportDialog.Mode.PRESCRIPTION, prescriptions);

    }
}
