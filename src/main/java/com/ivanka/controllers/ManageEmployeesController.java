package com.ivanka.controllers;

import com.ivanka.database.DatabaseConnection;
import com.ivanka.models.Employee;
import com.ivanka.models.PermanentEmployee;
import com.ivanka.models.FreelanceEmployee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ManageEmployeesController {

    @FXML
    private TextField nameField, baseSalaryField, hourlyRateField, leaveBalanceField, usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TableView<Employee> employeesTable;
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    @FXML
    private TableColumn<Employee, String> nameColumn, statusColumn;
    @FXML
    private TableColumn<Employee, Void> actionColumn;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load employees and setup action column
        loadEmployees();
        setupActionColumn();

        // Populate status ComboBox
        statusComboBox.setItems(FXCollections.observableArrayList("Tetap", "Freelance"));
    }

    private void loadEmployees() {
        employeeList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name, status, base_salary, hourly_rate, leave_balance FROM employees";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String status = rs.getString("status");

                if ("Tetap".equalsIgnoreCase(status)) {
                    double baseSalary = rs.getDouble("base_salary");
                    int leaveBalance = rs.getInt("leave_balance");
                    employeeList.add(new PermanentEmployee(id, name, baseSalary, leaveBalance));
                } else if ("Freelance".equalsIgnoreCase(status)) {
                    double hourlyRate = rs.getDouble("hourly_rate");
                    int hoursWorked = 0; // Default jam kerja diambil dari sumber lain
                    employeeList.add(new FreelanceEmployee(id, name, hourlyRate, hoursWorked));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load employees.");
        }
        employeesTable.setItems(employeeList);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    deleteEmployee(employee.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    @FXML
    private void handleAddEmployee() {
        // Input validation
        String name = nameField.getText();
        String status = statusComboBox.getValue();
        String baseSalary = baseSalaryField.getText();
        String hourlyRate = hourlyRateField.getText();
        String leaveBalance = leaveBalanceField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || status == null || username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Use transactions to ensure data consistency
            conn.setAutoCommit(false);

            String employeeQuery = "INSERT INTO employees (name, status, base_salary, hourly_rate, leave_balance, username, password) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            String userQuery = "INSERT INTO users (username, password, role_id, employee_id, created_at) " +
                            "VALUES (?, ?, ?, ?, NOW())";
            String salaryQuery = "INSERT INTO salaries (employee_id, month, total_salary, deductions, notes, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, NOW())";

            try (
                PreparedStatement employeeStmt = conn.prepareStatement(employeeQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement userStmt = conn.prepareStatement(userQuery);
                PreparedStatement salaryStmt = conn.prepareStatement(salaryQuery)
            ) {
                // Add employee
                employeeStmt.setString(1, name);
                employeeStmt.setString(2, status);
                employeeStmt.setBigDecimal(3, baseSalary.isEmpty() ? null : new BigDecimal(baseSalary));
                employeeStmt.setBigDecimal(4, hourlyRate.isEmpty() ? null : new BigDecimal(hourlyRate));
                employeeStmt.setInt(5, leaveBalance.isEmpty() ? 0 : Integer.parseInt(leaveBalance));
                employeeStmt.setString(6, username);
                employeeStmt.setString(7, password);
                employeeStmt.executeUpdate();

                ResultSet keys = employeeStmt.getGeneratedKeys();
                int employeeId = -1;
                if (keys.next()) {
                    employeeId = keys.getInt(1);
                }

                // Add user
                userStmt.setString(1, username);
                userStmt.setString(2, password);
                userStmt.setInt(3, 1); // role_id for employee
                userStmt.setInt(4, employeeId);
                userStmt.executeUpdate();

                // Add salary
                String currentMonth = LocalDate.now().withDayOfMonth(1).toString();
                BigDecimal totalSalary = baseSalary.isEmpty() ? BigDecimal.ZERO : new BigDecimal(baseSalary);
                salaryStmt.setInt(1, employeeId);
                salaryStmt.setString(2, currentMonth);
                salaryStmt.setBigDecimal(3, totalSalary);
                salaryStmt.setBigDecimal(4, BigDecimal.ZERO);
                salaryStmt.setString(5, "Initial salary");
                salaryStmt.executeUpdate();

                conn.commit();
                loadEmployees();
                clearForm();
                showAlert("Success", "Employee added successfully.");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add employee.");
        }
    }

    private void deleteEmployee(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM employees WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            loadEmployees();
            showAlert("Success", "Employee deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete employee.");
        }
    }

    private void clearForm() {
        nameField.clear();
        baseSalaryField.clear();
        hourlyRateField.clear();
        leaveBalanceField.clear();
        usernameField.clear();
        passwordField.clear();
        statusComboBox.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}



// package com.ivanka.controllers;

// import com.ivanka.database.DatabaseConnection;
// import com.ivanka.models.Employee;

// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.fxml.FXML;
// import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;

// import java.math.BigDecimal;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;

// public class ManageEmployeesController {

//     @FXML
//     private TextField nameField, baseSalaryField, hourlyRateField, leaveBalanceField, usernameField;
//     @FXML
//     private PasswordField passwordField;
//     @FXML
//     private ComboBox<String> statusComboBox;
//     @FXML
//     private TableView<Employee> employeesTable;
//     @FXML
//     private TableColumn<Employee, Integer> idColumn;
//     @FXML
//     private TableColumn<Employee, String> nameColumn, statusColumn;
//     @FXML
//     private TableColumn<Employee, Void> actionColumn;

//     private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

//     @FXML
//     private void initialize() {
//         // Initialize table columns
//         idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//         nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//         statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

//         // Load data and setup action column
//         loadEmployees();
//         setupActionColumn();

//         // Populate status ComboBox
//         // statusComboBox.setItems(FXCollections.observableArrayList("tetap", "freelance"));
//         statusComboBox.setItems(FXCollections.observableArrayList("tetap"));
//     }

//     private void loadEmployees() {
//         employeeList.clear();
//         try (Connection conn = DatabaseConnection.getConnection()) {
//             String query = "SELECT id, name, status FROM employees";
//             PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery();

//             while (rs.next()) {
//                 int id = rs.getInt("id");
//                 String name = rs.getString("name");
//                 String status = rs.getString("status");

//                 employeeList.add(new Employee(id, name, status));
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             showAlert("Error", "Failed to load employees.");
//         }
//         employeesTable.setItems(employeeList);
//     }

//     private void setupActionColumn() {
//         actionColumn.setCellFactory(column -> new TableCell<>() {
//             private final Button deleteButton = new Button("Delete");

//             {
//                 deleteButton.setOnAction(event -> {
//                     Employee employee = getTableView().getItems().get(getIndex());
//                     deleteEmployee(employee.getId());
//                 });
//             }

//             @Override
//             protected void updateItem(Void item, boolean empty) {
//                 super.updateItem(item, empty);
//                 if (empty) {
//                     setGraphic(null);
//                 } else {
//                     setGraphic(deleteButton);
//                 }
//             }
//         });
//     }

//     @FXML
//     private void handleAddEmployee() {
//         // Input validation
//         String name = nameField.getText();
//         String status = statusComboBox.getValue();
//         String baseSalary = baseSalaryField.getText();
//         // String hourlyRate = hourlyRateField.getText();
//         String leaveBalance = leaveBalanceField.getText();
//         String username = usernameField.getText();
//         String password = passwordField.getText();

//         if (name.isEmpty() || status == null || username.isEmpty() || password.isEmpty()) {
//             showAlert("Validation Error", "Please fill in all required fields.");
//             return;
//         }

//         try (Connection conn = DatabaseConnection.getConnection()) {
//             // Query untuk menambahkan karyawan ke tabel employees
//             String employeeQuery = "INSERT INTO employees (name, status, base_salary, hourly_rate, leave_balance, username, password) " +
//                                     "VALUES (?, ?, ?, 0, ?, ?, ?)";
            
//             // Query untuk menambahkan user ke tabel users
//             String userQuery = "INSERT INTO users (username, password, role_id, employee_id, created_at) " +
//                                 "VALUES (?, ?, ?, ?, NOW())";
        
//             // Query untuk menambahkan entri ke tabel salaries
//             String salaryQuery = "INSERT INTO salaries (employee_id, month, total_salary, deductions, notes, created_at) " +
//                                     "VALUES (?, ?, ?, ?, ?, NOW())";
        
//             // Gunakan transaksi untuk memastikan data konsisten
//             conn.setAutoCommit(false);
        
//             try (
//                 PreparedStatement employeeStmt = conn.prepareStatement(employeeQuery, PreparedStatement.RETURN_GENERATED_KEYS);
//                 PreparedStatement userStmt = conn.prepareStatement(userQuery);
//                 PreparedStatement salaryStmt = conn.prepareStatement(salaryQuery)
//             ) {
//                 // Menyisipkan data ke tabel employees
//                 employeeStmt.setString(1, name);
//                 employeeStmt.setString(2, status);
//                 employeeStmt.setBigDecimal(3, baseSalary.isEmpty() ? null : new BigDecimal(baseSalary));
//                 // employeeStmt.setBigDecimal(4, hourlyRate.isEmpty() ? null : new BigDecimal(hourlyRate));
//                 employeeStmt.setInt(4, leaveBalance.isEmpty() ? 0 : Integer.parseInt(leaveBalance));
//                 employeeStmt.setString(5, username);
//                 employeeStmt.setString(6, password);
//                 employeeStmt.executeUpdate();
        
//                 // Mendapatkan ID karyawan yang baru saja ditambahkan
//                 ResultSet generatedKeys = employeeStmt.getGeneratedKeys();
//                 int employeeId = -1;
//                 if (generatedKeys.next()) {
//                     employeeId = generatedKeys.getInt(1);
//                 }
        
//                 // Menyisipkan data ke tabel users
//                 userStmt.setString(1, username);
//                 userStmt.setString(2, password);
//                 userStmt.setInt(3, 1); // role_id = 1 untuk karyawan
//                 userStmt.setInt(4, employeeId);
//                 userStmt.executeUpdate();
        
//                 // Menyisipkan data ke tabel salaries (asumsi bulan penggajian adalah bulan saat ini)
//                 String currentMonth = java.time.LocalDate.now().withDayOfMonth(1).toString(); // Format: yyyy-MM-01
//                 BigDecimal totalSalary = baseSalary.isEmpty() ? BigDecimal.ZERO : new BigDecimal(baseSalary); // Total Salary for employee
//                 BigDecimal deductions = BigDecimal.ZERO; // Asumsikan tidak ada potongan saat pertama kali
//                 String notes = "Initial salary"; // Catatan untuk penggajian pertama
        
//                 salaryStmt.setInt(1, employeeId);
//                 salaryStmt.setString(2, currentMonth); // Bulan penggajian dalam format yyyy-MM-01
//                 salaryStmt.setBigDecimal(3, totalSalary);
//                 salaryStmt.setBigDecimal(4, deductions);
//                 salaryStmt.setString(5, notes);
//                 salaryStmt.executeUpdate();
        
//                 // Commit transaksi
//                 conn.commit();
        
//                 // Reload tabel karyawan dan bersihkan form
//                 loadEmployees();
//                 clearForm();
        
//                 showAlert("Success", "Employee, user, and salary record added successfully.");
//             } catch (Exception e) {
//                 conn.rollback(); // Rollback jika terjadi error
//                 throw e;
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             showAlert("Error", "Failed to add employee, user, and salary record.");
//         }          
//     }

//     private void deleteEmployee(int id) {
//         try (Connection conn = DatabaseConnection.getConnection()) {
//             String query = "DELETE FROM employees WHERE id = ?";
//             PreparedStatement stmt = conn.prepareStatement(query);
//             stmt.setInt(1, id);
//             stmt.executeUpdate();

//             loadEmployees();
//             showAlert("Success", "Employee deleted successfully.");
//         } catch (Exception e) {
//             e.printStackTrace();
//             showAlert("Error", "Failed to delete employee.");
//         }
//     }

//     private void clearForm() {
//         nameField.clear();
//         baseSalaryField.clear();
//         // hourlyRateField.clear();
//         leaveBalanceField.clear();
//         usernameField.clear();
//         passwordField.clear();
//         statusComboBox.setValue(null);
//     }

//     private void showAlert(String title, String content) {
//         Alert alert = new Alert(Alert.AlertType.INFORMATION);
//         alert.setTitle(title);
//         alert.setHeaderText(null);
//         alert.setContentText(content);
//         alert.showAndWait();
//     }
// }
