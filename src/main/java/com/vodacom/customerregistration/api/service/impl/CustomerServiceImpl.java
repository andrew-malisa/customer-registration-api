package com.vodacom.customerregistration.api.service.impl;

import com.vodacom.customerregistration.api.domain.Customer;
import com.vodacom.customerregistration.api.repository.CustomerRepository;
import com.vodacom.customerregistration.api.repository.search.CustomerSearchRepository;
import com.vodacom.customerregistration.api.service.CustomerService;
import com.vodacom.customerregistration.api.service.dto.CustomerDTO;
import com.vodacom.customerregistration.api.service.mapper.CustomerMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vodacom.customerregistration.api.domain.Customer}.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerServiceImpl(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    @Override
    public CustomerDTO save(CustomerDTO customerDTO) {
        LOG.debug("Request to save Customer : {}", customerDTO);
        Customer customer = customerMapper.toEntity(customerDTO);
        customer = customerRepository.save(customer);
        customerSearchRepository.index(customer);
        return customerMapper.toDto(customer);
    }

    @Override
    public CustomerDTO update(CustomerDTO customerDTO) {
        LOG.debug("Request to update Customer : {}", customerDTO);
        Customer customer = customerMapper.toEntity(customerDTO);
        customer = customerRepository.save(customer);
        customerSearchRepository.index(customer);
        return customerMapper.toDto(customer);
    }

    @Override
    public Optional<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        LOG.debug("Request to partially update Customer : {}", customerDTO);

        return customerRepository
            .findById(customerDTO.getId())
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);

                return existingCustomer;
            })
            .map(customerRepository::save)
            .map(savedCustomer -> {
                customerSearchRepository.index(savedCustomer);
                return savedCustomer;
            })
            .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findOne(Long id) {
        LOG.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Customer : {}", id);
        customerRepository.deleteById(id);
        customerSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Customers for query {}", query);
        return customerSearchRepository.search(query, pageable).map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAutocompleteSuggestions(String query, int limit) {
        LOG.debug("Request to get autocomplete suggestions for query: {}", query);
        return customerSearchRepository.getAutocompleteSuggestions(query, limit);
    }

    @Override
    @Transactional
    public int reindexAllCustomers() {
        LOG.info("Starting reindexing of all customers from database to Elasticsearch");

        try {
            // Get all customers from database
            List<Customer> allCustomers = customerRepository.findAll();
            LOG.info("Found {} customers in database to reindex", allCustomers.size());

            if (allCustomers.isEmpty()) {
                LOG.info("No customers found in database to reindex");
                return 0;
            }

            // Clear existing Elasticsearch index
            LOG.info("Clearing existing Elasticsearch index...");
            customerSearchRepository.deleteAll();

            // Reindex all customers
            int reindexedCount = 0;
            for (Customer customer : allCustomers) {
                try {
                    LOG.debug("Reindexing customer ID: {} - '{} {}'", customer.getId(), customer.getFirstName(), customer.getLastName());
                    customerSearchRepository.index(customer);
                    reindexedCount++;

                    // Log progress every 100 customers
                    if (reindexedCount % 100 == 0) {
                        LOG.info("Reindexed {} of {} customers", reindexedCount, allCustomers.size());
                    }
                } catch (Exception e) {
                    LOG.error("Failed to reindex customer ID: {} - Error: {}", customer.getId(), e.getMessage());
                    // Continue with other customers even if one fails
                }
            }

            LOG.info("✓ Successfully completed reindexing {} of {} customers", reindexedCount, allCustomers.size());
            return reindexedCount;

        } catch (Exception e) {
            LOG.error("✗ Failed to reindex customers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reindex customers: " + e.getMessage(), e);
        }
    }
}
