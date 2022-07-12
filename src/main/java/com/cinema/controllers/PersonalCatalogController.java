package com.cinema.controllers;

import com.cinema.Main;
import com.cinema.entity.Film;
import com.cinema.entity.Genre;
import com.cinema.entity.Personal;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PersonalCatalogController implements Initializable {
    Personal personal;
    Connection conn;
    static Statement stmt;

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

    private static ObservableList<Personal> personalData = FXCollections.observableArrayList();
    private ObservableList<String> rolesData = FXCollections.observableArrayList();

    @FXML
    private JFXButton toMenuButton;
    @FXML
    private JFXButton editPersonalButton;
    @FXML
    private JFXButton addPersonalButton;
    @FXML
    private JFXButton clearFiltersButton;
    @FXML
    private JFXComboBox roleCB;
    @FXML
    private TextField lastnameTF;
    @FXML
    private TextField firstnameTF;
    @FXML
    private TextField phonenumberTF;
    @FXML
    private TableView<Personal> tablePersonal;
    @FXML
    private TableColumn<Personal, String> lastnameCol;
    @FXML
    private TableColumn<Personal, String> firstnameCol;
    @FXML
    private TableColumn<Personal, String> middlenameCol;
    @FXML
    private TableColumn<Personal, String> phoneCol;
    @FXML
    private TableColumn<Personal, String> loginCol;
    @FXML
    private TableColumn<Personal, String> roleCol;

    private void filtersData() {
    String sql = "select personal.id as id, lastname,firstname,middlename,phonenumber,login,password,r.personal_role as role from personal " +
            "join role r on r.id = personal.role_id " +
            "where lastname like ? and firstname like ? and phonenumber like ? and r.personal_role like ?";

    String lastname="%";
    String firstname="%";
    String phonenumber="%";
    String role="%";

    if (lastnameTF.getText()!="")
        lastname="%"+lastnameTF.getText()+"%";
    if (firstnameTF.getText()!="")
        firstname="%"+firstnameTF.getText()+"%";
    if (phonenumberTF.getText()!="")
        phonenumber="%"+phonenumberTF.getText()+"%";
    if (roleCB.getValue()!=null)
        role = roleCB.getValue().toString();


        try {
            personalData.clear();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, lastname);
            ps.setString(2, firstname);
            ps.setString(3, phonenumber);
            ps.setString(4, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Personal personal = new Personal();
                personal.setId(rs.getInt("id"));
                personal.setLastname(rs.getString("lastname"));
                personal.setFirstname(rs.getString("firstname"));
                personal.setMiddlename(rs.getString("middlename"));
                personal.setPhonenumber(rs.getString("phonenumber"));
                personal.setLogin(rs.getString("login"));
                personal.setPassword(rs.getString("password"));
                personal.setRole(rs.getString("role"));
                personalData.add(personal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFiltersButtonClick() {
        personalData.clear();
        lastnameTF.clear();
        firstnameTF.clear();
        phonenumberTF.clear();
        roleCB.getSelectionModel().clearSelection();
        editPersonalButton.setDisable(true);
        loadPersonal();
        tablePersonal.setItems(personalData);
    }
    @FXML
    private void addPersonalButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("newPersonal.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        newWindow.setTitle("Кинотеатр Премьер");
        newWindow.setScene(scene);
        newWindow.show();
    }
    @FXML
    private void editPersonalButtonClick() throws IOException {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("personalEdit.fxml"));
        fxmlLoader.setController(new PersonalEditController(personal));
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
        stage.close();
        conn.close();
        newWindow.show();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTable();
        loadPersonal();
        loadRoles();
        setFilters();
    }

    public void loadRoles() {
        try {
            ResultSet rs = stmt.executeQuery("select personal_role from role");
            while (rs.next()) {
                rolesData.add(rs.getString("personal_role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadPersonal() {
        try {
            personalData.clear();
            ResultSet rs = stmt.executeQuery("select personal.id as id, lastname,firstname,middlename,phonenumber,login,password,r.personal_role as role from personal join role r on r.id = personal.role_id");
            while (rs.next()) {
                Personal personal = new Personal();
                personal.setId(rs.getInt("id"));
                personal.setLastname(rs.getString("lastname"));
                personal.setFirstname(rs.getString("firstname"));
                personal.setMiddlename(rs.getString("middlename"));
                personal.setPhonenumber(rs.getString("phonenumber"));
                personal.setLogin(rs.getString("login"));
                personal.setPassword(rs.getString("password"));
                personal.setRole(rs.getString("role"));
                personalData.add(personal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setFilters() {
        roleCB.getItems().addAll(rolesData);
        roleCB.setOnAction(event -> filtersData());
        tablePersonal.setRowFactory(rw -> {
            TableRow<Personal> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getClickCount() == 1) {
                    this.personal = row.getItem();
                    editPersonalButton.setDisable(false);
                }
            });
            return row;
        });
        lastnameTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtersData();
        });
        firstnameTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtersData();
        });
        phonenumberTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtersData();
        });

    }

    public void setTable() {
        lastnameCol.setCellValueFactory(new PropertyValueFactory<Personal, String>("lastname"));
        firstnameCol.setCellValueFactory(new PropertyValueFactory<Personal, String>("firstname"));
        middlenameCol.setCellValueFactory(new PropertyValueFactory<Personal, String>("middlename"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<Personal, String>("phonenumber"));
        loginCol.setCellValueFactory(new PropertyValueFactory<Personal, String>("login"));
        roleCol.setCellValueFactory(new PropertyValueFactory<Personal, String>("role"));
        tablePersonal.setItems(personalData);
    }
}
