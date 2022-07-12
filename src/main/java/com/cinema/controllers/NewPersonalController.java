package com.cinema.controllers;

import com.cinema.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ResourceBundle;

public class NewPersonalController implements Initializable {
    Connection conn;
    static Statement stmt;

    {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
            //conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            stmt = conn.createStatement();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<String> rolesData = FXCollections.observableArrayList();

    @FXML
    private JFXButton toMenuButton;
    @FXML
    private JFXButton addPersonalButton;
    @FXML
    private TextField lastnameTF;
    @FXML
    private TextField firstnameTF;
    @FXML
    private TextField middlenameTF;
    @FXML
    private TextField phonenumberTF;
    @FXML
    private TextField loginTF;
    @FXML
    private JFXComboBox roleCB;
    @FXML
    private PasswordField passwordPF;
    @FXML
    private PasswordField repeatPasswordPF;

    @FXML
    private void addPersonalButtonClick() throws IOException {
        if (loginTF.getText()==""||passwordPF.getText()==""||roleCB.getValue()==null||lastnameTF.getText()==""||firstnameTF.getText()==""||phonenumberTF.getText()==""||repeatPasswordPF.getText()=="")
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните необходимые поля");

            alert.showAndWait();
            return;
        }
        if (!passwordPF.getText().equals(repeatPasswordPF.getText()))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");

            alert.setHeaderText(null);
            alert.setContentText("Введенные пароли не совпадают");

            alert.showAndWait();
            return;
        }
        try {
            char[] tmp = phonenumberTF.getText().toCharArray();
            for (char t : tmp
                 ) {
                long i = Integer.parseInt(String.valueOf(t));
            }

        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");

            alert.setHeaderText(null);
            alert.setContentText("Введите корректный номер телефона");

            alert.showAndWait();
            return;
        }
            try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO personal(login, password, role_id, lastname, firstname, middlename, phonenumber) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1,loginTF.getText());
            ps.setString(2,makePass(passwordPF.getText()));
            ps.setInt(3,getRoleIdByTitle(roleCB.getValue().toString()));
            ps.setString(4,lastnameTF.getText());
            ps.setString(5,firstnameTF.getText());
            try {
                ps.setString(6, middlenameTF.getText());
            } catch (NullPointerException e) {
                ps.setString(6, "");
            }
            ps.setString(7,"+7"+phonenumberTF.getText());
            ps.addBatch();
            ps.executeBatch();
            showSuccessAlert();
            Stage stage = (Stage) addPersonalButton.getScene().getWindow();
                conn.close();
            stage.close();
            PersonalCatalogController.loadPersonal();
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
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRoles();
    }
    private void loadRoles() {
        try {
            ResultSet rs = stmt.executeQuery("select personal_role from role");
            while (rs.next()) {
                rolesData.add(rs.getString("personal_role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        roleCB.getItems().addAll(rolesData);
    }
    private int getRoleIdByTitle(String title){
        try {
            PreparedStatement ps = conn.prepareStatement("select id from role where personal_role=?");
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
    private String makePass(String oldpass){
        byte[] ress = new byte[0];
        try {
            ress = MessageDigest.getInstance("SHA-256").digest(oldpass.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        StringBuilder passBuild = new StringBuilder();
        for (byte b : ress) {
            passBuild.append(String.format("%02x", b));
        }
        return passBuild.toString();
    }
    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");

        alert.setHeaderText(null);
        alert.setContentText("Сотрудник добавлен успешно");

        alert.showAndWait();
    }
}
