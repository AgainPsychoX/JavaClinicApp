package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.util.concurrent.ThreadLocalRandom;

public class RegisterDialog extends ChildControllerBase<MainWindowController> {

    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query createPatientQuery = session.getNamedQuery("createPatient");
    Query createDatabaseUserQuery = session.getNamedQuery("createDatabaseUser");
    Query findDatabaseUserQuery = session.getNamedQuery("findDatabaseUser");
    @FXML
    protected HBox banner;
    @FXML
    protected PasswordField passwordField;
    @FXML
    protected PasswordField repPasswordField;
    @FXML
    protected BorderPane BPane;
    @FXML
    protected TextField PESELField;
    @FXML
    protected TextField buildingField;
    @FXML
    protected TextField cityField;
    @FXML
    protected TextField emailField;
    @FXML
    protected VBox logInForm;
    @FXML
    protected TextField nameField;
    @FXML
    protected TextField phoneField;
    @FXML
    protected TextField postCityField;
    @FXML
    protected TextField postCodeField;
    @FXML
    protected Button registerButton;
    @FXML
    protected TextField streetField;
    @FXML
    protected TextField surnameField;

    private static String registeredCredentials;

    public static String getRegisteredCredentials() {
        return registeredCredentials;
    }

    private void showErrorAlert(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    void register() {
        Transaction transaction;
//        TEST ONLY
        nameField.setText("Adam");
        surnameField.setText("Nowak");
        PESELField.setText("12345678901");
        passwordField.setText("pass");
        repPasswordField.setText("pass");

        if (nameField.getText() == null || nameField.getText().trim().equals("") || surnameField.getText() == null
                || surnameField.getText().trim().equals("") || PESELField.getText() == null
                || PESELField.getText().trim().equals("") || passwordField.getText() == null
                || passwordField.getText().trim().equals("") || repPasswordField.getText() == null
                || repPasswordField.getText().trim().equals("")) {
            showErrorAlert("Błąd zapisu", "Nie wypełniono wymaganych pól",
                    "Pola \"imię\", \"nazwisko\", \"PESEL\", \"hasło\" i \"powtórz hasło\" są wymagane.");
        }else if(!passwordField.getText().trim().equals(repPasswordField.getText().trim())){
            showErrorAlert("Błąd zapisu", "Niezgodne hasła",
                    "Pola \"hasło\" i \"powtórz hasło\" muszą być takie same.");
        } else {
            try {
                transaction = session.beginTransaction();
                String internalName;

                // XXX
                while (true) {
                    int random = ThreadLocalRandom.current().nextInt(1000, 10000);
                    internalName = "u" + nameField.getText().charAt(0)
                            + surnameField.getText().charAt(0) + random;
                    findDatabaseUserQuery.setParameter("rolname", internalName);
                    if (findDatabaseUserQuery.getResultList().size() == 0) break;
                }

                createDatabaseUserQuery.setParameter("userName", internalName);
                createDatabaseUserQuery.setParameter("password", passwordField.getText().trim());

                User newUser = new User();
                newUser.setDatabaseUsername(internalName);
                newUser.setEmail((emailField.getText() == null) ? null : emailField.getText().trim());
                newUser.setName(nameField.getText().trim());
                newUser.setPhone((phoneField.getText() == null) ? null : phoneField.getText().trim());
                newUser.setRole(User.Role.PATIENT);
                newUser.setSurname(surnameField.getText().trim());

                createPatientQuery.setParameter("building", (buildingField.getText() == null)
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : buildingField.getText().trim());
                createPatientQuery.setParameter("city", (cityField.getText() == null)
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : cityField.getText().trim());
                createPatientQuery.setParameter("pesel", PESELField.getText().trim());
                createPatientQuery.setParameter("post_city", (postCityField.getText() == null)
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : postCityField.getText().trim());
                createPatientQuery.setParameter("post_code", (postCodeField.getText() == null)
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : postCodeField.getText().trim());
                createPatientQuery.setParameter("street", (streetField.getText() == null)
                        ? new TypedParameterValue(StandardBasicTypes.STRING, null)
                        : streetField.getText().trim());
                createPatientQuery.setParameter("id", newUser.getId());

                session.persist(newUser);
                createDatabaseUserQuery.executeUpdate();
                createPatientQuery.executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction = session.getTransaction();
                if (transaction.isActive()) transaction.rollback();
                e.printStackTrace();
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Błąd zapisu");
//                alert.setHeaderText("Niepoprawny format godziny.");
//                alert.setContentText("Poprawne formaty: gg:mm lub gg:mm:ss");
//                alert.showAndWait();
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        if(context.length != 0){
            BPane.getChildren().remove(banner);
            registerButton.setText("Zarejestruj");
        }
    }

    @Override
    public void refresh() {

    }
}
