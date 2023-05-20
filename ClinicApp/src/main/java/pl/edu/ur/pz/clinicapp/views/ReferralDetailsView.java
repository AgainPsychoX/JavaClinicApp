package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
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

    /**
     * Available window modes (details of existing referral or creation of a new one).
     */
    public enum RefMode {DETAILS, CREATE};

    /**
     * Current view mode.
     */
    private RefMode currMode;
    @FXML
    protected CheckBox nursesCheck;
    @FXML
    protected HBox buttonBox;
    @FXML
    protected HBox interestBox;
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
    @FXML
    protected Text patientField;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editReferral");
    Query deleteQuery = session.getNamedQuery("deleteReferral");
    private Referral ref;

    private static BooleanProperty editState = new SimpleBooleanProperty(false);
    private Patient targetPatient;

    /**
     * Get current edit state (fields editable or non-editable).
     */
    public static boolean getEditState() {
        return editState.getValue();
    }

    /**
     * Set current edit state (fields editable or non-editable).
     */
    public static void setEditState(boolean editState) {
        ReferralDetailsView.editState.set(editState);
    }

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Displays alert about unsaved changes and returns whether user wants to discard them or not.
     */
    public static Boolean exitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Czy na pewno chcesz opuścić ten widok? Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    /**
     * Checks if window is in edit state and accordingly displays alert and/or changes view to previous one.
     */
    public void onBackClick() {
        if (editState.getValue()) {
            if (exitConfirm()) {
                editState.setValue(!editState.getValue());
                this.getParentController().goToViewRaw(MainWindowController.Views.REFERRALS);
            }
        } else {
            this.getParentController().goToViewRaw(MainWindowController.Views.REFERRALS);
        }
    }

    /**
     * Adds listener to the editState which accordingly sets fields to editable or non-editable.
     * Checks current window mode and user's identity and accordingly removes forbidden activities (edit and deletion
     * for non-creators of the referral or deletion if mode is set to CREATE).
     * Sets editState to true if mode is set to CREATE.
     */
    @Override
    public void populate(Object... context) {
        editState.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                if (after) {
                    if(ClinicApplication.getUser().getRole() == User.Role.NURSE){
                        editButton.setText("Zapisz");
                        fulDatePicker.setEditable(true);
                        fulDatePicker.setDisable(false);
                        fulDateTimeField.setEditable(true);
                        datePicker.setEditable(false);
                        datePicker.setDisable(true);
                        dateTimeField.setEditable(false);
                        interestField.setEditable(false);
                        notesArea.setEditable(false);
                        feedbackArea.setEditable(true);
                        codeField.setEditable(false);
                        tagsField.setEditable(false);
                        nursesCheck.setDisable(true);
                    }else {
                        editButton.setText("Zapisz");
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
                        nursesCheck.setDisable(false);
                    }
                } else {
                    editButton.setText("Edytuj");
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
                    nursesCheck.setDisable(true);
                }
            }
        });

        User.Role role = ClinicApplication.getUser().getRole();
        currMode = (RefMode) context[0];

        fulDatePicker.setStyle("-fx-opacity: 1.0;");
        fulDatePicker.getEditor().setStyle("-fx-opacity: 1.0;");
        datePicker.setStyle("-fx-opacity: 1.0;");
        datePicker.getEditor().setStyle("-fx-opacity: 1.0;");

        if (currMode == RefMode.DETAILS) {
            ref = (Referral) context[1];

            // in case the referral was edited while app is running
            ClinicApplication.getEntityManager().refresh(ref);

            if (role != User.Role.ADMIN && role != User.Role.NURSE && ref.getAddedBy() != ClinicApplication.getUser()) {
                buttonBox.getChildren().remove(editButton);
                buttonBox.getChildren().remove(deleteButton);
                interestBox.getChildren().remove(nursesCheck);
                patientField.setText(null);
            } else {
                buttonBox.getChildren().remove(IKPButton);
                if(!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);
                if(!buttonBox.getChildren().contains(deleteButton)) buttonBox.getChildren().add(deleteButton);
                if(!buttonBox.getChildren().contains(deleteButton)) buttonBox.getChildren().add(deleteButton);
                if(!interestBox.getChildren().contains(nursesCheck)) interestBox.getChildren().add(nursesCheck);
                buttonBox.getChildren().add(IKPButton);
                patientField.setText("Pacjent: " + ref.getPatient().getDisplayName());
            }
            refresh();
        } else {

            buttonBox.getChildren().remove(deleteButton);
            buttonBox.getChildren().remove(IKPButton);
            if(!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);
            buttonBox.getChildren().add(IKPButton);
            if(!interestBox.getChildren().contains(nursesCheck)) interestBox.getChildren().add(nursesCheck);

            doctorField.setText(ClinicApplication.getUser().getDisplayName());
            fulDatePicker.setValue(null);
            fulDateTimeField.setText(null);
            datePicker.setValue(null);
            dateTimeField.setText(null);
            interestField.setText(null);
            notesArea.setText(null);
            feedbackArea.setText(null);
            codeField.setText(null);
            tagsField.setText(null);
            targetPatient = (Patient) context[1];
            editState.setValue(true);

            patientField.setText("Pacjent: " + targetPatient.getDisplayName());
        }

        nursesCheck.setSelected(interestField.getText() != null && interestField.getText().equals(Referral.forNurses));
    }

    /**
     * Sets values of table cells.
     */
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
        nursesCheck.setSelected(interestField.getText() != null && interestField.getText().equals(Referral.forNurses));
        notesArea.setText(ref.getNotes());
        feedbackArea.setText(ref.getFeedback());
        codeField.setText(ref.getGovernmentId());
        tagsField.setText(ref.getStringTags());
    }

    /**
     * According to current edit state sets fields editable or saves entered data (edits chosen referral or creates
     * a new one).
     */
    public void editSave() {
        Transaction transaction;

        Alert exit = new Alert(Alert.AlertType.INFORMATION);

        try {
            String dateVal = (datePicker.getValue() == null) ? null : datePicker.getValue().toString();
            String dateTimeVal = (dateTimeField.getText() == null) ? "00:00:00" : dateTimeField.getText().trim();
            String fulDateVal = (fulDatePicker.getValue() == null) ? null : fulDatePicker.getValue().toString();
            String fulDateTimeVal = (fulDateTimeField.getText() == null) ? "00:00:00" : fulDateTimeField.getText().trim();
            if (currMode == RefMode.DETAILS) {
                if (editState.getValue()) {
                    if (dateVal == null || notesArea.getText() == null || notesArea.getText().trim().equals("")
                            || tagsField.getText() == null ||tagsField.getText().trim().equals("")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd zapisu");
                        alert.setHeaderText("Nie wypełniono wymaganych pól");
                        alert.setContentText("Pola daty wystawienia, notatek i tagów są wymagane.");
                        alert.showAndWait();
                        editState.setValue(!editState.getValue());
                    } else {
                        String newAddedDate = dateVal + " " + dateTimeVal;
                        String newFulDate = fulDateVal + " " + fulDateTimeVal;
                        editQuery.setParameter("addedDate", Timestamp.valueOf((dateTimeVal.length() != 8)
                                ? newAddedDate + ":00" : newAddedDate));
                        System.out.println("."+newFulDate+".");
                        if (newFulDate.isBlank()) {
                            editQuery.setParameter("fulfilmentDate",
                                    new TypedParameterValue(StandardBasicTypes.CALENDAR_DATE, null));
                        } else {
                            editQuery.setParameter("fulfilmentDate", Timestamp.valueOf((fulDateTimeVal.length() != 8)
                                    ? newFulDate + ":00" : newFulDate));
                        }
                        editQuery.setParameter("pointOfInterest", (interestField.getText() == null
                                || interestField.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : interestField.getText().trim());
                        editQuery.setParameter("notes", (notesArea.getText() == null
                                || notesArea.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : notesArea.getText().trim());
                        editQuery.setParameter("feedback", (feedbackArea.getText() == null
                                || feedbackArea.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : feedbackArea.getText().trim());
                        editQuery.setParameter("tags", (tagsField.getText() == null
                                || tagsField.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : tagsField.getText().trim());
                        editQuery.setParameter("governmentId", (codeField.getText() == null
                                || codeField.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : codeField.getText().trim());
                        editQuery.setParameter("refId", ref.getId());

                        transaction = session.beginTransaction();
                        editQuery.executeUpdate();
                        transaction.commit();
                        ClinicApplication.getEntityManager().refresh(ref);

                        exit.setTitle("Edycja skierowania");
                        exit.setHeaderText("Edycja zakończona pomyślnie");
                        exit.setContentText("Zedytowano wybrane skierowanie.");
                        exit.showAndWait();
                    }
                }
            } else {
                if (dateVal == null || notesArea.getText() == null || notesArea.getText().trim().equals("")
                        || tagsField.getText() == null ||tagsField.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd zapisu");
                    alert.setHeaderText("Nie wypełniono wymaganych pól");
                    alert.setContentText("Pola daty wystawienia, notatek i tagów są wymagane.");
                    alert.showAndWait();
                    editState.setValue(!editState.getValue());
                } else {
                    transaction = session.beginTransaction();
                    Referral newRef = new Referral();
                    newRef.setAddedBy(ClinicApplication.getUser());
                    String newAddedDate = dateVal + " " + dateTimeVal;
                    String newFulDate = fulDateVal + " " + fulDateTimeVal;
                    newRef.setAddedDate(Timestamp.valueOf((dateTimeVal.length() != 8)
                            ? newAddedDate + ":00" : newAddedDate));
                    if (fulDateVal == null) {
                        newRef.setFulfilmentDate(null);
                    } else {
                        newRef.setFulfilmentDate(Timestamp.valueOf((fulDateTimeVal.length() != 8)
                                ? newFulDate + ":00" : newFulDate));
                    }
                    newRef.setPointOfInterest((interestField.getText() == null
                            || interestField.getText().isBlank())
                            ? null : interestField.getText().trim());
                    newRef.setNotes((notesArea.getText() == null
                            || notesArea.getText().isBlank())
                            ? null : notesArea.getText().trim());
                    newRef.setFeedback((feedbackArea.getText() == null
                            || feedbackArea.getText().isBlank())
                            ? null : feedbackArea.getText().trim());
                    newRef.setStringTags((tagsField.getText() == null
                            || tagsField.getText().isBlank())
                            ? null : tagsField.getText().trim());
                    newRef.setGovernmentId((codeField.getText() == null
                            || codeField.getText().isBlank())
                            ? null : codeField.getText().trim());
                    newRef.setPatient(targetPatient);
                    session.persist(newRef);
                    transaction.commit();
                    editState.setValue(!editState.getValue());

                    exit.setTitle("Dodawnaie skierowania");
                    exit.setHeaderText("Dodawanie zakończone pomyślnie");
                    exit.setContentText("Dodano nowe skierowanie.");
                    exit.showAndWait();

                    this.getParentController().goToViewRaw(MainWindowController.Views.REFERRALS);
                    return;
                }
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            transaction = session.getTransaction();
            if (transaction.isActive()) transaction.rollback();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setHeaderText("Niepoprawny format godziny.");
            alert.setContentText("Poprawne formaty: gg:mm lub gg:mm:ss");
            alert.showAndWait();
        }
    }

    /**
     * Deletes current referral.
     */
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie skierowania");
        alert.setHeaderText("Czy na pewno chcesz usunąć to skierowanie?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteQuery.setParameter("refId", ref.getId());
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();

            Alert exit = new Alert(Alert.AlertType.INFORMATION);
            exit.setTitle("Usuwanie skierowania");
            exit.setHeaderText("Usuwanie zakończone pomyślnie");
            exit.setContentText("Usunięto wybrane skierowanie.");
            exit.showAndWait();

            this.getParentController().goToViewRaw(MainWindowController.Views.REFERRALS);
        } else {
            alert.close();
        }
    }

    /**
     * Opens government's website for patients.
     */
    public void sendToIKP() {
        try {
            Desktop desktop = Desktop.getDesktop();
            URI ikp = new URI("https://pacjent.gov.pl/");
            desktop.browse(ikp);
        } catch (URISyntaxException | IOException e) {
            System.err.println("Wystąpił problem z otwarciem witryny IKP.");
        }
    }

    public void setForNurses(ActionEvent actionEvent) {
        if (nursesCheck.isSelected()){
            interestField.setText(Referral.forNurses);
            interestField.setEditable(false);
        }else{
            interestField.setText("");
            interestField.setEditable(true);
        }
    }
}
