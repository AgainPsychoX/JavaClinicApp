package com.example.przychodnialocal.patient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MyDataController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;

    @FXML
    void editData(ActionEvent event) {
        nameTextField.setDisable(false);
        surnameTextField.setDisable(false);
    }


}
