package pl.edu.ur.pz.clinicapp.views;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Notification;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationsView extends ChildControllerBase<MainWindowController> implements Initializable {

    @FXML protected TextField searchTextField;
    @FXML protected TableView<Notification> table;
    @FXML protected TableColumn<Notification, Date> dateCol;
//    @FXML protected TableColumn<Notification, > categoryCol;
    @FXML protected TableColumn<Notification, String> fromCol;
    @FXML protected TableColumn<Notification, String> contentCol;
    @FXML protected Button markUnreadButton;
    @FXML protected Button deleteButton;

    protected ObservableList<Notification> notification = FXCollections.observableArrayList();
    protected FilteredList<Notification> filteredNotifications = new FilteredList<>(notification, b -> true);

    protected PauseTransition searchDebounce;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSentDate()));
        fromCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSourceUser().getDisplayName()));
        contentCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getContent()));

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

    }

    private List<Notification> getAllNotifications(){
        return ClinicApplication.getUser().getAllReceivedNotifications();

    }

    @Override
    public void populate(Object... context) {
        notification.setAll(getAllNotifications());
        table.getItems().setAll(notification);
    }

    @Override
    public void dispose() {
        super.dispose();
    }


    @Override
    public void refresh() {
        populate();
    }

    @FXML
    protected void searchAction(ActionEvent event) {
        searchDebounce.stop();

        final var text = searchTextField.getText().toLowerCase();
        filteredNotifications.setPredicate(notification -> {
            if(text.isBlank()) return true;
            if(notification.getSourceUser().getName().toLowerCase().contains(text)) return true;
            if(notification.getSourceUser().getSurname().toLowerCase().contains(text)) return true;
            if(notification.getSentDate().toLocalDateTime().toString().toLowerCase().contains(text)) return true;
            if(notification.getContent().toLowerCase().contains(text)) return true;
            return false;
        });

        SortedList<Notification> sortedNotification = new SortedList<>(filteredNotifications);
        sortedNotification.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedNotification);
        table.refresh();
    }

}
