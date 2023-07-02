package pl.edu.ur.pz.clinicapp.dialogs;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import freemarker.template.Configuration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ReportObject;
import pl.edu.ur.pz.clinicapp.views.PrescriptionsView;

import javax.persistence.Table;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReportDialogTest {
    private static ReportObject reportObject;
    private  ReportDialog reportDialog;

    private  ObservableList<String> selectedFields;
    private  ListView<String> selectedFieldsListView;

    @BeforeAll
    public static void setup() {
        Locale locale = new Locale("pl", "PL");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setLocale(locale);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        ConverterProperties properties = new ConverterProperties();
        DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);
        fontProvider.addFont(String.valueOf(ClinicApplication.class.getResource("fonts/calibri.ttf")));

        properties.setFontProvider(fontProvider);
        properties.setCharset("UTF-8");

        URL templatesURL = ClinicApplication.class.getResource("templates");

        reportObject = new ReportObject(configuration, properties, templatesURL);
    }

    @Test
    public void testCreateConfig() {
        assertNotNull(reportObject);

        Configuration configuration = reportObject.getConfiguration();
        assertNotNull(configuration);
        assertEquals("pl", configuration.getLocale().getLanguage());
        assertEquals("PL", configuration.getLocale().getCountry());
        assertEquals("UTF-8", configuration.getDefaultEncoding());

        ConverterProperties properties = reportObject.getProperties();
        assertNotNull(properties);
        assertNotNull(properties.getFontProvider());
        assertEquals("UTF-8", properties.getCharset());

        URL templatesURL = reportObject.getTemplatesURL();
        assertNotNull(templatesURL);
    }


//    @ParameterizedTest
//    @EnumSource(ReportDialog.Mode.class)
//    void testCurrMode(ReportDialog.Mode mode) {
//        ReportDialog dialog = new ReportDialog();
//        ReportDialog.Mode mode1 = (mode);
//
//        if(mode1 == ReportDialog.Mode.PATIENTS) assertEquals(view.currQuery, view.findUsersPrescriptions);
//        else if(role == User.Role.DOCTOR) assertEquals(view.currQuery, view.createdPrescriptions);
//        else assertEquals(view.currQuery, view.allPrescriptions);
//    }

}
