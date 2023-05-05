package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Optional;


/**
 * Available window modes (details of existing referral or creation of a new one).
 */


public class PrescriptionDetailsView extends ChildControllerBase<MainWindowController> {

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);
    @FXML
    protected HBox buttonBox;
    @FXML
    protected TextField patientTextField;
    @FXML
    protected TextField doctorTextField;
    @FXML
    protected TextArea notesTextField;
    @FXML
    protected TextField tagsTextField;
    @FXML
    protected TextField codeTextField;
    @FXML
    protected Button editButton;
    @FXML
    protected Button ikpButton;
    @FXML
    protected Button deleteButton;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editPrescription");
    Query deleteQuery = session.getNamedQuery("deletePrescription");
    /**
     * Current view mode.
     */
    private PrMode currMode;
    private Prescription prescription;
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
        PrescriptionDetailsView.editState.set(editState);
    }

    /**
     * Displays alert about unsaved changes and returns whether user wants to discard them or not.
     */
    public static Boolean exitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Checks if window is in edit state and accordingly displays alert and/or changes view to previous one.
     */
    public void onBackClick() {
        if (editState.getValue()) {
            if (exitConfirm()) {
                editState.setValue(!editState.getValue());
                this.getParentController().goBack();
            }
        } else {
            this.getParentController().goBack();
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
                    editButton.setText("Zapisz");
                    notesTextField.setEditable(true);
                    codeTextField.setEditable(true);
                    tagsTextField.setEditable(true);
                } else {
                    editButton.setText("Edytuj");
                    notesTextField.setEditable(false);
                    codeTextField.setEditable(false);
                    tagsTextField.setEditable(false);
                }
            }
        });
        User.Role role = ClinicApplication.getUser().getRole();
        currMode = (PrMode) context[0];

        if (currMode == PrMode.DETAILS) {
            prescription = (Prescription) context[1];

            // in case the referral was edited while app is running
            ClinicApplication.getEntityManager().refresh(prescription);

            if (role != User.Role.ADMIN && prescription.getAddedBy() != ClinicApplication.getUser()) {
                buttonBox.getChildren().remove(editButton);
                buttonBox.getChildren().remove(deleteButton);
            } else {
                buttonBox.getChildren().remove(ikpButton);
                if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);
                if (!buttonBox.getChildren().contains(deleteButton)) buttonBox.getChildren().add(deleteButton);
                buttonBox.getChildren().add(ikpButton);
            }
            refresh();
        } else {
            buttonBox.getChildren().remove(deleteButton);
            buttonBox.getChildren().remove(ikpButton);
            if (!buttonBox.getChildren().contains(editButton)) buttonBox.getChildren().add(editButton);
            buttonBox.getChildren().add(ikpButton);

            doctorTextField.setText(ClinicApplication.getUser().getDisplayName());
            notesTextField.setText(null);
            codeTextField.setText(null);
            tagsTextField.setText(null);
            targetPatient = (Patient) context[1];
            editState.setValue(true);

            patientTextField.setText(targetPatient.getDisplayName());
        }
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {
        doctorTextField.setText(prescription.getDoctorName());
        notesTextField.setText(prescription.getNotes());
//        tagsTextField.setText(prescription.getStringTags());
        codeTextField.setText(prescription.getGovernmentId());
        patientTextField.setText(prescription.getPatientName());
    }

    @FXML
    public void editSave() {
        Transaction transaction;
        try {
            if (currMode == PrMode.DETAILS) {
                if (editState.getValue()) {
                    if (notesTextField.getText().trim().equals("") || tagsTextField.getText() == null ||
                            tagsTextField.getText().trim().equals("")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd zapisu");
                        alert.setHeaderText("Nie wypełniono wymaganych pól");
                        alert.setContentText("Wszytkie pola są wymagane.");
                        alert.showAndWait();
                        editState.setValue(!editState.getValue());
                    } else {
                        editQuery.setParameter("notes", (notesTextField.getText() == null)
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : notesTextField.getText().trim());
                        editQuery.setParameter("tags", (tagsTextField.getText() == null)
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : tagsTextField.getText().trim());
                        editQuery.setParameter("governmentID", (codeTextField.getText() == null)
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : codeTextField.getText().trim());
                        editQuery.setParameter("prId", prescription.getId());
                        transaction = session.beginTransaction();
                        editQuery.executeUpdate();
                        transaction.commit();
                        ClinicApplication.getEntityManager().refresh(prescription);
                    }
                }
            } else {
                if (notesTextField.getText().trim().equals("")
                        || tagsTextField.getText() == null || tagsTextField.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd zapisu");
                    alert.setHeaderText("Nie wypełniono wymaganych pól");
                    alert.setContentText("Wszystkie pola są wymagane");
                    alert.showAndWait();
                    editState.setValue(!editState.getValue());
                } else {
                    transaction = session.beginTransaction();
                    Prescription newPr = new Prescription();
                    newPr.setAddedBy(ClinicApplication.getUser());
                    newPr.setNotes((notesTextField.getText() == null)
                            ? null : notesTextField.getText().trim());
//                    newPr.setStringTags((tagsTextField.getText() == null)
//                            ? null : tagsTextField.getText().trim());
                    newPr.setGovernmentId((codeTextField.getText() == null)
                            ? null : codeTextField.getText().trim());
                    newPr.setPatient(targetPatient);
                    newPr.setAddedDate(new Timestamp(System.currentTimeMillis()));
                    session.persist(newPr);
                    transaction.commit();
                    editState.setValue(!editState.getValue());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Dodawanie recepty");
                    alert.setHeaderText("Pomyślnie dodano receptę");
                    alert.setContentText("Kod recepty: " + newPr.getGovernmentId());
                    alert.showAndWait();
                    this.getParentController().goBack();
                    return;
                }
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    /**
     * Deletes selected prescription.
     */
    @FXML
    public void deletePrescription() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie recepty");
        alert.setHeaderText("Czy na pewno chcesz usunąć receptę?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteQuery.setParameter("id", prescription.getId());
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();
            this.getParentController().goBack();
        } else {
            alert.close();
        }
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

    public enum PrMode {DETAILS, CREATE}
}
