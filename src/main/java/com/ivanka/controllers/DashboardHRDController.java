package com.ivanka.controllers;

import com.ivanka.database.DatabaseConnection;
import com.ivanka.models.AttendanceRow;
import com.ivanka.models.EmployeeReport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardHRDController {

    @FXML
    private TableView<AttendanceRow> dailyAttendanceTable;
    @FXML
    private TableView<EmployeeReport> employeeReportTable;
    @FXML
    private Label roleLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Label nameLabel;
    @SuppressWarnings("unused")
    private String username;
    @SuppressWarnings("unused")
    private String name;


    private ObservableList<AttendanceRow> attendanceData = FXCollections.observableArrayList();
    private ObservableList<EmployeeReport> employeeReports = FXCollections.observableArrayList();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    // Dynamically create day columns
    private final int DAYS_IN_MONTH = 31;
    private List<TableColumn<AttendanceRow, String>> dayColumns = new ArrayList<>();


    // Metode ini untuk menginisialisasi username
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserData(String name, String role) {
        nameLabel.setText("Welcome, " + name + "!");
        roleLabel.setText("Role: " + role);
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            // Mendapatkan objek Stage dan memaksimalkan jendela
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setMaximized(true);
        });
        welcomeLabel.setText("Dashboard HRD");
        // Employee Report Table Setup
        setupEmployeeReportTable();

        // Daily Attendance Table Setup
        setupDailyAttendanceTable();

        // Load data
        loadEmployeeReportData();
        loadDailyAttendanceData();
    }

    // Method to set up employee report table
    private void setupEmployeeReportTable() {
        TableColumn<EmployeeReport, String> nameColumn = new TableColumn<>("Nama Karyawan");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<EmployeeReport, Integer> attendanceColumn = new TableColumn<>("Jumlah Hadir");
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceCount"));

        TableColumn<EmployeeReport, Integer> leaveColumn = new TableColumn<>("Jumlah Cuti");
        leaveColumn.setCellValueFactory(new PropertyValueFactory<>("leaveCount"));

        TableColumn<EmployeeReport, String> salaryColumn = new TableColumn<>("Gaji");
        salaryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(currencyFormat.format(cellData.getValue().getTotalSalary())));

        TableColumn<EmployeeReport, Integer> leaveBalanceColumn = new TableColumn<>("Sisa Cuti");
        leaveBalanceColumn.setCellValueFactory(new PropertyValueFactory<>("leaveBalance"));

        List<TableColumn<EmployeeReport, ?>> columns = List.of(
            nameColumn,
            attendanceColumn,
            leaveColumn,
            salaryColumn,
            leaveBalanceColumn
        );
        
        employeeReportTable.getColumns().addAll(columns);
    }

    // Method to set up daily attendance table
    private void setupDailyAttendanceTable() {
        TableColumn<AttendanceRow, String> nameColumn = new TableColumn<>("Nama Karyawan");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Create day columns dynamically
        for (int i = 0; i < DAYS_IN_MONTH; i++) {
            int day = i + 1;
            TableColumn<AttendanceRow, String> dayColumn = new TableColumn<>(String.valueOf(day));
            final int finalDay = day;
            dayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusForDay(finalDay)));
            dayColumns.add(dayColumn);
        }
        
        dailyAttendanceTable.getColumns().add(nameColumn);
        dailyAttendanceTable.getColumns().addAll(dayColumns);
    }

    // Load employee report data
    private void loadEmployeeReportData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = """
                SELECT e.name, 
                       COUNT(CASE WHEN a.status = 'hadir' THEN 1 END) AS attendance_count, 
                       COUNT(CASE WHEN a.status = 'cuti' THEN 1 END) AS leave_count,
                       s.total_salary,
                       e.leave_balance
                FROM employees e
                LEFT JOIN attendance a ON e.id = a.employee_id
                LEFT JOIN salaries s ON e.id = s.employee_id
                WHERE a.date >= DATE_ADD(CURDATE(), INTERVAL -1 YEAR)
                GROUP BY e.id, e.name, s.total_salary, e.leave_balance
            """;

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                employeeReports.clear();
                while (rs.next()) {
                    String name = rs.getString("name");
                    int attendanceCount = rs.getInt("attendance_count");
                    int leaveCount = rs.getInt("leave_count");
                    double totalSalary = rs.getDouble("total_salary");
                    int leaveBalance = rs.getInt("leave_balance");
                    employeeReports.add(new EmployeeReport(name, attendanceCount, leaveCount, totalSalary, leaveBalance));
                }
                employeeReportTable.setItems(employeeReports);
            }
        } catch (Exception e) {
            System.err.println("Error loading employee report data: " + e.getMessage());
        }
    }

    // Load daily attendance data
    private void loadDailyAttendanceData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = """
                SELECT e.name, 
                       DAY(a.date) AS day, 
                       a.status
                FROM employees e
                LEFT JOIN attendance a ON e.id = a.employee_id
                WHERE a.date BETWEEN ? AND ?
                ORDER BY e.id, a.date
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, Date.valueOf("2025-01-01"));
            stmt.setDate(2, Date.valueOf("2025-01-31"));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int day = rs.getInt("day");
                String status = rs.getString("status");
                updateDailyAttendance(name, day, status);
            }
        } catch (Exception e) {
            System.err.println("Error loading daily attendance data: " + e.getMessage());
        }
    }

    // Update daily attendance table
    private void updateDailyAttendance(String name, int day, String status) {
        boolean found = false;

        for (AttendanceRow row : attendanceData) {
            if (row.getName().equals(name)) {
                row.setStatusForDay(day, status);
                found = true;
                break;
            }
        }

        if (!found) {
            AttendanceRow newRow = new AttendanceRow(name);
            newRow.setStatusForDay(day, status);
            attendanceData.add(newRow);
        }

        dailyAttendanceTable.setItems(attendanceData);
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ivanka/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }

    @FXML
    private void openManageEmployees() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ivanka/ManageEmployees.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Employees");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


// <Button fx:id="manageEmployeesButton" text="Manage Employees" onAction="#openManageEmployees"
//                 style="-fx-pref-width: 200px; -fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;" />
