module com.example.przychodnialocal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.przychodnialocal to javafx.fxml;
    exports com.example.przychodnialocal;
    exports com.example.przychodnialocal.patient;
    opens com.example.przychodnialocal.patient to javafx.fxml;
    exports com.example.przychodnialocal.adminPackage;
    opens com.example.przychodnialocal.adminPackage to javafx.fxml;
}