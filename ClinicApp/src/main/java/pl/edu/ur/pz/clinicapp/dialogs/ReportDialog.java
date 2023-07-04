package pl.edu.ur.pz.clinicapp.dialogs;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.DateUtils;
import pl.edu.ur.pz.clinicapp.utils.ReportObject;
import pl.edu.ur.pz.clinicapp.utils.views.ViewControllerBase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class for generating PDF reports using .ftl templates based on HTML files using {@link Template} library.
 * PDF reports are created from .ftl templates using {@link HtmlConverter}.
 */
public class ReportDialog extends ViewControllerBase implements Initializable {

    @FXML private Button saveButton;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ListView<String> availableFieldsListView;
    @FXML private ListView<String> selectedFieldsListView;
    @FXML private VBox content;

    public String name;
    List<String> availableFields;
    List<String> selectedFields;
    Configuration configuration;
    ResourceBundle resourceBundle;
    ConverterProperties properties;

    private Mode mode;

    /**
     * Shows alert.
     * @param type   {@link javafx.scene.control.Alert.AlertType}
     * @param title  Alert title
     * @param header Alert header
     * @param text   Alert text
     */
    private static void showAlert(Alert.AlertType type, String title, String header, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /**
     * Creates new {@link ReportObject} containing necessary data to create new reports.
     * Report is configured for Polish language, using unicode encoding.
     * @return new {@link ReportObject}
     */
    public static ReportObject createConfig() throws IOException {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration
                (freemarker.template.Configuration.VERSION_2_3_32);

        Locale locale = new Locale("pl", "PL");
        configuration.setLocale(locale);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(new DefaultFontProvider(true, true, true));
        try {
            URL url = ClassLoader.getSystemResource("pl/edu/ur/pz/clinicapp");
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "templates");
            tempDir.mkdirs();

            try (InputStream inputStream = url.openStream()) {
                URL zip = ClassLoader.getSystemResource("templates.zip");
                try (InputStream zipStream = zip.openStream(); ZipInputStream zipInputStream = new ZipInputStream(zipStream)) {
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            String entryName = entry.getName();
                            File outputFile = new File(tempDir, entryName);
                            outputFile.getParentFile().mkdirs();
                            Files.copy(zipInputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
                File tempFile = new File(tempDir, "templates");
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                configuration.setDirectoryForTemplateLoading(tempDir);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd inicializacji", "Brak potrzebnych plików",
                    e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        return new ReportObject(configuration, properties);
    }

    /**
     * Creates new {@link ReportObject} containing report configuration, creates new {@link ArrayList} for
     * selected columns.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ReportObject reportObject = null;
        try {
            reportObject = createConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configuration = reportObject.getConfiguration();
        properties = reportObject.getProperties();

        selectedFields = new ArrayList<>();
        resourceBundle = ResourceBundle.getBundle("pl.edu.ur.pz.clinicapp.localization.strings",
                configuration.getLocale());

        try {
            URL url = ClassLoader.getSystemResource("pl/edu/ur/pz/clinicapp");
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "templates");
            tempDir.mkdirs();

            try(InputStream inputStream = url.openStream()) {
                URL zip = ClassLoader.getSystemResource("templates.zip");
                try(InputStream zipStream = zip.openStream(); ZipInputStream zipInputStream = new ZipInputStream(zipStream)) {
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            String entryName = entry.getName();
                            File outputFile = new File(tempDir, entryName);
                            outputFile.getParentFile().mkdirs();
                            Files.copy(zipInputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
                File tempFile = new File(tempDir, "templates");
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                configuration.setDirectoryForTemplateLoading(tempDir);
                configuration.setSharedVariable("bundle", resourceBundle);
                configuration.setSharedVariable("DateUtils", new DateUtils());
            }
        } catch (IOException | TemplateModelException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd inicializacji", "Brak potrzebnych plików",
                    e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * Clears available and selected columns.
     */
    @Override
    public void dispose() {
        availableFields.clear();
        selectedFields.clear();
        availableFieldsListView.getItems().clear();
        selectedFieldsListView.getItems().clear();
        super.dispose();
    }

    /**
     * Populates the view from given context.
     * If no arguments are given, the view will default to {@link Prescription}s report.
     * First context argument specifies {@link ReportDialog.Mode}.
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {

        var mode = Mode.PRESCRIPTIONS;
        if (context.length >= 1) {
            if (context[0] instanceof Mode m) {
                mode = m;
            } else {
                throw new IllegalArgumentException();
            }
        }

        this.mode = mode;
        refresh();
        Mode finalMode = mode;
        saveButton.setOnAction(event -> {
            switch (finalMode) {
                case PRESCRIPTIONS -> {
                    try {
                        prescriptionsReport((List<Prescription>) context[1]);
                    } catch (IOException | URISyntaxException | TemplateModelException e) {
                        throw new RuntimeException(e);
                    }
                }
                case REFERRALS -> {
                    try {
                        referralsReport((List<Referral>) context[1]);
                    } catch (IOException | URISyntaxException | TemplateModelException e) {
                        throw new RuntimeException(e);
                    }
                }
                case PATIENTS -> {
                    try {
                        patientsReport((List<Patient>) context[1]);
                    } catch (IOException | URISyntaxException | TemplateModelException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * Adds available columns to pick from based on current {@link Object} sent from previous view.
     * <ul>
     *     <li>{@link pl.edu.ur.pz.clinicapp.views.PrescriptionsView} matches {@link Prescription} fields</li>
     *     <li>{@link pl.edu.ur.pz.clinicapp.views.ReferralsView} matches {@link Referral} fields</li>
     *     <li>{@link pl.edu.ur.pz.clinicapp.views.PatientsView} matches {@link Patient} fields</li>
     * </ul>
     * Adds translations from {@see <a href="pl/edu/ur/pz/clinicapp/localization/strings_pl.properties>}"
     */
    @Override
    public void refresh() {
        availableFields = new ArrayList<>();
        switch (mode) {
            case PRESCRIPTIONS -> {
                name = "prescription.";
                availableFields = new ArrayList<>();
                availableFields.add("addedBy");
                availableFields.add("addedDate");
                availableFields.add("governmentId");
                availableFields.add("notes");
                availableFields.add("tags");
                availableFields.add("patient");
                for (String field : availableFields)
                    availableFieldsListView.getItems().add(resourceBundle.getString("prescription." + field));
            }
            case REFERRALS -> {
                name = "referral.";
                availableFields = new ArrayList<>();
                availableFields.add("notes");
                availableFields.add("addedDate");
                availableFields.add("tags");
                availableFields.add("addedBy");
                availableFields.add("patient");
                availableFields.add("pointOfInterest");
                availableFields.add("fulfilmentDate");
                availableFields.add("feedback");
                for (String field : availableFields)
                    availableFieldsListView.getItems().add(resourceBundle.getString("referral." + field));
            }
            case PATIENTS -> {
                name = "user.";
                availableFields = new ArrayList<>();
                availableFields.add("address");
                availableFields.add("pesel");
                availableFields.add("fullName");
                availableFields.add("phone");
                availableFields.add("mail");
                availableFields.add("id");
                availableFields.add("internalName");
                availableFields.add("doctorSpecialities");
                for (String field : availableFields)
                    availableFieldsListView.getItems().add(resourceBundle.getString("user." + field));
            }
        }
    }

    /**
     * Adds selected column
     */
    @FXML
    private void addField() {
        String selectedField = availableFieldsListView.getSelectionModel().getSelectedItem();
        int index = availableFieldsListView.getSelectionModel().getSelectedIndex();
        if (selectedField != null && !selectedFieldsListView.getItems().contains(selectedField)) {
            selectedFields.add(availableFields.get(index));
            selectedFieldsListView.getItems().add(selectedField);
        }
    }

    /**
     * Adds all columns
     */
    @FXML
    public void addAllFields() {
        for (String item : availableFields) {
            if (!selectedFields.contains(item)) {
                selectedFields.add(item);
                selectedFieldsListView.getItems().add(resourceBundle.getString(name + item));
            }
        }
    }

    /**
     * Removes selected column
     */
    @FXML
    private void removeField() {
        String selectedField = selectedFieldsListView.getSelectionModel().getSelectedItem();
        int index = selectedFieldsListView.getSelectionModel().getSelectedIndex();
        if (selectedField != null) {
            selectedFields.remove(index);
            selectedFieldsListView.getItems().remove(selectedField);
        }
    }

    /**
     * Removes all selected columns
     */
    @FXML
    public void removeAllFields() {
        selectedFields.clear();
        selectedFieldsListView.getItems().clear();
    }

    /**
     * Moves selected column up
     */
    @FXML
    private void moveSelectedFieldUp() {
        int selectedIndex = selectedFieldsListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex > 0) {
            String selectedField = selectedFieldsListView.getSelectionModel().getSelectedItem();
            selectedFieldsListView.getItems().remove(selectedIndex);
            selectedFieldsListView.getItems().add(selectedIndex - 1, selectedField);
            selectedFieldsListView.getSelectionModel().select(selectedIndex - 1);

            String selectedFieldEn = selectedFields.get(selectedIndex);
            selectedFields.remove(selectedIndex);
            selectedFields.add(selectedIndex - 1, selectedFieldEn);
        }
    }

    /**
     * Moves selected column down
     */
    @FXML
    private void moveSelectedFieldDown() {
        int selectedIndex = selectedFieldsListView.getSelectionModel().getSelectedIndex();
        int lastIndex = selectedFieldsListView.getItems().size() - 1;

        if (selectedIndex >= 0 && selectedIndex < lastIndex) {
            String selectedField = selectedFieldsListView.getSelectionModel().getSelectedItem();
            selectedFieldsListView.getItems().remove(selectedIndex);
            selectedFieldsListView.getItems().add(selectedIndex + 1, selectedField);
            selectedFieldsListView.getSelectionModel().select(selectedIndex + 1);


            String selectedFieldEn = selectedFields.get(selectedIndex);
            selectedFields.remove(selectedIndex);
            selectedFields.add(selectedIndex + 1, selectedFieldEn);

        }
    }

    /**
     * Sorts selected filed alphabetically. List is sorted by Polish names, then according to order new
     * list in english is created for template
     */
    @FXML
    private void sortFieldsAlphabetically() {
        Map<String, String> englishNames = new HashMap<>();
        for (int i = 0; i < selectedFields.size(); i++) {
            englishNames.put(selectedFieldsListView.getItems().get(i), selectedFields.get(i));
        }
        selectedFieldsListView.getItems().sort(Comparator.naturalOrder());
        selectedFields.clear();
        for (String name : selectedFieldsListView.getItems()) {
            selectedFields.add(englishNames.get(name));
        }
    }

    /**
     * Generates {@link Referral} report.
     *
     * @param list list of referrals sent from view
     * @throws IOException            when there is a file missing
     * @throws URISyntaxException     when there is a file missing
     * @throws TemplateModelException when there is a missing or broken template e.g. field name doesn't match
     *                                translation key
     */
    private void referralsReport(List<Referral> list) throws IOException, URISyntaxException, TemplateModelException {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Niepoprawny zakres dat",
                    "Data początkowa nie może być późniejsza od daty końcowej.");
        } else {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Zapisywanie raportu");
                fileChooser.setInitialFileName("referralsReport.pdf");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF",
                        "*.pdf"));

                File file = fileChooser.showSaveDialog(new Stage());

                Template template = configuration.getTemplate("referralsTemplate.ftl", "UTF-8");

                File outputFile = new File("output.html");
                Writer writer = new FileWriter(outputFile);

                Map<String, Object> dataModel = new HashMap<>();
                dataModel.put("headers", selectedFields);
                dataModel.put("referrals", list);
                if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                    dataModel.put("startDate", startDate);
                    dataModel.put("endDate", endDate);
                }

                template.process(dataModel, writer);
                writer.close();
                HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file),
                        properties);

                if(!outputFile.delete())
                    throw new RuntimeException();
                showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");

            } catch (FileNotFoundException | TemplateException e) {
                showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.",
                        e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Generates report containing 20 newest {@link pl.edu.ur.pz.clinicapp.models.Patient}.
     *
     * @param list - list of 20 newest {@link Patient} sent from view.
     * @throws IOException            when there is a file missing
     * @throws URISyntaxException     when there is a file missing
     * @throws TemplateModelException when there is a missing or broken template e.g. field name doesn't match
     *                                translation key
     */
    public void patientsReport(List<Patient> list) throws IOException, URISyntaxException, TemplateModelException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie raportu");
            fileChooser.setInitialFileName("patientsReport.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF",
                    "*.pdf"));

            File file = fileChooser.showSaveDialog(new Stage());

            Template template = configuration.getTemplate("usersTemplate.ftl", "UTF-8");

            File outputFile = new File("output.html");
            Writer writer = new FileWriter(outputFile);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("headers", selectedFields);
            dataModel.put("users", list);

            template.process(dataModel, writer);
            writer.close();
            HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file),
                    properties);

            if(!outputFile.delete())
                throw new RuntimeException();
            showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");

        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates {@link Prescription} report.
     *
     * @param list - list of prescriptions sent from view
     * @throws IOException            when there is a file missing
     * @throws URISyntaxException     when there is a file missing
     * @throws TemplateModelException when there is a missing or broken template
     */
    private void prescriptionsReport(List<Prescription> list) throws IOException, URISyntaxException,
            TemplateModelException {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Niepoprawny zakres dat",
                    "Data początkowa nie może być późniejsza od daty końcowej.");
        } else {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Zapisywanie raportu");
                fileChooser.setInitialFileName("prescriptionsReport.pdf");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF",
                        "*.pdf"));

                File file = fileChooser.showSaveDialog(new Stage());
                Template template = configuration.getTemplate("prescriptionsTemplate.ftl");

                File outputFile = new File("output.html");
                Writer writer = new FileWriter(outputFile);

                Map<String, Object> dataModel = new HashMap<>();
                dataModel.put("headers", selectedFields);
                dataModel.put("prescriptions", list);
                if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                    dataModel.put("startDate", startDate);
                    dataModel.put("endDate", endDate);
                }

                template.process(dataModel, writer);
                writer.close();
                HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file),
                        properties);

                if(!outputFile.delete())
                    throw new RuntimeException();
                showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");

            } catch (FileNotFoundException | TemplateException e) {
                showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.",
                        e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Generates {@link pl.edu.ur.pz.clinicapp.models.Timetable} report.
     *
     * @param content {@link WeekPane<WeekPane.Entry>} containing {@link pl.edu.ur.pz.clinicapp.models.Timetable}
     * @throws IOException when there is a file missing
     */
    public void timetableReport(WeekPane<WeekPane.Entry> content, LocalDate startWeek) throws IOException {
        WritableImage snapshot = content.getGrid().snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
        BufferedImage imageTable = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);

        WritableImage snapshotHeader = content.getHeader().snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedHeader = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
        BufferedImage imageHeader = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshotHeader, bufferedHeader);


        String finalImage;
        String headerImage;
        try {
            ByteArrayOutputStream ostt = new ByteArrayOutputStream();
            ImageIO.write(imageTable, "png", ostt);
            byte[] bytesTable = ostt.toByteArray();
            byte[] encodedTable = Base64.getEncoder().encode(bytesTable);
            String imageTableAsBase64 = new String(encodedTable);
            finalImage = "data:image/png;base64," + imageTableAsBase64;

            ByteArrayOutputStream osh = new ByteArrayOutputStream();
            ImageIO.write(imageHeader, "png", osh);
            byte[] bytesHeader = osh.toByteArray();
            byte[] encodedHeader = Base64.getEncoder().encode(bytesHeader);
            String imageHeaderAsBase64 = new String(encodedHeader);
            headerImage = "data:image/png;base64," + imageHeaderAsBase64;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie raportu");
            fileChooser.setInitialFileName("timetableReport.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF",
                    "*.pdf"));

            File file = fileChooser.showSaveDialog(new Stage());

            Template template = configuration.getTemplate("timetableTemplate.ftl");

            File outputFile = new File("output.html");
            Writer writer = new FileWriter(outputFile);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("startDate", startWeek);
            dataModel.put("endDate", startWeek.plusDays(4));
            dataModel.put("header", headerImage);
            dataModel.put("timetable", finalImage);

            template.process(dataModel, writer);

            writer.close();

            HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file),
                    properties);

            if(!outputFile.delete())
                throw new RuntimeException();
            showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");


        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.",
                    e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Goes back to previous view, calls dispose method to clear lists.
     */
    @FXML
    public void onBackClick() {
        dispose();
        this.getParentController().goBack();
    }


    /**
     * Available dialog modes
     */
    public enum Mode {PRESCRIPTIONS, REFERRALS, PATIENTS}
}


