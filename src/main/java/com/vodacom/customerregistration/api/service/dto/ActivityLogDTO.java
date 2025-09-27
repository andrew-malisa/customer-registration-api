package com.vodacom.customerregistration.api.service.dto;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class ActivityLogDTO implements Serializable {

    private Long id;

    @NotNull
    private ActivityLog.ActionType actionType;

    @NotNull
    @Size(max = 100)
    private String entityType;

    private Long entityId;

    @Size(max = 1000)
    private String description;

    @Size(max = 45)
    private String ipAddress;

    @Size(max = 500)
    private String userAgent;

    @NotNull
    private Instant timestamp;

    @Size(max = 100)
    private String sessionId;

    private ActivityLog.ActionStatus status;

    @Size(max = 2000)
    private String errorMessage;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityLog.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActivityLog.ActionType actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ActivityLog.ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityLog.ActionStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityLogDTO)) {
            return false;
        }

        ActivityLogDTO activityLogDTO = (ActivityLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, activityLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "ActivityLogDTO{" +
            "id=" + getId() +
            ", actionType='" + getActionType() + "'" +
            ", entityType='" + getEntityType() + "'" +
            ", entityId=" + getEntityId() +
            ", description='" + getDescription() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}