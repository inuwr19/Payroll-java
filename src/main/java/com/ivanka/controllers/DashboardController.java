package com.ivanka.controllers;

import com.ivanka.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label attendanceLabel;
    @FXML
    private Label salaryLabel;
    @FXML
    private DatePicker leaveDatePicker;

    private String username;

    @FXML
    private Label statusMessageLabel;

    @FXML
    private void initialize() {
        // Set default value for DatePicker to current date or after
        leaveDatePicker.setValue(LocalDate.now());

        // Disable selection of past dates
        leaveDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    public void setSalaryLabel(double totalSalary) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedSalary = currencyFormat.format(totalSalary);
        salaryLabel.setText("Total Salary: " + formattedSalary);
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ivanka/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleAttendance() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Dapatkan employee_id berdasarkan username
            String employeeIdQuery = "SELECT employee_id FROM users WHERE username = ?";
            try (PreparedStatement employeeStmt = conn.prepareStatement(employeeIdQuery)) {
                employeeStmt.setString(1, username);
                try (ResultSet rs = employeeStmt.executeQuery()) {
                    if (rs.next()) {
                        int employeeId = rs.getInt("employee_id");
                        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis()); // Tanggal saat ini

                        // Cek apakah karyawan sudah absen hari ini
                        String checkAttendanceQuery = "SELECT * FROM attendance WHERE employee_id = ? AND date = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkAttendanceQuery)) {
                            checkStmt.setInt(1, employeeId);
                            checkStmt.setDate(2, currentDate);
                            try (ResultSet checkRs = checkStmt.executeQuery()) {
                                if (checkRs.next()) {
                                    // Jika sudah absen, beri notifikasi
                                    attendanceLabel.setText("Anda sudah absen hari ini.");
                                } else {
                                    // Jika belum absen, masukkan absensi baru
                                    String insertAttendanceQuery = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
                                    try (PreparedStatement insertStmt = conn.prepareStatement(insertAttendanceQuery)) {
                                        insertStmt.setInt(1, employeeId);
                                        insertStmt.setDate(2, currentDate);
                                        insertStmt.setString(3, "hadir"); // Status hadir
                                        insertStmt.executeUpdate();

                                        attendanceLabel.setText("Absensi berhasil!"); // Notifikasi berhasil

                                        // Update status message label with success message
                                        statusMessageLabel.setText("Absensi berhasil untuk tanggal " + currentDate.toString());
                                        // Optionally reset the status message after a few seconds
                                        // statusMessageLabel.setText("");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during attendance: " + e.getMessage());
        }
    }

    @FXML
    private void handleLeaveRequest() {
        if (leaveDatePicker.getValue() == null) {
            attendanceLabel.setText("Silakan pilih tanggal cuti.");
            return;
        }
    
        java.sql.Date leaveDate = java.sql.Date.valueOf(leaveDatePicker.getValue());
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String employeeIdQuery = "SELECT employee_id FROM users WHERE username = ?";
            try (PreparedStatement employeeStmt = conn.prepareStatement(employeeIdQuery)) {
                employeeStmt.setString(1, username);
                try (ResultSet rs = employeeStmt.executeQuery()) {
                    if (rs.next()) {
                        int employeeId = rs.getInt("employee_id");
    
                        String checkLeaveQuery = "SELECT * FROM attendance WHERE employee_id = ? AND date = ? AND status = 'cuti'";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkLeaveQuery)) {
                            checkStmt.setInt(1, employeeId);
                            checkStmt.setDate(2, leaveDate);
                            try (ResultSet checkRs = checkStmt.executeQuery()) {
                                if (checkRs.next()) {
                                    attendanceLabel.setText("Anda sudah mengajukan cuti pada tanggal ini.");
                                } else {
                                    String insertLeaveQuery = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
                                    try (PreparedStatement insertStmt = conn.prepareStatement(insertLeaveQuery)) {
                                        insertStmt.setInt(1, employeeId);
                                        insertStmt.setDate(2, leaveDate);
                                        insertStmt.setString(3, "cuti");
                                        insertStmt.executeUpdate();
    
                                        String updateLeaveBalanceQuery = "UPDATE employees SET leave_balance = leave_balance - 1 WHERE id = ?";
                                        try (PreparedStatement updateStmt = conn.prepareStatement(updateLeaveBalanceQuery)) {
                                            updateStmt.setInt(1, employeeId);
                                            updateStmt.executeUpdate();
                                        }
    
                                        // Update status message label with success message
                                        statusMessageLabel.setText("Cuti berhasil diajukan untuk tanggal " + leaveDate.toString());
                                        // Optionally reset the status message after a few seconds
                                        // statusMessageLabel.setText("");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during leave request: " + e.getMessage());
            attendanceLabel.setText("Terjadi kesalahan saat mengajukan cuti.");
        }
    }
    
    public void setUsername(String username) {
        this.username = username;
        loadDashboardData();
    }

    public void setUserData(String name, String role) {
        welcomeLabel.setText("Welcome, " + name + "!");
    }

    private void loadDashboardData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userQuery = "SELECT e.name, r.role_name FROM users u " +
                                "JOIN employees e ON u.employee_id = e.id " +
                                "JOIN roles r ON u.role_id = r.id WHERE u.username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String role = rs.getString("role_name");
                        welcomeLabel.setText("Welcome, " + name + "!");
                        roleLabel.setText("Role: " + role);
                    }
                }
            }

            // Attendance count
            String attendanceQuery = "SELECT COUNT(*) AS total FROM attendance WHERE employee_id = (SELECT employee_id FROM users WHERE username = ?) AND status = 'hadir'";
            try (PreparedStatement stmt = conn.prepareStatement(attendanceQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int totalAttendance = rs.getInt("total");
                        attendanceLabel.setText("Total Attendance: " + totalAttendance);
                    }
                }
            }

            // Salary
            String salaryQuery = "SELECT SUM(total_salary) AS total FROM salaries WHERE employee_id = (SELECT employee_id FROM users WHERE username = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(salaryQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double totalSalary = rs.getDouble("total");
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                        salaryLabel.setText("Total Salary: " + currencyFormat.format(totalSalary));
                    }
                }
            }

            // Leave balance
            String leaveBalanceQuery = "SELECT leave_balance FROM employees WHERE id = (SELECT employee_id FROM users WHERE username = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(leaveBalanceQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int leaveBalance = rs.getInt("leave_balance");
                        attendanceLabel.setText("Sisa Cuti: " + leaveBalance);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }
}
