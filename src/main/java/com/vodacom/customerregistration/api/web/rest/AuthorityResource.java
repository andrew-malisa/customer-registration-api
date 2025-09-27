//package com.vodacom.customerregistration.api.web.rest;
//
//import com.vodacom.customerregistration.api.domain.Authority;
//import com.vodacom.customerregistration.api.repository.AuthorityRepository;
//import com.vodacom.customerregistration.api.web.rest.errors.BadRequestAlertException;
//import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.ArraySchema;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.Optional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import tech.jhipster.web.util.HeaderUtil;
//import tech.jhipster.web.util.ResponseUtil;
//
///**
// * REST controller for managing {@link com.vodacom.customerregistration.api.domain.Authority}.
// */
//@Tag(name = "Authority Management", description = "Admin APIs for managing user roles and permissions in the system. Requires ADMIN authority for all operations.")
//@RestController
//@RequestMapping("/api/v1/authorities")
//@Transactional
//public class AuthorityResource {
//
//    private static final Logger LOG = LoggerFactory.getLogger(AuthorityResource.class);
//
//    private static final String ENTITY_NAME = "adminAuthority";
//
//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;
//
//    private final AuthorityRepository authorityRepository;
//
//    public AuthorityResource(AuthorityRepository authorityRepository) {
//        this.authorityRepository = authorityRepository;
//    }
//
//    /**
//     * {@code POST  /authorities} : Create a new authority.
//     *
//     * @param authority the authority to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authority, or with status {@code 400 (Bad Request)} if the authority has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @Operation(
//        summary = "Create a new authority",
//        description = "Create a new role/authority in the system. Used for permission management. Requires ADMIN authority.",
//        tags = {"Authority Management"},
//        security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @ApiResponses(value = {
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "201",
//            description = "Authority created successfully",
//            content = @Content(
//                mediaType = "application/json",
//                schema = @Schema(implementation = ApiResponse.class),
//                examples = @ExampleObject(
//                    name = "Authority created",
//                    value = """
//                    {
//                        "status": "CREATED",
//                        "message": "Authority created successfully",
//                        "data": {
//                            "name": "ROLE_MANAGER"
//                        }
//                    }
//                    """
//                )
//            )
//        ),
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "400",
//            description = "Bad request - Authority already exists",
//            content = @Content(
//                mediaType = "application/json",
//                examples = @ExampleObject(
//                    name = "Authority exists",
//                    value = """
//                    {
//                        "error": "Bad Request",
//                        "message": "Authority already exists"
//                    }
//                    """
//                )
//            )
//        ),
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "403",
//            description = "Access denied - ADMIN authority required",
//            content = @Content(mediaType = "application/json")
//        )
//    })
//    @PostMapping("")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    public ResponseEntity<ApiResponse<Authority>> createAuthority(
//        @Parameter(
//            description = "Authority data to create. Name should follow ROLE_* pattern.",
//            required = true,
//            example = """
//            {
//                "name": "ROLE_MANAGER"
//            }
//            """
//        )
//        @Valid @RequestBody Authority authority
//    ) throws URISyntaxException {
//        LOG.debug("REST request to save Authority : {}", authority);
//        if (authorityRepository.existsById(authority.getName())) {
//            throw new BadRequestAlertException("authority already exists", ENTITY_NAME, "idexists");
//        }
//        Authority savedAuthority = authorityRepository.save(authority);
//        return ResponseEntity.created(new URI("/api/v1/authorities/" + savedAuthority.getName()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, savedAuthority.getName()))
//            .body(ApiResponse.created("Authority created successfully", savedAuthority));
//    }
//
//    /**
//     * {@code GET  /authorities} : get all the authorities.
//     *
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authorities in body.
//     */
//    @Operation(
//        summary = "Get all authorities",
//        description = "Retrieve all roles/authorities in the system. Used for permission management. Requires ADMIN authority.",
//        tags = {"Authority Management"},
//        security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @ApiResponses(value = {
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "200",
//            description = "Authorities retrieved successfully",
//            content = @Content(
//                mediaType = "application/json",
//                schema = @Schema(implementation = ApiResponse.class),
//                examples = @ExampleObject(
//                    name = "Authorities list",
//                    value = """
//                    {
//                        "status": "SUCCESS",
//                        "message": "Authorities retrieved successfully",
//                        "data": [
//                            {
//                                "name": "ROLE_ADMIN"
//                            },
//                            {
//                                "name": "ROLE_USER"
//                            },
//                            {
//                                "name": "ROLE_MANAGER"
//                            }
//                        ]
//                    }
//                    """
//                )
//            )
//        ),
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "403",
//            description = "Access denied - ADMIN authority required",
//            content = @Content(mediaType = "application/json")
//        )
//    })
//    @GetMapping("")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    public ResponseEntity<ApiResponse<List<Authority>>> getAllAuthorities() {
//        LOG.debug("REST request to get all Authorities");
//        List<Authority> authorities = authorityRepository.findAll();
//        return ResponseEntity.ok(ApiResponse.success("Authorities retrieved successfully", authorities));
//    }
//
//    /**
//     * {@code GET  /authorities/:id} : get the "id" authority.
//     *
//     * @param id the id of the authority to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority, or with status {@code 404 (Not Found)}.
//     */
//    @Operation(
//        summary = "Get authority by name",
//        description = "Retrieve a specific authority/role by its name. Requires ADMIN authority.",
//        tags = {"Authority Management"},
//        security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @ApiResponses(value = {
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "200",
//            description = "Authority found and returned successfully",
//            content = @Content(
//                mediaType = "application/json",
//                schema = @Schema(implementation = ApiResponse.class),
//                examples = @ExampleObject(
//                    name = "Authority found",
//                    value = """
//                    {
//                        "status": "SUCCESS",
//                        "message": "Authority retrieved successfully",
//                        "data": {
//                            "name": "ROLE_ADMIN"
//                        }
//                    }
//                    """
//                )
//            )
//        ),
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "404",
//            description = "Authority not found",
//            content = @Content(
//                mediaType = "application/json",
//                examples = @ExampleObject(
//                    name = "Authority not found",
//                    value = """
//                    {
//                        "status": "NOT_FOUND",
//                        "message": "Authority not found with id: ROLE_UNKNOWN"
//                    }
//                    """
//                )
//            )
//        )
//    })
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    public ResponseEntity<ApiResponse<Authority>> getAuthority(
//        @Parameter(
//            description = "Authority name to retrieve (typically ROLE_* format)",
//            required = true,
//            example = "ROLE_ADMIN"
//        )
//        @PathVariable("id") String id
//    ) {
//        LOG.debug("REST request to get Authority : {}", id);
//        Optional<Authority> authority = authorityRepository.findById(id);
//        if (authority.isPresent()) {
//            return ResponseEntity.ok(ApiResponse.success("Authority retrieved successfully", authority.orElseThrow()));
//        } else {
//            return ResponseEntity.status(404).body(ApiResponse.notFound("Authority not found with id: " + id));
//        }
//    }
//
//    /**
//     * {@code DELETE  /authorities/:id} : delete the "id" authority.
//     *
//     * @param id the id of the authority to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @Operation(
//        summary = "Delete an authority",
//        description = "Permanently delete an authority/role from the system. This will affect users with this role. Requires ADMIN authority.",
//        tags = {"Authority Management"},
//        security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @ApiResponses(value = {
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "200",
//            description = "Authority deleted successfully",
//            content = @Content(
//                mediaType = "application/json",
//                schema = @Schema(implementation = ApiResponse.class),
//                examples = @ExampleObject(
//                    name = "Authority deleted",
//                    value = """
//                    {
//                        "status": "SUCCESS",
//                        "message": "Authority deleted successfully"
//                    }
//                    """
//                )
//            )
//        ),
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "404",
//            description = "Authority not found",
//            content = @Content(mediaType = "application/json")
//        ),
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "403",
//            description = "Access denied - ADMIN authority required",
//            content = @Content(mediaType = "application/json")
//        )
//    })
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    public ResponseEntity<ApiResponse<Void>> deleteAuthority(
//        @Parameter(
//            description = "Authority name to delete (be careful - this affects users with this role)",
//            required = true,
//            example = "ROLE_MANAGER"
//        )
//        @PathVariable("id") String id
//    ) {
//        LOG.debug("REST request to delete Authority : {}", id);
//        authorityRepository.deleteById(id);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
//            .body(ApiResponse.deleted("Authority deleted successfully"));
//    }
//}
