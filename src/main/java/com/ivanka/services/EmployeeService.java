package com.ivanka.services;

import com.ivanka.database.DatabaseConnection;
import com.ivanka.models.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EmployeeService {
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Employee employee = new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("status"),
                    rs.getDouble("base_salary"),
                    rs.getDouble("hourly_rate"),
                    rs.getInt("leave_balance")
                );
                employees.add(employee);
            }
        }
        return employees;
    }

}
