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


    Patient currPat = Patient.getCurrent();


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

        name.setText(currPat.getName());
        surname.setText(currPat.getSurname());
        pesel.setText(currPat.getPESEL());
        phone.setText(currPat.getPhone());
        email.setText(currPat.getEmail());
        address.setText(currPat.getAddressDisplayShort());
        post.setText(currPat.getPostCity() + " " + currPat.getPostCode());


    }
}
