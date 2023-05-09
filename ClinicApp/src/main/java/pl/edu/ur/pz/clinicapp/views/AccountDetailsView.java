package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountDetailsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML protected VBox vBox;
    @FXML protected TextField name;
    @FXML protected TextField surname;
    @FXML protected TextField pesel;
    @FXML protected TextField address;
    @FXML protected TextField post;
    @FXML protected TextField phone;
    @FXML protected TextField email;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void populate(Object... context) {
        final var user = ClinicApplication.getUser();
        name.setText(user.getName());
        surname.setText(user.getSurname());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());

        final var patient = user.asPatient();
        if (patient != null) {
            // TODO: make sure patient-only fields are shown
            pesel.setText(patient.getPESEL());
            address.setText(patient.getAddressDisplayShort());
            post.setText(patient.getPostCity() + " " + patient.getPostCode());
        }
        else {
            // TODO: hide patient-only fields/section
            // TODO: clear the fields just in case
        }
    }

    @Override
    public void refresh() {
        // Note: for now empty, but should update the view in case data was updated in the database.
    }
}
