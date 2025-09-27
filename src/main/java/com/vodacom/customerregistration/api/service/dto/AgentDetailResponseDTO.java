package com.vodacom.customerregistration.api.service.dto;

import java.io.Serializable;

/**
 * DTO for comprehensive Agent response containing both User and Agent details
 */
public class AgentDetailResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private AdminUserDTO user;
     private AgentResponseDTO agent;

    public AgentDetailResponseDTO() {}

    public AgentDetailResponseDTO(AdminUserDTO user, AgentResponseDTO agent) {
        this.user = user;
        this.agent = agent;
    }

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
        return "AgentDetailResponseDTO{" +
            "user=" + user +
            ", agent=" + agent +
            '}';
    }
}