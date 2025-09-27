package com.vodacom.customerregistration.api.service.mapper;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import com.vodacom.customerregistration.api.service.dto.ActivityLogDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper extends EntityMapper<ActivityLogDTO, ActivityLog> {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "actionType", target = "actionType")
    @Mapping(source = "entityType", target = "entityType")
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ipAddress", target = "ipAddress")
    @Mapping(source = "userAgent", target = "userAgent")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "lastModifiedBy", target = "lastModifiedBy")
    @Mapping(source = "lastModifiedDate", target = "lastModifiedDate")
    ActivityLogDTO toDto(ActivityLog activityLog);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "actionType", target = "actionType")
    @Mapping(source = "entityType", target = "entityType")
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ipAddress", target = "ipAddress")
    @Mapping(source = "userAgent", target = "userAgent")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "errorMessage", target = "errorMessage")
    ActivityLog toEntity(ActivityLogDTO activityLogDTO);

    default ActivityLog fromId(Long id) {
        if (id == null) {
            return null;
        }
        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(id);
        return activityLog;
    }
}