package com.ivanka.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttendanceRow {

    private final StringProperty name;
    private final StringProperty[] dailyStatuses;

    public AttendanceRow(String name) {
        this.name = new SimpleStringProperty(name);
        this.dailyStatuses = new StringProperty[31];  // Untuk 31 hari dalam bulan

        // Inisialisasi semua hari dengan status kosong
        for (int i = 0; i < 31; i++) {
            dailyStatuses[i] = new SimpleStringProperty("");
        }
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setStatusForDay(int day, String status) {
        if (day >= 1 && day <= 31) {
            dailyStatuses[day - 1].set(status);
        }
    }

    public String getStatusForDay(int day) {
        if (day >= 1 && day <= 31) {
            return dailyStatuses[day - 1].get();
        }
        return "";
    }

    // Property untuk setiap hari
    public StringProperty day1Property() {
        return dailyStatuses[0];
    }

    public StringProperty day2Property() {
        return dailyStatuses[1];
    }

    public StringProperty day3Property() {
        return dailyStatuses[2];
    }

    public StringProperty day4Property() {
        return dailyStatuses[3];
    }

    public StringProperty day5Property() {
        return dailyStatuses[4];
    }

    public StringProperty day6Property() {
        return dailyStatuses[5];
    }

    public StringProperty day7Property() {
        return dailyStatuses[6];
    }

    public StringProperty day8Property() {
        return dailyStatuses[7];
    }

    public StringProperty day9Property() {
        return dailyStatuses[8];
    }

    public StringProperty day10Property() {
        return dailyStatuses[9];
    }

    public StringProperty day11Property() {
        return dailyStatuses[10];
    }

    public StringProperty day12Property() {
        return dailyStatuses[11];
    }

    public StringProperty day13Property() {
        return dailyStatuses[12];
    }

    public StringProperty day14Property() {
        return dailyStatuses[13];
    }

    public StringProperty day15Property() {
        return dailyStatuses[14];
    }

    public StringProperty day16Property() {
        return dailyStatuses[15];
    }

    public StringProperty day17Property() {
        return dailyStatuses[16];
    }

    public StringProperty day18Property() {
        return dailyStatuses[17];
    }

    public StringProperty day19Property() {
        return dailyStatuses[18];
    }

    public StringProperty day20Property() {
        return dailyStatuses[19];
    }

    public StringProperty day21Property() {
        return dailyStatuses[20];
    }

    public StringProperty day22Property() {
        return dailyStatuses[21];
    }

    public StringProperty day23Property() {
        return dailyStatuses[22];
    }

    public StringProperty day24Property() {
        return dailyStatuses[23];
    }

    public StringProperty day25Property() {
        return dailyStatuses[24];
    }

    public StringProperty day26Property() {
        return dailyStatuses[25];
    }

    public StringProperty day27Property() {
        return dailyStatuses[26];
    }

    public StringProperty day28Property() {
        return dailyStatuses[27];
    }

    public StringProperty day29Property() {
        return dailyStatuses[28];
    }

    public StringProperty day30Property() {
        return dailyStatuses[29];
    }

    public StringProperty day31Property() {
        return dailyStatuses[30];
    }
}
