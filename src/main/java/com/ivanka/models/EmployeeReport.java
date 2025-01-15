package com.ivanka.models;

public class EmployeeReport {
    private String name;
    private int attendanceCount;
    private int leaveCount;
    private double totalSalary;
    private int leaveBalance;  // Menambahkan field untuk sisa cuti

    public EmployeeReport(String name, int attendanceCount, int leaveCount, double totalSalary, int leaveBalance) {
        this.name = name;
        this.attendanceCount = attendanceCount;
        this.leaveCount = leaveCount;
        this.totalSalary = totalSalary;
        this.leaveBalance = leaveBalance;
    }

    public String getName() {
        return name;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public int getLeaveCount() {
        return leaveCount;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public int getLeaveBalance() {
        return leaveBalance;  // Menambahkan getter untuk sisa cuti
    }
}
