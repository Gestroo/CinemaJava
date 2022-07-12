package com.cinema.controllers;

import com.cinema.Main;
import com.cinema.entity.Film;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class NewSeanceController implements Initializable {
    Film film;

    boolean isNext=true;
    boolean isPrevious=true;
    Connection conn;
    Statement stmt;

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

    private ObservableList<String> hoursData = FXCollections.observableArrayList();
    private ObservableList<String> minutesData = FXCollections.observableArrayList();
    private ObservableList<String> filmsData = FXCollections.observableArrayList();
    @FXML
    private TextField costTF;
    @FXML
    private JFXComboBox filmTitleCB;
    @FXML
    private JFXComboBox seanceTimeHoursCB;
    @FXML
    private JFXComboBox seanceTimeMinutesCB;
    @FXML
    private DatePicker seanceDateDP;
    @FXML
    private JFXComboBox seanceHallCB;
    @FXML
    private JFXButton addSeanceButton;
    @FXML
    private JFXButton toMenuButton;
    @FXML
    private Label filmDateLabel;
    @FXML
    private Label filmDurationLabel;
    @FXML
    private Label previousSeanceLabel;
    @FXML
    private Label nextSeanceLabel;

    @FXML
    private void addSeanceButtonClick() {
        if (filmTitleCB.getValue()==null|| seanceTimeHoursCB.getValue()==null|| seanceTimeMinutesCB.getValue()==null||seanceHallCB.getValue()==null||seanceDateDP.getValue()==null)
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните необходимые поля");

            alert.showAndWait();
            return;
        }

        if (film.getDateStart().compareTo(Date.valueOf(seanceDateDP.getValue()))>0||
        film.getDateFinish().compareTo(Date.valueOf(seanceDateDP.getValue()))<0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, введите корректную дату");

            alert.showAndWait();
            return;
        }
        try {
            int i = Integer.parseInt(costTF.getText());
        }
        catch (Exception e)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");

            alert.setHeaderText(null);
            alert.setContentText("В поле \"Стоимость\" нужно вписать число");

            alert.showAndWait();
            return;
        }

            Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Кинотеатр Премьер");
        alert.setHeaderText("Подтверждение");
        alert.setContentText("Сохранить изменения?");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

         if (option.get() == ButtonType.OK) {
             String time = seanceTimeHoursCB.getValue().toString()+":"+seanceTimeMinutesCB.getValue().toString()+":00";
             String date = seanceDateDP.getValue().toString();
             try {
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO seance(film_id, cinemahall_id, seancedate,cost) VALUES (?,?,?,?)");
                 ps.setInt(1,getFilmIdByTitle(filmTitleCB.getValue().toString()));
                 ps.setInt(2,getHallIdByTitle(seanceHallCB.getValue().toString()));
                 ps.setTimestamp(3, Timestamp.valueOf((date+" "+time)));
                 ps.setInt(4, Integer.parseInt(costTF.getText()));
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
        alert.setContentText("Сеанс добавлен успешно");

        alert.showAndWait();
    }

    @FXML
    private void toMenuButtonClick() throws IOException, SQLException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        Stage stage = (Stage) toMenuButton.getScene().getWindow();
        stage.close();
        conn.close();
        newWindow.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadFilms();
        loadHours();
        loadMinutes();
        loadHalls();

        filmTitleCB.setOnAction(event ->{film = getFilmByTitle(filmTitleCB.getValue().toString());
            setFilmDates(film);
            setFilmDuration(film);
            findNearSeances();
        } );
        seanceTimeMinutesCB.setOnAction(event -> findNearSeances());
        seanceHallCB.setOnAction(event -> findNearSeances());
        seanceDateDP.setOnAction(event -> findNearSeances());
        seanceTimeHoursCB.setOnAction(event -> findNearSeances());
    }

    private void loadFilms() {
        try {
            ResultSet rs = stmt.executeQuery("select title from film WHERE current_date BETWEEN current_date AND datefinish ORDER BY title");
            while (rs.next()) {
                Film film = new Film();
                film.setTitle(rs.getString("title"));;
                filmsData.add(film.getTitle());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        filmTitleCB.getItems().addAll(filmsData);
        filmTitleCB.setVisibleRowCount(5);
    }

    private void loadHours() {

        for (int i = 0; i <= 23; i++) {
            hoursData.add(String.valueOf(i));
        }
        seanceTimeHoursCB.getItems().addAll(hoursData);
        seanceTimeHoursCB.setVisibleRowCount(5);
    }

    private void loadMinutes() {
        minutesData.add("00");
        minutesData.add("05");
        for (int i = 10; i <= 60; i += 5) {
            minutesData.add(String.valueOf(i));
        }
        seanceTimeMinutesCB.getItems().addAll(minutesData);
        seanceTimeMinutesCB.setVisibleRowCount(5);
    }

    private void loadHalls() {
        seanceHallCB.getItems().addAll("Зал 1","Зал 2","Зал 3","Зал 4","Зал 5");
        seanceHallCB.setVisibleRowCount(5);
    }

    private int getFilmIdByTitle(String title){
        try {
            PreparedStatement ps = conn.prepareStatement("select id from film where title=?");
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
    private int getHallIdByTitle(String title){
        try {
            PreparedStatement ps = conn.prepareStatement("select hallnumber from cinemahall where hallname=?");
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt("hallnumber");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private Film getFilmByTitle(String title){
        try {
            PreparedStatement ps = conn.prepareStatement("select id,title,restriction, duration,datestart,datefinish from film where title=?");
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.setDuration(rs.getInt("duration"));
                film.setDateStart(rs.getDate("datestart"));
                film.setDateFinish(rs.getDate("datefinish"));
                return film;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void setFilmDates(Film film){
        String dates = film.getDateStart().toString()+" - "+film.getDateFinish().toString();
        filmDateLabel.setText(dates);
    }
    private void setFilmDuration(Film film){
        String duration = "Длительность: "+film.getDuration()+" мин";
        filmDurationLabel.setText(duration);
    }

    private void findNearSeances(){
        if (filmTitleCB.getValue()!=null&&seanceHallCB.getValue()!=null&&seanceTimeHoursCB.getValue()!=null&&seanceTimeMinutesCB.getValue()!=null&&seanceDateDP.getValue()!=null){
            String previousSeance;
            String nextSeance;
           String time = ((seanceDateDP.getValue().toString()+" "+(seanceTimeHoursCB.getValue().toString()+":"+seanceTimeMinutesCB.getValue().toString()+":00")));
            String sqlPrev ="SELECT hallname , seancedate as time ,duration from seance " +
                    "join film f on f.id = seance.film_id join cinemahall c on c.hallnumber = seance.cinemahall_id " +
                    "WHERE date_trunc('day',seancedate) = ? AND hallname LIKE ? AND  seancedate <=?  ORDER BY seancedate DESC LIMIT 1";
            String sqlNext ="SELECT hallname , seancedate as time ,duration from seance " +
                    "join film f on f.id = seance.film_id join cinemahall c on c.hallnumber = seance.cinemahall_id " +
                    "WHERE date_trunc('day',seancedate) = ? AND hallname LIKE ? AND  seancedate >=?  ORDER BY seancedate LIMIT 1" ;
            try {
                PreparedStatement ps = conn.prepareStatement(sqlPrev);
                ps.setDate(1, Date.valueOf(seanceDateDP.getValue()));
                ps.setString(2,seanceHallCB.getValue().toString());
                ps.setTimestamp(3, Timestamp.valueOf(time));
                ResultSet rs = ps.executeQuery();
                if (rs.next()){
                    int duration = rs.getInt("duration");
                    Time seancetime = rs.getTime("time");
                    Time seanceEnd = Time.valueOf(seancetime.toLocalTime().plusMinutes(duration+10));
                    Time pickedSeance = Time.valueOf(seanceTimeHoursCB.getValue().toString()+":"+seanceTimeMinutesCB.getValue().toString()+":00");
                    if (seanceEnd.compareTo(pickedSeance)<0) {
                        previousSeanceLabel.setText("Нет предыдущих сеансов");
                        isPrevious=false;
                        checkAddButton();
                    }
                    else{
                        previousSeanceLabel.setText("Ближайшее свободное время: " + seanceEnd.toString());
                        isPrevious = true;
                        checkAddButton();
                    }
                }
                else {
                    previousSeanceLabel.setText("Нет предыдущих сеансов");
                    isPrevious=false;
                    checkAddButton();
                }
                ps = conn.prepareStatement(sqlNext);
                ps.setDate(1, Date.valueOf(seanceDateDP.getValue()));
                ps.setString(2,seanceHallCB.getValue().toString());
                ps.setTimestamp(3, Timestamp.valueOf(time));
                rs = ps.executeQuery();
                if (rs.next()){
                    int duration = film.getDuration();
                    Time seancetime = rs.getTime("time");
                    Time seanceEnd = Time.valueOf(LocalTime.parse(seanceTimeHoursCB.getValue().toString()+":"+seanceTimeMinutesCB.getValue().toString()+":00").plusMinutes(duration+10));
                    if (seanceEnd.compareTo(seancetime)>0) {
                        nextSeanceLabel.setText("Следующий сеанс начнется в " + seancetime.toString().substring(0,seancetime.toString().length()-3));
                        isNext = true;
                        checkAddButton();
                    }
                    else{
                        nextSeanceLabel.setText("Нет следующих сеансов");
                        isNext=false;
                        checkAddButton();
                    }
                }
                else {
                    nextSeanceLabel.setText("Нет следующих сеансов");
                    isNext=false;
                    checkAddButton();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void checkAddButton(){
        if (!isPrevious&&!isNext)
            addSeanceButton.setDisable(false);
        else addSeanceButton.setDisable(true);
    }
}
