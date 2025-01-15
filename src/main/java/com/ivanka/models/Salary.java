package com.ivanka.models;
import java.util.Date;

public class Salary {
    private int id;
    private int employeeId;
    private Date month;
    private double totalSalary;
    private double deductions; // Pemotongan gaji
    private String notes;

    public Salary(int id, int employeeId, Date month, double totalSalary, double deductions, String notes) {
        this.id = id;
        this.employeeId = employeeId;
        this.month = month;
        this.totalSalary = totalSalary;
        this.deductions = deductions;
        this.notes = notes;
    }

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public Date getMonth() { return month; }
    public double getTotalSalary() { return totalSalary; }
    public double getDeductions() { return deductions; }
    public String getNotes() { return notes; }

}
