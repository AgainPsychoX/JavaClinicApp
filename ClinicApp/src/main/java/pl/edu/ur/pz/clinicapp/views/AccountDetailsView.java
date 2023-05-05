package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
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
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
    }

    @Override
    public void refresh() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        name.setText(Patient.getCurrent().getName());
        surname.setText(Patient.getCurrent().getSurname());
        pesel.setText(Patient.getCurrent().getPESEL());
        phone.setText(Patient.getCurrent().getPhone());
        email.setText(Patient.getCurrent().getEmail());
        address.setText(Patient.getCurrent().getAddressDisplayShort());
        post.setText(Patient.getCurrent().getPostCity() + " " + Patient.getCurrent().getPostCode());

    }
}
