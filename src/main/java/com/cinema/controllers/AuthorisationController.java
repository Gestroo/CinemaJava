package com.cinema.controllers;

import com.cinema.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AuthorisationController {
    @FXML
    private TextField loginTF;
    @FXML
    private PasswordField passwordPF;
    @FXML
    private Button signInButton;

    @FXML
    private void signInButtonClick() throws IOException {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cinema","Gestroo","123");
        //   Connection conn = DriverManager.getConnection("jdbc:postgresql://45.10.244.15:55532/work100027", "work100027", "jGG*CL|1k9Xk04qjR%du");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM personal WHERE role_id=1 AND login= ? AND password=?");
            ps.setString(1,loginTF.getText());
            ps.setString(2,makePass(passwordPF.getText()));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Stage newWindow = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainMenu.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                newWindow.setTitle("Кинотеатр Премьер");
                newWindow.setScene(scene);
                Stage stage = (Stage) signInButton.getScene().getWindow();
                conn.close();
                stage.close();
                newWindow.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");

                alert.setHeaderText(null);
                alert.setContentText("Неверный логин или пароль");

                alert.showAndWait();
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
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
}
