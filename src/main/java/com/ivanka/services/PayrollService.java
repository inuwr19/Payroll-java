package com.ivanka.services;

import com.ivanka.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PayrollService {
    public double calculateSalary(int employeeId, String month) throws SQLException {
        double totalSalary = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = """
                SELECT e.status, e.base_salary, e.hourly_rate, a.status AS attendance_status, a.hours_worked
                FROM employees e
                JOIN attendance a ON e.id = a.employee_id
                WHERE e.id = ? AND DATE_FORMAT(a.date, '%Y-%m') = ?
            """;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, employeeId);
                stmt.setString(2, month);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String status = rs.getString("status");
                        String attendanceStatus = rs.getString("attendance_status");
                        double baseSalary = rs.getDouble("base_salary");
                        double hourlyRate = rs.getDouble("hourly_rate");
                        double hoursWorked = rs.getDouble("hours_worked");

                        if (status.equals("tetap")) {
                            if (attendanceStatus.equals("absen")) {
                                totalSalary -= baseSalary / 30; // Pemotongan harian
                            }
                            totalSalary += baseSalary;
                        } else if (status.equals("freelance")) {
                            if (attendanceStatus.equals("hadir")) {
                                totalSalary += hourlyRate * hoursWorked;
                            }
                        }
                    }
                }
            }
        }
        return totalSalary;
    }

}
