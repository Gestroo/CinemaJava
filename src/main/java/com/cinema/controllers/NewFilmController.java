package com.cinema.controllers;

import com.cinema.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.Dialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class NewFilmController implements Initializable {
    Connection conn;
    Statement stmt;

    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
            //conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextArea descriptionTA;
    @FXML
    private JFXButton toFilmsButton;
    @FXML
    private JFXButton toMenuButton;
    @FXML
    private TextField titleTF;
    @FXML
    private TextField durationTF;
    @FXML
    private DatePicker datestartDP;
    @FXML
    private DatePicker datefinishDP;
    @FXML
    private JFXComboBox restrictionCB;
    @FXML
    private JFXComboBox genreCB;
    ObservableList<String> restrictionOptions =
            FXCollections.observableArrayList("0+",
                    "6+",
                    "12+",
                    "16+",
                    "18+"
            );
    private ObservableList<String> titlesData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        restrictionCB.getItems().addAll(restrictionOptions);
        restrictionCB.setVisibleRowCount(5);
        try {
            ResultSet rs = stmt.executeQuery("Select title From genre order by title");
            while (rs.next()) {
                titlesData.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        genreCB.getItems().addAll(titlesData);
        genreCB.setVisibleRowCount(7);
    }

    @FXML
    private void AddFilmButtonClick(ActionEvent event) {
        if (titleTF.getText()=="" || durationTF.getText()=="" || genreCB.getValue()==null || restrictionCB.getValue() == null || datestartDP.getValue()==null ||datefinishDP.getValue()==null)
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните необходимые поля");

            alert.showAndWait();
            return;
        }
        Date date = new Date();
        if (datefinishDP.getValue().compareTo(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())<=0||
        datestartDP.getValue().compareTo(datefinishDP.getValue())>=0)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, введите корректные даты");

            alert.showAndWait();
            return;
        }
        try {
            int i = Integer.parseInt(durationTF.getText());
        }
        catch (Exception e)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");

            alert.setHeaderText(null);
            alert.setContentText("В поле \"Длительность\" нужно вписать число");

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
                PreparedStatement ps = conn.prepareStatement("INSERT INTO film(title,duration,genre_id,restriction,datestart,datefinish,description) VALUES(?,?,?,?,?,?,?);");
                ps.setString(1, titleTF.getText());
                ps.setInt(2, Integer.parseInt(durationTF.getText()));
                ps.setInt(3, getIdByTitle(genreCB.getValue().toString()));
                ps.setInt(4, Integer.parseInt(restrictionCB.getValue().toString().substring(0, restrictionCB.getValue().toString().length() - 1)));
                ps.setObject(5, datestartDP.getValue());
                ps.setObject(6, datefinishDP.getValue());
                ps.setString(7, descriptionTA.getText());
                ps.addBatch();
                ps.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            showSuccessAlert();
        }
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Успех");

        alert.setHeaderText(null);
        alert.setContentText("Фильм добавлен успешно");

        alert.showAndWait();
    }

    @FXML
    private void ToFilmsButtonClick(ActionEvent event) throws IOException, SQLException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("filmsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        Stage stage = (Stage) toFilmsButton.getScene().getWindow();
        conn.close();
        stage.close();
        newWindow.show();
    }

    @FXML
    private void toMenuButtonClick() throws IOException, SQLException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        Stage stage =(Stage) toMenuButton.getScene().getWindow();
        conn.close();
        stage.close();
        newWindow.show();
    }

    private int getIdByTitle(String title) {

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