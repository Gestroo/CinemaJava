module cinema {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.jfoenix;
    requires java.desktop;


    opens com.cinema to javafx.fxml;
    exports com.cinema;
    exports com.cinema.controllers;
    exports com.cinema.entity;
    opens com.cinema.controllers to javafx.fxml;
    opens com.cinema.entity to javafx.fxml;
}