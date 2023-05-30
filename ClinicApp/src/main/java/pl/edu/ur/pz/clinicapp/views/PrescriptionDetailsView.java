package pl.edu.ur.pz.clinicapp.views;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import pl.edu.ur.pz.clinicapp.utils.DateUtils;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;


/**
 * Available window modes (details of existing referral or creation of a new one).
 */


public class PrescriptionDetailsView extends ChildControllerBase<MainWindowController> {

    /**
     * Available window modes (details of existing prescription or creation of a new one).
     */

    public enum PrMode {DETAILS, CREATE}
    /**
     * Current view mode.
     */
    private PrMode currMode;

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
    @FXML
    protected Button printButton;

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);


    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editPrescription");
    Query deleteQuery = session.getNamedQuery("deletePrescription");

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
                this.getParentController().goToViewRaw(MainWindowController.Views.PRESCRIPTIONS);
            }
        } else {
            this.getParentController().goToViewRaw(MainWindowController.Views.PRESCRIPTIONS);
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
        User.Role role = ClinicApplication.getUser().getRole();
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
            targetPatient = ((User) context[1]).asPatient();
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
        tagsTextField.setText(prescription.getTags());
        codeTextField.setText(prescription.getGovernmentId());
        patientTextField.setText(prescription.getPatientName());
//        TODO add DatePicker
    }

    @FXML
    public void editSave() {
        Transaction transaction;
        try {
            if (currMode == PrMode.DETAILS) {
                if (editState.getValue()) {
//                    TODO add DatePicker check
                    if (notesTextField.getText().trim().equals("") || notesTextField.getText() == null
                            || tagsTextField.getText() == null || tagsTextField.getText().trim().equals("")
                            || codeTextField.getText().trim().equals("") || codeTextField.getText() == null){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd zapisu");
                        alert.setHeaderText("Nie wypełniono wymaganych pól");
                        alert.setContentText("Wszystkie pola są wymagane.");
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
//                    TODO add alert for successful edit
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
                    newPr.setStringTags((tagsTextField.getText() == null)
                            ? null : tagsTextField.getText().trim());
                    newPr.setGovernmentId((codeTextField.getText() == null)
                            ? null : codeTextField.getText().trim());
                    newPr.setPatient(targetPatient);
                    newPr.setAddedDate(Instant.now());

                    session.persist(newPr);
                    transaction.commit();
                    editState.setValue(!editState.getValue());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Dodawanie recepty");
                    alert.setHeaderText("Pomyślnie dodano receptę");
                    alert.setContentText("Kod recepty: " + newPr.getGovernmentId());
                    alert.showAndWait();

                    this.getParentController().goToViewRaw(MainWindowController.Views.PRESCRIPTIONS);
                    return;
                }
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            transaction = session.getTransaction();
            if(transaction.isActive())
                transaction.rollback();
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
    protected void sendToIKP() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.pacjent.gov.pl"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void prescriptionReport() throws IOException, URISyntaxException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        ConverterProperties properties = new ConverterProperties();
        DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);

        fontProvider.addFont(String.valueOf(ClinicApplication.class.getResource("fonts/calibri.ttf")));

        properties.setFontProvider(fontProvider);
        properties.setCharset("UTF-8");

        URL templatesURL = ClinicApplication.class.getResource("templates");

        try {
            configuration.setDirectoryForTemplateLoading(new File(templatesURL.toURI()));
            configuration.setDefaultEncoding("UTF-8");
            configuration.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
            configuration.setSharedVariable("DateUtils", new DateUtils());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie recepty");
            fileChooser.setInitialFileName("prescription.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF",
                    "*.pdf"));
            File file = fileChooser.showSaveDialog(new Stage());

            Template template = configuration.getTemplate("prescriptionDetailsTemplate.ftl");
            File outputFile = new File("output.html");
            Writer writer = new FileWriter(outputFile);

            Map<String, Object> dataModel = new HashMap<>();

            dataModel.put("prescription", prescription);

            template.process(dataModel, writer);

            writer.close();

            HtmlConverter.convertToPdf(new FileInputStream("output.html"),
                    new FileOutputStream(file), properties);

            outputFile.delete();
            showAlert(Alert.AlertType.INFORMATION, "Generowanie recepty", "Utworzono receptę", "");

        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.",
                    e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }


}
