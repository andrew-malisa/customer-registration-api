package com.vodacom.customerregistration.api.service.dto;

import com.vodacom.customerregistration.api.domain.enumeration.AgentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.vodacom.customerregistration.api.domain.Agent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentDTO implements Serializable {

    private UUID id;

    @NotNull
    @Pattern(regexp = "^(\\+255|0)[67]\\d{8}$")
    private String phoneNumber;

    @NotNull
    private AgentStatus status;

    @NotNull
    private UUID userId;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
        if (!(o instanceof AgentDTO)) {
            return false;
        }

        AgentDTO agentDTO = (AgentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentDTO{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", userId=" + getUserId() +
            ", region='" + getRegion() + "'" +
            ", district='" + getDistrict() + "'" +
            ", ward='" + getWard() + "'" +
            "}";
    }
}
