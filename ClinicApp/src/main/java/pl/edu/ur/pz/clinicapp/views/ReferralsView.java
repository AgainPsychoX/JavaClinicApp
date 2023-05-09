package pl.edu.ur.pz.clinicapp.views;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.time.Instant;
import java.util.List;

public class ReferralsView extends ChildControllerBase<MainWindowController> {

    @FXML protected Button addButton;
    @FXML protected Button ikpButton;
    @FXML protected Button detailsButton;
    @FXML protected TextField searchTextField;
    @FXML protected VBox vBox;
    @FXML protected TableView<Referral> table;
    @FXML protected TableColumn<Referral, Timestamp> fulDateCol;
    @FXML protected TableColumn<Referral, String> interestCol;
    @FXML protected TableColumn<Referral, String> tagsCol;
    @FXML protected TableColumn<Referral, String> notesCol;
    @FXML protected TableColumn<Referral, String> feedbackCol;
    @FXML protected TableColumn<Referral, String> codeCol;
    @FXML protected TableColumn<Referral, Timestamp> dateCol;
    @FXML protected TableColumn<Referral, String> doctorCol;

    protected ObservableList<Referral> referrals = FXCollections.observableArrayList();
    protected FilteredList<Referral> filteredReferrals = new FilteredList<>(referrals, b -> true);
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    protected PauseTransition searchDebounce;
    Query query = session.getNamedQuery("findUsersReferrals").setParameter("uname",
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

        dateCol.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        fulDateCol.setCellValueFactory(new PropertyValueFactory<>("fulfilmentDate"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("pointOfInterest"));
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

        refresh();

        // if search field is not empty, perform search again - for user's convenience (no need to hit enter/type again)
        if (searchTextField.getText() != null && !searchTextField.getText().trim().equals("")) {
            searchAction(new ActionEvent());
        }
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        List results = query.getResultList();

        //in case some element gets edited while app is running
        for (Object refElem : results) {
            ClinicApplication.getEntityManager().refresh(refElem);
        }
        referrals.setAll(query.getResultList());
        table.setItems(referrals);
    }

    /**
     * Filters table rows according to text typed in the search field.
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
                ReferralDetailsView.RefMode.DETAILS, table.getSelectionModel().getSelectedItem());
    }

    // TEST ONLY - last param should be a patient, for now it's current user
    public void addReferral() {
        this.getParentController().goToView(MainWindowController.Views.REFERRAL_DETAILS,
                ReferralDetailsView.RefMode.CREATE, ClinicApplication.getUser());
    }
}
