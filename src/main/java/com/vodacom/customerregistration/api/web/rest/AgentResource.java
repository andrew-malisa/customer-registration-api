package com.vodacom.customerregistration.api.web.rest;

import com.vodacom.customerregistration.api.domain.ActivityLog;
import com.vodacom.customerregistration.api.repository.AgentRepository;
import com.vodacom.customerregistration.api.service.ActivityLogService;
import com.vodacom.customerregistration.api.service.AgentQueryService;
import com.vodacom.customerregistration.api.service.AgentService;
import com.vodacom.customerregistration.api.service.criteria.AgentCriteria;
import com.vodacom.customerregistration.api.service.dto.AgentDTO;
import com.vodacom.customerregistration.api.service.dto.AgentDetailResponseDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationDTO;
import com.vodacom.customerregistration.api.service.dto.AgentRegistrationResponseDTO;
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
 * REST controller for managing {@link com.vodacom.customerregistration.api.domain.Agent}.
 */
@Tag(name = "Agent Management", description = "APIs for managing registration agents in the system. Agents are responsible for registering customers and have specific location assignments.")
@RestController
@RequestMapping("/api/v1/agents")
public class AgentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentResource.class);

    private static final String ENTITY_NAME = "agent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgentService agentService;

    private final AgentRepository agentRepository;

    private final AgentQueryService agentQueryService;
    private final ActivityLogService activityLogService;

    public AgentResource(AgentService agentService, AgentRepository agentRepository, AgentQueryService agentQueryService, ActivityLogService activityLogService) {
        this.agentService = agentService;
        this.agentRepository = agentRepository;
        this.agentQueryService = agentQueryService;
        this.activityLogService = activityLogService;
    }

    /**
     * {@code POST  /agents} : Create a new agent.
     *
     * @param agentDTO the agentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agentDTO, or with status {@code 400 (Bad Request)} if the agent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Create a new agent", description = "Register a new agent in the system with personal details, location assignment, and user account association. Agents are responsible for customer registration in their assigned areas.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Agent created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent created", value = """
        {
            "status": "CREATED",
            "message": "Agent created successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com",
                "phoneNumber": "+255123456789",
                "region": "Dar es Salaam",
                "district": "Kinondoni",
                "ward": "Mikocheni",
                "userId": 123
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid data or agent already has ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Agent creation error", value = """
        {
            "error": "Bad Request",
            "message": "A new agent cannot already have an ID"
        }
        """)))})
    @PostMapping("")
    public ResponseEntity<ApiResponse<AgentDetailResponseDTO>> createAgent(@Parameter(description = "Agent data to create. Must not include ID field.", required = true, example = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "phoneNumber": "+255123456789",
            "region": "Dar es Salaam",
            "district": "Kinondoni",
            "ward": "Mikocheni",
            "userId": 123
        }
        """) @Valid @RequestBody AgentDTO agentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Agent : {}", agentDTO);
        if (agentDTO.getId() != null) {
            throw new BadRequestAlertException("A new agent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AgentDetailResponseDTO savedAgent = agentService.save(agentDTO);

        ApiResponse<AgentDetailResponseDTO> response = ApiResponse.created("Agent created successfully", savedAgent);

        return ResponseEntity.created(new URI("/api/v1/agents/" + savedAgent.getAgent().getId())).headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, savedAgent.getAgent().getId().toString())).body(response);
    }

    /**
     * {@code POST  /agents/register} : Register a new agent with user account in single transaction.
     *
     * @param registrationDTO the agent registration data containing both user and agent information.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body containing both created user and agent,
     * or with status {@code 400 (Bad Request)} if the data is invalid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Register agent with user account", description = "Create both User (for authentication) and Agent (for business logic) entities in a single atomic transaction. This is the recommended way to register new agents as it ensures data consistency.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Agent and user account created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent registration successful", value = """
        {
            "status": "CREATED",
            "message": "Agent registered successfully",
            "data": {
                "user": {
                    "id": 123,
                    "login": "john.doe",
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "activated": true,
                    "langKey": "en",
                    "authorities": ["ROLE_USER"]
                },
                "agent": {
                    "id": 456,
                    "phoneNumber": "+255123456789",
                    "status": "ACTIVE",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni",
                    "ward": "Mikocheni",
                    "userId": 123
                }
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid data, validation errors, or user already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Registration validation error", value = """
        {
            "error": "Bad Request",
            "message": "Login name already used!"
        }
        """)))})
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AgentRegistrationResponseDTO>> registerAgent(@Parameter(description = "Complete agent registration data including user account details and agent-specific information", required = true, example = """
        {
            "login": "john.doe",
            "password": "password123",
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "langKey": "en",
            "phoneNumber": "+255123456789",
            "status": "ACTIVE",
            "region": "Dar es Salaam",
            "district": "Kinondoni",
            "ward": "Mikocheni"
        }
        """) @Valid @RequestBody AgentRegistrationDTO registrationDTO) throws URISyntaxException {
        LOG.debug("REST request to register Agent with User : {}", registrationDTO);

        AgentRegistrationResponseDTO result = agentService.registerAgentWithUser(registrationDTO);

        // Log agent registration activity
        activityLogService.logActivity(ActivityLog.ActionType.AGENT_REGISTERED, "Agent", result.getAgent().getId(), String.format("Registered new agent: %s %s (Login: %s)", result.getUser().getFirstName(), result.getUser().getLastName(),
//                result.getAgent().getPhoneNumber(),
            result.getUser().getLogin()));

        ApiResponse<AgentRegistrationResponseDTO> response = ApiResponse.created("Agent registered successfully", result);

        return ResponseEntity.created(new URI("/api/v1/agents/" + result.getAgent().getId())).headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getAgent().getId().toString())).body(response);
    }

    /**
     * {@code PUT  /agents/:id} : Updates an existing agent.
     *
     * @param id       the id of the agentDTO to save.
     * @param agentDTO the agentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentDTO,
     * or with status {@code 400 (Bad Request)} if the agentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Update an existing agent", description = "Update all fields of an existing agent. The entire agent object must be provided. Use PATCH for partial updates.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent updated", value = """
        {
            "status": "SUCCESS",
            "message": "Agent updated successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@updated.com",
                "phoneNumber": "+255987654321",
                "region": "Arusha",
                "district": "Arusha Urban",
                "ward": "Central",
                "userId": 123
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid ID or data validation errors", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agent not found with provided ID", content = @Content(mediaType = "application/json"))})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentDetailResponseDTO>> updateAgent(@Parameter(description = "Agent ID to update", required = true, example = "1") @PathVariable(value = "id", required = false) final Long id, @Parameter(description = "Complete agent data for update. Must include ID field matching path parameter.", required = true) @Valid @RequestBody AgentDTO agentDTO) throws URISyntaxException {
        LOG.debug("REST request to update Agent : {}, {}", id, agentDTO);
        if (agentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AgentDetailResponseDTO updatedAgent = agentService.update(agentDTO);

        // Log agent update activity
        activityLogService.logActivity(ActivityLog.ActionType.AGENT_UPDATED, "Agent", updatedAgent.getAgent().getId(), String.format("Updated agent: %s %s", updatedAgent.getUser().getFirstName(), updatedAgent.getUser().getLastName()));

        ApiResponse<AgentDetailResponseDTO> response = ApiResponse.updated("Agent updated successfully", updatedAgent);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, updatedAgent.getAgent().getId().toString())).body(response);
    }

    /**
     * {@code PATCH  /agents/:id} : Partial updates given fields of an existing agent, field will ignore if it is null
     *
     * @param id       the id of the agentDTO to save.
     * @param agentDTO the agentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentDTO,
     * or with status {@code 400 (Bad Request)} if the agentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the agentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the agentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Partially update an agent", description = "Update only the provided fields of an existing agent. Null fields are ignored. Use PUT for complete updates.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent partially updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent partially updated", value = """
        {
            "status": "SUCCESS",
            "message": "Agent partially updated successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@updated.com",
                "phoneNumber": "+255987654321",
                "region": "Arusha",
                "district": "Arusha Urban",
                "ward": "Central",
                "userId": 123
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid ID or data validation errors", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agent not found with provided ID", content = @Content(mediaType = "application/json"))})
    @PatchMapping(value = "/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<ApiResponse<AgentDetailResponseDTO>> partialUpdateAgent(@Parameter(description = "Agent ID to partially update", required = true, example = "1") @PathVariable(value = "id", required = false) final Long id, @Parameter(description = "Partial agent data. Only provided fields will be updated. Null fields are ignored.", required = true, example = """
        {
            "email": "new.email@example.com",
            "phoneNumber": "+255999888777"
        }
        """) @NotNull @RequestBody AgentDTO agentDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update Agent partially : {}, {}", id, agentDTO);
        if (agentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AgentDetailResponseDTO> result = agentService.partialUpdate(agentDTO);

        if (result.isPresent()) {
            AgentDetailResponseDTO updatedAgent = result.orElseThrow();

            // Log agent partial update activity
            activityLogService.logActivity(ActivityLog.ActionType.AGENT_UPDATED, "Agent", updatedAgent.getAgent().getId(), String.format("Partially updated agent: %s %s", updatedAgent.getUser().getFirstName(), updatedAgent.getUser().getLastName()));

            return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentDTO.getId().toString())).body(ApiResponse.success("Agent partially updated successfully", updatedAgent));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.notFound("Agent not found with id: " + id));
        }
    }

    /**
     * {@code GET  /agents} : get all the agents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agents in body.
     */
    @Operation(summary = "Get all agents with filtering", description = "Retrieve all agents with optional filtering and pagination. Supports complex filtering by various agent attributes including location data.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agents retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agents list", value = """
        {
            "status": "SUCCESS",
            "message": "Agents retrieved successfully",
            "data": [
                {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phoneNumber": "+255123456789",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni",
                    "ward": "Mikocheni",
                    "userId": 123
                },
                {
                    "id": 2,
                    "firstName": "Jane",
                    "lastName": "Smith",
                    "email": "jane.smith@example.com",
                    "phoneNumber": "+255987654321",
                    "region": "Arusha",
                    "district": "Arusha Urban",
                    "ward": "Central",
                    "userId": 124
                }
            ]
        }
        """)))})
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<AgentDetailResponseDTO>>> getAllAgents(@Parameter(description = "Filtering criteria for agents. Supports filtering by firstName, lastName, email, phoneNumber, region, district, ward, and userId.", example = "firstName.contains=John&region.equals=Dar es Salaam") AgentCriteria criteria, @Parameter(description = "Pagination and sorting parameters", example = "page=0&size=20&sort=firstName,asc") @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get Agents by criteria: {}", criteria);

        Page<AgentDTO> page = agentQueryService.findByCriteria(criteria, pageable);

        // Convert AgentDTO to AgentDetailResponseDTO to include user data and remove redundancy
        List<AgentDetailResponseDTO> agentDetails = page.getContent().stream().map(agentDTO -> agentService.findOne(agentDTO.getId()).orElse(null)).filter(agentDetail -> agentDetail != null).toList();

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(ApiResponse.success("Agents retrieved successfully", agentDetails));
    }

    /**
     * {@code GET  /agents/count} : count all the agents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Operation(summary = "Count agents with filtering", description = "Get the total count of agents matching the specified criteria. Useful for pagination calculations.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent count retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent count", value = """
        {
            "status": "SUCCESS",
            "message": "Agent count retrieved successfully",
            "data": 25
        }
        """)))})
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countAgents(@Parameter(description = "Filtering criteria for counting agents. Same filters as GET /agents endpoint.", example = "region.equals=Dar es Salaam") AgentCriteria criteria) {
        LOG.debug("REST request to count Agents by criteria: {}", criteria);
        return ResponseEntity.ok().body(ApiResponse.success("Agent count retrieved successfully", agentQueryService.countByCriteria(criteria)));
    }

    /**
     * {@code GET  /agents/:id} : get the "id" agent.
     *
     * @param id the id of the agentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agentDTO, or with status {@code 404 (Not Found)}.
     */
    @Operation(summary = "Get agent by ID", description = "Retrieve detailed information about a specific agent by their unique identifier.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent found and retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent details", value = """
        {
            "status": "SUCCESS",
            "message": "Agent retrieved successfully",
            "data": {
                "id": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com",
                "phoneNumber": "+255123456789",
                "region": "Dar es Salaam",
                "district": "Kinondoni",
                "ward": "Mikocheni",
                "userId": 123
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agent not found with provided ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Agent not found", value = """
        {
            "status": "NOT_FOUND",
            "message": "Agent not found with id: 999"
        }
        """)))})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentDetailResponseDTO>> getAgent(@Parameter(description = "Agent ID to retrieve", required = true, example = "1") @PathVariable("id") Long id) {
        LOG.debug("REST request to get Agent : {}", id);
        Optional<AgentDetailResponseDTO> agentDTO = agentService.findOne(id);

        if (agentDTO.isPresent()) {
            ApiResponse<AgentDetailResponseDTO> response = ApiResponse.success("Agent retrieved successfully", agentDTO.orElseThrow());
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AgentDetailResponseDTO> response = ApiResponse.notFound("Agent not found with id: " + id);
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * {@code GET  /agents/:id/details} : get the "id" agent with complete user details.
     *
     * @param id the id of the agent to retrieve with details.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing both agent and user details, or with status {@code 404 (Not Found)}.
     */
    @Operation(summary = "Get agent with complete user details", description = "Retrieve comprehensive information about a specific agent including both agent data and associated user account details. This provides a complete view of the agent's profile.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent with user details retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent with user details", value = """
        {
            "status": "SUCCESS",
            "message": "Agent details retrieved successfully",
            "data": {
                "user": {
                    "id": 123,
                    "login": "john.doe",
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "activated": true,
                    "langKey": "en",
                    "authorities": ["ROLE_USER", "ROLE_AGENT"],
                    "createdBy": "system",
                    "createdDate": "2024-01-15T10:30:00Z",
                    "lastModifiedBy": "admin",
                    "lastModifiedDate": "2024-01-20T14:45:00Z"
                },
                "agent": {
                    "id": 456,
                    "phoneNumber": "+255123456789",
                    "status": "ACTIVE",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni",
                    "ward": "Mikocheni",
                    "userId": 123
                }
            }
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agent not found with provided ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Agent not found", value = """
        {
            "status": "NOT_FOUND",
            "message": "Agent not found with id: 999"
        }
        """)))})
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<AgentDetailResponseDTO>> getAgentWithDetails(@Parameter(description = "Agent ID to retrieve with complete user details", required = true, example = "1") @PathVariable("id") Long id) {
        LOG.debug("REST request to get Agent with details : {}", id);
        Optional<AgentDetailResponseDTO> agentDetail = agentService.findOne(id);

        if (agentDetail.isPresent()) {
            ApiResponse<AgentDetailResponseDTO> response = ApiResponse.success("Agent details retrieved successfully", agentDetail.orElseThrow());
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AgentDetailResponseDTO> response = ApiResponse.notFound("Agent not found with id: " + id);
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * {@code DELETE  /agents/:id} : delete the "id" agent.
     *
     * @param id the id of the agentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Operation(summary = "Delete an agent", description = "Permanently delete an agent from the system. This action cannot be undone. Consider the impact on associated customer registrations.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent deleted", value = """
        {
            "status": "SUCCESS",
            "message": "Agent deleted successfully"
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agent not found with provided ID", content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAgent(@Parameter(description = "Agent ID to delete (permanent action - cannot be undone)", required = true, example = "1") @PathVariable("id") Long id) {
        LOG.debug("REST request to delete Agent : {}", id);

        // Get agent details before deletion for logging
        Optional<AgentDetailResponseDTO> agentDetailOpt = agentService.findOne(id);
        if (agentDetailOpt.isPresent()) {
            AgentDetailResponseDTO agentDetail = agentDetailOpt.orElseThrow();

            agentService.delete(id);

            // Log agent deletion activity
            activityLogService.logActivity(ActivityLog.ActionType.AGENT_DELETED, "Agent", id, String.format("Deleted agent: %s %s (Login: %s)", agentDetail.getUser().getFirstName(), agentDetail.getUser().getLastName(), agentDetail.getUser().getLogin()));

            ApiResponse<Object> response = ApiResponse.deleted("Agent deleted successfully");

            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).body(response);
        } else {
            return ResponseEntity.status(404).body(ApiResponse.notFound("Agent not found with id: " + id));
        }
    }

    /**
     * {@code SEARCH  /agents/_search?query=:query} : search for the agent corresponding
     * to the query.
     *
     * @param query    the query of the agent search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @Operation(summary = "Search agents using Elasticsearch", description = "Full-text search across agent data using Elasticsearch. Searches firstName, lastName, email, phoneNumber, and location fields.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent search completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Search results", value = """
        {
            "status": "SUCCESS",
            "message": "Agent search completed successfully",
            "data": [
                {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phoneNumber": "+255123456789",
                    "region": "Dar es Salaam",
                    "district": "Kinondoni",
                    "ward": "Mikocheni",
                    "userId": 123
                }
            ]
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Elasticsearch service error", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Search error", value = """
        {
            "error": "Internal Server Error",
            "message": "Search service temporarily unavailable"
        }
        """)))})
    @GetMapping("/_search")
    public ResponseEntity<ApiResponse<List<AgentDetailResponseDTO>>> searchAgents(@Parameter(description = "Search query string. Searches across agent names, email, phone, and location fields.", required = true, example = "john kinondoni") @RequestParam("query") String query, @Parameter(description = "Pagination parameters for search results", example = "page=0&size=10&sort=firstName,asc") @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to search for a page of Agents for query {}", query);
        try {
            Page<AgentDetailResponseDTO> page = agentService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

            ApiResponse<List<AgentDetailResponseDTO>> response = ApiResponse.success("Agent search completed successfully", page.getContent());

            return ResponseEntity.ok().headers(headers).body(response);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code GET  /agents/_autocomplete?query=:query} : get autocomplete suggestions for the agent search.
     *
     * @param query the partial query for autocomplete suggestions.
     * @param limit the maximum number of suggestions to return (default: 10).
     * @return the list of autocomplete suggestions.
     */
    @Operation(summary = "Get agent search autocomplete suggestions", description = "Get autocomplete suggestions for agent search based on partial input. Useful for implementing search-as-you-type functionality.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Autocomplete suggestions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Autocomplete suggestions", value = """
        {
            "status": "SUCCESS",
            "message": "Autocomplete suggestions retrieved successfully",
            "data": [
                "John Doe",
                "John Smith",
                "Johnson Williams",
                "johnny@example.com",
                "Dar es Salaam"
            ]
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Elasticsearch service error", content = @Content(mediaType = "application/json"))})
    @GetMapping("/_autocomplete")
    public ResponseEntity<ApiResponse<List<String>>> getAutocompleteSuggestions(@Parameter(description = "Partial search query for autocomplete suggestions", required = true, example = "joh") @RequestParam("query") String query, @Parameter(description = "Maximum number of suggestions to return", example = "10") @RequestParam(value = "limit", defaultValue = "10") int limit) {
        LOG.debug("REST request to get autocomplete suggestions for query: {}", query);
        try {
            List<String> suggestions = agentService.getAutocompleteSuggestions(query, limit);

            ApiResponse<List<String>> response = ApiResponse.success("Autocomplete suggestions retrieved successfully", suggestions);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

//    /**
//     * {@code POST  /agents/user/{userId}} : Create agent for existing user.
//     *
//     * @param userId      the id of the user to create agent for.
//     * @param phoneNumber the phone number for the agent.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the agent details, or with status {@code 200 (OK)} if agent already exists.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @Operation(summary = "Create agent for existing user", description = "Creates an agent profile for an existing user. If an agent already exists for the user, returns the existing agent details. This endpoint is useful when you have a user account but need to create the associated agent profile.", tags = {"Agent Management"})
//    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Agent created successfully for the user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent created for user", value = """
//        {
//            "status": "CREATED",
//            "message": "Agent created successfully for user",
//            "data": {
//                "user": {
//                    "id": 123,
//                    "login": "john.doe",
//                    "firstName": "John",
//                    "lastName": "Doe",
//                    "email": "john.doe@example.com",
//                    "activated": true,
//                    "langKey": "en",
//                    "authorities": ["ROLE_USER"]
//                },
//                "agent": {
//                    "id": 456,
//                    "phoneNumber": "+255123456789",
//                    "status": "ACTIVE",
//                    "userId": 123
//                }
//            }
//        }
//        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent already exists for this user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Agent already exists", value = """
//        {
//            "status": "SUCCESS",
//            "message": "Agent already exists for user",
//            "data": {
//                "user": {
//                    "id": 123,
//                    "login": "john.doe",
//                    "firstName": "John",
//                    "lastName": "Doe",
//                    "email": "john.doe@example.com",
//                    "activated": true,
//                    "langKey": "en",
//                    "authorities": ["ROLE_USER"]
//                },
//                "agent": {
//                    "id": 456,
//                    "phoneNumber": "+255123456789",
//                    "status": "ACTIVE",
//                    "userId": 123
//                }
//            }
//        }
//        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid phone number format or validation errors", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation error", value = """
//        {
//            "status": "BAD_REQUEST",
//            "message": "Phone number must be a valid Tanzanian mobile number"
//        }
//        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found with provided ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "User not found", value = """
//        {
//            "status": "NOT_FOUND",
//            "message": "User not found with ID: 999"
//        }
//        """)))})
//    @PostMapping("/user/{userId}")
//    public ResponseEntity<ApiResponse<AgentDetailResponseDTO>> createAgentForUser(@Parameter(description = "User ID to create agent for", required = true, example = "123") @PathVariable @NotNull Long userId,
//
//                                                                                  @Parameter(description = "Phone number for the agent (must be valid Tanzanian mobile number)", required = true, example = "+255123456789") @RequestParam @NotNull @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters") @Pattern(regexp = "^(\\+?255|0)[67]\\d{8}$", message = "Phone number must be a valid Tanzanian mobile number") String phoneNumber) throws URISyntaxException {
//
//        LOG.debug("REST request to create Agent for User ID: {} with phone: {}", userId, phoneNumber);
//
//        try {
//            AgentDetailResponseDTO result = agentService.createAgentForUser(userId, phoneNumber);
//
//            // Check if this was a new agent creation or existing agent retrieval
//            // We can determine this by checking if the service created a new agent vs returned existing
//            // Since the service method returns existing agent if found, we'll assume it's a successful operation
//            // and distinguish based on whether we can detect if it's new or existing
//
//            return ResponseEntity.created(new URI("/api/v1/agents/" + result.getAgent().getId())).headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getAgent().getId().toString())).body(ApiResponse.created("Agent created successfully for user", result));
//
//        } catch (IllegalArgumentException e) {
//            LOG.error("User not found with ID: {}", userId, e);
//            return ResponseEntity.status(404).body(ApiResponse.notFound("User not found with ID: " + userId));
//        } catch (Exception e) {
//            LOG.error("Error creating agent for user ID: {}", userId, e);
//            return ResponseEntity.internalServerError().body(ApiResponse.internalServerError("Error creating agent for user"));
//        }
//    }

    /**
     * {@code POST  /agents/_reindex} : Reindex all agents from database to Elasticsearch.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and reindexing result.
     */
    @Operation(summary = "Reindex all agents to Elasticsearch", description = "Rebuild the Elasticsearch index for all agents from the database. Use this when search functionality is not working correctly or after data migrations. This operation may take some time for large datasets.", tags = {"Agent Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agent reindexing completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, subTypes = {AgentDetailResponseDTO.class}), examples = @ExampleObject(name = "Reindexing completed", value = """
        {
            "status": "SUCCESS",
            "message": "Agent reindexing completed successfully",
            "data": "Successfully reindexed 150 agents to Elasticsearch"
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Elasticsearch reindexing failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Reindexing error", value = """
        {
            "error": "Internal Server Error",
            "message": "Failed to reindex agents: Elasticsearch cluster unavailable"
        }
        """)))})
    @PostMapping("/_reindex")
    public ResponseEntity<ApiResponse<String>> reindexAgents() {
        LOG.info("REST request to reindex all agents to Elasticsearch");
        try {
            int reindexedCount = agentService.reindexAllAgents();
            String message = String.format("Successfully reindexed %d agents to Elasticsearch", reindexedCount);
            LOG.info(message);

            ApiResponse<String> response = ApiResponse.success("Agent reindexing completed successfully", message);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            LOG.error("Failed to reindex agents: {}", e.getMessage(), e);
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
