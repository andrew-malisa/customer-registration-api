package com.vodacom.customerregistration.api.web.rest.util;

import java.time.Instant;

/**
 * Builder class for constructing ApiResponse objects with fluent interface.
 *
 * @param <T> The type of data being returned
 */
public class ApiResponseBuilder<T> {

    private boolean success;
    private String status;
    private String message;
    private T data;
    private ApiError error;
    private Instant timestamp;
    private String path;

    public ApiResponseBuilder() {
        this.timestamp = Instant.now();
    }

    public ApiResponseBuilder<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public ApiResponseBuilder<T> success() {
        this.success = true;
        return this;
    }

    public ApiResponseBuilder<T> failure() {
        this.success = false;
        return this;
    }

    public ApiResponseBuilder<T> status(String status) {
        this.status = status;
        return this;
    }

    public ApiResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public ApiResponseBuilder<T> error(ApiError error) {
        this.error = error;
        this.success = false;
        return this;
    }

    public ApiResponseBuilder<T> error(String code, String message) {
        this.error = new ApiError(code, message);
        this.success = false;
        return this;
    }

    public ApiResponseBuilder<T> error(String code, String message, String details) {
        this.error = new ApiError(code, message, details);
        this.success = false;
        return this;
    }

    public ApiResponseBuilder<T> timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApiResponseBuilder<T> path(String path) {
        this.path = path;
        if (this.error != null) {
            this.error.setPath(path);
        }
        return this;
    }

    // Convenience methods for common success responses
    public ApiResponseBuilder<T> successResponse(T data) {
        return success()
            .status("SUCCESS")
            .message("Operation completed successfully")
            .data(data);
    }

    public ApiResponseBuilder<T> successResponse(String message, T data) {
        return success()
            .status("SUCCESS")
            .message(message)
            .data(data);
    }

    public ApiResponseBuilder<T> createdResponse(T data) {
        return success()
            .status("CREATED")
            .message("Resource created successfully")
            .data(data);
    }

    public ApiResponseBuilder<T> createdResponse(String message, T data) {
        return success()
            .status("CREATED")
            .message(message)
            .data(data);
    }

    public ApiResponseBuilder<T> updatedResponse(T data) {
        return success()
            .status("UPDATED")
            .message("Resource updated successfully")
            .data(data);
    }

    public ApiResponseBuilder<T> updatedResponse(String message, T data) {
        return success()
            .status("UPDATED")
            .message(message)
            .data(data);
    }

    public ApiResponseBuilder<T> deletedResponse() {
        return success()
            .status("DELETED")
            .message("Resource deleted successfully");
    }

    public ApiResponseBuilder<T> deletedResponse(String message) {
        return success()
            .status("DELETED")
            .message(message);
    }

    // Convenience methods for common error responses
    public ApiResponseBuilder<T> badRequestResponse(String message) {
        return failure()
            .status("BAD_REQUEST")
            .message(message)
            .error(ApiError.badRequest(message));
    }

    public ApiResponseBuilder<T> notFoundResponse(String message) {
        return failure()
            .status("NOT_FOUND")
            .message(message)
            .error(ApiError.notFound(message));
    }

    public ApiResponseBuilder<T> forbiddenResponse(String message) {
        return failure()
            .status("FORBIDDEN")
            .message(message)
            .error(ApiError.forbidden(message));
    }

    public ApiResponseBuilder<T> unauthorizedResponse(String message) {
        return failure()
            .status("UNAUTHORIZED")
            .message(message)
            .error(ApiError.unauthorized(message));
    }

    public ApiResponseBuilder<T> conflictResponse(String message) {
        return failure()
            .status("CONFLICT")
            .message(message)
            .error(ApiError.conflict(message));
    }

    public ApiResponseBuilder<T> validationErrorResponse(String message, ApiError validationError) {
        return failure()
            .status("VALIDATION_ERROR")
            .message(message)
            .error(validationError);
    }

    public ApiResponseBuilder<T> internalServerErrorResponse(String message) {
        return failure()
            .status("INTERNAL_SERVER_ERROR")
            .message(message)
            .error(ApiError.internalServerError(message));
    }

    public ApiResponse<T> build() {
        ApiResponse<T> response = new ApiResponse<>();
//        response.setSuccess(success);
        response.setStatus(status);
        response.setMessage(message);
        response.setData(data);
        response.setError(error);
        response.setTimestamp(timestamp);
        response.setPath(path);
        return response;
    }
}
