package com.vodacom.customerregistration.api.service.dto;

import com.vodacom.customerregistration.api.domain.enumeration.AgentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * DTO for registering an Agent with associated User account in a single request
 */
public class AgentRegistrationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // User account fields
    @NotBlank(message = "Login is required")
    @Pattern(regexp = "^(\\+?255|0)[67]\\d{8}$", 
             message = "Login must be a valid Tanzanian phone number")
    @Size(min = 1, max = 50, message = "Login must be between 1 and 50 characters")
    private String login;


    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @Size(min = 5, max = 254, message = "Email must be between 5 and 254 characters")
    private String email;

    @Size(max = 6, message = "Language key cannot exceed 6 characters")
    private String langKey = "en";

    // Agent-specific fields
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+?255|0)[67]\\d{8}$", message = "Phone number must be a valid Tanzanian mobile number")
    private String phoneNumber;

    @NotNull(message = "Agent status is required")
    private AgentStatus status = AgentStatus.ACTIVE;

    @Size(max = 100, message = "Region cannot exceed 100 characters")
    private String region;

    @Size(max = 100, message = "District cannot exceed 100 characters")
    private String district;

    @Size(max = 100, message = "Ward cannot exceed 100 characters")
    private String ward;

    // Constructors
    public AgentRegistrationDTO() {}

    public AgentRegistrationDTO(String login, String firstName, String lastName, 
                               String email, String phoneNumber, AgentStatus status) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    // Getters and Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Override
    public String toString() {
        return "AgentRegistrationDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", status=" + status +
            ", region='" + region + '\'' +
            ", district='" + district + '\'' +
            ", ward='" + ward + '\'' +
            '}';
    }
}