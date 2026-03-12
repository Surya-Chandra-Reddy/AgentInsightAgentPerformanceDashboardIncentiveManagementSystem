package com.AgentInsight.dto;

import com.AgentInsight.enums.UserRole;

public class UserDto {
    private String agentid;
    private String name;
    private String email;
    private String phone;
    private UserRole role;

    public UserDto() {}

    public UserDto(String agentid, String name, String email, String phone, UserRole role) {
        this.agentid = agentid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
