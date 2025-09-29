package com.vodacom.customerregistration.api.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.vodacom.customerregistration.api.domain.Customer;
import com.vodacom.customerregistration.api.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Customer} entity.
 */
public interface CustomerSearchRepository extends ElasticsearchRepository<Customer, Long>, CustomerSearchRepositoryInternal {}

interface CustomerSearchRepositoryInternal {
    Page<Customer> search(String query, Pageable pageable);

    Page<Customer> search(Query query);

    List<String> getAutocompleteSuggestions(String query, int limit);

    void index(Customer entity);

    void deleteFromIndexById(UUID id);
}

class CustomerSearchRepositoryInternalImpl implements CustomerSearchRepositoryInternal {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerSearchRepositoryInternalImpl.class);

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CustomerRepository repository;

    CustomerSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CustomerRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Customer> search(String query, Pageable pageable) {
        LOG.debug("Searching customers with query: '{}'", query);
        try {
            NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
            return search(nativeQuery.setPageable(pageable));
        } catch (Exception e) {
            LOG.error("Customer search failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<Customer> search(Query query) {
        try {
            SearchHits<Customer> searchHits = elasticsearchTemplate.search(query, Customer.class);
            List<Customer> hits = searchHits.map(SearchHit::getContent).stream().toList();
            return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
        } catch (Exception e) {
            LOG.error("Elasticsearch search failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<String> getAutocompleteSuggestions(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        try {
            String lowercaseQuery = query.toLowerCase().trim();
            String searchQuery = "*" + lowercaseQuery + "*";

            NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(searchQuery))._toQuery());
            nativeQuery.setPageable(PageRequest.of(0, limit * 3));

            SearchHits<Customer> searchHits = elasticsearchTemplate.search(nativeQuery, Customer.class);

            return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .flatMap(customer -> Stream.of(
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    customer.getNidaNumber()
                ))
                .filter(suggestion -> suggestion != null &&
                        suggestion.toLowerCase().contains(lowercaseQuery))
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Autocomplete failed: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void index(Customer entity) {
        try {
            if (entity.getId() == null) {
                return;
            }
            Optional<Customer> customerOpt = repository.findById(entity.getId());
            if (customerOpt.isPresent()) {
                elasticsearchTemplate.save(customerOpt.orElseThrow());
                elasticsearchTemplate.indexOps(Customer.class).refresh();
            }
        } catch (Exception e) {
            LOG.error("Failed to index customer ID {}: {}", entity.getId(), e.getMessage());
        }
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        try {
            elasticsearchTemplate.delete(String.valueOf(id), Customer.class);
            elasticsearchTemplate.indexOps(Customer.class).refresh();
        } catch (Exception e) {
            LOG.error("Failed to delete customer from Elasticsearch: {}", e.getMessage());
        }
    }
}
