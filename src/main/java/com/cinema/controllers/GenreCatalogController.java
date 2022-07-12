package com.cinema.controllers;

import com.cinema.Main;
import com.cinema.entity.Film;
import com.cinema.entity.Genre;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class GenreCatalogController implements Initializable {

    Genre genre;
    Connection conn;
    static Statement stmt;
    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
           // conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            stmt = conn.createStatement();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private JFXButton toMenuButton;
    @FXML
    private JFXButton editGenreButton;
    @FXML
    private JFXButton addGenreButton;
    @FXML
    private TableView<Genre> tableGenres;
    @FXML
    private TableColumn<Genre,String> titleCol;
    @FXML
    private TableColumn<Genre,String> descriptionCol;

    private static ObservableList<Genre> genresData = FXCollections.observableArrayList();
    @FXML
    private void toMenuButtonClick() throws IOException, SQLException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        Stage stage = (Stage) toMenuButton.getScene().getWindow();
        conn.close();
        stage.close();
        newWindow.show();
    }
    @FXML
    private void editGenreButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("genreEdit.fxml"));
        fxmlLoader.setController(new GenreEditController(genre));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        newWindow.show();
    }
    @FXML
    private void addGenreButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("newGenre.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        newWindow.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTable();
        loadData();
        tableGenres.setRowFactory(rw -> {
            TableRow<Genre> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getClickCount() == 1) {
                    this.genre = row.getItem();
                    editGenreButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void setTable(){
    titleCol.setCellValueFactory(new PropertyValueFactory<Genre,String>("title"));
    descriptionCol.setCellValueFactory(new PropertyValueFactory<Genre,String>("desc"));
    tableGenres.setItems(genresData);
    }
    protected static void loadData(){

        try {
            genresData.clear();
            ResultSet rs = stmt.executeQuery("SELECT id,title,description FROM genre");
            while (rs.next()){
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setTitle(rs.getString("title"));
                genre.setDesc(rs.getString("description"));
            genresData.add(genre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
