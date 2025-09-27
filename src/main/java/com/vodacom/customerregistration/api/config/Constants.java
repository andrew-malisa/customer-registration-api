package com.vodacom.customerregistration.api.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)|(?>(\\+?255|0)[67]\\d{8})$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    private Constants() {}
}
