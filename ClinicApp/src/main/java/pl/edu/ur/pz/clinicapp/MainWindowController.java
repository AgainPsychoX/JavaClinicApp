package pl.edu.ur.pz.clinicapp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.edu.ur.pz.clinicapp.dialogs.RegisterDialog;
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.views.ViewController;
import pl.edu.ur.pz.clinicapp.utils.views.ViewsContainerController;
import pl.edu.ur.pz.clinicapp.views.*;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainWindowController extends ViewsContainerController {
    private static final Logger logger = Logger.getLogger(MainWindowController.class.getName());

    private static URL getViewResource(String resourcePath) {
        return ClinicApplication.class.getResource(resourcePath);
    }

    private static final Map<Class<? extends ViewController>, URL> viewToResource = Map.ofEntries(
        Map.entry(NotificationsView.class,          getViewResource("views/NotificationsView.fxml")),
        Map.entry(AccountsView.class,               getViewResource("views/AccountsView.fxml")),
        Map.entry(AccountDetailsView.class,         getViewResource("views/AccountDetailsView.fxml")),
        Map.entry(VisitsView.class,                 getViewResource("views/VisitsView.fxml")),
        Map.entry(VisitsDetailsView.class,          getViewResource("views/VisitsDetailsView.fxml")),
        Map.entry(PatientsView.class,               getViewResource("views/PatientsView.fxml")),
        Map.entry(PatientDetailsView.class,         getViewResource("views/PatientDetailsView.fxml")),
        Map.entry(ReferralsView.class,              getViewResource("views/ReferralsView.fxml")),
        Map.entry(ReferralDetailsView.class,        getViewResource("views/ReferralDetailsView.fxml")),
        Map.entry(PrescriptionsView.class,          getViewResource("views/PrescriptionsView.fxml")),
        Map.entry(PrescriptionDetailsView.class,    getViewResource("views/PrescriptionDetailsView.fxml")),
        Map.entry(TimetableView.class,              getViewResource("views/TimetableView.fxml")),
        Map.entry(ScheduleView.class,               getViewResource("views/ScheduleView.fxml")),
        Map.entry(RegisterDialog.class,             getViewResource("dialogs/RegisterDialog.fxml")),
        Map.entry(ReportDialog.class,               getViewResource("dialogs/ReportDialog.fxml"))
    );

    @Override
    protected URL getViewResource(Class<? extends ViewController> which) {
        return viewToResource.get(which);
    }

    @FXML private BorderPane contentPane;
    @FXML private VBox navigationButtons;
    @FXML private Text loggedAsText;
    @FXML private Text roleText;

    @Override
    protected void setContent(Node node) {
        contentPane.setCenter(node);
        this.updateNavigationMenuButtons();
    }

    public boolean isPreventingClose() {
        final var oldView = getPreviousView();
        if (oldView != null && oldView.controller != null) {
            if (!oldView.controller.onNavigation(null)) {
                return true;
            }
            oldView.controller.dispose();
        }
        return false;
    }

    public Window getWindow() {
        return this.contentPane.getScene().getWindow();
    }

    public Stage getStage() {
        return (Stage) getWindow();
    }

    private Button buttonForNavigationMenu(String text, EventHandler<ActionEvent> action) {
        final var button = new Button(text);
        button.getStyleClass().add("navigation-menu-button");
        button.setOnAction(action);
        return button;
    }

    private Button buttonForLogout() {
        final var button = new Button("Wyloguj się");
        button.getStyleClass().addAll("navigation-menu-button", "log-out");
        button.setOnAction((e) -> {
            if (isPreventingClose()) {
                logger.fine("Log-out via button cancelled");
                return;
            }

            // Log-out is necessary here, even tho there already is `setOnCloseRequest` for the stage,
            // as it just catches window event.
            ClinicApplication.logOut();
            getStage().close();
        });
        return button;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        // Update displayed names if any
        final var loggedInUser = ClinicApplication.requireUser();
        final var displayName = loggedInUser.getDisplayName();
        final var role = loggedInUser.getRole();
        if (displayName != null) {
            loggedAsText.setText("Zalogowany jako " + displayName);
        }
        roleText.setText(loggedInUser.getRole().localizedName());

        // Populate navigation menu
        {
            final var c = navigationButtons.getChildren();

            // TODO: notifications button should include red dot when there are any unread
            c.add(buttonForNavigationMenu("Powiadomienia", (e) -> goToView(NotificationsView.class)));
            if (!role.isGroupUser()) {
                c.add(buttonForNavigationMenu("Moje dane", (e) -> goToView(AccountDetailsView.class, loggedInUser)));
            }

            if (role == User.Role.DOCTOR) {
                c.add(buttonForNavigationMenu("Terminarz", (e) -> goToView(ScheduleView.class)));
            }

            if (role == User.Role.PATIENT) {
                c.add(buttonForNavigationMenu("Wizyty", (e) -> goToView(VisitsView.class, loggedInUser)));
                c.add(buttonForNavigationMenu("Recepty", (e) -> goToView(PrescriptionsView.class)));
                c.add(buttonForNavigationMenu("Skierowania", (e) -> goToView(ReferralsView.class)));
            } else if (role == User.Role.NURSE) {
                c.add(buttonForNavigationMenu("Skierowania", (e) -> goToView(ReferralsView.class)));
            } else if (role == User.Role.RECEPTION) {
                c.add(buttonForNavigationMenu("Pacjenci", (e) -> goToView(PatientsView.class)));
            } else {
                c.add(buttonForNavigationMenu("Wizyty", (e) -> goToView(VisitsView.class)));
                c.add(buttonForNavigationMenu("Recepty", (e) -> goToView(PrescriptionsView.class)));
                c.add(buttonForNavigationMenu("Skierowania", (e) -> goToView(ReferralsView.class)));
                c.add(buttonForNavigationMenu("Pacjenci", (e) -> goToView(PatientsView.class)));
            }

            if (role == User.Role.ADMIN) {
                c.add(buttonForNavigationMenu("Zarządzanie kontami", (e) -> goToView(AccountsView.class)));
                c.add(buttonForNavigationMenu("Raporty", (e) -> goToView(ReportDialog.class)));
            }

            c.add(buttonForLogout());
        }

        // Choose initial view
        // TODO: happy welcome/dashboard screen? even if user would only see it once
        // TODO: if pending notifications, start with notification view
        switch (role) {
            case PATIENT -> goToView(VisitsView.class);
            case DOCTOR -> goToView(ScheduleView.class);
            default -> goToView(NotificationsView.class);
        }
    }

    private void updateNavigationMenuButtons() {
        // TODO: update 'active' class (CSS)
    }
}
