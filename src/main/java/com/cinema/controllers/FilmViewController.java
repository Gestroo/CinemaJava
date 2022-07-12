package com.cinema.controllers;

import com.cinema.Main;
import com.cinema.entity.Film;
import com.cinema.entity.Genre;
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
import java.util.ResourceBundle;

public class FilmViewController implements Initializable {

    Film film;
    Connection conn;
    Statement stmt;

    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
          //  conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            stmt = conn.createStatement();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    private String sql = "SELECT film.id,film.title,g.title AS genre, duration, restriction, datestart,datefinish FROM film JOIN genre g ON g.id = film.genre" +
            "WHERE film.title LIKE ? AND g.title LIKE ? AND duration BETWEEN ? AND ? AND restriction >= ? ";

    private ObservableList<String> titlesData = FXCollections.observableArrayList();
    private ObservableList<Film> filmsData = FXCollections.observableArrayList();
    @FXML
    private TableView<Film> tableFilms;
    @FXML
    private TableColumn<Film, String> titleCol;
    @FXML
    private TableColumn<Genre, String> genreCol;
    @FXML
    private TableColumn<Film, Integer> durationCol;
    @FXML
    private TableColumn<Film, String> restrictionCol;
    @FXML
    private TableColumn<Film, String> dateStartCol;
    @FXML
    private TableColumn<Film, String> dateFinishCol;
    @FXML
    private JFXButton toMenuButton;
    @FXML
    private JFXComboBox restrictionCB;
    @FXML
    private JFXComboBox genreCB;
    @FXML
    private TextField filmTitleTF;
    @FXML
    private TextField minDurationTF;
    @FXML
    private TextField maxDurationTF;
    @FXML
    private DatePicker filmDateDP;
    @FXML
    private JFXButton editFilmButton;

    private void filtersData() {
        String sql = "SELECT film.id,film.title,g.title AS genre, duration, restriction, datestart,datefinish,film.description FROM film JOIN genre g ON g.id = film.genre_id " +
                "WHERE film.title LIKE ? AND g.title LIKE ? AND duration BETWEEN ? AND ? AND restriction BETWEEN ? AND ?";

        String title = "";
        String genre = "";
        int restriction = -1;
        int minDuration = 0;
        int maxDuration = Integer.MAX_VALUE;
        LocalDate date = null;
        String dateStart = "datestart";
        String dateFinish = "datefinish";


        if (filmTitleTF.getText() == "")
            title = "%";
        else title = "%" + filmTitleTF.getText() + "%";

        if (genreCB.getValue() == null)
            genre = "%";
        else genre = genreCB.getValue().toString();

        if (minDurationTF.getText() != "")
            minDuration = Integer.parseInt(minDurationTF.getText());

        if (maxDurationTF.getText() != "")
            maxDuration = Integer.parseInt(maxDurationTF.getText());

        if (restrictionCB.getValue() != null)
            restriction = Integer.parseInt(restrictionCB.getValue().toString().substring(0, restrictionCB.getValue().toString().length() - 1));
        if (filmDateDP.getValue() != null) {
            date = filmDateDP.getValue();
            sql += "AND ? BETWEEN datestart AND datefinish";
        }
        try {
            filmsData.clear();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, genre);
            ps.setInt(3, minDuration);
            ps.setInt(4, maxDuration);
            ps.setInt(5, restriction);
            if (restriction == -1)
                ps.setInt(6, 19);
            else ps.setInt(6, restriction);
            if (filmDateDP.getValue() != null)
                ps.setDate(7, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("id"));
                film.setTitle(rs.getString("title"));
                film.setGenre(rs.getString("genre"));
                film.setDuration(rs.getInt("duration"));
                film.setRestriction(rs.getInt("restriction") + "+");
                film.setDateStart(rs.getDate("datestart"));
                film.setDateFinish(rs.getDate("datefinish"));
                film.setDescription(rs.getString("description"));
                filmsData.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editFilmButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("filmEdit.fxml"));
        fxmlLoader.setController(new FilmEditController(film));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        newWindow.show();
    }

    @FXML
    private void clearFiltersButtonClick() {
        filmsData.clear();
        filmTitleTF.clear();
        minDurationTF.clear();
        maxDurationTF.clear();
        genreCB.getSelectionModel().clearSelection();
        restrictionCB.getSelectionModel().clearSelection();
        filmDateDP.getEditor().clear();
        editFilmButton.setDisable(true);
        loadData();
        tableFilms.setItems(filmsData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGenres();
        loadRestriction();
        loadFilms();
        loadData();
        filmTitleTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtersData();
        });
        minDurationTF.textProperty().addListener((observableValue, s, t1) -> {
            filtersData();
        });
        maxDurationTF.textProperty().addListener((observableValue, s, t1) -> {
            filtersData();
        });
        filmDateDP.setOnAction(event -> filtersData());
        tableFilms.setRowFactory(rw -> {
            TableRow<Film> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getClickCount() == 1) {
                    this.film = row.getItem();
                    editFilmButton.setDisable(false);
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
    }

    private void loadRestriction() {
        restrictionCB.getItems().addAll("0+",
                "6+",
                "12+",
                "16+",
                "18+");
        restrictionCB.setVisibleRowCount(5);
        restrictionCB.setOnAction(event -> filtersData()
        );
    }

    private void loadFilms() {
        titleCol.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
        genreCol.setCellValueFactory(new PropertyValueFactory<Genre, String>("genre"));
        durationCol.setCellValueFactory(new PropertyValueFactory<Film, Integer>("duration"));
        restrictionCol.setCellValueFactory(new PropertyValueFactory<Film, String>("restriction"));
        dateStartCol.setCellValueFactory(new PropertyValueFactory<Film, String>("dateStart"));
        dateFinishCol.setCellValueFactory(new PropertyValueFactory<Film, String>("dateFinish"));
        tableFilms.setItems(filmsData);
    }

    private void loadData() {
        try {
            filmsData.clear();
            ResultSet rs = stmt.executeQuery("select film.id,film.title,g.title as genre, duration, restriction, datestart,datefinish from film join genre g on g.id = film.genre_id");
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("id"));
                film.setTitle(rs.getString("title"));
                film.setGenre(rs.getString("genre"));
                film.setDuration(rs.getInt("duration"));
                film.setRestriction(rs.getInt("restriction") + "+");
                film.setDateStart(rs.getDate("datestart"));
                film.setDateFinish(rs.getDate("datefinish"));
                filmsData.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    private void addSeanceButtonClick(){}
    private Genre setGenreByTitle(String title){
        try {
            PreparedStatement ps = conn.prepareStatement("select id,title,description from genre where title=?");
            ps.setString(1,title);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setTitle(rs.getString("title"));
                genre.setDesc(rs.getString("description"));
                return genre;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTitleById(int id) {

        try {
            PreparedStatement ps = conn.prepareStatement("select title from genre where id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString("title");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
