package com.AgentInsight.service;

import com.AgentInsight.dto.AgentPerformanceDTO;
import com.AgentInsight.dto.AgentReportDTO;
import com.AgentInsight.dto.UserDto;
import com.AgentInsight.entity.Users;
import java.util.List;
import java.util.Map;

public interface UserService {
    Users createUser(Users user); // <--- CHANGE return type to Users
    void addUser(Users user);
    List<UserDto> getAllUsers();
    UserDto getUserById(String agentid);
    Map<String,String> verify(Users user);
    void deleteUser(String agentid);
    void updateUser(String agentid, Users user);
    List<AgentReportDTO> getAgentReport();
    AgentPerformanceDTO getAgentPerformanceById(String agentid);
}