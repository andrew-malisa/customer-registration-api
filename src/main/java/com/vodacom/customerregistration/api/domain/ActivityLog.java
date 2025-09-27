package com.vodacom.customerregistration.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "activity_log")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "activity_log")
public class ActivityLog extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowflake", strategy = "com.vodacom.customerregistration.api.util.SnowflakeJpaIdGenerator")
    @GeneratedValue(generator = "snowflake")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @NotNull
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 500)
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp = Instant.now();

    @Size(max = 100)
    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ActionStatus status = ActionStatus.SUCCESS;

    @Size(max = 2000)
    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    public enum ActionType {
        AGENT_REGISTERED,
        AGENT_LOGIN,
        AGENT_LOGOUT,
        AGENT_PASSWORD_CHANGED,
        AGENT_PASSWORD_RESET_REQUESTED,
        AGENT_PASSWORD_RESET_COMPLETED,
        AGENT_PROFILE_UPDATED,
        AGENT_UPDATED,
        AGENT_DELETED,
        CUSTOMER_REGISTERED,
        CUSTOMER_UPDATED,
        CUSTOMER_DELETED,
        CUSTOMER_VIEWED,
        CUSTOMER_SEARCHED,
        ADMIN_USER_CREATED,
        ADMIN_USER_UPDATED,
        ADMIN_USER_DELETED,
        ADMIN_USER_ACTIVATED,
        ADMIN_USER_DEACTIVATED,
        ADMIN_PASSWORD_RESET,
        SYSTEM_ACCESS_DENIED,
        SYSTEM_ERROR
    }

    public enum ActionStatus {
        SUCCESS,
        FAILED,
        PENDING
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityLog id(Long id) {
        this.setId(id);
        return this;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActivityLog actionType(ActionType actionType) {
        this.setActionType(actionType);
        return this;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public ActivityLog entityType(String entityType) {
        this.setEntityType(entityType);
        return this;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public ActivityLog entityId(Long entityId) {
        this.setEntityId(entityId);
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityLog description(String description) {
        this.setDescription(description);
        return this;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ActivityLog ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public ActivityLog userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public ActivityLog timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ActivityLog sessionId(String sessionId) {
        this.setSessionId(sessionId);
        return this;
    }

    public ActionStatus getStatus() {
        return this.status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public ActivityLog status(ActionStatus status) {
        this.setStatus(status);
        return this;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ActivityLog errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityLog)) {
            return false;
        }
        return getId() != null && getId().equals(((ActivityLog) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
            "id=" + getId() +
            ", actionType='" + getActionType() + "'" +
            ", entityType='" + getEntityType() + "'" +
            ", entityId=" + getEntityId() +
            ", description='" + getDescription() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}