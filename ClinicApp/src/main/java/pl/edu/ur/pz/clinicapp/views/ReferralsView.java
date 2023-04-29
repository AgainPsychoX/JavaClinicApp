package pl.edu.ur.pz.clinicapp.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import java.sql.Timestamp;

public class ReferralsView extends ChildControllerBase<MainWindowController> {

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
    protected TableColumn<Referral, Timestamp> fulDateCol;
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
    protected TableColumn<Referral, Timestamp> dateCol;
    @FXML
    protected TableColumn<Referral, String> doctorCol;

    protected ObservableList<Referral> referrals = FXCollections.observableArrayList();
    protected FilteredList<Referral> filteredReferrals = new FilteredList<>(referrals, b -> true);
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query query = session.getNamedQuery("findUsersReferrals").setParameter("uname", ClinicApplication.getUser().getDatabaseUsername());

    @Override
    public void dispose() {
        super.dispose();
    }

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

        refresh();
    }

    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        referrals.setAll(query.getResultList());
        table.setItems(referrals);
    }

    public void searchAction() {

        table.getSelectionModel().clearSelection();
        var newValue = searchTextField.getText();
        filteredReferrals.setPredicate(referral -> {

            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            if (referral.getAddedDate().toString().contains(lowerCaseFilter)) return true;
            if (referral.getFulfilmentDate() != null && referral.getFulfilmentDate().toString().contains(lowerCaseFilter))
                return true;
            if (referral.getPointOfInterest().toLowerCase().contains(lowerCaseFilter)) return true;
            if (referral.getDoctorName().toLowerCase().contains(lowerCaseFilter)) return true;
            if (referral.getNotes().toLowerCase().contains(lowerCaseFilter)) return true;
            if (referral.getFeedback() != null && referral.getFeedback().toLowerCase().contains(lowerCaseFilter)) return true;
            if (referral.getStringTags().toLowerCase().contains(lowerCaseFilter)) return true;
            return referral.getGovernmentId() != null && referral.getGovernmentId().contains(lowerCaseFilter);
        });

        SortedList<Referral> sortedReferrals = new SortedList<>(filteredReferrals);
        sortedReferrals.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedReferrals);
    }

    public void displayDetails() {
        this.getParentController().goToView(MainWindowController.Views.REFERRAL_DETAILS, table.getSelectionModel().getSelectedItem());
    }
}
