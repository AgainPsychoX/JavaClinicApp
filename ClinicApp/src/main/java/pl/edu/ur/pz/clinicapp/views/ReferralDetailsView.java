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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.DateUtils;
import pl.edu.ur.pz.clinicapp.utils.ReportObject;
import pl.edu.ur.pz.clinicapp.utils.views.ViewController;
import pl.edu.ur.pz.clinicapp.utils.views.ViewControllerBase;

import javax.persistence.TemporalType;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * View controller to edit, delete or display details of a {@link Referral}.
 */
public class ReferralDetailsView extends ViewControllerBase {

    /**
     * Available window modes (details of existing referral or creation of a new one).
     */
    public enum RefMode {DETAILS, CREATE}

    private RefMode currMode;
    @FXML
    protected CheckBox nursesCheck;
    @FXML
    protected HBox buttonBox;
    @FXML
    protected HBox interestBox;
    @FXML
    protected TextField dateTimeField;
    @FXML
    protected TextField fulDateTimeField;
    @FXML
    protected Text backText;
    @FXML
    protected TextField doctorField;
    @FXML
    protected DatePicker fulDatePicker;
    @FXML
    protected DatePicker datePicker;
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
    protected Button deleteButton;
    @FXML
    protected Button IKPButton;
    @FXML
    protected Button printButton;
    @FXML
    protected VBox vBox;
    @FXML
    protected Text patientField;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query editQuery = session.getNamedQuery("editReferral");
    Query deleteQuery = session.getNamedQuery("deleteReferral");
    private Referral ref;

    /**
     * Indicates whether view is editable or not.
     */
    private static BooleanProperty editState = new SimpleBooleanProperty(false);
    private Patient targetPatient;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static boolean getEditState() {
        return editState.getValue();
    }

    public static void setEditState(boolean editState) {
        ReferralDetailsView.editState.set(editState);
    }

    @Override
    public boolean onNavigation(Class<? extends ViewController> which, Object... context) {
        return !getEditState() || exitConfirm();
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
                if (targetPatient != null) {
                    this.getParentController().goToView(ReferralsView.class, targetPatient);
                } else this.getParentController().goToViewRaw(ReferralsView.class);
            }
        } else {
            if (targetPatient != null) {
                this.getParentController().goToView(ReferralsView.class, targetPatient);
            } else this.getParentController().goToViewRaw(ReferralsView.class);
        }
    }

    /**
     * Populates the view for given context. <br>
     * Sets action buttons according to logged user's role.
     * <p>
     * Context arguments:
     * <ol>
     * <li>First argument can specify {@link RefMode}.
     * <li>Second argument can specify {@link Referral} whose info is to be displayed, edited or deleted.
     * <li>Third argument can specify {@link Patient} in whose context the app is currently displaying referrals.
     * </ol>
     *
     * @param context Context arguments.
     */
    @Override
    public void populate(Object... context) {
        if (context.length > 2) targetPatient = ((Patient) context[2]);
        datePicker.setEditable(false);
        datePicker.setDisable(true);
        dateTimeField.setEditable(false);
        editState.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                if (after) {
                    if (ClinicApplication.getUser().getRole() == User.Role.NURSE) {
                        editButton.setText("Zapisz");
                        if(ref == null || ref.getFulfilmentDate() == null || (currMode != RefMode.DETAILS)) {
                            fulDatePicker.setEditable(true);
                            fulDatePicker.setDisable(false);
                            fulDateTimeField.setEditable(true);
                        }
                        notesArea.setEditable(false);
                        feedbackArea.setEditable(true);
                        codeField.setEditable(false);
                        tagsField.setEditable(false);
                        nursesCheck.setDisable(true);
                    } else {
                        editButton.setText("Zapisz");
                        if(ref == null || ref.getFulfilmentDate() == null || (currMode != RefMode.DETAILS)) {
                            fulDatePicker.setEditable(true);
                            fulDatePicker.setDisable(false);
                            fulDateTimeField.setEditable(true);
                        }
                        if (!nursesCheck.isSelected() || currMode == RefMode.CREATE) interestField.setEditable(true);
                        notesArea.setEditable(true);
                        feedbackArea.setEditable(true);
                        codeField.setEditable(true);
                        tagsField.setEditable(true);
                        nursesCheck.setDisable(false);
                    }
                } else {
                    editButton.setText("Edytuj");
                    fulDatePicker.setEditable(false);
                    fulDatePicker.setDisable(true);
                    fulDateTimeField.setEditable(false);
                    interestField.setEditable(false);
                    notesArea.setEditable(false);
                    feedbackArea.setEditable(false);
                    codeField.setEditable(false);
                    tagsField.setEditable(false);
                    nursesCheck.setDisable(true);
                }
            }
        });

        User.Role role = ClinicApplication.getUser().getRole();
        currMode = (RefMode) context[0];

        fulDatePicker.setStyle("-fx-opacity: 1.0;");
        fulDatePicker.getEditor().setStyle("-fx-opacity: 1.0;");
        datePicker.setStyle("-fx-opacity: 1.0;");
        datePicker.getEditor().setStyle("-fx-opacity: 1.0;");

        buttonBox.getChildren().clear();

        if (currMode == RefMode.DETAILS) {
            ref = (Referral) context[1];

            // in case the referral was edited while app is running
            ClinicApplication.getEntityManager().refresh(ref);

            if (role != User.Role.ADMIN && role != User.Role.NURSE && ref.getAddedBy() != ClinicApplication.getUser()) {
                buttonBox.getChildren().add(IKPButton);
                buttonBox.getChildren().add(printButton); //added
                patientField.setText(null);
            } else if (role == User.Role.NURSE) {
                buttonBox.getChildren().add(editButton);
                buttonBox.getChildren().add(printButton); //aded
                patientField.setText("Pacjent: " + ref.getPatient().getDisplayName());
            } else {
                if (!interestBox.getChildren().contains(nursesCheck)) interestBox.getChildren().add(nursesCheck);
                buttonBox.getChildren().add(editButton);
                buttonBox.getChildren().add(deleteButton);
                buttonBox.getChildren().add(IKPButton);
                buttonBox.getChildren().add(printButton); //added
                patientField.setText("Pacjent: " + ref.getPatient().getDisplayName());
            }
            refresh();
        } else {
            fulDatePicker.setEditable(true);
            fulDatePicker.setDisable(false);
            fulDateTimeField.setEditable(true);

            buttonBox.getChildren().add(editButton);
            if (!interestBox.getChildren().contains(nursesCheck)) interestBox.getChildren().add(nursesCheck);

            doctorField.setText(ClinicApplication.getUser().getDisplayName());
            fulDatePicker.setValue(null);
            fulDateTimeField.setText(null);
            datePicker.setValue(LocalDate.now());
            dateTimeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            interestField.setText(null);
            notesArea.setText(null);
            feedbackArea.setText(null);
            codeField.setText(null);
            tagsField.setText(null);
            targetPatient = ((Patient) context[1]);
            editState.setValue(true);

            patientField.setText("Pacjent: " + targetPatient.getDisplayName());
        }

        nursesCheck.setSelected(interestField.getText() != null && interestField.getText().equals(Referral.forNurses));
    }

    /**
     * Sets values of table cells.
     */
    @Override
    public void refresh() {
        doctorField.setText(ref.getAddedBy().getDisplayName());
        fulDatePicker.setValue((ref.getFulfilmentDate() == null)
                ? null
                : Timestamp.from(ref.getFulfilmentDate()).toLocalDateTime().toLocalDate());
        fulDateTimeField.setText((ref.getFulfilmentDate() == null)
                ? null
                : Timestamp.from(ref.getFulfilmentDate()).toLocalDateTime().toLocalTime().toString());
        datePicker.setValue((ref.getAddedDate() == null)
                ? null
                : Timestamp.from(ref.getAddedDate()).toLocalDateTime().toLocalDate());
        dateTimeField.setText((ref.getAddedDate() == null)
                ? null
                : Timestamp.from(ref.getAddedDate()).toLocalDateTime().toLocalTime().toString());
        interestField.setText(ref.getPointOfInterest());
        nursesCheck.setSelected(interestField.getText() != null && interestField.getText().equals(Referral.forNurses));
        notesArea.setText(ref.getNotes());
        feedbackArea.setText(ref.getFeedback());
        codeField.setText(ref.getGovernmentId());
        tagsField.setText(ref.getStringTags());
    }

    /**
     * According to current edit state sets fields editable or saves entered data (edits chosen referral or creates
     * a new one).
     */
    public void editSave() {
        Transaction transaction;

        Alert exit = new Alert(Alert.AlertType.INFORMATION);

        try {
            String dateVal = (datePicker.getEditor().getText() == null) ? null : datePicker.getEditor().getText().trim();
            String dateTimeVal = (dateTimeField.getText() == null) ? "00:00:00" : dateTimeField.getText().trim();
            String fulDateVal = (fulDatePicker.getEditor().getText() == null) ? null : fulDatePicker.getEditor().getText().trim();
            String fulDateTimeVal = (fulDateTimeField.getText() == null) ? "00:00:00" : fulDateTimeField.getText().trim();
            if (currMode == RefMode.DETAILS) {
                if (editState.getValue()) {
                    if (dateVal == null || notesArea.getText() == null || notesArea.getText().trim().equals("")
                            || tagsField.getText() == null || tagsField.getText().trim().equals("")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd zapisu");
                        alert.setHeaderText("Nie wypełniono wymaganych pól");
                        alert.setContentText("Pola daty wystawienia, notatek i tagów są wymagane.");
                        alert.showAndWait();
                        editState.setValue(!editState.getValue());
                    } else {
                        String newAddedDate = LocalDate.parse(dateVal, formatter).toString() + " " + dateTimeVal;
                        String newFulDate = (fulDateVal == null || fulDateVal.isBlank()) ? "" : LocalDate.parse(fulDateVal, formatter).toString() + " " + fulDateTimeVal;
                        Timestamp addedDate = Timestamp.valueOf((dateTimeVal.length() != 8)
                                ? newAddedDate + ":00" : newAddedDate);
                        Timestamp fulfilmentDate;
                        editQuery.setParameter("addedDate", addedDate);
                        if (newFulDate.isBlank()) {
                            editQuery.setParameter("fulfilmentDate", (Instant) null, TemporalType.TIMESTAMP);
                        } else {
                            fulfilmentDate = Timestamp.valueOf((fulDateTimeVal.length() != 8)
                                    ? newFulDate + ":00" : newFulDate);
                            if (addedDate.compareTo(fulfilmentDate) >= 0)
                                throw new IllegalArgumentException("IllegalFulDate");
                            editQuery.setParameter("fulfilmentDate", fulfilmentDate);
                        }
                        editQuery.setParameter("pointOfInterest", (interestField.getText() == null
                                || interestField.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : interestField.getText().trim());
                        editQuery.setParameter("notes", (notesArea.getText() == null
                                || notesArea.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : notesArea.getText().trim());
                        editQuery.setParameter("feedback", (feedbackArea.getText() == null
                                || feedbackArea.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : feedbackArea.getText().trim());
                        editQuery.setParameter("tags", (tagsField.getText() == null
                                || tagsField.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : tagsField.getText().trim());
                        editQuery.setParameter("governmentId", (codeField.getText() == null
                                || codeField.getText().isBlank())
                                ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                                : codeField.getText().trim());
                        editQuery.setParameter("refId", ref.getId());

                        transaction = session.beginTransaction();
                        editQuery.executeUpdate();
                        transaction.commit();
                        ClinicApplication.getEntityManager().refresh(ref);

                        exit.setTitle("Edycja skierowania");
                        exit.setHeaderText("Edycja zakończona pomyślnie");
                        exit.setContentText("Zedytowano wybrane skierowanie.");
                        exit.showAndWait();
                    }
                }
            } else {
                if (dateVal == null || notesArea.getText() == null || notesArea.getText().trim().equals("")
                        || tagsField.getText() == null || tagsField.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd zapisu");
                    alert.setHeaderText("Nie wypełniono wymaganych pól");
                    alert.setContentText("Pola daty wystawienia, notatek i tagów są wymagane.");
                    alert.showAndWait();
                    editState.setValue(!editState.getValue());
                } else {
                    String newAddedDate = LocalDate.parse(dateVal, formatter).toString() + " " + dateTimeVal;
                    String newFulDate = (fulDateVal == null || fulDateVal.isBlank()) ? "" : LocalDate.parse(fulDateVal, formatter).toString() + " " + fulDateTimeVal;
                    Timestamp addedDate = Timestamp.valueOf((dateTimeVal.length() != 8)
                            ? newAddedDate + ":00" : newAddedDate);
                    Timestamp fulfilmentDate;
                    Referral newRef = new Referral();
                    if (fulDateVal == null || fulDateVal.isBlank()) {
                        newRef.setFulfilmentDate(null);
                    } else {
                        fulfilmentDate = Timestamp.valueOf((fulDateTimeVal.length() != 8)
                                ? newFulDate + ":00" : newFulDate);
                        if (addedDate.compareTo(fulfilmentDate) >= 0) {
                            throw new IllegalArgumentException("IllegalFulDate");
                        }

                        newRef.setFulfilmentDate(fulfilmentDate.toInstant());
                    }
                    newRef.setAddedDate(addedDate.toInstant());
                    newRef.setPointOfInterest((interestField.getText() == null
                            || interestField.getText().isBlank())
                            ? null : interestField.getText().trim());
                    newRef.setNotes((notesArea.getText() == null
                            || notesArea.getText().isBlank())
                            ? null : notesArea.getText().trim());
                    newRef.setFeedback((feedbackArea.getText() == null
                            || feedbackArea.getText().isBlank())
                            ? null : feedbackArea.getText().trim());
                    newRef.setStringTags((tagsField.getText() == null
                            || tagsField.getText().isBlank())
                            ? null : tagsField.getText().trim());
                    newRef.setGovernmentId((codeField.getText() == null
                            || codeField.getText().isBlank())
                            ? null : codeField.getText().trim());
                    newRef.setPatient(targetPatient);
                    newRef.setAddedBy(ClinicApplication.getUser());

                    transaction = session.beginTransaction();
                    session.persist(newRef);
                    transaction.commit();

                    editState.setValue(!editState.getValue());

                    exit.setTitle("Dodawnaie skierowania");
                    exit.setHeaderText("Dodawanie zakończone pomyślnie");
                    exit.setContentText("Dodano nowe skierowanie.");
                    exit.showAndWait();

                    this.getParentController().goToView(ReferralsView.class, targetPatient);
                    return;
                }
            }
            editState.setValue(!editState.getValue());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            transaction = session.getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
                System.out.println("ROLLBACK");
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            if (e.getMessage().equals("IllegalFulDate")) {
                alert.setHeaderText("Niepoprawna data realizacji.");
                alert.setContentText("Data realizacji musi być późniejsza niż wystawienia.");
            } else {
                alert.setHeaderText("Niepoprawny format godziny.");
                alert.setContentText("Poprawne formaty: gg:mm lub gg:mm:ss");
            }
            alert.showAndWait();
        } catch (DateTimeParseException d) {
            d.printStackTrace();
            transaction = session.getTransaction();
            if (transaction.isActive()) transaction.rollback();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setHeaderText("Niepoprawny format daty.");
            alert.setContentText("Poprawny format: dd.mm.rrrr");
            alert.showAndWait();
        }
    }

    /**
     * Deletes current referral.
     */
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie skierowania");
        alert.setHeaderText("Czy na pewno chcesz usunąć to skierowanie?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteQuery.setParameter("refId", ref.getId());
            Transaction transaction = session.beginTransaction();
            deleteQuery.executeUpdate();
            transaction.commit();

            Alert exit = new Alert(Alert.AlertType.INFORMATION);
            exit.setTitle("Usuwanie skierowania");
            exit.setHeaderText("Usuwanie zakończone pomyślnie");
            exit.setContentText("Usunięto wybrane skierowanie.");
            exit.showAndWait();

            if (targetPatient != null) {
                this.getParentController().goToView(ReferralsView.class, targetPatient);
            } else this.getParentController().goToViewRaw(ReferralsView.class);
        } else {
            alert.close();
        }
    }

    /**
     * Opens government's website for patients.
     */
    public void sendToIKP() {
        try {
            Desktop desktop = Desktop.getDesktop();
            URI ikp = new URI("https://pacjent.gov.pl/");
            desktop.browse(ikp);
        } catch (URISyntaxException | IOException e) {
            System.err.println("Wystąpił problem z otwarciem witryny IKP.");
        }
    }

    /**
     * Redirects referral to nurses (sets special value and disables field).
     */
    public void setForNurses(ActionEvent actionEvent) {
        if (nursesCheck.isSelected()) {
            interestField.setText(Referral.forNurses);
            interestField.setEditable(false);
        } else {
            interestField.setText("");
            interestField.setEditable(true);
        }
    }

    /**
     * Generates PDF report containing selected {@link Referral}.
     *
     * @throws IOException        when there is a file missing
     * @throws URISyntaxException when string couldn't be passed as {@link URI} reference
     */
    @FXML
    protected void referralReport() throws IOException, URISyntaxException {
        ReportObject reportObject = ReportDialog.createConfig();
        Configuration configuration = reportObject.getConfiguration();
        ConverterProperties properties = reportObject.getProperties();
        try {
            configuration.setDefaultEncoding("UTF-8");
            configuration.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
            configuration.setSharedVariable("DateUtils", new DateUtils());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie recepty");
            fileChooser.setInitialFileName("referral.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF",
                    "*.pdf"));
            File file = fileChooser.showSaveDialog(new Stage());

            Template template = configuration.getTemplate("referralDetailsTemplate.ftl", "UTF-8");

            File tempDir = new File(System.getProperty("java.io.tmpdir"), "templates");
            tempDir.mkdirs();

            File outputFile = new File(tempDir, "output.html");
            Writer writer = new FileWriter(outputFile);

            Map<String, Object> dataModel = new HashMap<>();

            dataModel.put("referral", ref);

            template.process(dataModel, writer);

            writer.close();

            HtmlConverter.convertToPdf(new FileInputStream(new File(tempDir, "output.html")),
                    new FileOutputStream(file), properties);


            if (!outputFile.delete()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd usuwania pliku");
                alert.setHeaderText("Nie można usunąć pliku");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generowanie skierowania");
            alert.setHeaderText("Utworzono skierowanie");
            alert.showAndWait();
        } catch (FileNotFoundException | TemplateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd generowania");
            alert.setHeaderText("Wystąpił błąd");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

}
