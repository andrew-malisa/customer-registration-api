package com.vodacom.customerregistration.api.config;

import com.vodacom.customerregistration.api.service.AgentService;
import com.vodacom.customerregistration.api.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Configuration class that handles post-seeding operations.
 * Specifically triggers Elasticsearch reindexing after the application is ready
 * and database seeding has completed.
 */
@Component
public class DatabaseSeederConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSeederConfiguration.class);

    private final CustomerService customerService;
    private final AgentService agentService;

    public DatabaseSeederConfiguration(CustomerService customerService, AgentService agentService) {
        this.customerService = customerService;
        this.agentService = agentService;
    }

    /**
     * Listens for ApplicationReadyEvent and triggers Elasticsearch reindexing.
     * This ensures that any data seeded during application startup is properly
     * indexed in Elasticsearch for search functionality.
     */
    @EventListener
    @Order(1000) // Run after other startup processes
    public void onApplicationReady(ApplicationReadyEvent event) {
        LOG.info("Application is ready. Checking if Elasticsearch reindexing is needed...");
        
        // Reindex customers
        try {
            int customerReindexedCount = customerService.reindexAllCustomers();
            
            if (customerReindexedCount > 0) {
                LOG.info("✓ Successfully reindexed {} customers to Elasticsearch after application startup", customerReindexedCount);
            } else {
                LOG.info("No customers found to reindex in Elasticsearch");
            }
        } catch (Exception e) {
            LOG.error("✗ Failed to reindex customers to Elasticsearch after application startup: {}", e.getMessage(), e);
            // Don't fail application startup if reindexing fails
        }
        
        // Reindex agents
        try {
            int agentReindexedCount = agentService.reindexAllAgents();
            
            if (agentReindexedCount > 0) {
                LOG.info("✓ Successfully reindexed {} agents to Elasticsearch after application startup", agentReindexedCount);
            } else {
                LOG.info("No agents found to reindex in Elasticsearch");
            }
        } catch (Exception e) {
            LOG.error("✗ Failed to reindex agents to Elasticsearch after application startup: {}", e.getMessage(), e);
            // Don't fail application startup if reindexing fails
        }
    }
}