package com.vodacom.customerregistration.api.service.impl;

import com.vodacom.customerregistration.api.domain.Agent;
import com.vodacom.customerregistration.api.domain.User;
import com.vodacom.customerregistration.api.domain.enumeration.AgentStatus;
import com.vodacom.customerregistration.api.repository.AgentRepository;
import com.vodacom.customerregistration.api.repository.UserRepository;
import com.vodacom.customerregistration.api.repository.search.AgentSearchRepository;
import com.vodacom.customerregistration.api.service.AgentService;
import com.vodacom.customerregistration.api.service.MailService;
import com.vodacom.customerregistration.api.service.UserService;
import com.vodacom.customerregistration.api.service.dto.AgentDTO;
import com.vodacom.customerregistration.api.service.dto.AgentDetailResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AdminUserDTO;
import com.vodacom.customerregistration.api.web.rest.vm.ManagedUserVM;
import com.vodacom.customerregistration.api.service.mapper.AgentMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vodacom.customerregistration.api.domain.Agent}.
 */
@Service
@Transactional
public class AgentServiceImpl implements AgentService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentServiceImpl.class);

    private final AgentRepository agentRepository;

    private final AgentMapper agentMapper;

    private final AgentSearchRepository agentSearchRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AgentServiceImpl(AgentRepository agentRepository, AgentMapper agentMapper, AgentSearchRepository agentSearchRepository,
                           UserRepository userRepository, UserService userService, MailService mailService) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
        this.agentSearchRepository = agentSearchRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    public AgentDetailResponseDTO save(AgentDTO agentDTO) {
        LOG.debug("Request to save Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        agentSearchRepository.index(agent);
        return agentMapper.toDetailResponse(agent);
    }

    @Override
    public AgentDetailResponseDTO update(AgentDTO agentDTO) {
        LOG.debug("Request to update Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        agentSearchRepository.index(agent);
        return agentMapper.toDetailResponse(agent);
    }

    @Override
    public Optional<AgentDetailResponseDTO> partialUpdate(AgentDTO agentDTO) {
        LOG.debug("Request to partially update Agent : {}", agentDTO);

        return agentRepository
            .findById(agentDTO.getId())
            .map(existingAgent -> {
                agentMapper.partialUpdate(existingAgent, agentDTO);

                return existingAgent;
            })
            .map(agentRepository::save)
            .map(savedAgent -> {
                agentSearchRepository.index(savedAgent);
                return savedAgent;
            })
            .map(agentMapper::toDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentDetailResponseDTO> findOne(UUID id) {
        LOG.debug("Request to get Agent : {}", id);
        return agentRepository.findById(id).map(agentMapper::toDetailResponse);
    }

    @Override
    public void delete(UUID id) {
        LOG.debug("Request to delete Agent : {}", id);
        agentRepository.deleteById(id);
        agentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional
    public AgentRegistrationResponseDTO registerAgentWithUser(AgentRegistrationDTO registrationDTO) {
        LOG.debug("Request to register Agent with User: {}", registrationDTO);

        AdminUserDTO adminUserDTO = agentMapper.toAdminUserDTOWithRoles(registrationDTO);

        User createdUser = userService.createUser(adminUserDTO);

        LOG.debug("Created User account: {}", createdUser.getLogin());

        Agent agent = agentMapper.toAgentFromRegistration(registrationDTO);
        agent.setUser(createdUser);

        agent = agentRepository.save(agent);
        agentSearchRepository.index(agent);

        LOG.debug("Created Agent: {} for User: {}", agent.getId(), createdUser.getLogin());

        // Send email only after successful creation
        mailService.sendCreationEmail(createdUser);

        AdminUserDTO userDTO = new AdminUserDTO(createdUser);
        return agentMapper.toRegistrationResponse(userDTO, agent);
    }

//    @Override
//    @Transactional
//    public AgentDetailResponseDTO createAgentForUser(Long userId, String phoneNumber) {
//        LOG.debug("Request to create Agent for User ID: {} with phone number: {}", userId, phoneNumber);
//
//        User user = userRepository.findById(userId)
//            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
//
//        Optional<Agent> existingAgent = agentRepository.findByUser(user);
//        if (existingAgent.isPresent()) {
//            LOG.warn("Agent already exists for User ID: {}", userId);
//            return agentMapper.toDetailResponse(existingAgent.orElseThrow());
//        }
//
//        // Create new agent
//        Agent agent = new Agent();
//        agent.setPhoneNumber(phoneNumber);
//        agent.setStatus(AgentStatus.ACTIVE);
//        agent.setUser(user);
//
//        agent = agentRepository.save(agent);
//        agentSearchRepository.index(agent);
//
//        LOG.debug("Created Agent for User: {}", agent);
//        return agentMapper.toDetailResponse(agent);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgentDetailResponseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Agents for query {}", query);
        return agentSearchRepository.search(query, pageable).map(agentMapper::toDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAutocompleteSuggestions(String query, int limit) {
        LOG.debug("Request to get autocomplete suggestions for query: {}", query);
        return agentSearchRepository.getAutocompleteSuggestions(query, limit);
    }

    @Override
    @Transactional
    public int reindexAllAgents() {
        LOG.info("Starting reindexing of all agents from database to Elasticsearch");

        try {
            List<Agent> allAgents = agentRepository.findAll();
            LOG.info("Found {} agents in database to reindex", allAgents.size());

            if (allAgents.isEmpty()) {
                return 0;
            }

            agentSearchRepository.deleteAll();

            int reindexedCount = 0;
            for (Agent agent : allAgents) {
                try {
                    agentSearchRepository.index(agent);
                    reindexedCount++;
                } catch (Exception e) {
                    LOG.error("Failed to reindex agent ID: {}", agent.getId());
                }
            }

            LOG.info("Successfully completed reindexing {} of {} agents", reindexedCount, allAgents.size());
            return reindexedCount;

        } catch (Exception e) {
            LOG.error("Failed to reindex agents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reindex agents: " + e.getMessage(), e);
        }
    }
}
