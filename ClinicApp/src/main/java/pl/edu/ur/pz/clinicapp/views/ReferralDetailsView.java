package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ReferralDetailsView extends ChildControllerBase<MainWindowController> {

    @FXML
    protected Text backText;
    @FXML
    protected TextField doctorField;
    @FXML
    protected TextField fulDateField;
    @FXML
    protected TextField dateField;
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
    protected Button IKPButton;
    @FXML
    protected VBox vBox;
    Referral ref;

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        ref = (Referral)context[0];
        refresh();
    }

    @Override
    public void refresh() {
        doctorField.setText(ref.getDoctorName());
        fulDateField.setText(ref.getFulfilmentDate().toString());
        dateField.setText(ref.getAddedDate().toString());
        interestField.setText(ref.getPointOfInterest());
        notesArea.setText(ref.getNotes());
        feedbackArea.setText(ref.getFeedback());
        codeField.setText(ref.getGovernmentId());
        tagsField.setText(ref.getStringTags());
    }

    public void onBackClick() {
        this.getParentController().goBack();
    }

    public void editSave() {
    }

    public void sendToIKP() {
        try{
            Desktop desktop = Desktop.getDesktop();
            URI ikp = new URI("https://pacjent.gov.pl/");
            desktop.browse(ikp);
        }catch(URISyntaxException | IOException e){
            System.err.println("Wystąpił problem z otwarciem witryny IKP.");
        }
    }
}
