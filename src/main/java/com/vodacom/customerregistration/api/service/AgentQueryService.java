package com.vodacom.customerregistration.api.service;

import com.vodacom.customerregistration.api.domain.*; // for static metamodels
import com.vodacom.customerregistration.api.domain.Agent;
import com.vodacom.customerregistration.api.repository.AgentRepository;
import com.vodacom.customerregistration.api.repository.search.AgentSearchRepository;
import com.vodacom.customerregistration.api.service.criteria.AgentCriteria;
import com.vodacom.customerregistration.api.service.dto.AgentDTO;
import com.vodacom.customerregistration.api.service.mapper.AgentMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Agent} entities in the database.
 * The main input is a {@link AgentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AgentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AgentQueryService extends QueryService<Agent> {

    private static final Logger LOG = LoggerFactory.getLogger(AgentQueryService.class);

    private final AgentRepository agentRepository;

    private final AgentMapper agentMapper;

    private final AgentSearchRepository agentSearchRepository;

    public AgentQueryService(AgentRepository agentRepository, AgentMapper agentMapper, AgentSearchRepository agentSearchRepository) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
        this.agentSearchRepository = agentSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link AgentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AgentDTO> findByCriteria(AgentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Agent> specification = createSpecification(criteria);
        return agentRepository.findAll(specification, page).map(agentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AgentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Agent> specification = createSpecification(criteria);
        return agentRepository.count(specification);
    }

    /**
     * Function to convert {@link AgentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Agent> createSpecification(AgentCriteria criteria) {
        Specification<Agent> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildSpecification(criteria.getId(), Agent_.id),
                buildStringSpecification(criteria.getPhoneNumber(), Agent_.phoneNumber),
                buildSpecification(criteria.getStatus(), Agent_.status),
                buildSpecification(criteria.getUserId(), root -> root.join(Agent_.user, JoinType.LEFT).get(User_.id)),
                buildStringSpecification(criteria.getRegion(), Agent_.region),
                buildStringSpecification(criteria.getDistrict(), Agent_.district),
                buildStringSpecification(criteria.getWard(), Agent_.ward)
            );
        }
        return specification;
    }
}
