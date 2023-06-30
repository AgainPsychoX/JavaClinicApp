package pl.edu.ur.pz.clinicapp.views;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.Notification;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.ReportObject;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class NotificationsView extends ChildControllerBase<MainWindowController> implements Initializable {

    @FXML protected TextField searchTextField;
    @FXML protected TableView<Notification> table;
    @FXML protected TableColumn<Notification, ZonedDateTime> dateCol;
    @FXML protected TableColumn<Notification, String> fromCol;
    @FXML protected TableColumn<Notification, String> contentCol;
    @FXML protected TableColumn<Notification, String> readCol;
    @FXML protected Button markReadButton;
    @FXML protected Button markUnreadButton;
    @FXML protected Button deleteButton;

    protected ObservableList<Notification> notification = FXCollections.observableArrayList();
    protected FilteredList<Notification> filteredNotifications = new FilteredList<>(notification, b -> true);

    protected PauseTransition searchDebounce;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Transaction transaction = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSentDate().atZone(ZoneId.systemDefault())));
        fromCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSourceUser().getDisplayName()));
        contentCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getContent()));
        readCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(wasReadPL(features.getValue().wasRead())));

        table.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (table.getSelectionModel().getSelectedItem() == null){
                deleteButton.setDisable(true);
                markUnreadButton.setDisable(true);
                markReadButton.setDisable(true);
            }else {
                deleteButton.setDisable(false);
                markUnreadButton.setDisable(!table.getSelectionModel().getSelectedItem().wasRead());
                markReadButton.setDisable(table.getSelectionModel().getSelectedItem().wasRead());
            }

        });

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

    }

    /**
     * @return all notifications fot current user
     */
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
            if(notification.getSentDate().toString().toLowerCase().contains(text)) return true;
            if(notification.getContent().toLowerCase().contains(text)) return true;
            return false;
        });

        SortedList<Notification> sortedNotification = new SortedList<>(filteredNotifications);
        sortedNotification.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedNotification);
        table.refresh();
    }


    /**
     * If notification not read, sets read date, if read date set, deletes read date.
     * @param event
     */
    @FXML
    protected void readAction(ActionEvent event){

        int id = table.getSelectionModel().getSelectedItem().getId();
        Notification notification = session.get(Notification.class, id);
        transaction = session.beginTransaction();

        if(table.getSelectionModel().getSelectedItem().wasRead()){

            notification.setReadDate(null);
            session.update(notification);
        }else {
            notification.setReadDate(Timestamp.valueOf(LocalDateTime.now()).toInstant());
            session.update(notification);
        }

        transaction.commit();
        refresh();
    }

    /**
     * Deletes selected notification.
     * @param event
     */

    @FXML
    protected void deleteAction(ActionEvent event){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie powiadomienia.");
        alert.setHeaderText("Czy na pewno chcesz usunąć to powiadomienie?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        int id = table.getSelectionModel().getSelectedItem().getId();
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
            transaction = session.beginTransaction();
            Notification notification = session.get(Notification.class, id);
            session.delete(notification);
            transaction.commit();
            refresh();
        } else {
            alert.close();
        }

    }

    /**
     * Localization to polish.
     * @return "Tak" if already read, "Nie" if pending.
     */
    public String wasReadPL(boolean b){
        if (b) {
            return "Tak";
        }else return "Nie";
    }

}
