package com.ivanka.models;

public class Employee {
    private int id;
    private String name;
    private String status; // "tetap" atau "freelance"
    private double baseSalary; // Gaji pokok untuk karyawan tetap
    private double hourlyRate; // Tarif per jam untuk freelance
    private int leaveBalance; // Sisa cuti karyawan

    public Employee(int id, String name, String status, double baseSalary, double hourlyRate, int leaveBalance) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.baseSalary = baseSalary;
        this.hourlyRate = hourlyRate;
        this.leaveBalance = leaveBalance;
    }

    // Konstruktor tambahan (untuk data minimal)
    public Employee(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public double getBaseSalary() { return baseSalary; }
    public double getHourlyRate() { return hourlyRate; }
    public int getLeaveBalance() { return leaveBalance; }

    public void deductLeave(int days) {
        this.leaveBalance -= days;
    }

}
