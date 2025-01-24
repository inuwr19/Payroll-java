package com.ivanka.models;

// public package com.ivanka.models;

public class FreelanceEmployee extends Employee {
    private double hourlyRate; // Tarif per jam
    private int hoursWorked;   // Jam kerja

    public FreelanceEmployee(int id, String name, double hourlyRate, int hoursWorked) {
        super(id, name, "Freelance");
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked; // Gaji dihitung berdasarkan tarif per jam x jam kerja
    }
}
