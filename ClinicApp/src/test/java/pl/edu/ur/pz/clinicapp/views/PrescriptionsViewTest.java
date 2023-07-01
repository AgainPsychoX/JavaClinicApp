package pl.edu.ur.pz.clinicapp.views;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.models.Prescription;
import pl.edu.ur.pz.clinicapp.models.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionsViewTest {
    @ParameterizedTest
    @EnumSource(User.Role.class)
    void testCurrQuery(User.Role role) {
        PrescriptionsView view = new PrescriptionsView();
        view.setCurrQuery(role);
        if(role == User.Role.PATIENT) assertEquals(view.currQuery, view.findUsersPrescriptions);
        else if(role == User.Role.DOCTOR) assertEquals(view.currQuery, view.createdPrescriptions);
        else assertEquals(view.currQuery, view.allPrescriptions);
    }

}