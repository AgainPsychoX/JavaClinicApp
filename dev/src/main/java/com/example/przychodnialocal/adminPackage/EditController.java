package com.example.przychodnialocal.adminPackage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class EditController {

    @FXML
    private Button btResources;

    @FXML
    private AnchorPane contentAnchorPane;

    @FXML
    private Button deleteNotificationsButton;

    @FXML
    private Button examinationsButton;

    @FXML
    private Button myDataButton;

    @FXML
    private Button notificationsButton;

    @FXML
    private TableView<?> notificationsTableView;

    @FXML
    private Button prescriptionsButton;

    @FXML
    private TextField searchNotificationsTextField;

    @FXML
    private AnchorPane topBar;

    @FXML
    private Button unreadButton;

    @FXML
    private Button visitsButton;

    @FXML
    private Button editButton;


    @FXML
    void notificationsButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("notifications.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }

    @FXML
    void editButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-default.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }

    @FXML
    void myDataButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-data.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }

    @FXML
    void editPatient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-data.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }
    @FXML
    void editDoctor(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-doctors.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }
    @FXML
    void editNurse(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-data.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }
    @FXML
    void editReception(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-data.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }

    @FXML
    void editAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-data.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }


    @FXML
    void backButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-default.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
    }
}
