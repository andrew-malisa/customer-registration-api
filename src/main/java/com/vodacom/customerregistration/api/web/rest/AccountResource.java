package com.vodacom.customerregistration.api.web.rest;

import com.vodacom.customerregistration.api.domain.User;
import com.vodacom.customerregistration.api.repository.UserRepository;
import com.vodacom.customerregistration.api.security.SecurityUtils;
import com.vodacom.customerregistration.api.service.MailService;
import com.vodacom.customerregistration.api.service.UserService;
import com.vodacom.customerregistration.api.service.dto.AdminUserDTO;
import com.vodacom.customerregistration.api.service.dto.PasswordChangeDTO;
import com.vodacom.customerregistration.api.service.dto.PasswordResetRequestDTO;
import com.vodacom.customerregistration.api.web.rest.errors.EmailAlreadyUsedException;
import com.vodacom.customerregistration.api.web.rest.errors.InvalidPasswordException;
import com.vodacom.customerregistration.api.web.rest.errors.LoginAlreadyUsedException;
import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
import com.vodacom.customerregistration.api.web.rest.vm.KeyAndPasswordVM;
import com.vodacom.customerregistration.api.web.rest.vm.ManagedUserVM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@Tag(name = "Account Management", description = "User account management APIs including registration, activation, profile updates, and password management.")
@RestController
@RequestMapping("/api/v1")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException  {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @Operation(summary = "Register a new user account", description = "Create a new user account. Sends activation email. No authentication required.", tags = {"Account Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully - activation email sent", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid data or email/login already exists", content = @Content(mediaType = "application/json"))})
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerAccount(@Parameter(description = "User registration information", required = true) @Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>created("User registered successfully", null));
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @Operation(summary = "Activate user account", description = "Activate a user account using the activation key sent via email.", tags = {"Account Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account activated successfully", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Invalid activation key", content = @Content(mediaType = "application/json"))})
    @GetMapping("/activate")
    public ResponseEntity<ApiResponse<Void>> activateAccount(@Parameter(description = "Activation key from email", required = true) @RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
        return ResponseEntity.ok(ApiResponse.success("Account activated successfully"));
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @Operation(summary = "Get current user account", description = "Retrieve the current authenticated user's account information.", tags = {"Account Management"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account information retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = "application/json"))})
    @GetMapping("/account")
    public ResponseEntity<ApiResponse<AdminUserDTO>> getAccount() {
        AdminUserDTO userDTO = userService.getUserWithAuthorities().map(AdminUserDTO::new).orElseThrow(() -> new AccountResourceException("User could not be found"));
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", userDTO));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @Operation(summary = "Update current user account", description = "Update the current authenticated user's profile information.", tags = {"Account Management"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account updated successfully", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Email already in use", content = @Content(mediaType = "application/json"))})
    @PostMapping("/account")
    public ResponseEntity<ApiResponse<Void>> saveAccount(@Parameter(description = "Updated user information", required = true) @Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getLangKey(), userDTO.getImageUrl());
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully"));
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @Operation(summary = "Change user password", description = "Change the current authenticated user's password. Requires current password for verification.", tags = {"Account Management"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid password", content = @Content(mediaType = "application/json"))})
    @PostMapping(path = "/account/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Parameter(description = "Current and new password", required = true) @RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param passwordResetRequest the password reset request containing the email.
     */
    @Operation(summary = "Request password reset", description = "Send password reset email to user. Always returns success for security reasons.", tags = {"Account Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset request processed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Reset request processed", value = """
        {
            "status": "SUCCESS",
            "message": "Password reset request processed"
        }
        """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid email format", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Invalid email", value = """
        {
            "error": "Bad Request",
            "message": "Email must be valid"
        }
        """)))})
    @PostMapping(path = "/account/reset-password/init")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Parameter(description = "Password reset request containing email address", required = true, example = """
        {
            "email": "user@example.com"
        }
        """) @Valid @RequestBody PasswordResetRequestDTO passwordResetRequest) {
        Optional<User> user = userService.requestPasswordReset(passwordResetRequest.getEmail());
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            LOG.warn("Password reset requested for non existing mail");
        }
        return ResponseEntity.ok(ApiResponse.success("Password reset request processed"));
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @Operation(summary = "Complete password reset", description = "Complete password reset using the key from reset email and new password.", tags = {"Account Management"})
    @ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset completed successfully", content = @Content(mediaType = "application/json")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid password or reset key", content = @Content(mediaType = "application/json"))})
    @PostMapping(path = "/account/reset-password/finish")
    public ResponseEntity<ApiResponse<Void>> finishPasswordReset(@Parameter(description = "Reset key and new password", required = true) @RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
        return ResponseEntity.ok(ApiResponse.success("Password reset completed successfully"));
    }


    private static boolean isPasswordLengthInvalid(String password) {
        return (StringUtils.isEmpty(password) || password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH || password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH);
    }
}
