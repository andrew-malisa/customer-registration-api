package com.vodacom.customerregistration.api.service.mapper;

import com.vodacom.customerregistration.api.domain.Agent;
import com.vodacom.customerregistration.api.service.dto.AgentDTO;
import com.vodacom.customerregistration.api.service.dto.AgentDetailResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AgentResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AdminUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "userId", source = "user.id")
    AgentDTO toDto(Agent s);

    @Mapping(target = "user", ignore = true)
    Agent toEntity(AgentDTO agentDTO);

    @Mapping(target = "activated", constant = "true")
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    AdminUserDTO toAdminUserDTO(AgentRegistrationDTO registrationDTO);
    
    default AdminUserDTO toAdminUserDTOWithRoles(AgentRegistrationDTO registrationDTO) {
        AdminUserDTO userDTO = toAdminUserDTO(registrationDTO);
        if (userDTO != null) {
            userDTO.setAuthorities(java.util.Set.of("ROLE_AGENT"));
        }
        return userDTO;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Agent toAgentFromRegistration(AgentRegistrationDTO registrationDTO);

    // Mapping to clean AgentResponseDTO (without phoneNumber and userId)
    AgentResponseDTO toResponseDto(Agent agent);

    default AgentDetailResponseDTO toDetailResponse(Agent agent) {
        if (agent == null) {
            return null;
        }
        AdminUserDTO userDTO = agent.getUser() != null ? new AdminUserDTO(agent.getUser()) : null;
        AgentResponseDTO agentResponseDTO = toResponseDto(agent);
        return new AgentDetailResponseDTO(userDTO, agentResponseDTO);
    }

    default AgentRegistrationResponseDTO toRegistrationResponse(AdminUserDTO userDTO, Agent agent) {
        if (agent == null || userDTO == null) {
            return null;
        }
        AgentResponseDTO agentResponseDTO = toResponseDto(agent);
        return new AgentRegistrationResponseDTO(userDTO, agentResponseDTO);
    }
}
