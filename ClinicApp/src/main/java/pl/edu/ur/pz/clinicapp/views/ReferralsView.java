package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.sql.Timestamp;
import java.util.List;

public class ReferralsView extends ChildControllerBase<MainWindowController> {

    @FXML protected VBox vBox;
    @FXML protected TableView<Referral> table;
    @FXML protected TableColumn<Referral, Timestamp> fulDateCol;
    @FXML protected TableColumn<Referral, String> interestCol;
    @FXML protected TableColumn<Referral, List<String>> tagsCol;
    @FXML protected TableColumn<Referral, String> notesCol;
    @FXML protected TableColumn<Referral, String> feedbackCol;
    @FXML protected TableColumn<Referral, String> codeCol;
    @FXML protected TableColumn<Referral, Timestamp> dateCol;
    @FXML protected TableColumn<Referral, String> doctorCol;

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
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("governmentId"));

        refresh();
    }

    @Override
    public void refresh() {
        List<Referral> referralList = query.getResultList();
        table.getItems().setAll(referralList);
    }
}
