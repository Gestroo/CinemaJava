package com.cinema.controllers;

import com.cinema.Main;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class NewGenreController {

    @FXML
    private JFXButton toMenuButton;
    @FXML
    private TextField titleTF;
    @FXML
    private TextArea descriptionTA;
    @FXML
    private JFXButton addGenreButton;

    @FXML
    private void addGenreButtonClick(){
        if (titleTF.getText()=="")
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните необходимые поля");

            alert.showAndWait();
            return;
        }
        try {
           Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
         //  Connection conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            PreparedStatement ps = conn.prepareStatement("INSERT INTO genre(title, description) VALUES (?,?)");
            ps.setString(1,titleTF.getText());
            try {
                ps.setString(2, descriptionTA.getText());
            } catch (NullPointerException e) {
                ps.setString(2, "");
            }
            ps.addBatch();
            ps.executeBatch();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        showSuccessAlert();
        Stage stage = (Stage) addGenreButton.getScene().getWindow();
        stage.close();
        GenreCatalogController.loadData();
    }
    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");

        alert.setHeaderText(null);
        alert.setContentText("Жанр добавлен успешно");

        alert.showAndWait();
    }

    @FXML
    private void toMenuButtonClick() throws IOException {
        Stage stage = (Stage) toMenuButton.getScene().getWindow();
        stage.close();
    }

}
