package pl.edu.ur.pz.clinicapp.views;

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
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.sql.Timestamp;
import java.util.List;

public class ReferralsView extends ChildControllerBase<MainWindowController> {

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
        dateCol.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        fulDateCol.setCellValueFactory(new PropertyValueFactory<>("fulfilmentDate"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("pointOfInterest"));
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        feedbackCol.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("StringTags"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("governmentId"));

        refresh();
    }

    @Override
    public void refresh() {
        referrals.setAll(query.getResultList());
        table.getItems().setAll(referrals);
    }

    public void searchAction(ActionEvent event) {

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
            if (referral.getFeedback() != null && referral.getFeedback().contains(lowerCaseFilter)) return true;
            if (referral.getStringTags().toLowerCase().contains(lowerCaseFilter)) return true;
            return referral.getGovernmentId() != null && referral.getGovernmentId().contains(lowerCaseFilter);
        });

        SortedList<Referral> sortedReferrals = new SortedList<>(filteredReferrals);

        sortedReferrals.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedReferrals);

        table.refresh();

    }
}
