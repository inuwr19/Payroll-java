package com.ivanka.models;

public class User {
    private int id;
    private String username;
    private String password; // Hashed password
    private int roleId;
    private int employeeId; // Relasi ke tabel employees

    public User(int id, String username, String password, int roleId, int employeeId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.employeeId = employeeId;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getRoleId() { return roleId; }
    public int getEmployeeId() { return employeeId; }

}
