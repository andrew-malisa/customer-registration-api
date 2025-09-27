package com.vodacom.customerregistration.api.web.rest;

import com.vodacom.customerregistration.api.config.Constants;
import com.vodacom.customerregistration.api.domain.User;
import com.vodacom.customerregistration.api.repository.UserRepository;
import com.vodacom.customerregistration.api.security.AuthoritiesConstants;
import com.vodacom.customerregistration.api.service.MailService;
import com.vodacom.customerregistration.api.service.UserService;
import com.vodacom.customerregistration.api.service.dto.AdminUserDTO;
import com.vodacom.customerregistration.api.web.rest.errors.BadRequestAlertException;
import com.vodacom.customerregistration.api.web.rest.errors.EmailAlreadyUsedException;
import com.vodacom.customerregistration.api.web.rest.errors.LoginAlreadyUsedException;
import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link com.vodacom.customerregistration.api.domain.User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@Tag(name = "User Administration", description = "Admin APIs for managing system users, roles, and permissions. Requires ADMIN authority.")
@RestController
@RequestMapping("/api/v1/admin")
public class UserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"));

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final MailService mailService;

    public UserResource(UserService userService, UserRepository userRepository, MailService mailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /admin/users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends a
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException       if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @Operation(summary = "Create a new user", description = "Create a new system user with roles and permissions. Sends activation email. Requires ADMIN authority.", tags = {"User Administration"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Login or email already exists", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - ADMIN authority required", content = @Content(mediaType = "application/json"))})
    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ApiResponse<User>> createUser(@Parameter(description = "User data to create", required = true) @Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {
        LOG.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/v1/admin/users/" + newUser.getLogin())).headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUser.getLogin())).body(ApiResponse.created("User created successfully", newUser));
        }
    }

    /**
     * {@code PUT /admin/users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @Operation(summary = "Update an existing user", description = "Update user details, roles, and permissions. Requires ADMIN authority.", tags = {"User Administration"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Login or email conflict", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json"))})
    @PutMapping({"/users", "/users/{login}"})
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUser(@Parameter(description = "User login (optional)") @PathVariable(name = "login", required = false) @Pattern(regexp = Constants.LOGIN_REGEX) String login, @Parameter(description = "Updated user data", required = true) @Valid @RequestBody AdminUserDTO userDTO) {
        LOG.debug("REST request to update User : {}", userDTO);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);

        if (updatedUser.isPresent()) {
            return ResponseEntity.ok().headers(HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getLogin())).body(ApiResponse.updated("User updated successfully", updatedUser.orElseThrow()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code GET /admin/users} : get all users with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @Operation(summary = "Get all users", description = "Retrieve all system users with pagination. Only accessible to administrators.", tags = {"User Administration"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - ADMIN authority required", content = @Content(mediaType = "application/json"))})
    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ApiResponse<List<AdminUserDTO>>> getAllUsers(@Parameter(description = "Pagination parameters") @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all User for an admin");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<AdminUserDTO> page = userService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(ApiResponse.success("Users retrieved successfully", page.getContent()), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * {@code GET /admin/users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @Operation(summary = "Get user by login", description = "Retrieve a specific user by their login username. Requires ADMIN authority.", tags = {"User Administration"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found and returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json"))})
    @GetMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ApiResponse<AdminUserDTO>> getUser(@Parameter(description = "User login username", required = true, example = "admin") @PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to get User : {}", login);
        Optional<AdminUserDTO> userDTO = userService.getUserWithAuthoritiesByLogin(login).map(AdminUserDTO::new);
        if (userDTO.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO.orElseThrow()));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.notFound("User not found with login: " + login));
        }
    }

    /**
     * {@code DELETE /admin/users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Operation(summary = "Delete a user", description = "Permanently delete a user from the system. This action cannot be undone. Requires ADMIN authority.", tags = {"User Administration"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@Parameter(description = "User login to delete", required = true, example = "username") @PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", login)).body(ApiResponse.deleted("User deleted successfully"));
    }
}
