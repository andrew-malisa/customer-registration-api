package com.vodacom.customerregistration.api.web.rest.vm;

import com.vodacom.customerregistration.api.service.dto.AdminUserDTO;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends AdminUserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Pattern(regexp = "^(\\+?255|0)[67]\\d{8}$", message = "Phone number must be a valid Tanzanian mobile number")
    private String phoneNumber;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + ", phoneNumber='" + phoneNumber + "'}";
    }
}
