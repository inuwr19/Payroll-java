package com.ivanka.services;

import com.ivanka.database.DatabaseConnection;
import com.ivanka.models.Employee;
import com.ivanka.models.PermanentEmployee;
import com.ivanka.models.FreelanceEmployee;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    /**
     * Retrieves all employees from the database.
     *
     * @return a list of Employee objects (either PermanentEmployee or FreelanceEmployee)
     * @throws SQLException if a database access error occurs
     */
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT id, name, status, base_salary, hourly_rate, leave_balance FROM employees";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String status = rs.getString("status");

                if ("Permanent".equalsIgnoreCase(status)) {
                    // Create PermanentEmployee object
                    double baseSalary = rs.getDouble("base_salary");
                    int leaveBalance = rs.getInt("leave_balance");
                    employees.add(new PermanentEmployee(id, name, baseSalary, leaveBalance));
                } else if ("Freelance".equalsIgnoreCase(status)) {
                    // Create FreelanceEmployee object
                    double hourlyRate = rs.getDouble("hourly_rate");
                    employees.add(new FreelanceEmployee(id, name, hourlyRate, 0)); // Default hoursWorked = 0
                }
            }
        }

        return employees;
    }

    /**
     * Retrieves a specific employee by ID.
     *
     * @param id the ID of the employee to retrieve
     * @return an Employee object (either PermanentEmployee or FreelanceEmployee), or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Employee getEmployeeById(int id) throws SQLException {
        String query = "SELECT id, name, status, base_salary, hourly_rate, leave_balance FROM employees WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String status = rs.getString("status");

                    if ("Permanent".equalsIgnoreCase(status)) {
                        double baseSalary = rs.getDouble("base_salary");
                        int leaveBalance = rs.getInt("leave_balance");
                        return new PermanentEmployee(id, name, baseSalary, leaveBalance);
                    } else if ("Freelance".equalsIgnoreCase(status)) {
                        double hourlyRate = rs.getDouble("hourly_rate");
                        return new FreelanceEmployee(id, name, hourlyRate, 0);
                    }
                }
            }
        }
        return null; // Employee not found
    }

    /**
     * Adds a new employee to the database.
     *
     * @param employee the Employee object to add (must be PermanentEmployee or FreelanceEmployee)
     * @throws SQLException if a database access error occurs
     */
    public void addEmployee(Employee employee) throws SQLException {
        String query = "INSERT INTO employees (name, status, base_salary, hourly_rate, leave_balance) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getStatus());

            if (employee instanceof PermanentEmployee) {
                PermanentEmployee perm = (PermanentEmployee) employee;
                stmt.setBigDecimal(3, BigDecimal.valueOf(perm.getBaseSalary()));
                stmt.setNull(4, java.sql.Types.DOUBLE); // hourly_rate is null for PermanentEmployee
                stmt.setInt(5, perm.getLeaveBalance());
            } else if (employee instanceof FreelanceEmployee) {
                FreelanceEmployee free = (FreelanceEmployee) employee;
                stmt.setNull(3, java.sql.Types.DOUBLE); // base_salary is null for FreelanceEmployee
                stmt.setBigDecimal(4, BigDecimal.valueOf(free.getHourlyRate()));
                stmt.setNull(5, java.sql.Types.INTEGER); // leave_balance is null for FreelanceEmployee
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an employee from the database by ID.
     *
     * @param id the ID of the employee to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteEmployee(int id) throws SQLException {
        String query = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
