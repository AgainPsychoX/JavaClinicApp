package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.hibernate.Session;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Doctor;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AccountDetailsView extends ChildControllerBase<MainWindowController> implements Initializable {

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);

    @FXML protected VBox vBox;
    @FXML protected GridPane medicalDataGridPane;
    @FXML protected TextField nameTextField;
    @FXML protected TextField surnameTextField;
    @FXML protected TextField peselTextField;
    @FXML protected TextField addressTextField;
    @FXML protected TextField postTextField;
    @FXML protected TextField phoneTextField;
    @FXML protected TextField emailTextField;


    @FXML protected HBox crudButtonBox;
    @FXML protected Button deleteButton;
    @FXML protected Button editButton;

    @FXML protected HBox moveToViewsButtonBox;
    @FXML protected Button moveToVisitsButton;
    @FXML protected Button moveToPrescriptionsButton;
    @FXML protected Button moveToReferralsButton;

    @FXML protected Text roleText;
    @FXML protected Text specialityText;
    @FXML protected ComboBox<User.Role> roleComboBox;
    @FXML protected TextField specialityTextField;


    private AccMode currMode;
    private User user;
    Patient currPat = Patient.getCurrent();
    Doctor currDoc;

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
        AccountDetailsView.editState.set(editState);
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




    @Override
    public void dispose() {
        super.dispose();
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
                    nameTextField.setEditable(true);
                    surnameTextField.setEditable(true);
                    peselTextField.setEditable(true);
                    addressTextField.setEditable(true);
                    postTextField.setEditable(true);
                    phoneTextField.setEditable(true);
                    emailTextField.setEditable(true);
                } else {
                    editButton.setText("Edytuj");
                    nameTextField.setEditable(false);
                    surnameTextField.setEditable(false);
                    peselTextField.setEditable(false);
                    addressTextField.setEditable(false);
                    postTextField.setEditable(false);
                    phoneTextField.setEditable(false);
                    emailTextField.setEditable(false);
                }
            }
        });


        currMode = (AccMode) context[0];

        User.Role role = ClinicApplication.getUser().getRole();

        //Set properties for admin operations
        if(ClinicApplication.getUser().getRole() == User.Role.ADMIN){
            roleText.setVisible(true);
            specialityText.setVisible(true);
            roleComboBox.setVisible(true);
            specialityTextField.setVisible(true);
            roleComboBox.setDisable(false);
            specialityTextField.setDisable(false);
        }

        if(currMode == AccMode.DETAILS){
            user = (User) context[1];
            currPat = Patient.getPatient(user.getDatabaseUsername());
            ClinicApplication.getEntityManager().refresh(user);

            if(user.getRole() == User.Role.DOCTOR)
                currDoc = Doctor.getDoctor(user.getDatabaseUsername());

            if(role != User.Role.ADMIN){
                crudButtonBox.getChildren().remove(deleteButton);
            }else {
                if(!crudButtonBox.getChildren().contains(deleteButton)) crudButtonBox.getChildren().add(deleteButton);
                if(!moveToViewsButtonBox.getChildren().contains(moveToReferralsButton))
                    crudButtonBox.getChildren().add(moveToReferralsButton);
                if(!moveToViewsButtonBox.getChildren().contains(moveToPrescriptionsButton))
                    crudButtonBox.getChildren().add(moveToPrescriptionsButton);
                if(!moveToViewsButtonBox.getChildren().contains(moveToVisitsButton))
                    crudButtonBox.getChildren().add(moveToVisitsButton);
                specialityTextField.setDisable(false);
            }

            refresh();

            //CREATE
            } else{
                if(!crudButtonBox.getChildren().contains(editButton)) crudButtonBox.getChildren().add(editButton);

                moveToViewsButtonBox.getChildren().removeAll();

                nameTextField.setText(null);
                surnameTextField.setText(null);
                peselTextField.setText(null);
                addressTextField.setText(null);
                postTextField.setText(null);
                phoneTextField.setText(null);
                emailTextField.setText(null);

            }

    }


    /**
     * Sets values of text fields
     */
    @Override
    public void refresh() {
        nameTextField.setText(user.getName());
        surnameTextField.setText(user.getSurname());
        phoneTextField.setText(user.getPhone());
        emailTextField.setText(user.getPhone());
        roleComboBox.setValue(user.getRole());
        addressTextField.setText(currPat.getAddressDisplayShort());
        postTextField.setText(currPat.getPostCity() + " " + currPat.getPostCode());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleComboBox.getItems().addAll(User.Role.ADMIN, User.Role.PATIENT, User.Role.DOCTOR, User.Role.NURSE,
                User.Role.RECEPTION);
        roleComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User.Role object) {
                return (object != null) ? object.toString() : "";
            }

            @Override
            public User.Role fromString(String roleName) {
                return switch (roleName) {
                    case "Pacjent" -> User.Role.PATIENT;
                    case "Lekarz" -> User.Role.DOCTOR;
                    case "Recepcja" -> User.Role.RECEPTION;
                    case "Pielęgniarka" -> User.Role.NURSE;
                    case "Administrator" -> User.Role.ADMIN;
                    default -> throw new IllegalArgumentException("Nieznana rola użytkownika: " + roleName);
                };
            }
        });
//        name.setText(currPat.getName());
//        surname.setText(currPat.getSurname());
//        pesel.setText(currPat.getPESEL());
//        phone.setText(currPat.getPhone());
//        email.setText(currPat.getEmail());
//        address.setText(currPat.getAddressDisplayShort());
//        post.setText(currPat.getPostCity() + " " + currPat.getPostCode());

    }

    @FXML
    protected void editSave(){
        try{
            Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
            session.beginTransaction();

            if(currMode == AccMode.DETAILS){
                //TODO update
            }

            //CREATE
            // TODO proper role name for insertion, create Doctor account
            else{
                if(nameTextField.getText() == null || surnameTextField.getText() == null ||
                        peselTextField.getText() == null ||  addressTextField.getText() == null ||
                        postTextField.getText() == null || phoneTextField.getText() == null ||
                        emailTextField.getText() == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd zapisu");
                    alert.setHeaderText("Nie wypełniono wymaganych pól");
                    alert.setContentText("Wszystkie pola są wymagane");
                    alert.showAndWait();
                    editState.setValue(!editState.getValue());
                }
                else {
                    String dbUname = "u" + nameTextField.getText().toLowerCase().charAt(0) +
                            surnameTextField.getText().toLowerCase().charAt(0) +
                            phoneTextField.getText().substring(0, 4);
                    String temp = roleComboBox.getSelectionModel().getSelectedItem().toString();
                    User newUser = new User(nameTextField.getText(), surnameTextField.getText(),
                            emailTextField.getText(),
                            phoneTextField.getText(), User.Role.PATIENT, dbUname);
                    session.persist(newUser);

                    Patient newPatient = new Patient("12", "Rzeszów", peselTextField.getText(),
                            "Rzeszów", "35-301", "Rejtana");
                    session.persist(newPatient);

                    //If creating doctor - add new doctor
//                    if (roleComboBox.getSelectionModel().getSelectedItem().equals("Lekarz")) {
//                        Doctor newDoctor = new Doctor();
//                        session.persist(newDoctor);
//                    }

                    session.getTransaction().commit();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Dodawanie użytkownika");
                    alert.setHeaderText("Pomyślnie dodano użytkownika");
                    alert.setContentText("ID" + newUser.getDatabaseUsername());
                    this.getParentController().goBack();
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    public enum AccMode{DETAILS, CREATE, VIEW}
}
