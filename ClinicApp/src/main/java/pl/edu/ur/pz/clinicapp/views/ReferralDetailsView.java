package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Optional;

public class ReferralDetailsView extends ChildControllerBase<MainWindowController> {

    private Referral ref;
    private static BooleanProperty editState = new SimpleBooleanProperty(false);
    @FXML
    protected HBox buttonBox;
    @FXML
    protected TextField dateTimeField;
    @FXML
    protected TextField fulDateTimeField;
    @FXML
    protected Text backText;
    @FXML
    protected TextField doctorField;
    @FXML
    protected DatePicker fulDatePicker;
    @FXML
    protected DatePicker datePicker;
    @FXML
    protected TextField interestField;
    @FXML
    protected TextArea notesArea;
    @FXML
    protected TextArea feedbackArea;
    @FXML
    protected TextField codeField;
    @FXML
    protected TextField tagsField;
    @FXML
    protected Button editButton;
    @FXML
    protected Button deleteButton;
    @FXML
    protected Button IKPButton;
    @FXML
    protected VBox vBox;

    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editReferral");
    Query deleteQuery = session.getNamedQuery("deleteReferral");

    public static boolean getEditState(){
        return editState.getValue();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public static Boolean exitConfirm (){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    public void onBackClick() {
        if(editState.getValue()){
            if (exitConfirm()){
                editState.setValue(!editState.getValue());
                this.getParentController().goBack();
            }
        }else{
            this.getParentController().goBack();
        }
    }

    @Override
    public void populate(Object... context) {
        ref = (Referral) context[0];
        User.Role role = ClinicApplication.getUser().getRole();

//        TEST ONLY
//
//        if(role != User.Role.ADMIN && ref.getAddedBy() != ClinicApplication.getUser()){
//            buttonBox.getChildren().remove(editButton);
//            buttonBox.getChildren().remove(deleteButton);
//        }

        fulDatePicker.setStyle("-fx-opacity: 1.0;");
        fulDatePicker.getEditor().setStyle("-fx-opacity: 1.0;");
        datePicker.setStyle("-fx-opacity: 1.0;");
        datePicker.getEditor().setStyle("-fx-opacity: 1.0;");
        refresh();

        editState.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                if (after) {
                    editButton.setText("Zapisz");

                    doctorField.setEditable(true);
                    fulDatePicker.setEditable(true);
                    fulDatePicker.setDisable(false);
                    fulDateTimeField.setEditable(true);
                    datePicker.setEditable(true);
                    datePicker.setDisable(false);
                    dateTimeField.setEditable(true);
                    interestField.setEditable(true);
                    notesArea.setEditable(true);
                    feedbackArea.setEditable(true);
                    codeField.setEditable(true);
                    tagsField.setEditable(true);
                } else {
                    editButton.setText("Edytuj");
                    doctorField.setEditable(false);
                    fulDatePicker.setEditable(false);
                    fulDatePicker.setDisable(true);
                    fulDateTimeField.setEditable(false);
                    datePicker.setEditable(false);
                    datePicker.setDisable(true);
                    dateTimeField.setEditable(false);
                    interestField.setEditable(false);
                    notesArea.setEditable(false);
                    feedbackArea.setEditable(false);
                    codeField.setEditable(false);
                    tagsField.setEditable(false);
                }
            }
        });
    }

    @Override
    public void refresh() {
        doctorField.setText(ref.getDoctorName());
        fulDatePicker.setValue((ref.getFulfilmentDate() == null)
                ? null
                : ref.getFulfilmentDate().toLocalDateTime().toLocalDate());
        fulDateTimeField.setText((ref.getFulfilmentDate() == null)
                ? null
                : ref.getFulfilmentDate().toLocalDateTime().toLocalTime().toString());
        datePicker.setValue((ref.getAddedDate() == null)
                ? null
                : ref.getAddedDate().toLocalDateTime().toLocalDate());
        dateTimeField.setText((ref.getAddedDate() == null)
                ? null
                : ref.getAddedDate().toLocalDateTime().toLocalTime().toString());
        interestField.setText(ref.getPointOfInterest());
        notesArea.setText(ref.getNotes());
        feedbackArea.setText(ref.getFeedback());
        codeField.setText(ref.getGovernmentId());
        tagsField.setText(ref.getStringTags());
    }

    public void editSave() {
        try {
            if (editState.getValue()) {
                String newAddedDate = datePicker.getValue().toString().trim() + " " + dateTimeField.getText().trim();
                editQuery.setParameter("addedDate", Timestamp.valueOf((dateTimeField.getText().length() != 8) ? newAddedDate + ":00" : newAddedDate));
                String newFulDate = fulDatePicker.getValue().toString().trim() + " " + fulDateTimeField.getText().trim();
                editQuery.setParameter("fulfilmentDate", Timestamp.valueOf((fulDateTimeField.getText().length() != 8) ? newFulDate + ":00" : newFulDate));
                editQuery.setParameter("pointOfInterest", interestField.getText().trim());
                editQuery.setParameter("notes", notesArea.getText().trim());
                editQuery.setParameter("feedback", feedbackArea.getText().trim());
                editQuery.setParameter("tags", tagsField.getText().trim());
                editQuery.setParameter("governmentId", codeField.getText().trim());
                editQuery.setParameter("refId", ref.getId());

                Transaction transaction = session.beginTransaction();
                editQuery.executeUpdate();
                transaction.commit();
                ClinicApplication.getEntityManager().refresh(ref);
            }
            editState.setValue(!editState.getValue());
        }catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setHeaderText("Niepoprawny format godziny.");
            alert.setContentText("Poprawne formaty: gg:mm lub gg:mm:ss");
            alert.showAndWait();
        }
    }

    public void delete() {
        Alert alert  = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie skierowania");
        alert.setHeaderText("Czy na pewno chcesz usunąć to skierowanie?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            deleteQuery.setParameter("refId", ref.getId());
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();
            this.getParentController().goBack();
        } else {
            alert.close();
        }
    }

    public void sendToIKP() {
        try {
            Desktop desktop = Desktop.getDesktop();
            URI ikp = new URI("https://pacjent.gov.pl/");
            desktop.browse(ikp);
        } catch (URISyntaxException | IOException e) {
            System.err.println("Wystąpił problem z otwarciem witryny IKP.");
        }
    }
}
