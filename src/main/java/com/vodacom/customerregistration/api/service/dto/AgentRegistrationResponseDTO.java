package com.vodacom.customerregistration.api.service.dto;

import java.io.Serializable;

/**
 * DTO for Agent registration response containing both User and Agent data
 */
public class AgentRegistrationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private AdminUserDTO user;
    private AgentResponseDTO agent;

    // Constructors
    public AgentRegistrationResponseDTO() {}

    public AgentRegistrationResponseDTO(AdminUserDTO user, AgentResponseDTO agent) {
        this.user = user;
        this.agent = agent;
    }

    // Getters and Setters
    public AdminUserDTO getUser() {
        return user;
    }

    public void setUser(AdminUserDTO user) {
        this.user = user;
    }

    public AgentResponseDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentResponseDTO agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        return "AgentRegistrationResponseDTO{" +
            "user=" + user +
            ", agent=" + agent +
            '}';
    }
}