package com.vodacom.customerregistration.api.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * A DTO representing a password reset request - contains the email address.
 */
public class PasswordResetRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public PasswordResetRequestDTO() {
        // Empty constructor needed for Jackson.
    }

    public PasswordResetRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}