package com.example.przychodnialocal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("patient/main-view.fxml"));
//
//        if dane admina takie to
//            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("adminPackage/notifications.fxml"));
//        else if dane pacjenta takie to
//            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("patient/main-view.fxml"));
//        else "zle dane"


        Scene scene = new Scene(fxmlLoader.load(), 1440, 1024);
        stage.setTitle("Przychodnia");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}