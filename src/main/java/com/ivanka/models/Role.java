package com.ivanka.models;

public class Role {
    private int id;
    private String roleName; // "karyawan" atau "hrd"

    public Role(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() { return id; }
    public String getRoleName() { return roleName; }

}
