package com.cinema.controllers;

import com.cinema.entity.Film;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.jfoenix.controls.*;

import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;

public class FilmEditController implements Initializable {

    public FilmEditController(Film film) {
        this._film = film;
    }

    private Film _film;

    Connection conn;
    Statement stmt;

    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
          //  conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> titlesData = FXCollections.observableArrayList();
    ObservableList<String> restrictionOptions =
            FXCollections.observableArrayList("0+",
                    "6+",
                    "12+",
                    "16+",
                    "18+"
            );

    @FXML
    private JFXComboBox filmRestrictionCB;
    @FXML
    private JFXComboBox filmGenreCB;
    @FXML
    private TextField filmTitleTF;
    @FXML
    private TextField filmDurationTF;
    @FXML
    private DatePicker filmDateStartDP;
    @FXML
    private DatePicker filmDateFinishDP;
    @FXML
    private TextArea filmDescriptionTA;
    @FXML
    private JFXButton saveChangesButton;
    @FXML
    private JFXButton backToFilmsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRestriction();
        loadGenres();
        loadFilmData();
    }

    @FXML
    private void backToFilmsButtonClick() throws SQLException {
        Stage stage = (Stage) backToFilmsButton.getScene().getWindow();
        stage.close();
        conn.close();
    }

    @FXML
    private void saveChangesButtonClick() {
        if (filmTitleTF.getText()=="" || filmDurationTF.getText()=="")
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните необходимые поля");

            alert.showAndWait();
            return;
        }
        try {
            int i = Integer.parseInt(filmDurationTF.getText());
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
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
                PreparedStatement ps = conn.prepareStatement("UPDATE film SET title=?, duration=?, genre_id=?,restriction=?,datestart=?,datefinish=?,description=? WHERE id=?");
                ps.setString(1, filmTitleTF.getText());
                ps.setInt(2, Integer.parseInt(filmDurationTF.getText()));
                ps.setInt(3, getIdByTitle(filmGenreCB.getValue().toString()));
                ps.setInt(4, Integer.parseInt(filmRestrictionCB.getValue().toString().substring(0, filmRestrictionCB.getValue().toString().length() - 1)));
                ps.setObject(5, filmDateStartDP.getValue());
                ps.setObject(6, filmDateFinishDP.getValue());
                try {
                    ps.setString(7, filmDescriptionTA.getText());
                } catch (NullPointerException e) {
                    ps.setString(7, "");
                }
                ps.setInt(8, _film.getId());
                ps.addBatch();
                ps.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            showSuccessAlert();
            Stage stage = (Stage) saveChangesButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");

        alert.setHeaderText(null);
        alert.setContentText("Информация обновлена успешно");
        alert.showAndWait();
    }

    private void loadRestriction() {
        filmRestrictionCB.getItems().addAll(restrictionOptions);
        filmRestrictionCB.setVisibleRowCount(5);
    }

    private void loadGenres() {
        try {
            ResultSet rs = stmt.executeQuery("Select title From genre order by title");
            while (rs.next()) {
                titlesData.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        filmGenreCB.getItems().addAll(titlesData);
        filmGenreCB.setVisibleRowCount(7);
    }

    private void loadFilmData() {
        filmTitleTF.setText(_film.getTitle());
        filmGenreCB.getSelectionModel().select(_film.getGenre());
        filmRestrictionCB.getSelectionModel().select(_film.getRestriction());
        filmDurationTF.setText(String.valueOf(_film.getDuration()));
        filmDateStartDP.setValue(Instant.ofEpochMilli(_film.getDateStart().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        filmDateFinishDP.setValue(Instant.ofEpochMilli(_film.getDateFinish().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        if (_film.getDescription() != null)
            filmDescriptionTA.setText(_film.getDescription());
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
