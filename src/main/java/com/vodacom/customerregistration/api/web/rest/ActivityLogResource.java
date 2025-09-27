package com.vodacom.customerregistration.api.web.rest;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import com.vodacom.customerregistration.api.service.ActivityLogService;
import com.vodacom.customerregistration.api.service.dto.ActivityLogDTO;
import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Tag(name = "Activity Log", description = "APIs for accessing agent activity logs and audit trail")
@RestController
@RequestMapping("/api/v1/activity-logs")
public class ActivityLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityLogResource.class);

    private final ActivityLogService activityLogService;

    public ActivityLogResource(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @Operation(summary = "Get current agent's activity history",
        description = "Retrieve paginated activity logs for the currently authenticated agent. Shows all actions performed by the agent including customer registrations, login/logout, profile changes, etc.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Activity logs retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Agent not authenticated"
        )
    })
    @GetMapping("/me")
    public ResponseEntity<List<ActivityLogDTO>> getMyActivityLogs(
        @Parameter(description = "Pagination parameters", example = "page=0&size=20&sort=timestamp,desc")
        @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        LOG.debug("REST request to get current user's activity logs");

        Page<ActivityLogDTO> page = activityLogService.findByCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok()
            .headers(headers)
            .body(page.getContent());
    }

    @Operation(summary = "Get activity logs by action type",
        description = "Filter activity logs by specific action type (e.g., CUSTOMER_REGISTERED, AGENT_LOGIN, etc.)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Filtered activity logs retrieved successfully"
        )
    })
    @GetMapping("/me/by-action/{actionType}")
    public ResponseEntity<List<ActivityLogDTO>> getMyActivityLogsByAction(
        @Parameter(description = "Action type to filter by", example = "CUSTOMER_REGISTERED")
        @PathVariable ActivityLog.ActionType actionType,
        @Parameter(description = "Pagination parameters")
        @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        LOG.debug("REST request to get activity logs by action type: {}", actionType);

        Page<ActivityLogDTO> page = activityLogService.findByCurrentUserAndActionType(actionType, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok()
            .headers(headers)
            .body(page.getContent());
    }

    @Operation(summary = "Search activity logs",
        description = "Search through activity logs by description or entity type")
    @GetMapping("/me/search")
    public ResponseEntity<List<ActivityLogDTO>> searchMyActivityLogs(
        @Parameter(description = "Search term to look for in description or entity type", example = "customer registration")
        @RequestParam String searchTerm,
        @Parameter(description = "Pagination parameters")
        @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        LOG.debug("REST request to search activity logs with term: {}", searchTerm);

        Page<ActivityLogDTO> page = activityLogService.findByCurrentUserAndSearch(searchTerm, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok()
            .headers(headers)
            .body(page.getContent());
    }

    @Operation(summary = "Get activity logs by date range",
        description = "Filter activity logs by date range")
    @GetMapping("/me/date-range")
    public ResponseEntity<List<ActivityLogDTO>> getMyActivityLogsByDateRange(
        @Parameter(description = "Start date (inclusive)", example = "2024-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date (inclusive)", example = "2024-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @Parameter(description = "Pagination parameters")
        @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        LOG.debug("REST request to get activity logs between {} and {}", startDate, endDate);

        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        Page<ActivityLogDTO> page = activityLogService.findByCurrentUserAndDateRange(startInstant, endInstant, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok()
            .headers(headers)
            .body(page.getContent());
    }

    @Operation(summary = "Get activity statistics",
        description = "Get summary statistics about agent activities for different time periods")
    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<ActivityStatsDTO>> getMyActivityStats() {
        LOG.debug("REST request to get activity statistics");

        Instant now = Instant.now();
        Instant dayAgo = now.minusSeconds(24 * 60 * 60);
        Instant weekAgo = now.minusSeconds(7 * 24 * 60 * 60);
        Instant monthAgo = now.minusSeconds(30L * 24 * 60 * 60);

        ActivityStatsDTO stats = new ActivityStatsDTO();
        stats.setCustomersRegisteredToday(activityLogService.countActivitiesByTypeAndSince(ActivityLog.ActionType.CUSTOMER_REGISTERED, dayAgo));
        stats.setCustomersRegisteredThisWeek(activityLogService.countActivitiesByTypeAndSince(ActivityLog.ActionType.CUSTOMER_REGISTERED, weekAgo));
        stats.setCustomersRegisteredThisMonth(activityLogService.countActivitiesByTypeAndSince(ActivityLog.ActionType.CUSTOMER_REGISTERED, monthAgo));
        stats.setLoginsToday(activityLogService.countActivitiesByTypeAndSince(ActivityLog.ActionType.AGENT_LOGIN, dayAgo));
        stats.setLoginsThisWeek(activityLogService.countActivitiesByTypeAndSince(ActivityLog.ActionType.AGENT_LOGIN, weekAgo));

        return ResponseEntity.ok().body(ApiResponse.success("Activity statistics retrieved successfully", stats));
    }

    // Admin endpoints for comprehensive audit trail management
    
    @Operation(summary = "Get all activity logs (Admin)", 
               description = "Admin endpoint to retrieve all activity logs across the system with filtering and pagination.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "All activity logs retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Admin access required"
        )
    })
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<Page<ActivityLogDTO>>> getAllActivityLogs(
            @Parameter(description = "Pagination parameters") 
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        
        LOG.debug("Admin request to get all activity logs");
        
        Page<ActivityLogDTO> page = activityLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success("All activity logs retrieved successfully", page));
    }

    @Operation(summary = "Get activity logs by user (Admin)", 
               description = "Admin endpoint to retrieve activity logs for a specific user.")
    @GetMapping("/admin/user/{username}")
    public ResponseEntity<ApiResponse<Page<ActivityLogDTO>>> getActivityLogsByUser(
            @Parameter(description = "Username to filter by", required = true, example = "agent.doe")
            @PathVariable String username,
            @Parameter(description = "Pagination parameters")
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        
        LOG.debug("Admin request to get activity logs for user: {}", username);
        
        Page<ActivityLogDTO> page = activityLogService.findByUser(username, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success("Activity logs for user retrieved successfully", page));
    }

    @Operation(summary = "Get activity logs by action type (Admin)", 
               description = "Admin endpoint to retrieve all activity logs filtered by action type.")
    @GetMapping("/admin/by-action/{actionType}")
    public ResponseEntity<ApiResponse<Page<ActivityLogDTO>>> getActivityLogsByActionType(
            @Parameter(description = "Action type to filter by", example = "AGENT_REGISTERED")
            @PathVariable ActivityLog.ActionType actionType,
            @Parameter(description = "Pagination parameters")
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        
        LOG.debug("Admin request to get activity logs by action type: {}", actionType);
        
        Page<ActivityLogDTO> page = activityLogService.findByActionType(actionType, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success("Activity logs by action type retrieved successfully", page));
    }

    @Operation(summary = "Search all activity logs (Admin)", 
               description = "Admin endpoint to search across all activity logs by description or entity type.")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<Page<ActivityLogDTO>>> searchAllActivityLogs(
            @Parameter(description = "Search term to look for in description or entity type", example = "agent registration")
            @RequestParam String searchTerm,
            @Parameter(description = "Pagination parameters")
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        
        LOG.debug("Admin request to search all activity logs with term: {}", searchTerm);
        
        Page<ActivityLogDTO> page = activityLogService.searchAll(searchTerm, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success("Activity logs search completed successfully", page));
    }

    @Operation(summary = "Get system-wide activity statistics (Admin)", 
               description = "Admin endpoint to get comprehensive system-wide activity statistics.")
    @GetMapping("/admin/system-stats")
    public ResponseEntity<ApiResponse<SystemActivityStatsDTO>> getSystemActivityStats() {
        LOG.debug("Admin request to get system-wide activity statistics");
        
        Instant now = Instant.now();
        Instant dayAgo = now.minusSeconds(24 * 60 * 60);
        Instant weekAgo = now.minusSeconds(7 * 24 * 60 * 60);
        Instant monthAgo = now.minusSeconds(30L * 24 * 60 * 60);
        
        SystemActivityStatsDTO stats = new SystemActivityStatsDTO();
        stats.setTotalAgentsRegisteredToday(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.AGENT_REGISTERED, dayAgo));
        stats.setTotalAgentsRegisteredThisWeek(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.AGENT_REGISTERED, weekAgo));
        stats.setTotalAgentsRegisteredThisMonth(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.AGENT_REGISTERED, monthAgo));
        stats.setTotalCustomersRegisteredToday(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.CUSTOMER_REGISTERED, dayAgo));
        stats.setTotalCustomersRegisteredThisWeek(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.CUSTOMER_REGISTERED, weekAgo));
        stats.setTotalCustomersRegisteredThisMonth(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.CUSTOMER_REGISTERED, monthAgo));
        stats.setTotalLoginsToday(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.AGENT_LOGIN, dayAgo));
        stats.setTotalLoginsThisWeek(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.AGENT_LOGIN, weekAgo));
        stats.setTotalLoginsThisMonth(activityLogService.countByActionTypeAndSince(ActivityLog.ActionType.AGENT_LOGIN, monthAgo));
        
        return ResponseEntity.ok().body(ApiResponse.success("System activity statistics retrieved successfully", stats));
    }

    @Operation(summary = "Get activity logs by date range (Admin)", 
               description = "Admin endpoint to filter all activity logs by date range.")
    @GetMapping("/admin/date-range")
    public ResponseEntity<ApiResponse<Page<ActivityLogDTO>>> getActivityLogsByDateRange(
            @Parameter(description = "Start date (inclusive)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Pagination parameters")
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        
        LOG.debug("Admin request to get activity logs between {} and {}", startDate, endDate);
        
        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        
        Page<ActivityLogDTO> page = activityLogService.findByDateRange(startInstant, endInstant, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success("Activity logs by date range retrieved successfully", page));
    }

    public static class SystemActivityStatsDTO {
        private long totalAgentsRegisteredToday;
        private long totalAgentsRegisteredThisWeek;
        private long totalAgentsRegisteredThisMonth;
        private long totalCustomersRegisteredToday;
        private long totalCustomersRegisteredThisWeek;
        private long totalCustomersRegisteredThisMonth;
        private long totalLoginsToday;
        private long totalLoginsThisWeek;
        private long totalLoginsThisMonth;

        public long getTotalAgentsRegisteredToday() { return totalAgentsRegisteredToday; }
        public void setTotalAgentsRegisteredToday(long totalAgentsRegisteredToday) { this.totalAgentsRegisteredToday = totalAgentsRegisteredToday; }
        
        public long getTotalAgentsRegisteredThisWeek() { return totalAgentsRegisteredThisWeek; }
        public void setTotalAgentsRegisteredThisWeek(long totalAgentsRegisteredThisWeek) { this.totalAgentsRegisteredThisWeek = totalAgentsRegisteredThisWeek; }
        
        public long getTotalAgentsRegisteredThisMonth() { return totalAgentsRegisteredThisMonth; }
        public void setTotalAgentsRegisteredThisMonth(long totalAgentsRegisteredThisMonth) { this.totalAgentsRegisteredThisMonth = totalAgentsRegisteredThisMonth; }
        
        public long getTotalCustomersRegisteredToday() { return totalCustomersRegisteredToday; }
        public void setTotalCustomersRegisteredToday(long totalCustomersRegisteredToday) { this.totalCustomersRegisteredToday = totalCustomersRegisteredToday; }
        
        public long getTotalCustomersRegisteredThisWeek() { return totalCustomersRegisteredThisWeek; }
        public void setTotalCustomersRegisteredThisWeek(long totalCustomersRegisteredThisWeek) { this.totalCustomersRegisteredThisWeek = totalCustomersRegisteredThisWeek; }
        
        public long getTotalCustomersRegisteredThisMonth() { return totalCustomersRegisteredThisMonth; }
        public void setTotalCustomersRegisteredThisMonth(long totalCustomersRegisteredThisMonth) { this.totalCustomersRegisteredThisMonth = totalCustomersRegisteredThisMonth; }
        
        public long getTotalLoginsToday() { return totalLoginsToday; }
        public void setTotalLoginsToday(long totalLoginsToday) { this.totalLoginsToday = totalLoginsToday; }
        
        public long getTotalLoginsThisWeek() { return totalLoginsThisWeek; }
        public void setTotalLoginsThisWeek(long totalLoginsThisWeek) { this.totalLoginsThisWeek = totalLoginsThisWeek; }
        
        public long getTotalLoginsThisMonth() { return totalLoginsThisMonth; }
        public void setTotalLoginsThisMonth(long totalLoginsThisMonth) { this.totalLoginsThisMonth = totalLoginsThisMonth; }
    }

    public static class ActivityStatsDTO {
        private long customersRegisteredToday;
        private long customersRegisteredThisWeek;
        private long customersRegisteredThisMonth;
        private long loginsToday;
        private long loginsThisWeek;

        public long getCustomersRegisteredToday() { return customersRegisteredToday; }
        public void setCustomersRegisteredToday(long customersRegisteredToday) { this.customersRegisteredToday = customersRegisteredToday; }

        public long getCustomersRegisteredThisWeek() { return customersRegisteredThisWeek; }
        public void setCustomersRegisteredThisWeek(long customersRegisteredThisWeek) { this.customersRegisteredThisWeek = customersRegisteredThisWeek; }

        public long getCustomersRegisteredThisMonth() { return customersRegisteredThisMonth; }
        public void setCustomersRegisteredThisMonth(long customersRegisteredThisMonth) { this.customersRegisteredThisMonth = customersRegisteredThisMonth; }

        public long getLoginsToday() { return loginsToday; }
        public void setLoginsToday(long loginsToday) { this.loginsToday = loginsToday; }

        public long getLoginsThisWeek() { return loginsThisWeek; }
        public void setLoginsThisWeek(long loginsThisWeek) { this.loginsThisWeek = loginsThisWeek; }
    }
}
