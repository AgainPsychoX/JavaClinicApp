package pl.edu.ur.pz.clinicapp.dialogs;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Patient;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.Referral;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.DateUtils;
import pl.edu.ur.pz.clinicapp.utils.ReportObject;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ReportDialog extends ChildControllerBase<MainWindowController> implements Initializable {

    @FXML private Button saveButton;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ListView<String> availableFieldsListView;
    @FXML private ListView<String> selectedFieldsListView;
    private ReportMode mode;


    public String name;
    List<String> availableFields;
    List<String> selectedFields;
    Configuration configuration;
    ResourceBundle resourceBundle;
    ConverterProperties properties;

    private static void showAlert(Alert.AlertType type, String title, String header, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ReportObject reportObject = createConfig();
        configuration = reportObject.getConfiguration();
        ConverterProperties properties = reportObject.getProperties();
        URL templatesURL = reportObject.getTemplatesURL();

        selectedFields = new ArrayList<>();
        resourceBundle = ResourceBundle.getBundle("pl.edu.ur.pz.clinicapp.localization.strings",
                configuration.getLocale());

        try {
            configuration.setDirectoryForTemplateLoading(new File(templatesURL.toURI()));
            configuration.setSharedVariable("bundle", resourceBundle);
            configuration.setSharedVariable("DateUtils", new DateUtils());
        } catch (IOException | URISyntaxException | TemplateModelException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd inicializacji", "Brak potrzebnych plików",
                    e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void dispose() {
        availableFields.clear();
        selectedFields.clear();
        availableFieldsListView.getItems().clear();
        selectedFieldsListView.getItems().clear();
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        mode = (ReportMode) context[0];
        refresh();
        saveButton.setOnAction(event -> {
            switch (mode) {
                case PRESCRIPTION -> {
                    try {
                        prescriptionsReport((List<Prescription>) context[1]);
                    } catch (IOException | URISyntaxException | TemplateModelException e) {
                        throw new RuntimeException(e);
                    }
                }
                case REFERRAL -> {
                    try {
                        referralsReport((List<Referral>) context[1]);
                    } catch (IOException | URISyntaxException | TemplateModelException e) {
                        throw new RuntimeException(e);
                    }
                }
                case USERS_ADMIN -> {
                    try {
                        patientsReport((List<Patient>) context[1]);
                    } catch (IOException | URISyntaxException | TemplateModelException e) {
                        throw new RuntimeException(e);
                    }
                }
                case USERS_DOCTOR -> {
                    refresh();
                }
            }
        });
    }

    @Override
    public void refresh() {
        availableFields = new ArrayList<>();
        switch (mode) {
            case PRESCRIPTION -> {
                name = "prescription.";
                availableFields = new ArrayList<>();
                availableFields.add("addedBy");
                availableFields.add("addedDate");
                availableFields.add("governmentId");
                availableFields.add("notes");
                availableFields.add("tags");
                availableFields.add("patient");
                for (String field : availableFields) {
                    availableFieldsListView.getItems().add(resourceBundle.getString("prescription." + field));
                }
            }
            case REFERRAL -> {
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
                for (String field : availableFields) {
                    availableFieldsListView.getItems().add(resourceBundle.getString("referral." + field));
                }
            }
            case USERS_ADMIN -> {
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
                for (String field : availableFields) {
                    availableFieldsListView.getItems().add(resourceBundle.getString("user." + field));
                }
            }
            case USERS_DOCTOR -> {
                availableFields = new ArrayList<>();
                availableFields.add("address");
                availableFields.add("pesel");
                availableFields.add("fullName");
                availableFields.add("phone");
                availableFields.add("mail");
            }
        }
    }

    @FXML
    private void addField() {
        String selectedField = availableFieldsListView.getSelectionModel().getSelectedItem();
        int index = availableFieldsListView.getSelectionModel().getSelectedIndex();
        if (selectedField != null && !selectedFieldsListView.getItems().contains(selectedField)) {
            selectedFields.add(availableFields.get(index));
            selectedFieldsListView.getItems().add(selectedField);
        }
    }

    @FXML
    public void addAllFields() {
        for (String item : availableFields) {
            if (!selectedFields.contains(item)) {
                selectedFields.add(item);
                selectedFieldsListView.getItems().add(resourceBundle.getString(name + item));
            }
        }
    }

    @FXML
    private void removeField() {
        String selectedField = selectedFieldsListView.getSelectionModel().getSelectedItem();
        int index = selectedFieldsListView.getSelectionModel().getSelectedIndex();
        if (selectedField != null) {
            selectedFields.remove(index);
            selectedFieldsListView.getItems().remove(selectedField);
        }
    }

    @FXML
    public void removeAllFields() {
        selectedFields.clear();
        selectedFieldsListView.getItems().clear();
    }

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
     * Sort selected fileds alphabetically. List is sorted by polish names, then according to order new
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
     * Generates PDF report by creating HTML file with data using template from resources and then converting it to PDF
     *
     * @param list - list of referrals sent from view
     * @throws IOException            when there is a file missing
     * @throws URISyntaxException     when there is a file missing
     * @throws TemplateModelException when there is a missing or broken template e.g. field name doesn't match
     * translation key from string_pl.properties
     */
    private void referralsReport(List<Referral> list) throws IOException, URISyntaxException, TemplateModelException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie raportu");
            fileChooser.setInitialFileName("referralsReport.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF", "*.pdf"));

            File file = fileChooser.showSaveDialog(new Stage());

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Niepoprawny zakres dat",
                        "Data początkowa nie może być późniejsza od daty końcowej.");
            } else {
                Template template = configuration.getTemplate("referralsTemplate.ftl");

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

                HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file), properties);

                outputFile.delete();
                showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");

            }
        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }


    public void patientsReport(List<Patient> list) throws IOException, URISyntaxException, TemplateModelException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie raportu");
            fileChooser.setInitialFileName("patientsReport.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF", "*.pdf"));

            File file = fileChooser.showSaveDialog(new Stage());

            Template template = configuration.getTemplate("usersTemplate.ftl");

            File outputFile = new File("output.html");
            Writer writer = new FileWriter(outputFile);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("headers", selectedFields);
            dataModel.put("users", list);

            template.process(dataModel, writer);

            writer.close();

            HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file), properties);

            outputFile.delete();
            showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");

        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates PDF report by creating HTML file with data using template from resources and then converting it to PDF
     *
     * @param list - list of prescriptions sent from view
     * @throws IOException            when there is a file missing
     * @throws URISyntaxException     when there is a file missing
     * @throws TemplateModelException when there is a missing or broken template
     */
    private void prescriptionsReport(List<Prescription> list) throws IOException, URISyntaxException,
            TemplateModelException {
        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisywanie raportu");
            fileChooser.setInitialFileName("prescriptionsReport.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki PDF", "*.pdf"));

            File file = fileChooser.showSaveDialog(new Stage());

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Niepoprawny zakres dat",
                        "Data początkowa nie może być późniejsza od daty końcowej.");
            } else {
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

                HtmlConverter.convertToPdf(new FileInputStream("output.html"), new FileOutputStream(file), properties);

                outputFile.delete();
                showAlert(Alert.AlertType.INFORMATION, "Generowanie raportu", "Utworzono raport", "");

            }
        } catch (FileNotFoundException | TemplateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd generowania", "Wystąpił błąd.", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public static ReportObject createConfig(){
        freemarker.template.Configuration configuration =
                new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);


        Locale locale = new Locale("pl", "PL");
        configuration.setLocale(locale);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        ConverterProperties properties = new ConverterProperties();
        DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);

        fontProvider.addFont(String.valueOf(ClinicApplication.class.getResource("fonts/calibri.ttf")));

        properties.setFontProvider(fontProvider);
        properties.setCharset("UTF-8");
        URL templatesURL = ClinicApplication.class.getResource("templates");
        return new ReportObject(configuration, properties, templatesURL);
    }


    public enum ReportMode {PRESCRIPTION, REFERRAL, USERS_ADMIN, USERS_DOCTOR}
}
