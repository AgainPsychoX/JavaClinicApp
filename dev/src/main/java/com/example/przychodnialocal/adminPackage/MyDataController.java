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

public class MyDataController {

    @FXML
    private AnchorPane contentAnchorPane;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField surnameTextField;


    @FXML
    void notificationsButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("notifications.fxml"));
        Parent newView = loader.load();
        contentAnchorPane.getChildren().setAll(newView);

    }

    @FXML
    void editButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit/edit-default.fxml"));
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
    void innerEditButton(ActionEvent event) throws IOException {
        nameTextField.setDisable(false);
        surnameTextField.setDisable(false);
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
//        TODO
    }

}
