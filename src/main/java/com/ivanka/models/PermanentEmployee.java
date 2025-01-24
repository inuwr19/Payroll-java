package com.ivanka.models;

public class PermanentEmployee extends Employee {
    private double baseSalary; // Gaji pokok untuk karyawan tetap
    private int leaveBalance;  // Sisa cuti

    public PermanentEmployee(int id, String name, double baseSalary, int leaveBalance) {
        super(id, name, "Permanent");
        this.baseSalary = baseSalary;
        this.leaveBalance = leaveBalance;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public int getLeaveBalance() {
        return leaveBalance;
    }

    public void deductLeave(int days) {
        this.leaveBalance -= days;
    }

    @Override
    public double calculateSalary() {
        return baseSalary; // Gaji tetap adalah base salary
    }
}
