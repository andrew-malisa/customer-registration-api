package com.vodacom.customerregistration.api.web.rest;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import com.vodacom.customerregistration.api.repository.CustomerRepository;
import com.vodacom.customerregistration.api.service.ActivityLogService;
import com.vodacom.customerregistration.api.service.CustomerQueryService;
import com.vodacom.customerregistration.api.service.CustomerService;
import com.vodacom.customerregistration.api.service.criteria.CustomerCriteria;
import com.vodacom.customerregistration.api.service.dto.CustomerDTO;
import com.vodacom.customerregistration.api.web.rest.errors.BadRequestAlertException;
import com.vodacom.customerregistration.api.web.rest.errors.ElasticsearchExceptionMapper;
import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.vodacom.customerregistration.api.domain.Customer}.
 */
@Tag(name = "Customer Management", description = "APIs for managing customer records in the system. Includes CRUD operations, search functionality, and recommendations.")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerService customerService;

    private final CustomerRepository customerRepository;

    private final CustomerQueryService customerQueryService;
    private final ActivityLogService activityLogService;

    public CustomerResource(CustomerService customerService, CustomerRepository customerRepository, CustomerQueryService customerQueryService, ActivityLogService activityLogService) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.customerQueryService = customerQueryService;
        this.activityLogService = activityLogService;
    }

    /**
     * {@code POST  /customers} : Create a new customer.
     *
     * @param customerDTO the customerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerDTO, or with status {@code 400 (Bad Request)} if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Create a new customer", description = "Register a new customer in the system with personal details, location information, and associated registration agent.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Customer created", value = """
        {
            "status": "CREATED",
            "message": "Customer created successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "middleName": "Michael",
                "lastName": "Doe",
                "dateOfBirth": "1990-05-15",
                "nidaNumber": "19900515123456789",
                "registrationDate": "2024-01-15T10:30:00Z",
                "region": "Dar es Salaam",
                "district": "Kinondoni",
                "ward": "Mikocheni",
                "registeredById": 2
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Customer data is invalid or customer already has an ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Invalid request", value = """
        {
            "error": "Bad Request",
            "message": "A new customer cannot already have an ID",
            "path": "/api/v1/customers"
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation failed - Required fields are missing or invalid", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation error", value = """
        {
            "error": "Validation Failed",
            "message": "firstName is required, nidaNumber must be valid"
        }
        """)))})
    @PostMapping("")
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(@Parameter(description = "Customer information to create. ID should not be provided for new customers.", required = true, example = """
        {
            "firstName": "John",
            "middleName": "Michael",
            "lastName": "Doe",
            "dateOfBirth": "1990-05-15",
            "nidaNumber": "19900515123456789",
            "region": "Dar es Salaam",
            "district": "Kinondoni",
            "ward": "Mikocheni",
            "registeredById": 2
        }
        """) @Valid @RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Customer : {}", customerDTO);
        if (customerDTO.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CustomerDTO savedCustomer = customerService.save(customerDTO);

        activityLogService.logActivity(ActivityLog.ActionType.CUSTOMER_REGISTERED, "Customer", savedCustomer.getId(), String.format("Registered new customer: %s %s (NIDA: %s)", savedCustomer.getFirstName(), savedCustomer.getLastName(), savedCustomer.getNidaNumber()));

        ApiResponse<CustomerDTO> response = ApiResponse.created("Customer created successfully", savedCustomer);

        return ResponseEntity.created(new URI("/api/v1/customers/" + savedCustomer.getId())).headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, savedCustomer.getId().toString())).body(response);
    }

    /**
     * {@code PUT  /customers/:id} : Updates an existing customer.
     *
     * @param id          the id of the customerDTO to save.
     * @param customerDTO the customerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerDTO,
     * or with status {@code 400 (Bad Request)} if the customerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Update an existing customer", description = "Update all fields of an existing customer record. Requires the complete customer object.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Updated customer", value = """
        {
            "status": "SUCCESS",
            "message": "Customer updated successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "middleName": "Michael",
                "lastName": "Smith",
                "dateOfBirth": "1990-05-15",
                "nidaNumber": "19900515123456789",
                "registrationDate": "2024-01-15T10:30:00Z",
                "region": "Dar es Salaam",
                "district": "Kinondoni",
                "ward": "Mikocheni",
                "registeredById": 2
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid ID or customer data", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(@Parameter(description = "ID of the customer to update", required = true, example = "1") @PathVariable(value = "id", required = false) final Long id, @Parameter(description = "Complete customer object with updated information", required = true) @Valid @RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        LOG.debug("REST request to update Customer : {}, {}", id, customerDTO);
        if (customerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerDTO updatedCustomer = customerService.update(customerDTO);

        // Log customer update activity
        activityLogService.logActivity(ActivityLog.ActionType.CUSTOMER_UPDATED, "Customer", updatedCustomer.getId(), String.format("Updated customer: %s %s (NIDA: %s)", updatedCustomer.getFirstName(), updatedCustomer.getLastName(), updatedCustomer.getNidaNumber()));

        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, updatedCustomer.getId().toString())).body(ApiResponse.success("Customer updated successfully", updatedCustomer));
    }

    /**
     * {@code PATCH  /customers/:id} : Partial updates given fields of an existing customer, field will ignore if it is null
     *
     * @param id          the id of the customerDTO to save.
     * @param customerDTO the customerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerDTO,
     * or with status {@code 400 (Bad Request)} if the customerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Partially update a customer", description = "Update specific fields of an existing customer. Only provided fields will be updated, null fields are ignored.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer partially updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Customer partially updated", value = """
        {
            "status": "SUCCESS",
            "message": "Customer partially updated successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "middleName": "Michael",
                "lastName": "Smith",
                "dateOfBirth": "1990-05-15",
                "nidaNumber": "19900515123456789",
                "registrationDate": "2024-01-15T10:30:00Z",
                "region": "Dar es Salaam",
                "district": "Kinondoni",
                "ward": "Mikocheni",
                "registeredById": 2
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid ID or customer data", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))})
    @PatchMapping(value = "/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<ApiResponse<CustomerDTO>> partialUpdateCustomer(@Parameter(description = "ID of the customer to partially update", required = true, example = "1") @PathVariable(value = "id", required = false) final Long id, @Parameter(description = "Customer object with fields to update (only non-null fields will be updated)", required = true) @NotNull @RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update Customer partially : {}, {}", id, customerDTO);
        if (customerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomerDTO> result = customerService.partialUpdate(customerDTO);

        if (result.isPresent()) {
            CustomerDTO updatedCustomer = result.orElseThrow();

            // Log customer partial update activity
            activityLogService.logActivity(ActivityLog.ActionType.CUSTOMER_UPDATED, "Customer", updatedCustomer.getId(), String.format("Partially updated customer: %s %s (NIDA: %s)", updatedCustomer.getFirstName(), updatedCustomer.getLastName(), updatedCustomer.getNidaNumber()));

            return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerDTO.getId().toString())).body(ApiResponse.success("Customer partially updated successfully", updatedCustomer));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.notFound("Customer not found with id: " + id));
        }
    }

    /**
     * {@code GET  /customers} : get all the customers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customers in body.
     */
    @Operation(summary = "Get all customers with filtering and pagination", description = "Retrieve a paginated list of customers with optional filtering by various criteria such as name, region, district, ward, registration date, etc.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customers retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Paginated customers list", value = """
        {
            "status": "SUCCESS",
            "message": "Customers retrieved successfully",
            "data": [
                {
                    "id": 1,
                    "firstName": "John",
                    "middleName": "Michael",
                    "lastName": "Doe",
                    "dateOfBirth": "1990-05-15",
                    "nidaNumber": "19900515123456789",
                    "registrationDate": "2024-01-15T10:30:00Z",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni",
                    "ward": "Mikocheni",
                    "registeredById": 2
                },
                {
                    "id": 2,
                    "firstName": "Jane",
                    "middleName": "Elizabeth",
                    "lastName": "Smith",
                    "dateOfBirth": "1992-08-20",
                    "nidaNumber": "19920820987654321",
                    "registrationDate": "2024-01-16T14:15:00Z",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni",
                    "ward": "Sinza",
                    "registeredById": 2
                }
            ]
        }
        """)))})
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers(@Parameter(description = "Filtering criteria for customers (firstName, lastName, region, district, ward, etc.)") CustomerCriteria criteria, @Parameter(description = "Pagination information (page, size, sort)") @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get Customers by criteria: {}", criteria);

        Page<CustomerDTO> page = customerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(ApiResponse.success("Customers retrieved successfully", page.getContent()));
    }

    /**
     * {@code GET  /customers/count} : count all the customers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Operation(summary = "Count customers with filtering", description = "Get the total number of customers that match the specified filtering criteria. Useful for pagination and statistics.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer count retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Customer count", value = """
        {
            "status": "SUCCESS",
            "message": "Customer count retrieved successfully",
            "data": 150
        }
        """)))})
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countCustomers(@Parameter(description = "Filtering criteria for customers (same as GET /customers)") CustomerCriteria criteria) {
        LOG.debug("REST request to count Customers by criteria: {}", criteria);
        return ResponseEntity.ok().body(ApiResponse.success("Customer count retrieved successfully", customerQueryService.countByCriteria(criteria)));
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer.
     *
     * @param id the id of the customerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerDTO, or with status {@code 404 (Not Found)}.
     */
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer's details using their unique identifier.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer found and returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Customer found", value = """
        {
            "status": "SUCCESS",
            "message": "Customer retrieved successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "middleName": "Michael",
                "lastName": "Doe",
                "dateOfBirth": "1990-05-15",
                "nidaNumber": "19900515123456789",
                "registrationDate": "2024-01-15T10:30:00Z",
                "region": "Dar es Salaam",
                "district": "Kinondoni",
                "ward": "Mikocheni",
                "registeredById": 2
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found with the provided ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Customer not found", value = """
        {
            "status": "NOT_FOUND",
            "message": "Customer not found with id: 999"
        }
        """)))})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomer(@Parameter(description = "Unique identifier of the customer to retrieve", required = true, example = "1") @PathVariable("id") Long id) {
        LOG.debug("REST request to get Customer : {}", id);
        Optional<CustomerDTO> customerDTO = customerService.findOne(id);

        if (customerDTO.isPresent()) {
            ApiResponse<CustomerDTO> response = ApiResponse.success("Customer retrieved successfully", customerDTO.orElseThrow());
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<CustomerDTO> response = ApiResponse.notFound("Customer not found with id: " + id);
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * {@code DELETE  /customers/:id} : delete the "id" customer.
     *
     * @param id the id of the customerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Operation(summary = "Delete a customer", description = "Permanently remove a customer record from the system. This action cannot be undone.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Customer deleted", value = """
        {
            "status": "SUCCESS",
            "message": "Customer deleted successfully"
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(@Parameter(description = "ID of the customer to delete", required = true, example = "1") @PathVariable("id") Long id) {
        LOG.debug("REST request to delete Customer : {}", id);

        // Get customer details before deletion for logging
        Optional<CustomerDTO> customerDTO = customerService.findOne(id);
        if (customerDTO.isPresent()) {
            CustomerDTO customer = customerDTO.orElseThrow();

            customerService.delete(id);

            // Log customer deletion activity
            activityLogService.logActivity(ActivityLog.ActionType.CUSTOMER_DELETED, "Customer", id, String.format("Deleted customer: %s %s (NIDA: %s)", customer.getFirstName(), customer.getLastName(), customer.getNidaNumber()));

            ApiResponse<Object> response = ApiResponse.deleted("Customer deleted successfully");

            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).body(response);
        } else {
            return ResponseEntity.status(404).body(ApiResponse.notFound("Customer not found with id: " + id));
        }
    }

    /**
     * {@code SEARCH  /customers/_search?query=:query} : search for the customer corresponding
     * to the query.
     *
     * @param query    the query of the customer search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @Operation(summary = "Search customers with Elasticsearch", description = "Perform full-text search across customer records using Elasticsearch. Supports searching by name, NIDA number, location, and other fields.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Search results", value = """
        {
            "status": "SUCCESS",
            "message": "Customer search completed successfully",
            "data": [
                {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni"
                }
            ]
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Elasticsearch error", content = @Content(mediaType = "application/json"))})
    @GetMapping("/_search")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> searchCustomers(@Parameter(description = "Search query string (supports full-text search)", required = true, example = "John Doe") @RequestParam("query") String query, @Parameter(description = "Pagination for search results") @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to search for a page of Customers for query {}", query);
        try {
            Page<CustomerDTO> page = customerService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

            ApiResponse<List<CustomerDTO>> response = ApiResponse.success("Customer search completed successfully", page.getContent());

            return ResponseEntity.ok().headers(headers).body(response);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code GET  /customers/_autocomplete?query=:query} : get autocomplete suggestions for the customer search.
     *
     * @param query the partial query for autocomplete suggestions.
     * @param limit the maximum number of suggestions to return (default: 10).
     * @return the list of autocomplete suggestions.
     */
    @Operation(summary = "Get autocomplete suggestions", description = "Get intelligent autocomplete suggestions for customer search queries.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Autocomplete suggestions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))})
    @GetMapping("/_autocomplete")
    public ResponseEntity<ApiResponse<List<String>>> getAutocompleteSuggestions(@Parameter(description = "Partial query for autocomplete", required = true, example = "joh") @RequestParam("query") String query, @Parameter(description = "Maximum number of suggestions", example = "10") @RequestParam(value = "limit", defaultValue = "10") int limit) {
        LOG.debug("REST request to get autocomplete suggestions for query: {}", query);
        try {
            List<String> suggestions = customerService.getAutocompleteSuggestions(query, limit);

            ApiResponse<List<String>> response = ApiResponse.success("Autocomplete suggestions retrieved successfully", suggestions);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code POST  /customers/_reindex} : Reindex all customers from database to Elasticsearch.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and reindexing result.
     */
    @Operation(summary = "Reindex all customers", description = "Reindex all customer records from database to Elasticsearch. Use this for search index maintenance.", tags = {"Customer Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reindexing completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))})
    @PostMapping("/_reindex")
    public ResponseEntity<ApiResponse<String>> reindexCustomers() {
        LOG.info("REST request to reindex all customers to Elasticsearch");
        try {
            int reindexedCount = customerService.reindexAllCustomers();
            String message = String.format("Successfully reindexed %d customers to Elasticsearch", reindexedCount);
            LOG.info(message);

            ApiResponse<String> response = ApiResponse.success("Customer reindexing completed successfully", message);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            LOG.error("Failed to reindex customers: {}", e.getMessage(), e);
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
