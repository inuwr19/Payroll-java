package com.ivanka.models;
import java.util.Date;

public class Attendance {
    private int id;
    private int employeeId;
    private Date date;
    private String status; // "hadir", "cuti", "absen"
    private double hoursWorked; // Jam kerja untuk freelance

    public Attendance(int id, int employeeId, Date date, String status, double hoursWorked) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
        this.hoursWorked = hoursWorked;
    }

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public Date getDate() { return date; }
    public String getStatus() { return status; }
    public double getHoursWorked() { return hoursWorked; }

}
