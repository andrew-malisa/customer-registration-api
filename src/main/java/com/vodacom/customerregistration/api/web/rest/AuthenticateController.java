package com.vodacom.customerregistration.api.web.rest;

import static com.vodacom.customerregistration.api.security.SecurityUtils.AUTHORITIES_CLAIM;
import static com.vodacom.customerregistration.api.security.SecurityUtils.JWT_ALGORITHM;
import static com.vodacom.customerregistration.api.security.SecurityUtils.USER_ID_CLAIM;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vodacom.customerregistration.api.domain.ActivityLog;
import com.vodacom.customerregistration.api.security.DomainUserDetailsService.UserWithId;
import com.vodacom.customerregistration.api.service.ActivityLogService;
import com.vodacom.customerregistration.api.web.rest.vm.LoginVM;
import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@Tag(name = "Authentication", description = "User authentication and session management APIs. Handles login, logout, and token validation.")
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ActivityLogService activityLogService;

    public AuthenticateController(JwtEncoder jwtEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, ActivityLogService activityLogService) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.activityLogService = activityLogService;
    }

    @Operation(
        summary = "Authenticate user",
        description = "Authenticate a user with username/password and return a JWT access token. Supports 'remember me' functionality for extended token validity.",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Authentication successful - JWT token returned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Successful authentication",
                    value = """
                    {
                        "status": "SUCCESS",
                        "message": "Authentication successful",
                        "data": {
                            "access_token": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOIFJPTEVfVVNFUiIsImV4cCI6MTY0MDk5NTIwMH0...",
                            "token_type": "Bearer",
                            "expires_in": 86400
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Authentication failed - Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Authentication failed",
                    value = """
                    {
                        "error": "Unauthorized",
                        "message": "Bad credentials"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid input format",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid input",
                    value = """
                    {
                        "error": "Bad Request",
                        "message": "Username and password are required"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<TokenResponse>> authorize(
        @Parameter(
            description = "Login credentials with username and password",
            required = true,
            example = """
            {
                "username": "admin",
                "password": "admin",
                "rememberMe": false
            }
            """
        )
        @Valid @RequestBody LoginVM loginVM
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = this.createToken(authentication, loginVM.isRememberMe());
        
        // Log successful login
        activityLogService.logActivity(
            ActivityLog.ActionType.AGENT_LOGIN,
            "Agent",
            null,
            String.format("Agent %s logged in successfully", authentication.getName())
        );
        
        // Calculate expires_in value
        long expiresIn = loginVM.isRememberMe() ? 
            this.tokenValidityInSecondsForRememberMe : 
            this.tokenValidityInSeconds;
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        TokenResponse tokenResponse = new TokenResponse(jwt, expiresIn);
        return new ResponseEntity<>(ApiResponse.success("Authentication successful", tokenResponse), httpHeaders, HttpStatus.OK);
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)},
     * or with status {@code 401 (Unauthorized)} if not authenticated.
     */
    @Operation(
        summary = "Check authentication status",
        description = "Verify if the current user is authenticated by checking for a valid JWT token in the request headers.",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User is authenticated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Authenticated user",
                    value = """
                    {
                        "status": "SUCCESS",
                        "message": "User is authenticated"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "User not authenticated - No valid token provided",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not authenticated",
                    value = """
                    {
                        "status": "UNAUTHORIZED",
                        "message": "User not authenticated"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/authenticate")
    public ResponseEntity<ApiResponse<Void>> isAuthenticated(
        @Parameter(
            description = "Automatically injected Principal from JWT token (if valid)",
            hidden = true
        )
        Principal principal
    ) {
        LOG.debug("REST request to check if the current user is authenticated");
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.unauthorized("User not authenticated"));
        } else {
            return ResponseEntity.ok(ApiResponse.success("User is authenticated"));
        }
    }

    /**
     * {@code POST /logout} : logout the current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and logout message.
     */
    @Operation(
        summary = "Logout user",
        description = "Logout the current authenticated user by clearing the security context. Note: JWT tokens are stateless, so this only clears server-side session data.",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Logout successful",
                    value = """
                    {
                        "status": "SUCCESS",
                        "message": "Logout successful",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(Principal principal) {
        LOG.debug("REST request to logout current user");
        
        // Log logout activity
        if (principal != null) {
            activityLogService.logActivity(
                ActivityLog.ActionType.AGENT_LOGOUT,
                "Agent",
                null,
                String.format("Agent %s logged out", principal.getName())
            );
        }
        
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        // @formatter:off
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_CLAIM, authorities);
        if (authentication.getPrincipal() instanceof UserWithId user) {
            builder.claim(USER_ID_CLAIM, user.getId());
        }

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class TokenResponse {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType = "Bearer";

        @JsonProperty("expires_in")
        private long expiresIn;

        TokenResponse(String accessToken, long expiresIn) {
            this.accessToken = accessToken;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
