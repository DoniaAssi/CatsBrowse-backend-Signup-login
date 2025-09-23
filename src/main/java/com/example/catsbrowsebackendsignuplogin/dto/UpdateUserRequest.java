package com.example.catsbrowsebackendsignuplogin.dto;

public class UpdateUserRequest {
    private String email;
    private String password;
    private String rolesCsv; // مثال: "USER,ADMIN" أو "ROLE_USER,ROLE_ADMIN"

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRolesCsv() { return rolesCsv; }
    public void setRolesCsv(String rolesCsv) { this.rolesCsv = rolesCsv; }
}

