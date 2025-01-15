package com.ivanka.controllers;

import com.ivanka.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessage;

    private String username;
    

    @FXML
    private void handleLogin() {
        username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username atau password tidak boleh kosong.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.id, u.username, e.name, r.role_name FROM users u " +
                        "JOIN roles r ON u.role_id = r.id " +
                        "JOIN employees e ON u.employee_id = e.id " +  // Join dengan tabel employees
                        "WHERE u.username = ? AND u.password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // String role = rs.getString("role_name");
                        // navigateToDashboard(role);
                        String role = rs.getString("role_name");
                        String name = rs.getString("name");  // Mendapatkan nama lengkap

                        navigateToDashboard(role, name);
                    } else {
                        showError("Username atau password salah.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            showError("Terjadi kesalahan saat login. Silakan coba lagi.");
        }
    }

    private void navigateToDashboard(String role, String name) throws Exception {
        FXMLLoader loader;
        if ("karyawan".equalsIgnoreCase(role)) {
            loader = new FXMLLoader(getClass().getResource("/com/ivanka/dashboard.fxml"));
        } else if ("hrd".equalsIgnoreCase(role)) {
            loader = new FXMLLoader(getClass().getResource("/com/ivanka/dashboardHRD.fxml"));
        } else {
            showError("Role tidak valid.");
            return;
        }
    
        // Load the appropriate dashboard scene
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard - " + role);
    
        // Pass the username, name, and role to the respective controller
        if ("karyawan".equalsIgnoreCase(role)) {
            DashboardController karyawanController = loader.getController();
            karyawanController.setUsername(username);  // Pass username
            karyawanController.setUserData(name, role);  // Pass name and role
        } else if ("hrd".equalsIgnoreCase(role)) {
            DashboardHRDController hrdController = loader.getController();
            hrdController.setUsername(username);  // Pass username
            hrdController.setUserData(name, role);  // Pass name and role
        }
    
        stage.show();
    }
    
    

    private void showError(String message) {
        errorMessage.setText(message);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
