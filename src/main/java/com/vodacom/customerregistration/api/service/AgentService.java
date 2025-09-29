package com.vodacom.customerregistration.api.service;

import com.vodacom.customerregistration.api.service.dto.AgentDTO;
import com.vodacom.customerregistration.api.service.dto.AgentDetailResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationResponseDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.vodacom.customerregistration.api.domain.Agent}.
 */
public interface AgentService {
    /**
     * Save a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity with complete user details.
     */
    AgentDetailResponseDTO save(AgentDTO agentDTO);

    /**
     * Updates a agent.
     *
     * @param agentDTO the entity to update.
     * @return the persisted entity with complete user details.
     */
    AgentDetailResponseDTO update(AgentDTO agentDTO);

    /**
     * Partially updates a agent.
     *
     * @param agentDTO the entity to update partially.
     * @return the persisted entity with complete user details.
     */
    Optional<AgentDetailResponseDTO> partialUpdate(AgentDTO agentDTO);

    /**
     * Get the "id" agent with complete user details.
     *
     * @param id the id of the entity.
     * @return the entity with user details.
     */
    Optional<AgentDetailResponseDTO> findOne(UUID id);

    /**
     * Delete the "id" agent.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);

    /**
     * Register a new agent with associated user account in a single transaction.
     * Creates both User (for authentication) and Agent (for business logic) entities.
     *
     * @param registrationDTO the registration data containing both user and agent information.
     * @return the response containing both created user and agent.
     */
    AgentRegistrationResponseDTO registerAgentWithUser(AgentRegistrationDTO registrationDTO);

    /**
     * Create an agent for a user with phone number.
     *
     * @param userId the user ID to link the agent to.
     * @param phoneNumber the agent's phone number.
     * @return the created agent with complete user details.
     */
//    AgentDetailResponseDTO createAgentForUser(Long userId, String phoneNumber);

    /**
     * Search for agents with detailed user information.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities with user details.
     */
    Page<AgentDetailResponseDTO> search(String query, Pageable pageable);

    /**
     * Get autocomplete suggestions for agent search.
     *
     * @param query the partial query for autocomplete suggestions.
     * @param limit the maximum number of suggestions to return.
     * @return the list of autocomplete suggestions.
     */
    List<String> getAutocompleteSuggestions(String query, int limit);

    /**
     * Reindex all agents from database to Elasticsearch.
     *
     * @return the number of agents reindexed.
     */
    int reindexAllAgents();
}
