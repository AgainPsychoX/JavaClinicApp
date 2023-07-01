package pl.edu.ur.pz.clinicapp.views;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.DateUtils;
import pl.edu.ur.pz.clinicapp.utils.ReportObject;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;


/**
 * View controller to edit, delete or display details of a {@link Prescription}.
 */
public class PrescriptionDetailsView extends ChildControllerBase<MainWindowController> implements Initializable {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Elements and initialization
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final BooleanProperty editState = new SimpleBooleanProperty(false);
    private final ReadOnlyObjectWrapper<Mode> mode = new ReadOnlyObjectWrapper<>();
    protected User user;

    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editPrescription");
    Query deleteQuery = session.getNamedQuery("deletePrescription");

    @FXML private HBox buttonBox;

    @FXML private TextField patientTextField;

    @FXML private TextArea notesTextField;
    @FXML private TextField doctorTextField;
    @FXML private TextField tagsTextField;
    @FXML private TextField govIdTextField;

    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private DatePicker addedDatePicker;

    private Prescription prescription;
    private Patient targetPatient;
    private boolean isTarget;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<Node> patientOnlyThings;

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
        return showAlert(Alert.AlertType.CONFIRMATION, "Niezapisane zmiany", "Widok w trybie edycji",
                "Wszystkie niezapisane zmiany zostaną utracone.");
    }

    /**
     * Shows alert.
     * @param type {@link javafx.scene.control.Alert.AlertType}
     * @param title Alert title
     * @param header Alert header
     * @param text Alert text
     * @return true when OK button was pressed, false otherwise
     */
    private static boolean showAlert(Alert.AlertType type, String title, String header, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Adds listener to the editState which accordingly sets fields to editable or non-editable.
     * Checks current window mode and user's identity and accordingly removes forbidden activities (edit and deletion
     * for non-creators of the referral or deletion if mode is set to CREATE).
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editState.addListener((observableValue, before, after) -> {
            if (after) {
                editButton.setText("Zapisz");
                notesTextField.setEditable(true);
                govIdTextField.setEditable(true);
                tagsTextField.setEditable(true);
            } else {
                editButton.setText("Edytuj");
                notesTextField.setEditable(false);
                govIdTextField.setEditable(false);
                tagsTextField.setEditable(false);
            }
        });
        patientOnlyThings = List.of(
                doctorTextField,
                notesTextField,
                tagsTextField,
                govIdTextField
        );
    }

    public ReadOnlyObjectProperty<Mode> modeProperty() {
        return mode;
    }

    /**
     * Gets current mode
     * @return {@link Mode}
     */
    public Mode getMode() {
        return mode.get();
    }

    /**
     * Sets whether fields are visible or editable depending on user role.
     * Sets editState to true if mode is set to CREATE.
     *
     * @param mode {@link Mode} used to specify which settings are set (view/create).
     */
    public void setMode(Mode mode) {
        this.mode.set(mode);

        final var patient = user.asPatient();
        if (patient != null) {
            for (final var node : patientOnlyThings) {
                setNodeEnabledVisibleManaged(node, true);
            }
            setNodeEnabledVisibleManaged(editButton, true);
        } else {
            for (final var node : patientOnlyThings) {
                setNodeEnabledVisibleManaged(node, false);
                if (node instanceof TextField field) {
                    field.setText("");
                } else if (node instanceof TextArea area) {
                    area.setText("");
                }
                buttonBox.getChildren().remove(editButton);
                buttonBox.getChildren().remove(deleteButton);
            }
        }

        switch (mode) {
            case DETAILS -> {
                patientTextField.setEditable(false);

                for (final var node : patientOnlyThings) {
                    if (node instanceof TextField field) {
                        field.setEditable(false);
                    } else if (node instanceof TextArea area) {
                        area.setEditable(false);
                    }
                }
            }
            case CREATE -> {
                setNodeEnabledVisibleManaged(editButton, true);

                patientTextField.setEditable(true);
                notesTextField.setEditable(true);
                tagsTextField.setEditable(true);
                govIdTextField.setEditable(true);

                editState.setValue(true);
                addedDatePicker.setValue(LocalDate.now());
                if (patient != null) {
                    for (final var node : patientOnlyThings) {
                        if (node instanceof TextField field) {
                            field.setEditable(true);
                        }
                    }
                }

            }
        }
    }

    /**
     * Sets whether node should be visible
     * @param node node to set
     * @param show true or false depending if node should be shown
     */
    private void setNodeEnabledVisibleManaged(Node node, boolean show) {
        node.setDisable(!show);
        node.setVisible(show);
        node.setManaged(show);
    }

    /**
     * Populates the view from given context.
     * <p>
     * If no arguments are given, the view will show empty values.
     *
     * <ol>
     *     <li>First argument can specify {@link Mode} /li>
     *     <li>Second argument can specify either {@link Prescription} if record will be edited or displayed or
     *     {@link Patient} if new record will be created</li>
     * </ol>
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {
        var mode = Mode.DETAILS;
        var user = ClinicApplication.getUser();

        targetPatient = null;
        prescription = null;
        isTarget = context.length > 2;

        if (context.length >= 1) {
            if (context[0] instanceof Mode m) {
                mode = m;
            } else {
                throw new IllegalArgumentException();
            }
            if (context.length >= 2) {
                if (context[1] instanceof Prescription p) {
                    prescription = p;
                    targetPatient = prescription.getPatient();
                } else if (context[1] instanceof User p) {
                    targetPatient = p.asPatient();
                } else if (context[1] instanceof Patient p){
                    targetPatient = p;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        this.user = user;

        setMode(mode);

        refresh();
    }

    /**
     * Checks if window is in edit state and accordingly displays alert and/or changes view to previous one.
     */
    public void onBackClick() {
        if (editState.getValue()) {
            if (exitConfirm()) {
                editState.setValue(!editState.getValue());
                if (isTarget) {
                    this.getParentController().goToView(MainWindowController.Views.PRESCRIPTIONS, targetPatient);
                } else this.getParentController().goToViewRaw(MainWindowController.Views.PRESCRIPTIONS);
            }
        } else {
            if (isTarget) {
                this.getParentController().goToView(MainWindowController.Views.PRESCRIPTIONS, targetPatient);
            } else this.getParentController().goToViewRaw(MainWindowController.Views.PRESCRIPTIONS);
        }
    }

    /**
     * Clears TextField list and removes buttons from box.
     */
    @Override
    public void dispose() {
        buttonBox.getChildren().removeAll();
        doctorTextField.setText(null);
        notesTextField.setText(null);
        tagsTextField.setText(null);
        govIdTextField.setText(null);
        patientTextField.setText(null);
        super.dispose();
    }

    /**
     * Sets values of text fields.
     */
    @Override
    public void refresh() {
        if (getMode() == Mode.DETAILS) {
            doctorTextField.setText(prescription.getAddedBy().getDisplayName());
            notesTextField.setText(prescription.getNotes());
            tagsTextField.setText(prescription.getStringTags());
            govIdTextField.setText(prescription.getGovernmentId());
            patientTextField.setText(prescription.getPatient().getDisplayName());
            Instant instant = prescription.getAddedDate();
            LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            addedDatePicker.setValue(date);
        } else {
            doctorTextField.setText(user.getDisplayName());
            patientTextField.setText(targetPatient.getDisplayName());
            addedDatePicker.setValue(LocalDate.now());
        }
    }

    /**
     * According to current edit state sets fields editable or saves entered data (edits chosen {@link Prescription}
     * or creates a new one).
     */
    @FXML
    public void editSave() {
        Transaction transaction;
        try {
            if (getMode() == Mode.DETAILS) {
                if (editState.getValue()) {
                    if (notesTextField.getText().trim().equals("") || notesTextField.getText() == null
                            || tagsTextField.getText() == null || tagsTextField.getText().trim().equals("")
                            || govIdTextField.getText().trim().equals("") || govIdTextField.getText() == null) {
                        if (!showAlert(Alert.AlertType.ERROR, "Błąd zaipsu",
                                "Nie wypełniniono wymaganych pól", "Wszystkie pola są wymagane")) {
                            editState.setValue(!editState.getValue());
                        }
                    } else {
                        editQuery.setParameter("notes", (notesTextField.getText() == null)
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : notesTextField.getText().trim());
                        editQuery.setParameter("tags", (tagsTextField.getText() == null)
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : tagsTextField.getText().trim());
                        editQuery.setParameter("governmentID", (govIdTextField.getText() == null)
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : govIdTextField.getText().trim());
                        editQuery.setParameter("prId", prescription.getId());
                        transaction = session.beginTransaction();
                        editQuery.executeUpdate();
                        transaction.commit();
                        ClinicApplication.getEntityManager().refresh(prescription);

                    }
                }
            } else {
                if (notesTextField.getText() == null || notesTextField.getText().trim().equals("")
                        || tagsTextField.getText() == null || tagsTextField.getText().trim().equals("")
                        || govIdTextField.getText() == null || govIdTextField.getText().trim().equals("")
                ) {
                    if (showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie wypełniniono wymaganych pól",
                            "Wszystkie pola są wymagane")) {
                        editState.setValue(!editState.getValue());
                    }
                } else {
                    transaction = session.beginTransaction();
                    Prescription newPr = new Prescription();
                    newPr.setAddedBy(ClinicApplication.getUser());
                    newPr.setNotes((notesTextField.getText() == null)
                            ? null : notesTextField.getText().trim());
                    newPr.setStringTags((tagsTextField.getText() == null)
                            ? null : tagsTextField.getText().trim());
                    newPr.setGovernmentId((govIdTextField.getText() == null)
                            ? null : govIdTextField.getText().trim());
                    newPr.setPatient(targetPatient);
                    newPr.setAddedDate(Instant.now());

                    session.persist(newPr);
                    transaction.commit();
                    editState.setValue(!editState.getValue());

                    if(showAlert(Alert.AlertType.CONFIRMATION, "Dodawanie recepty",
                            "Pomyślnie dodano receptę", "Kod recepty: " + newPr.getGovernmentId())){
                        this.getParentController().goToViewRaw(MainWindowController.Views.PRESCRIPTIONS);
                    }
                    return;
                }
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            transaction = session.getTransaction();
            if (transaction.isActive())
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
        if (showAlert(Alert.AlertType.CONFIRMATION, "Anulowanie recepty",
                "Czy na pewno chcesz anulować receptę?",
                "Recepta zostanie usunięta z bazy. Tej operacji nie można cofnąć")) {
            deleteQuery.setParameter("id", prescription.getId());
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();
            this.getParentController().goBack();
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

    /**
     * Generates PDF report containing selected {@link Prescription}.
     * @throws IOException when there is a file missing
     * @throws URISyntaxException when string couldn't be passed as {@link URI} reference
     */
    @FXML
    protected void prescriptionReport() throws IOException, URISyntaxException {
        ReportObject reportObject = ReportDialog.createConfig();
        Configuration configuration = reportObject.getConfiguration();
        ConverterProperties properties = reportObject.getProperties();
        URL templatesURL = reportObject.getTemplatesURL();
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

            Template template = configuration.getTemplate("prescriptionDetailsTemplate.ftl", "UTF-8");
            File outputFile = new File("output.html");
            Writer writer = new FileWriter(outputFile);

            Map<String, Object> dataModel = new HashMap<>();

            dataModel.put("prescription", prescription);

            template.process(dataModel, writer);

            writer.close();

            HtmlConverter.convertToPdf(new FileInputStream("output.html"),
                    new FileOutputStream(file), properties);

            if (!outputFile.delete()) {
                showAlert(Alert.AlertType.ERROR, "Błąd usuwania pliku", "Nie można usunąć pliku", "");
            }
            showAlert(Alert.AlertType.INFORMATION, "Generowanie recepty", "Utworzono receptę", "");

        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.",
                    e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Available window modes (details of existing prescription or creation of a new one).
     */
    public enum Mode {DETAILS, CREATE}

}