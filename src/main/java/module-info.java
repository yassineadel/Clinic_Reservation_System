module com.example.clinicgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;


    opens com.example.clinicgui to javafx.fxml;
    exports com.example.clinicgui;
}