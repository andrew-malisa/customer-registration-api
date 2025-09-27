package com.vodacom.customerregistration.api.web.rest.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Standardized error information for API responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String code;
    private String message;
    private String details;
    private Instant timestamp;
    private String path;
    private Map<String, Object> metadata;
    private List<FieldError> fieldErrors;

    public ApiError() {
        this.timestamp = Instant.now();
    }

    public ApiError(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ApiError(String code, String message, String details) {
        this(code, message);
        this.details = details;
    }

    public ApiError(String code, String message, String details, String path) {
        this(code, message, details);
        this.path = path;
    }

    // Static factory methods
    public static ApiError of(String code, String message) {
        return new ApiError(code, message);
    }

    public static ApiError of(String code, String message, String details) {
        return new ApiError(code, message, details);
    }

    public static ApiError of(String code, String message, String details, String path) {
        return new ApiError(code, message, details, path);
    }

    public static ApiError validationError(String message, List<FieldError> fieldErrors) {
        ApiError error = new ApiError("VALIDATION_ERROR", message);
        error.setFieldErrors(fieldErrors);
        return error;
    }

    public static ApiError badRequest(String message) {
        return new ApiError("BAD_REQUEST", message);
    }

    public static ApiError notFound(String message) {
        return new ApiError("NOT_FOUND", message);
    }

    public static ApiError forbidden(String message) {
        return new ApiError("FORBIDDEN", message);
    }

    public static ApiError unauthorized(String message) {
        return new ApiError("UNAUTHORIZED", message);
    }

    public static ApiError conflict(String message) {
        return new ApiError("CONFLICT", message);
    }

    public static ApiError internalServerError(String message) {
        return new ApiError("INTERNAL_SERVER_ERROR", message);
    }

    // Fluent methods
    public ApiError withDetails(String details) {
        this.details = details;
        return this;
    }

    public ApiError withPath(String path) {
        this.path = path;
        return this;
    }

    public ApiError withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public ApiError withFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
        return this;
    }

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    /**
     * Represents a field-specific validation error.
     */
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String message;
        private String code;

        public FieldError() {}

        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        public FieldError(String field, Object rejectedValue, String message, String code) {
            this(field, rejectedValue, message);
            this.code = code;
        }

        // Getters and setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                "field='" + field + '\'' +
                ", rejectedValue=" + rejectedValue +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "ApiError{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            ", details='" + details + '\'' +
            ", timestamp=" + timestamp +
            ", path='" + path + '\'' +
            ", metadata=" + metadata +
            ", fieldErrors=" + fieldErrors +
            '}';
    }
}