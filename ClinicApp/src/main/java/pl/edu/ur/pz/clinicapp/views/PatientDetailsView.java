package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.util.Optional;


public class PatientDetailsView extends ChildControllerBase<MainWindowController> {

    public HBox CRUDBox;
    @FXML protected HBox medEntryBox;
    @FXML protected Button addVisitButton;
    @FXML protected Button addPrescriptionButton;
    @FXML protected Button addReferralButton;
    @FXML protected TextField nameField;
    @FXML protected TextField surnameField;
    @FXML protected TextField emailField;
    @FXML protected TextField phoneField;
    @FXML protected TextField PESELField;
    @FXML protected TextField cityField;
    @FXML protected TextField streetField;
    @FXML protected TextField buildingField;
    @FXML protected TextField postCodeField;
    @FXML protected TextField postCityField;
    @FXML protected Button saveButton;
    @FXML protected Button deleteButton;

    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Transaction transaction = null;

    private Patient pat;

    /**
     * Available window modes (details of existing referral or creation of a new one).
     */
    public enum RefMode {DETAILS, CREATE};

    /**
     * Current view mode.
     */
    private RefMode currMode;

    private static BooleanProperty editState = new SimpleBooleanProperty(false);

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
        PatientDetailsView.editState.set(editState);
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
                this.getParentController().goToViewRaw(MainWindowController.Views.PATIENTS);
            }
        } else {
            this.getParentController().goToViewRaw(MainWindowController.Views.PATIENTS);
        }
    }



    @Override
    public void populate(Object... context) {

        saveButton.setText("Edytuj");
        nameField.setEditable(false);
        surnameField.setEditable(false);
        PESELField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        cityField.setEditable(false);
        postCityField.setEditable(false);
        postCodeField.setEditable(false);
        streetField.setEditable(false);
        buildingField.setEditable(false);

        editState.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                if (after) {
                    saveButton.setText("Zapisz");
                    nameField.setEditable(true);
                    surnameField.setEditable(true);
                    PESELField.setEditable(true);
                    emailField.setEditable(true);
                    phoneField.setEditable(true);
                    cityField.setEditable(true);
                    postCityField.setEditable(true);
                    postCodeField.setEditable(true);
                    streetField.setEditable(true);
                    buildingField.setEditable(true);
                } else {
                    saveButton.setText("Edytuj");
                    nameField.setEditable(false);
                    surnameField.setEditable(false);
                    PESELField.setEditable(false);
                    emailField.setEditable(false);
                    phoneField.setEditable(false);
                    cityField.setEditable(false);
                    postCityField.setEditable(false);
                    postCodeField.setEditable(false);
                    streetField.setEditable(false);
                    buildingField.setEditable(false);
                }
            }
        });

        User.Role role = ClinicApplication.getUser().getRole();
        currMode = (PatientDetailsView.RefMode) context[0];

        if (currMode == PatientDetailsView.RefMode.DETAILS) {
            pat = (Patient) context[1];

            // in case the referral was edited while app is running
            ClinicApplication.getEntityManager().refresh(pat);

            if (role != User.Role.ADMIN && pat.getId() != ClinicApplication.getUser().getId()) {
                CRUDBox.getChildren().remove(saveButton);
                CRUDBox.getChildren().remove(deleteButton);
            } else {
                if (!CRUDBox.getChildren().contains(saveButton)) CRUDBox.getChildren().add(saveButton);
                if (!CRUDBox.getChildren().contains(deleteButton)) CRUDBox.getChildren().add(deleteButton);
            }
            refresh();
        } else {

            CRUDBox.getChildren().remove(deleteButton);
            if (!CRUDBox.getChildren().contains(saveButton)) CRUDBox.getChildren().add(saveButton);

            nameField.setText(null);
            surnameField.setText(null);
            PESELField.setText(null);
            emailField.setText(null);
            phoneField.setText(null);
            cityField.setText(null);
            postCityField.setText(null);
            postCodeField.setText(null);
            streetField.setText(null);
            buildingField.setText(null);
            editState.setValue(true);

        }
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {
        nameField.setText(pat.getName());
        surnameField.setText(pat.getSurname());
        PESELField.setText(pat.getPESEL());
        emailField.setText(pat.getEmail());
        phoneField.setText(pat.getPhone());
        cityField.setText(pat.getCity());
        postCityField.setText(pat.getPostCity());
        postCodeField.setText(pat.getPostCode());
        streetField.setText(pat.getStreet());
        buildingField.setText(pat.getBuilding());
    }

    @FXML public void saveAction(){

        if(getEditState()){
            int id = pat.getId();
            Patient patient = session.get(Patient.class,id);

            if (nameField.getText().trim().equals("") || surnameField.getText().trim().equals("")
                    || PESELField.getText().trim().equals("") || cityField.getText().trim().equals("")
                    || postCityField.getText().trim().equals("") || postCodeField.getText().trim().equals("")
                    || streetField.getText().trim().equals("") || buildingField.getText().trim().equals("")) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd zapisu");
                alert.setHeaderText("Nie wypełniono wymaganych pól");
                alert.setContentText("Pola daty wystawienia, notatek i tagów są wymagane.");
                alert.showAndWait();


            }else {
                patient.setName(nameField.getText());
                patient.setSurname(surnameField.getText());
                patient.setPESEL(PESELField.getText());
                patient.setPhone(phoneField.getText());
                patient.setEmail(emailField.getText());
                patient.setCity(cityField.getText());
                patient.setPostCity(postCityField.getText());
                patient.setPostCode(postCodeField.getText());
                patient.setStreet(streetField.getText());
                patient.setBuilding(buildingField.getText());
                session.update(patient);
                transaction = session.beginTransaction();
                transaction.commit();
            }

        }

        setEditState(!getEditState());
        refresh();

    }

    /**
     * Deletes current patient.
     */
    @FXML public void deleteAction(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie pacjenta.");
        alert.setHeaderText("Czy na pewno chcesz usunąć tego pacjenta?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            Patient patient = session.get(Patient.class, pat.getId());
            transaction = session.beginTransaction();
            session.delete(patient);
            transaction.commit();
            this.getParentController().goBack();
        } else {
            alert.close();
        }
    }

    public void addVisit() {
    }

    /**
     * Opens {@link PrescriptionsView}, passing current {@link Patient}
     */
    public void addPrescription() {this.getParentController().goToView(MainWindowController.Views.PRESCRIPTIONS, pat);
    }


    public void addReferral() {
        this.getParentController().goToView(MainWindowController.Views.REFERRALS, pat);
    }

}
