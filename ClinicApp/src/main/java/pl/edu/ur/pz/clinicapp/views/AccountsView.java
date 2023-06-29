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
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AccountsView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML protected VBox vBox;
    @FXML protected TableView<User> table;
    @FXML protected TableColumn<User, Integer> idCol;
    @FXML protected TableColumn<User, String> nameCol;
    @FXML protected TableColumn<User, String> surnameCol;
    @FXML protected TableColumn<User, String> emailCol;
    @FXML protected TableColumn<User, String> phoneCol;
    @FXML protected ComboBox filter;
    @FXML protected Button detailsButton;
    @FXML protected TextField searchTextField;

    protected ObservableList<User> users = FXCollections.observableArrayList();
    protected FilteredList<User> filteredUsers = new FilteredList<>(users, b -> true);
    protected PauseTransition searchDebounce;
    Session session;
    Query currQuery;
    Query allUsers;
    Query allDoctors;
    Query allWorkers;
    Query allPatients;

    User.Role currUserRole;
    private enum filterMode{ALL, DOCTORS, WORKERS, PATIENTS}
    private static final EnumMap<AccountsView.filterMode, String> filteredModeToString = new EnumMap<>(AccountsView.filterMode.class);

    /**
     * Sets available combobox options for filtering {@link User}s
     */
    public void setFilterVals(){
            filter.setItems(FXCollections.observableArrayList(
                    filteredModeToString.get(filterMode.ALL),
                    filteredModeToString.get(filterMode.DOCTORS),
                    filteredModeToString.get(filterMode.WORKERS),
                    filteredModeToString.get(filterMode.PATIENTS)
            ));
            filter.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        idCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getId()));
        nameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
        surnameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getSurname()));
        emailCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getEmail()));
        phoneCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getPhone()));
        table.getSelectionModel().selectedItemProperty().addListener(observable ->
                detailsButton.setDisable(table.getSelectionModel().getSelectedItem() == null));

    }


    @Override
    public void populate(Object... context) {
        session = ClinicApplication.getEntityManager().unwrap(Session.class);
        allUsers = session.getNamedQuery("allUsers");
        allDoctors = session.getNamedQuery("allDoctors");
        allWorkers = session.getNamedQuery("allWorkers");
        allPatients = session.getNamedQuery("allPatients");

        table.getSelectionModel().clearSelection();

        searchDebounce = new PauseTransition(Duration.millis(250));
        searchDebounce.setOnFinished(this::searchAction);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchDebounce.playFromStart());

        filteredModeToString.put(filterMode.ALL, "Wszyscy użytkownicy");
        filteredModeToString.put(filterMode.DOCTORS, "Lekarze");
        filteredModeToString.put(filterMode.PATIENTS, "Pacjenci");
        filteredModeToString.put(filterMode.WORKERS, "Pozostali praocownicy");

        currQuery = allUsers;
        setFilterVals();

        if(currQuery == allDoctors) filter.setValue(filteredModeToString.get(filterMode.ALL));
        else if(currQuery == allPatients) filter.setValue(filteredModeToString.get(filterMode.PATIENTS));
        else if(currQuery == allWorkers) filter.setValue(filteredModeToString.get(filterMode.WORKERS));

        refresh();
    }

    @Override
    public void refresh() {
        table.getSelectionModel().clearSelection();
        List results = currQuery.getResultList();
        for (Object presElem : results){
            ClinicApplication.getEntityManager().refresh(presElem);
        }
        users.setAll(currQuery.getResultList());
        table.setItems(users);

        if (searchTextField.getText() != null && !searchTextField.getText().trim().equals("")) {
            searchAction(new ActionEvent());
        }

    }

    @Override
    public void dispose() {
        super.dispose();
    }


    @FXML
    protected void changeFilter(){
        if(filter.getSelectionModel().getSelectedItem() == null) return;
        if(filter.getSelectionModel().getSelectedItem() == filteredModeToString.get(filterMode.ALL))
            currQuery = allUsers;
        else if (filter.getSelectionModel().getSelectedItem() == filteredModeToString.get(filterMode.DOCTORS))
            currQuery = allDoctors;
        else if(filter.getSelectionModel().getSelectedItem() == filteredModeToString.get(filterMode.PATIENTS))
            currQuery = allPatients;
        else
            currQuery = allWorkers;

        refresh();
    }


    @FXML
    public void displayDetails(){
        this.getParentController().goToView(MainWindowController.Views.ACCOUNT_DETAILS,
                table.getSelectionModel().getSelectedItem(), AccountDetailsView.Mode.VIEW);
    }

    @FXML
    public void addUser(){
        this.getParentController().goToView(MainWindowController.Views.ACCOUNT_DETAILS,
             ClinicApplication.getUser(), AccountDetailsView.Mode.CREATE);
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

