package com.cinema.controllers;

import com.cinema.entity.Genre;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class GenreEditController  implements Initializable {
    public GenreEditController(Genre genre) {
        this._genre = genre;
    }
    private Genre _genre;
    Connection conn;

    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
            //conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private JFXButton toMenuButton;
    @FXML
    private TextField titleTF;
    @FXML
    private TextArea descriptionTA;
    @FXML
    private JFXButton editGenreButton;

    @FXML
    private void editGenreButtonClick(){
        if (titleTF.getText()=="")
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните необходимые поля");

            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Кинотеатр Премьер");
        alert.setHeaderText("Подтверждение");
        alert.setContentText("Сохранить изменения?");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {
            try {

                PreparedStatement ps = conn.prepareStatement("UPDATE genre SET title=?, description=? where id=?");
                ps.setString(1, titleTF.getText());
                try {
                    ps.setString(2, descriptionTA.getText());
                } catch (NullPointerException e) {
                    ps.setString(2, "");
                }
                ps.setInt(3, getGenreIdByTitle(_genre.getTitle()));
                ps.addBatch();
                ps.executeBatch();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            showSuccessAlert();
            Stage stage = (Stage) editGenreButton.getScene().getWindow();
            stage.close();
            GenreCatalogController.loadData();
        }
    }
    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");

        alert.setHeaderText(null);
        alert.setContentText("Информация обновлена успешно");

        alert.showAndWait();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGenreData();
    }
    private void loadGenreData(){
    titleTF.setText(_genre.getTitle());
    descriptionTA.setText(_genre.getDesc());
    }
    @FXML
    private void toMenuButtonClick() throws IOException, SQLException {
        Stage stage = (Stage) toMenuButton.getScene().getWindow();
        conn.close();
        stage.close();
    }
    private int getGenreIdByTitle(String title){
        try {
            PreparedStatement ps = conn.prepareStatement("select id from genre where title=?");
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

