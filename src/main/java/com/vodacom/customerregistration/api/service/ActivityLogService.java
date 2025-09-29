package com.vodacom.customerregistration.api.service;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import com.vodacom.customerregistration.api.repository.ActivityLogRepository;
import com.vodacom.customerregistration.api.security.SecurityUtils;
import com.vodacom.customerregistration.api.service.dto.ActivityLogDTO;
import com.vodacom.customerregistration.api.service.mapper.ActivityLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ActivityLogService {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityLogService.class);

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogService(ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    public ActivityLogDTO save(ActivityLogDTO activityLogDTO) {
        LOG.debug("Request to save ActivityLog : {}", activityLogDTO);
        ActivityLog activityLog = activityLogMapper.toEntity(activityLogDTO);
        activityLog = activityLogRepository.save(activityLog);
        return activityLogMapper.toDto(activityLog);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ActivityLogs");
        return activityLogRepository.findAll(pageable).map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ActivityLogDTO> findOne(UUID id) {
        LOG.debug("Request to get ActivityLog : {}", id);
        return activityLogRepository.findById(id).map(activityLogMapper::toDto);
    }

    public void delete(UUID id) {
        LOG.debug("Request to delete ActivityLog : {}", id);
        activityLogRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByCurrentUser(Pageable pageable) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No authenticated user found"));

        LOG.debug("Request to get ActivityLogs for user: {}", currentUserLogin);
        return activityLogRepository.findByCreatedByOrderByTimestampDesc(currentUserLogin, pageable).map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByCurrentUserAndActionType(ActivityLog.ActionType actionType, Pageable pageable) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No authenticated user found"));

        LOG.debug("Request to get ActivityLogs for user: {} with action type: {}", currentUserLogin, actionType);
        return activityLogRepository.findByCreatedByAndActionTypeOrderByTimestampDesc(currentUserLogin, actionType, pageable).map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByCurrentUserAndSearch(String searchTerm, Pageable pageable) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No authenticated user found"));

        LOG.debug("Request to search ActivityLogs for user: {} with term: {}", currentUserLogin, searchTerm);
        return activityLogRepository.findByCreatedByAndDescriptionOrEntityTypeContaining(currentUserLogin, searchTerm, pageable).map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByCurrentUserAndDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No authenticated user found"));

        LOG.debug("Request to get ActivityLogs for user: {} between {} and {}", currentUserLogin, startDate, endDate);
        return activityLogRepository.findByCreatedByAndTimestampBetweenOrderByTimestampDesc(currentUserLogin, startDate, endDate, pageable).map(activityLogMapper::toDto);
    }

    public void logActivity(ActivityLog.ActionType actionType, String entityType, UUID entityId, String description) {
        logActivity(actionType, entityType, entityId, description, ActivityLog.ActionStatus.SUCCESS, null);
    }

    public void logActivity(ActivityLog.ActionType actionType, String entityType, UUID entityId, String description, ActivityLog.ActionStatus status) {
        logActivity(actionType, entityType, entityId, description, status, null);
    }

    public void logActivity(ActivityLog.ActionType actionType, String entityType, UUID entityId, String description, ActivityLog.ActionStatus status, String errorMessage) {
        try {
            ActivityLog activityLog = new ActivityLog().actionType(actionType).entityType(entityType).entityId(entityId).description(description).timestamp(Instant.now()).status(status).errorMessage(errorMessage);

            try {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpServletRequest request = requestAttributes.getRequest();
                activityLog.setIpAddress(getClientIpAddress(request));
                activityLog.setUserAgent(request.getHeader("User-Agent"));
                activityLog.setSessionId(request.getSession(false) != null ? request.getSession(false).getId() : null);
            } catch (Exception e) {
                LOG.debug("Could not extract request details for activity log: {}", e.getMessage());
            }

            activityLogRepository.save(activityLog);
            LOG.debug("Activity logged: {} - {}", actionType, description);
        } catch (Exception e) {
            LOG.error("Failed to log activity: {}", e.getMessage(), e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedForHeader)) {
            return xForwardedForHeader.split(",")[0].trim();
        }

        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty() && !"unknown".equalsIgnoreCase(xRealIpHeader)) {
            return xRealIpHeader;
        }

        return request.getRemoteAddr();
    }

    public long countActivitiesByTypeAndSince(ActivityLog.ActionType actionType, Instant since) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No authenticated user found"));

        return activityLogRepository.countByCreatedByAndActionTypeAndTimestampAfter(currentUserLogin, actionType, since);
    }

    // Admin methods for comprehensive system-wide activity log management

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByUser(String username, Pageable pageable) {
        LOG.debug("Admin request to get ActivityLogs for user: {}", username);
        return activityLogRepository.findByCreatedByOrderByTimestampDesc(username, pageable)
                .map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByActionType(ActivityLog.ActionType actionType, Pageable pageable) {
        LOG.debug("Admin request to get ActivityLogs by action type: {}", actionType);
        return activityLogRepository.findByActionTypeOrderByTimestampDesc(actionType, pageable)
                .map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> searchAll(String searchTerm, Pageable pageable) {
        LOG.debug("Admin request to search all ActivityLogs with term: {}", searchTerm);
        return activityLogRepository.findByDescriptionContainingIgnoreCaseOrEntityTypeContainingIgnoreCaseOrderByTimestampDesc(searchTerm, searchTerm, pageable)
                .map(activityLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        LOG.debug("Admin request to get ActivityLogs between {} and {}", startDate, endDate);
        return activityLogRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate, pageable)
                .map(activityLogMapper::toDto);
    }

    public long countByActionTypeAndSince(ActivityLog.ActionType actionType, Instant since) {
        return activityLogRepository.countByActionTypeAndTimestampAfter(actionType, since);
    }
}
