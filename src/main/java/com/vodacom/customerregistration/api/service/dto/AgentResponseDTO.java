package com.vodacom.customerregistration.api.service.dto;

import com.vodacom.customerregistration.api.domain.enumeration.AgentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A clean DTO for Agent responses when used with UserDTO (without redundant userId)
 */
public class AgentResponseDTO implements Serializable {

    private UUID id;

    @NotNull
    private AgentStatus status;

    @Size(max = 100)
    private String region;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String ward;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentResponseDTO)) {
            return false;
        }

        AgentResponseDTO agentResponseDTO = (AgentResponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agentResponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "AgentResponseDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", region='" + getRegion() + "'" +
            ", district='" + getDistrict() + "'" +
            ", ward='" + getWard() + "'" +
            "}";
    }
}
