package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Notification;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyAccount extends ChildControllerBase<MainWindowController> implements Initializable {

    @FXML protected VBox vBox;
    @FXML protected TextField name;
    @FXML protected TextField surname;
    @FXML protected TextField pesel;
    @FXML protected TextField address;
    @FXML protected TextField post;
    @FXML protected TextField phone;
    @FXML protected TextField email;
    @FXML protected Button editButton;
    @FXML protected Button moveToReferral;
    @FXML protected Button moveToPrescription;
    @FXML protected Button moveToVisits;

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);

    Patient currPat = Patient.getCurrent();
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);

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
        MyAccount.editState.set(editState);
    }

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
    }

    @Override
    public void refresh() {
        populate();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        name.setEditable(false);
        surname.setEditable(false);
        pesel.setEditable(false);
        phone.setEditable(false);
        email.setEditable(false);
        address.setEditable(false);
        post.setEditable(false);

        editState.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {

                if (after) {
                    editButton.setText("Zapisz");
                    name.setEditable(true);
                    surname.setEditable(true);
                    pesel.setEditable(true);
                    phone.setEditable(true);
                    email.setEditable(true);
                    address.setEditable(true);
                    post.setEditable(true);
                } else {
                    editButton.setText("Edytuj");
                    name.setEditable(false);
                    surname.setEditable(false);
                    pesel.setEditable(false);
                    phone.setEditable(false);
                    email.setEditable(false);
                    address.setEditable(false);
                    post.setEditable(false);

                }
            }
        });

        name.setText(currPat.getName());
        surname.setText(currPat.getSurname());
        pesel.setText(currPat.getPESEL());
        phone.setText(currPat.getPhone());
        email.setText(currPat.getEmail());
        address.setText(currPat.getAddressDisplayShort());
        post.setText(currPat.getPostCity() + " " + currPat.getPostCode());

    }

    public void editSave(){

        if(getEditState()){

            Transaction transaction;
            int id = currPat.getId();
            Patient patient = session.get(Patient.class, id);

            if (name.getText().trim().equals("") || surname.getText().trim().equals("") || pesel.getText().trim().equals("")
                || phone.getText().trim().equals("") || email.getText().trim().equals("")){

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd zapisu");
                alert.setHeaderText("Nie wypełniono wymaganych pól");
                alert.setContentText("Pola daty wystawienia, notatek i tagów są wymagane.");
                alert.showAndWait();


            }else {
                patient.setName(name.getText());
                patient.setSurname(surname.getText());
                patient.setPESEL(pesel.getText());
                patient.setPhone(phone.getText());
                patient.setEmail(email.getText());
                session.update(patient);
                transaction = session.beginTransaction();
                transaction.commit();
            }

        }

        setEditState(!getEditState());
        refresh();
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


}
