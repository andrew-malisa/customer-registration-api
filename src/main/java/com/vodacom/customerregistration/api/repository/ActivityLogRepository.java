package com.vodacom.customerregistration.api.repository;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID>, JpaSpecificationExecutor<ActivityLog> {

    Page<ActivityLog> findByCreatedByOrderByTimestampDesc(String createdBy, Pageable pageable);

    Page<ActivityLog> findByCreatedByAndActionTypeOrderByTimestampDesc(String createdBy, ActivityLog.ActionType actionType, Pageable pageable);

    Page<ActivityLog> findByCreatedByAndEntityTypeOrderByTimestampDesc(String createdBy, String entityType, Pageable pageable);

    Page<ActivityLog> findByCreatedByAndTimestampBetweenOrderByTimestampDesc(String createdBy, Instant startDate, Instant endDate, Pageable pageable);

    @Query("SELECT al FROM ActivityLog al WHERE al.createdBy = :createdBy AND " +
           "(LOWER(al.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(al.entityType) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY al.timestamp DESC")
    Page<ActivityLog> findByCreatedByAndDescriptionOrEntityTypeContaining(@Param("createdBy") String createdBy,
                                                                          @Param("searchTerm") String searchTerm,
                                                                          Pageable pageable);

    List<ActivityLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, UUID entityId);

    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.createdBy = :createdBy AND al.actionType = :actionType AND al.timestamp >= :since")
    long countByCreatedByAndActionTypeAndTimestampAfter(@Param("createdBy") String createdBy,
                                                        @Param("actionType") ActivityLog.ActionType actionType,
                                                        @Param("since") Instant since);

    // Admin repository methods for system-wide queries

    Page<ActivityLog> findByActionTypeOrderByTimestampDesc(ActivityLog.ActionType actionType, Pageable pageable);

    Page<ActivityLog> findByDescriptionContainingIgnoreCaseOrEntityTypeContainingIgnoreCaseOrderByTimestampDesc(
            String descriptionTerm, String entityTypeTerm, Pageable pageable);

    Page<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(Instant startDate, Instant endDate, Pageable pageable);

    long countByActionTypeAndTimestampAfter(ActivityLog.ActionType actionType, Instant since);
}
