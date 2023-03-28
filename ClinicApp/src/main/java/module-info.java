module pl.edu.ur.pz.clinicapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    exports pl.edu.ur.pz.clinicapp;
    exports pl.edu.ur.pz.clinicapp.models;

    opens pl.edu.ur.pz.clinicapp to javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.dialogs to javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.utils to javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.views to javafx.fxml;
}