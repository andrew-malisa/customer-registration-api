package com.vodacom.customerregistration.api.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.vodacom.customerregistration.api.domain.Agent;
import com.vodacom.customerregistration.api.repository.AgentRepository;
import java.util.List;
import java.util.Optional;
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
 * Spring Data Elasticsearch repository for the {@link Agent} entity.
 */
public interface AgentSearchRepository extends ElasticsearchRepository<Agent, Long>, AgentSearchRepositoryInternal {}

interface AgentSearchRepositoryInternal {
    Page<Agent> search(String query, Pageable pageable);

    Page<Agent> search(Query query);

    List<String> getAutocompleteSuggestions(String query, int limit);

//    List<Agent> getRecommendations(Long agentId, int limit);

    void index(Agent entity);

    void deleteFromIndexById(Long id);
}

class AgentSearchRepositoryInternalImpl implements AgentSearchRepositoryInternal {

    private static final Logger LOG = LoggerFactory.getLogger(AgentSearchRepositoryInternalImpl.class);

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AgentRepository repository;

    AgentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AgentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
        LOG.info("AgentSearchRepositoryInternalImpl initialized with ES template: {}, Agent repository: {}",
            elasticsearchTemplate != null ? "OK" : "NULL",
            repository != null ? "OK" : "NULL");
    }

    @Override
    public Page<Agent> search(String query, Pageable pageable) {
        LOG.info("=== ELASTICSEARCH AGENT SEARCH START ===");
        LOG.info("Search query: '{}', page: {}, size: {}", query, pageable.getPageNumber(), pageable.getPageSize());

        try {
            NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
            LOG.info("Created native query for Elasticsearch");

            Page<Agent> result = search(nativeQuery.setPageable(pageable));
            LOG.info("✓ Search completed with {} results", result.getTotalElements());

            return result;

        } catch (Exception e) {
            LOG.error("✗ Search failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<Agent> search(Query query) {
        LOG.info("Executing search query against Elasticsearch...");

        try {
            SearchHits<Agent> searchHits = elasticsearchTemplate.search(query, Agent.class);
            LOG.info("Elasticsearch returned {} hits", searchHits.getTotalHits());

            List<Agent> hits = searchHits.map(SearchHit::getContent).stream().toList();
            LOG.info("Converted {} search hits to Agent objects", hits.size());

            if (!hits.isEmpty()) {
                LOG.info("Sample results:");
                hits.stream().limit(3).forEach(agent ->
                    LOG.info("  - ID: {}, Phone: '{}'", agent.getId(), agent.getPhoneNumber())
                );
            }

            Page<Agent> result = new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
            LOG.info("=== ELASTICSEARCH AGENT SEARCH END ===");
            return result;

        } catch (Exception e) {
            LOG.error("✗ Elasticsearch search execution failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<String> getAutocompleteSuggestions(String query, int limit) {
        LOG.info("=== ELASTICSEARCH AGENT AUTOCOMPLETE START ===");
        LOG.info("Autocomplete query: '{}', limit: {}", query, limit);

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        try {
            String lowercaseQuery = query.toLowerCase().trim();
            LOG.info("Processing autocomplete for lowercase query: '{}'", lowercaseQuery);

            String searchQuery = "*" + lowercaseQuery + "*";
            LOG.info("Using search query: '{}'", searchQuery);

            NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(searchQuery))._toQuery());
            nativeQuery.setPageable(PageRequest.of(0, limit * 3));

            LOG.info("Executing autocomplete search...");
            SearchHits<Agent> searchHits = elasticsearchTemplate.search(nativeQuery, Agent.class);
            LOG.info("Elasticsearch returned {} hits for autocomplete", searchHits.getTotalHits());

            List<String> suggestions = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .peek(agent -> LOG.debug("Processing agent: phoneNumber='{}'", agent.getPhoneNumber()))
                .flatMap(agent -> Stream.of(
                    agent.getPhoneNumber()
                ))
                .filter(suggestion -> suggestion != null &&
                        suggestion.toLowerCase().contains(lowercaseQuery))
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());

            LOG.info("✓ Autocomplete completed with {} suggestions: {}", suggestions.size(), suggestions);
            return suggestions;

        } catch (Exception e) {
            LOG.error("✗ Autocomplete failed: {}", e.getMessage(), e);
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public void index(Agent entity) {
        LOG.info("=== ELASTICSEARCH AGENT INDEXING START ===");
        LOG.info("Attempting to index agent - ID: {}, Phone: '{}'", entity.getId(), entity.getPhoneNumber());

        try {
            if (entity.getId() == null) {
                LOG.error("✗ Cannot index agent - ID is null");
                return;
            }

            LOG.info("Fetching agent from database for indexing...");
            Optional<Agent> agentOpt = repository.findById(entity.getId());

            if (agentOpt.isPresent()) {
                Agent agent = agentOpt.orElseThrow();
                LOG.info("✓ Found agent in database: ID={}, Phone='{}'", agent.getId(), agent.getPhoneNumber());
                LOG.debug("Full agent details: {}", agent);

                LOG.info("Saving agent to Elasticsearch...");
                Agent savedAgent = elasticsearchTemplate.save(agent);
                LOG.info("✓ Agent saved to Elasticsearch with ID: {}", savedAgent.getId());

                LOG.info("Refreshing Elasticsearch index...");
                elasticsearchTemplate.indexOps(Agent.class).refresh();
                LOG.info("✓ Elasticsearch index refreshed - agent should be immediately searchable");

                LOG.info("Verifying indexing by searching for the agent...");
                try {
                    Optional<Agent> indexedAgent = Optional.ofNullable(elasticsearchTemplate.get(String.valueOf(agent.getId()), Agent.class));
                    if (indexedAgent.isPresent()) {
                        LOG.info("✓ VERIFICATION SUCCESS: Agent found in Elasticsearch index");
                    } else {
                        LOG.error("✗ VERIFICATION FAILED: Agent not found in Elasticsearch index after save");
                    }
                } catch (Exception verifyEx) {
                    LOG.error("✗ VERIFICATION ERROR: Could not verify indexing: {}", verifyEx.getMessage());
                }

            } else {
                LOG.error("✗ Agent not found in database with ID: {} - cannot index", entity.getId());
            }

        } catch (Exception e) {
            LOG.error("✗ Failed to index agent ID {}: {}", entity.getId(), e.getMessage(), e);
            LOG.error("Full indexing exception:", e);
        }

        LOG.info("=== ELASTICSEARCH AGENT INDEXING END ===");
    }

    @Override
    public void deleteFromIndexById(Long id) {
        LOG.info("=== ELASTICSEARCH AGENT DELETE START ===");
        LOG.info("Attempting to delete agent from index - ID: {}", id);

        try {
            elasticsearchTemplate.delete(String.valueOf(id), Agent.class);
            LOG.info("✓ Agent deleted from Elasticsearch index");

            elasticsearchTemplate.indexOps(Agent.class).refresh();
            LOG.info("✓ Elasticsearch index refreshed after deletion");

        } catch (Exception e) {
            LOG.error("✗ Failed to delete agent from Elasticsearch: {}", e.getMessage(), e);
        }

        LOG.info("=== ELASTICSEARCH AGENT DELETE END ===");
    }
}
