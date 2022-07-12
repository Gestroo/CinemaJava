package com.cinema.controllers;

import com.cinema.Main;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController  implements Initializable {

@FXML
private JFXButton toCatalogButton;
    @FXML
    private JFXButton exitButton;
    Stage stage;
    @FXML
    private void newFilmButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("newFilm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        stage =  (Stage) exitButton.getScene().getWindow();;
        stage.close();
        newWindow.show();
    }
    @FXML
    private void filmViewButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("filmsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        stage =  (Stage) exitButton.getScene().getWindow();
        stage.close();
        newWindow.show();
    }
    @FXML
    private void newSeanceButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("newSeance.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        stage =  (Stage) exitButton.getScene().getWindow();
        stage.close();
        newWindow.show();
    }
    @FXML
    private void toCatalogButtonClick() throws IOException{
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("catalogs.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        stage =  (Stage) exitButton.getScene().getWindow();
        stage.close();
        newWindow.show();
    }
    @FXML
    private void seanceViewButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("seanceView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        stage =  (Stage) exitButton.getScene().getWindow();
        stage.close();
        newWindow.show();
    }
    @FXML
    private void exitButtonClick(){
        stage =  (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
