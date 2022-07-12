package com.cinema.controllers;

import com.cinema.Main;
import com.cinema.entity.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class SeanceViewController implements Initializable {

    Seance seance;
    Connection conn;
    Statement stmt;
    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
         //   conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            stmt = conn.createStatement();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<Seance> seanceData = FXCollections.observableArrayList();
    private ObservableList<String> titlesData = FXCollections.observableArrayList();
    @FXML
    private TableView<Seance> tableSeances;
    @FXML
    private TableColumn<Film, String> filmCol;
    @FXML
    private TableColumn<Seance, Date> dateCol;
    @FXML
    private TableColumn<Seance, Time> timeCol;
    @FXML
    private TableColumn<Genre, String> genreCol;
    @FXML
    private TableColumn<Film, String> restrictionCol;
    @FXML
    private TableColumn<Hall, String> hallCol;
    @FXML
    private JFXButton toMenuButton;
    @FXML
    private JFXButton editSeanceButton;
    @FXML
    private JFXComboBox restrictionCB;
    @FXML
    private JFXComboBox genreCB;
    @FXML
    private JFXComboBox hallCB;
    @FXML
    private TextField filmTitleTF;
    @FXML
    private DatePicker dateDP;

    @FXML
    private void editSeanceButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("seanceEdit.fxml"));
        fxmlLoader.setController(new SeanceEditController(seance));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        newWindow.show();
    }
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
    private void clearFiltersButtonClick() {
        seanceData.clear();
        filmTitleTF.clear();
        genreCB.getSelectionModel().clearSelection();
        restrictionCB.getSelectionModel().clearSelection();
        dateDP.getEditor().clear();
        dateDP.setValue(null);
        hallCB.getSelectionModel().clearSelection();
        editSeanceButton.setDisable(true);
        loadData();
        tableSeances.setItems(seanceData);
    }
    private void filtersData(){
        String sql = "select seance.id, f.title as film,seancedate, restriction, h.hallname as hall ,g.title as genre from " +
                "seance join film f on f.id = seance.film_id " +
                "join cinemahall h on h.hallnumber = seance.cinemahall_id " +
                "join genre g on g.id = f.genre_id" +
                " WHERE f.title LIKE ? AND g.title LIKE ? AND h.hallname LIKE ? AND restriction BETWEEN ? AND ?";

        String title = "%";
        String genre = "%";
        int restriction = -1;
        LocalDate date = null;
        String hall ="%";


        if (filmTitleTF.getText() != "")
        title = "%" + filmTitleTF.getText() + "%";

        if (genreCB.getValue() != null)
            genre = genreCB.getValue().toString();

        if (restrictionCB.getValue() != null)
            restriction = Integer.parseInt(restrictionCB.getValue().toString().substring(0, restrictionCB.getValue().toString().length() - 1));

        if (hallCB.getValue()!=null)
            hall = hallCB.getValue().toString();

        if (dateDP.getValue() != null) {
            date = dateDP.getValue();
            sql += " AND date_trunc('day',seancedate) = ?";
        }
        try {
            seanceData.clear();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, genre);
            ps.setString(3,hall);
            ps.setInt(4, restriction);
            if (restriction == -1)
                ps.setInt(5, 19);
            else ps.setInt(5, restriction);
            if (dateDP.getValue() != null)
                ps.setDate(6, java.sql.Date.valueOf((date)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Seance seance = new Seance();
                seance.setId(rs.getInt("id"));
                seance.setFilm(rs.getString("film"));
                seance.setGenre(rs.getString("genre"));
                seance.setSeanceDate(rs.getTimestamp("seancedate").toLocalDateTime());
                seance.setDate(rs.getDate("seancedate"));
                seance.setTime(rs.getTime("seancedate"));
                seance.setRestriction(rs.getInt("restriction") + "+");
                seance.setHall(rs.getString("hall"));
                seanceData.add(seance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGenres();
        loadRestriction();
        loadSeances();
        loadData();
        loadHalls();
        filmTitleTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtersData();
        });
        hallCB.setOnAction(event -> filtersData());
        dateDP.setOnAction(event -> filtersData());
        tableSeances.setRowFactory(rw -> {
            TableRow<Seance> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getClickCount() == 1) {
                    this.seance = row.getItem();
                    editSeanceButton.setDisable(false);
                }
            });
            return row;
        });
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
        genreCB.getItems().addAll(titlesData);
        genreCB.setVisibleRowCount(7);
        genreCB.setOnAction(event -> filtersData()

        );
    } private void loadRestriction() {
        restrictionCB.getItems().addAll("0+",
                "6+",
                "12+",
                "16+",
                "18+");
        restrictionCB.setVisibleRowCount(5);
        restrictionCB.setOnAction(event -> filtersData()
        );
    }
    private void loadSeances() {
        filmCol.setCellValueFactory(new PropertyValueFactory<Film, String>("film"));
        dateCol.setCellValueFactory(new PropertyValueFactory<Seance,Date>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<Seance, Time>("time"));
        genreCol.setCellValueFactory(new PropertyValueFactory<Genre, String>("genre"));
        restrictionCol.setCellValueFactory(new PropertyValueFactory<Film, String>("restriction"));
        hallCol.setCellValueFactory(new PropertyValueFactory<Hall, String>("hall"));
        tableSeances.setItems(seanceData);
    }
    private void loadData() {
        try {
            seanceData.clear();
            ResultSet rs = stmt.executeQuery("select seance.id, f.title as film,seancedate, restriction, h.hallname as hall ,g.title as genre from seance join film f on f.id = seance.film_id join cinemahall h on h.hallnumber = seance.cinemahall_id join genre g on g.id = f.genre_id");
            while (rs.next()) {
                Seance seance = new Seance();
                seance.setId(rs.getInt("id"));
                seance.setFilm(rs.getString("film"));
                seance.setGenre(rs.getString("genre"));
                seance.setSeanceDate(rs.getTimestamp("seancedate").toLocalDateTime());
                seance.setDate(rs.getDate("seancedate"));
                seance.setTime(rs.getTime("seancedate"));
                seance.setRestriction(rs.getInt("restriction") + "+");
                seance.setHall(rs.getString("hall"));
                seanceData.add(seance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadHalls() {
        hallCB.getItems().addAll("Зал 1","Зал 2","Зал 3","Зал 4","Зал 5");
        hallCB.setVisibleRowCount(5);
    }
}
