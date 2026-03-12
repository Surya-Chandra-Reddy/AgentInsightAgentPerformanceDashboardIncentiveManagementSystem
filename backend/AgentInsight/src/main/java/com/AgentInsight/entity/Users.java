package com.AgentInsight.entity;

import com.AgentInsight.CustomGenerator.AgentId.GeneratedAgentId;
import com.AgentInsight.enums.UserRole;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Users {

    @Id
    @GeneratedAgentId
    @Column(name="agentid")
    private String agentid;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private UserRole role;

    @Column(name="password")
    private String password;

    // Relationships
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sales> sales;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Incentive> incentives;

    @OneToOne(mappedBy = "agent", cascade = CascadeType.ALL)
    private Leaderboard leaderboard;

    // Getters and Setters
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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Sales> getSales() { return sales; }
    public void setSales(List<Sales> sales) { this.sales = sales; }

    public List<Incentive> getIncentives() { return incentives; }
    public void setIncentives(List<Incentive> incentives) { this.incentives = incentives; }

    public Leaderboard getLeaderboard() { return leaderboard; }
    public void setLeaderboard(Leaderboard leaderboard) { this.leaderboard = leaderboard; }
}
