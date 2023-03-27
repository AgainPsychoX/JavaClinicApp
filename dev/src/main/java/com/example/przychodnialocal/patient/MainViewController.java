package com.example.przychodnialocal.patient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainViewController {

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
    void visitsButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("visits.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
        notificationsButton.setStyle("-fx-background-color: #A4C2FD");
        myDataButton.setStyle("-fx-background-color: #A4C2FD");
        examinationsButton.setStyle("-fx-background-color: #A4C2FD");
        prescriptionsButton.setStyle("-fx-background-color: #A4C2FD");
        visitsButton.setStyle("-fx-background-color:  #284C92");
    }



    @FXML
    void notificationsButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("notifications.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
        visitsButton.setStyle("-fx-background-color: #A4C2FD");
        myDataButton.setStyle("-fx-background-color: #A4C2FD");
        examinationsButton.setStyle("-fx-background-color: #A4C2FD");
        prescriptionsButton.setStyle("-fx-background-color: #A4C2FD");
        notificationsButton.setStyle("-fx-background-color:  #284C92");
    }

    @FXML
    void prescriptionsButton(ActionEvent event) throws  IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("prescriptions.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
        visitsButton.setStyle("-fx-background-color: #A4C2FD");
        notificationsButton.setStyle("-fx-background-color: #A4C2FD");
        myDataButton.setStyle("-fx-background-color: #A4C2FD");
        examinationsButton.setStyle("-fx-background-color: #A4C2FD");
        prescriptionsButton.setStyle("-fx-background-color:  #284C92");
    }

    @FXML
    void examinationsButton(ActionEvent event) throws  IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("prescriptions.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
        notificationsButton.setStyle("-fx-background-color: #A4C2FD");
        visitsButton.setStyle("-fx-background-color: #A4C2FD");
        prescriptionsButton.setStyle("-fx-background-color: #A4C2FD");
        myDataButton.setStyle("-fx-background-color: #A4C2FD");
        examinationsButton.setStyle("-fx-background-color:  #284C92");
    }
    @FXML
    void myDataButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-data.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);
        visitsButton.setStyle("-fx-background-color: #A4C2FD");
        notificationsButton.setStyle("-fx-background-color: #A4C2FD");
        prescriptionsButton.setStyle("-fx-background-color: #A4C2FD");
        examinationsButton.setStyle("-fx-background-color: #A4C2FD");
        myDataButton.setStyle("-fx-background-color:  #284C92");
    }

    public static class MyDataController {

        @FXML
        private Button btResources;

        @FXML
        private Button scheduleButton;

        @FXML
        private Button usersButton;

        @FXML
        private Button visitsButton;

        @FXML
        void accountsButton(ActionEvent event) {

        }

        @FXML
        void myDataButton(ActionEvent event) {

        }

        @FXML
        void notificationsButton(ActionEvent event) {

        }

        @FXML
        void editData(ActionEvent event){
            System.out.println("Edycja");
        }
    }
}
