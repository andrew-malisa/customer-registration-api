package com.vodacom.customerregistration.api.service;

import com.vodacom.customerregistration.api.domain.*; // for static metamodels
import com.vodacom.customerregistration.api.domain.Customer;
import com.vodacom.customerregistration.api.repository.CustomerRepository;
import com.vodacom.customerregistration.api.repository.search.CustomerSearchRepository;
import com.vodacom.customerregistration.api.service.criteria.CustomerCriteria;
import com.vodacom.customerregistration.api.service.dto.CustomerDTO;
import com.vodacom.customerregistration.api.service.dto.CustomerResponseDTO;
import com.vodacom.customerregistration.api.service.mapper.CustomerMapper;
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
 * Service for executing complex queries for {@link Customer} entities in the database.
 * The main input is a {@link CustomerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CustomerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerQueryService extends QueryService<Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerQueryService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerQueryService(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CustomerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCriteria(CustomerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification, page).map(customerMapper::toDto);
    }

    /**
     * Return a {@link Page} of {@link CustomerResponseDTO} with audit fields which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities with audit fields.
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> findByCriteriaWithAuditFields(CustomerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria with audit fields : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification, page).map(customerMapper::toResponseDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildSpecification(criteria.getId(), Customer_.id),
                buildStringSpecification(criteria.getFirstName(), Customer_.firstName),
                buildStringSpecification(criteria.getMiddleName(), Customer_.middleName),
                buildStringSpecification(criteria.getLastName(), Customer_.lastName),
                buildRangeSpecification(criteria.getDateOfBirth(), Customer_.dateOfBirth),
                buildStringSpecification(criteria.getNidaNumber(), Customer_.nidaNumber),
                buildStringSpecification(criteria.getRegion(), Customer_.region),
                buildStringSpecification(criteria.getDistrict(), Customer_.district),
                buildStringSpecification(criteria.getWard(), Customer_.ward)
//                buildSpecification(criteria.getRegisteredById(), root -> root.join(Customer_.registeredBy, JoinType.LEFT).get(Agent_.id))
            );
        }
        return specification;
    }
}
