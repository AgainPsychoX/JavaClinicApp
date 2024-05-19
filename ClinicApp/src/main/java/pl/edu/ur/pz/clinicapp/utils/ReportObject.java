package pl.edu.ur.pz.clinicapp.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import freemarker.template.Configuration;

import java.net.URL;

/**
 * Class containing fields necessary for creating proper {@link freemarker.template.Template} configuration
 */
public class ReportObject {
    private Configuration configuration;
    private ConverterProperties properties;

    public ReportObject(Configuration configuration, ConverterProperties properties) {
        this.configuration = configuration;
        this.properties = properties;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ConverterProperties getProperties() {
        return properties;
    }

    public void setProperties(ConverterProperties properties) {
        this.properties = properties;
    }

}
