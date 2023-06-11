package pl.edu.ur.pz.clinicapp.views;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;

/**
 * View controller to view and search for {@link Referral}s.
 */
public class ReferralsView extends ChildControllerBase<MainWindowController> {

    @FXML
    protected HBox backBox;
    @FXML
    protected Text backText;
    @FXML
    protected HBox buttonBox;
    @FXML
    protected ComboBox filter;
    @FXML
    protected Button addButton;
    @FXML
    protected Button ikpButton;
    @FXML
    protected Button detailsButton;
    @FXML
    protected TextField searchTextField;
    @FXML
    protected VBox vBox;
    @FXML
    protected TableView<Referral> table;
    @FXML
    protected TableColumn<Referral, LocalDate> fulDateCol;
    @FXML
    protected TableColumn<Referral, String> interestCol;
    @FXML
    protected TableColumn<Referral, String> tagsCol;
    @FXML
    protected TableColumn<Referral, String> notesCol;
    @FXML
    protected TableColumn<Referral, String> feedbackCol;
    @FXML
    protected TableColumn<Referral, String> codeCol;
    @FXML
    protected TableColumn<Referral, LocalDate> dateCol;
    @FXML
    protected TableColumn<Referral, String> doctorCol;
    @FXML
    protected TableColumn<Referral, String> patientCol;

    protected ObservableList<Referral> referrals = FXCollections.observableArrayList();
    protected FilteredList<Referral> filteredReferrals = new FilteredList<>(referrals, b -> true);
    Session session;
    protected PauseTransition searchDebounce;
    Query currQuery;
    Query findUsersReferrals;
    Query allReferrals;
    Query nursesReferrals;
    Query createdReferrals;
    Query findTargetUsersReferrals;

    User.Role currUserRole;
    private Patient targetPatient;

    private enum filterMode {OWN, NURSES, CREATED, ALL}

    private static final EnumMap<filterMode, String> filterModeToString = new EnumMap<>(filterMode.class);

    /**
     * Sets query currently used for displaying {@link Referral}s according to given
     * {@link pl.edu.ur.pz.clinicapp.models.User.Role}.
     *
     * @param role {@link pl.edu.ur.pz.clinicapp.models.User.Role} of current user.
     */
    public void setCurrQuery(User.Role role) {
        if (currQuery == null) {
            if (role == User.Role.NURSE) {
                currQuery = nursesReferrals;
            } else if (role == User.Role.PATIENT) {
                currQuery = findUsersReferrals;
            } else if (role == User.Role.DOCTOR) {
                currQuery = createdReferrals;
            } else {
                currQuery = allReferrals;
            }
        }
    }

    /**
     * Sets available combobox options for filtering {@link Referral}s according to given
     * {@link pl.edu.ur.pz.clinicapp.models.User.Role}.
     *
     * @param role {@link pl.edu.ur.pz.clinicapp.models.User.Role} of current user.
     */
    public void setFilterVals(User.Role role) {
        if (role == User.Role.NURSE || role == User.Role.PATIENT || targetPatient != null) {
            filter.setVisible(false);
//        } else if (role == User.Role.DOCTOR) {
//            filter.setItems(FXCollections.observableArrayList(
//                    filterModeToString.get(filterMode.CREATED),
//                    filterModeToString.get(filterMode.OWN)
//            ));
        } else {
            filter.setItems(FXCollections.observableArrayList(
                    filterModeToString.get(filterMode.ALL),
                    filterModeToString.get(filterMode.CREATED),
                    filterModeToString.get(filterMode.OWN),
                    filterModeToString.get(filterMode.NURSES)

            ));
            filter.setVisible(true);
        }
    }

    /**
     * Populates the view for given context. <br>
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {
        if (currQuery == findTargetUsersReferrals) currQuery = null;
        targetPatient = ((context.length > 0) ? ((Patient) context[0]) : null);
        buttonBox.getChildren().clear();
        if (targetPatient != null) {
            buttonBox.getChildren().add(detailsButton);
            if (!(currUserRole == User.Role.RECEPTION)) buttonBox.getChildren().add(addButton);
            if (!vBox.getChildren().contains(backBox)) vBox.getChildren().add(0, backBox);
            backText.setText("< Powrót do pacjenta " + targetPatient.getDisplayName());
        } else {
            buttonBox.getChildren().add(detailsButton);
            vBox.getChildren().remove(backBox);
        }

        session = ClinicApplication.getEntityManager().unwrap(Session.class);
        findUsersReferrals = session.getNamedQuery("findUsersReferrals").setParameter("patient",
                ClinicApplication.requireUser().asPatient());
        findTargetUsersReferrals = session.getNamedQuery("findUsersReferrals").setParameter("patient",
                targetPatient);
        allReferrals = session.getNamedQuery("allReferrals");
        nursesReferrals = session.getNamedQuery("nursesReferrals");
        createdReferrals = session.getNamedQuery("createdReferrals").setParameter("user",
                ClinicApplication.requireUser());

        table.getSelectionModel().clearSelection();

        dateCol.setCellValueFactory(new PropertyValueFactory<>("addedDateFormatted"));
        fulDateCol.setCellValueFactory(new PropertyValueFactory<>("fulfilmentDateFormatted"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("pointOfInterest"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        feedbackCol.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("StringTags"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("governmentId"));

        table.getSelectionModel().selectedItemProperty().addListener(observable -> {
            detailsButton.setDisable(table.getSelectionModel().getSelectedItem() == null);
        });

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

        filterModeToString.put(filterMode.OWN, "Moje skierowania");
        filterModeToString.put(filterMode.NURSES, "Skierowania do gabinetu zabiegowego");
        filterModeToString.put(filterMode.CREATED, "Utworzone przeze mnie");
        filterModeToString.put(filterMode.ALL, "Wszystkie");

        User.Role currUserRole = ClinicApplication.requireUser().getRole();
        if (targetPatient != null) {
            currQuery = findTargetUsersReferrals;
        } else setCurrQuery(currUserRole);
        setFilterVals(currUserRole);

        if (currUserRole == User.Role.PATIENT) {
            table.getColumns().remove(patientCol);
        }

        if (currUserRole == User.Role.NURSE) {
            table.getColumns().remove(interestCol);
        }

        if (currQuery == allReferrals) filter.setValue(filterModeToString.get(filterMode.ALL));
        else if (currQuery == createdReferrals) filter.setValue(filterModeToString.get(filterMode.CREATED));
        else if (currQuery == findUsersReferrals) filter.setValue(filterModeToString.get(filterMode.OWN));
        else if (currQuery == nursesReferrals) filter.setValue(filterModeToString.get(filterMode.NURSES));

        refresh();
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        List results = currQuery.getResultList();

        //in case some element gets edited while app is running
        for (Object refElem : results) {
            ClinicApplication.getEntityManager().refresh(refElem);
        }
        referrals.setAll(currQuery.getResultList());
        table.setItems(referrals);

        // if search field is not empty, perform search again - for user's convenience (no need to hit enter/type again)
        if (searchTextField.getText() != null && !searchTextField.getText().trim().equals("")) {
            searchAction(new ActionEvent());
        }
    }

    /**
     * Changes items in table according to selected filter mode.
     */

    public void changeFilter() {
        if (filter.getSelectionModel().getSelectedItem() == null || targetPatient != null) return;
        if (filter.getSelectionModel().getSelectedItem() == filterModeToString.get(filterMode.ALL))
            currQuery = allReferrals;
        else if (filter.getSelectionModel().getSelectedItem() == filterModeToString.get(filterMode.CREATED))
            currQuery = createdReferrals;
        else if (filter.getSelectionModel().getSelectedItem() == filterModeToString.get(filterMode.OWN))
            currQuery = findUsersReferrals;
        else currQuery = nursesReferrals;

        refresh();
    }

    /**
     * Filters table rows according to text typed in the search field.
     *
     * @param event Performed action.
     */
    public void searchAction(ActionEvent event) {
        searchDebounce.stop();
        table.getSelectionModel().clearSelection();
        final var text = searchTextField.getText().toLowerCase();
        filteredReferrals.setPredicate(referral -> {
            if (text.isBlank()) return true;
            if (referral.getAddedDate().toString().contains(text.trim())) return true;
            if (referral.getFulfilmentDate() != null && referral.getFulfilmentDate().toString().contains(text.trim()))
                return true;
            if (referral.getPointOfInterest() != null && referral.getPointOfInterest().toLowerCase().contains(text.trim()))
                return true;
            if (referral.getDoctorName().toLowerCase().contains(text.trim())) return true;
            if (referral.getNotes().toLowerCase().contains(text.trim())) return true;
            if (referral.getFeedback() != null && referral.getFeedback().toLowerCase().contains(text.trim()))
                return true;
            if (referral.getStringTags().toLowerCase().contains(text.trim())) return true;
            return referral.getGovernmentId() != null && referral.getGovernmentId().contains(text.trim());
        });

        SortedList<Referral> sortedReferrals = new SortedList<>(filteredReferrals);
        sortedReferrals.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedReferrals);
    }

    /**
     * Opens details view of the chosen referral in DETAILS mode.
     */
    public void displayDetails() {
        this.getParentController().goToView(MainWindowController.Views.REFERRAL_DETAILS,
                ReferralDetailsView.RefMode.DETAILS, table.getSelectionModel().getSelectedItem(), targetPatient);
    }

    /**
     * Opens details view in CREATE mode.
     */
    public void addReferral() {
        this.getParentController().goToView(MainWindowController.Views.REFERRAL_DETAILS,
                ReferralDetailsView.RefMode.CREATE, targetPatient);
    }

    public void onBackClick() {
        this.getParentController().goToView(MainWindowController.Views.PATIENT_DETAILS,
                PatientDetailsView.RefMode.DETAILS, targetPatient);
    }
}
