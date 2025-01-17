module pl.edu.ur.pz.clinicapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires org.controlsfx.controls;
    requires java.naming;
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires html2pdf;
    requires freemarker;
    requires javafx.swing;
    requires itextpdf;
    requires com.google.guice;
    requires javax.inject;
    requires javafaker;
    requires commons.lang3;

    exports pl.edu.ur.pz.clinicapp;
    exports pl.edu.ur.pz.clinicapp.controls;
    exports pl.edu.ur.pz.clinicapp.models;
    exports pl.edu.ur.pz.clinicapp.utils;
    exports pl.edu.ur.pz.clinicapp.utils.javafx;
    exports pl.edu.ur.pz.clinicapp.utils.views;

    opens pl.edu.ur.pz.clinicapp to javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.controls to javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.dialogs to javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.models to org.hibernate.orm.core;
    opens pl.edu.ur.pz.clinicapp.utils to org.hibernate.orm.core, javafx.fxml;
    opens pl.edu.ur.pz.clinicapp.utils.javafx to javafx.fxml, org.hibernate.orm.core;
    opens pl.edu.ur.pz.clinicapp.utils.views to javafx.fxml, org.hibernate.orm.core;
    opens pl.edu.ur.pz.clinicapp.views to javafx.fxml;
}