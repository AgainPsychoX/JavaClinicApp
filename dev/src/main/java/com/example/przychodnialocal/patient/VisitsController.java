package com.example.przychodnialocal.patient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class VisitsController {

    @FXML
    private Button returnButton;


    @FXML
    private Label visitsLabel;

    @FXML
    void createVisit(ActionEvent event) {

    }

    @FXML
    void showCurrentVisits(ActionEvent event) {
        returnButton.setVisible(false);
        returnButton.setDisable(true);
        visitsLabel.setText("Nadchodzące wizyty");
    }

    @FXML
    void showOldVisits(ActionEvent event) {
        returnButton.setVisible(true);
        returnButton.setDisable(false);
        visitsLabel.setText("Wizyty archiwalne");
    }

}
