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
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML protected VBox vBox;
    @FXML protected TableView<User> table;
    @FXML protected TableColumn<User, Integer> idCol;
    @FXML protected TableColumn<User, String> nameCol;
    @FXML protected TableColumn<User, String> surnameCol;
    @FXML protected TableColumn<User, String> emailCol;
    @FXML protected TableColumn<User, String> phoneCol;
    @FXML protected ComboBox<String> accountTypeComboBox;
    @FXML protected Button detailsButton;
    @FXML protected TextField searchTextField;

    protected ObservableList<User> users = FXCollections.observableArrayList();
    protected FilteredList<User> filteredUsers = new FilteredList<>(users, b -> true);
    protected PauseTransition searchDebounce;
    Session session = ClinicApplication.getEntityManager().unwrap(Session.class);
    Query query = session.getNamedQuery("users");
    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        table.getSelectionModel().clearSelection();

        idCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getId()));
        nameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
        surnameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSurname()));
        emailCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getEmail()));
        phoneCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPhone()));
        table.getSelectionModel().selectedItemProperty().addListener(observable ->
                detailsButton.setDisable(table.getSelectionModel().getSelectedItem() == null));

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

        refresh();
    }

    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        List results = query.getResultList();
        for (Object presElem : results){
            ClinicApplication.getEntityManager().refresh(presElem);
        }
        users.setAll(query.getResultList());
        table.setItems(users);
    }

    @FXML
    protected void filterTable(){
        String selectedItem = accountTypeComboBox.getSelectionModel().getSelectedItem();
        switch (selectedItem) {
            case "Wszyscy" -> query = session.getNamedQuery("findAllUsers");
            case "Lekarze" -> query = session.getNamedQuery("findFilteredUsers")
                    .setParameter("role", "DOCTOR");
            case "Pacjenci" -> query = session.getNamedQuery("findFilteredUsers")
                    .setParameter("role", "PATIENT");
            case "Pielęgniarki" -> query = session.getNamedQuery("findFilteredUsers")
                    .setParameter("role", "NURSE");
            case "Recepcja" -> query = session.getNamedQuery("findFilteredUsers")
                    .setParameter("role", "RECEPTION");
        }
        refresh();
        }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountTypeComboBox.getItems().addAll("Wszyscy", "Lekarze", "Pacjenci", "Pielęgniarki", "Recepcja");
    }

    @FXML
    public void displayDetails(){
        this.getParentController().goToView(MainWindowController.Views.ACCOUNT_DETAILS,
                AccountDetailsView.AccMode.DETAILS, table.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void addUser(){
        this.getParentController().goToView(MainWindowController.Views.ACCOUNT_DETAILS,
                AccountDetailsView.AccMode.CREATE, ClinicApplication.getUser());
    }

    @FXML
    public void searchAction(ActionEvent event) {
        searchDebounce.stop();
        table.getSelectionModel().clearSelection();
        final var text = searchTextField.getText().toLowerCase();
        filteredUsers.setPredicate(user -> {
            if(text.isBlank()) return true;
            if(user.getRole().toString().toLowerCase().contains(text.trim())) return true;
            if(user.getId().toString().toLowerCase().contains(text.trim())) return true;
            if(user.getName().toLowerCase().contains(text.trim())) return true;
            if(user.getSurname().toLowerCase().contains(text.trim())) return true;

            //Temporary - some users don't have email and phone resulting in error
            if(user.getEmail() != null && user.getPhone() != null) {
                if(user.getPhone().toLowerCase().contains(text.trim())) return true;
                return user.getEmail().toLowerCase().contains(text.trim());
            }
            return false;
        });

        SortedList<User> sortedUsers = new SortedList<>(filteredUsers);
        sortedUsers.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedUsers);
    }

}

