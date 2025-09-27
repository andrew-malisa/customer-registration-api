package com.vodacom.customerregistration.api.repository;

import com.vodacom.customerregistration.api.domain.Agent;
import com.vodacom.customerregistration.api.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Agent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {
    
    /**
     * Find an agent by user.
     *
     * @param user the user to find agent for.
     * @return Optional agent associated with the user.
     */
    Optional<Agent> findByUser(User user);
}
