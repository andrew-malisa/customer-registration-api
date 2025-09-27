package com.vodacom.customerregistration.api.web.rest.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Generic API response wrapper for consistent response structure across the application.
 *
 * @param <T> The type of data being returned
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

//    private boolean success;
    private String status;
    private String message;
    private T data;
    private ApiError error;
    private Instant timestamp;
    private String path;

    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    public ApiResponse(boolean success, String status, String message) {
        this();
//        this.success = success;
        this.status = status;
        this.message = message;
    }

    public ApiResponse(boolean success, String status, String message, T data) {
        this(success, status, message);
        this.data = data;
    }

    public ApiResponse(boolean success, String status, String message, T data, ApiError error) {
        this(success, status, message, data);
        this.error = error;
    }

    // Static factory methods for success responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "Operation completed successfully", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, "SUCCESS", message);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "CREATED", "Resource created successfully", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, "CREATED", message, data);
    }

    public static <T> ApiResponse<T> updated(T data) {
        return new ApiResponse<>(true, "UPDATED", "Resource updated successfully", data);
    }

    public static <T> ApiResponse<T> updated(String message, T data) {
        return new ApiResponse<>(true, "UPDATED", message, data);
    }

    public static <T> ApiResponse<T> deleted() {
        return new ApiResponse<>(true, "DELETED", "Resource deleted successfully");
    }

    public static <T> ApiResponse<T> deleted(String message) {
        return new ApiResponse<>(true, "DELETED", message);
    }

    // Static factory methods for error responses
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, "ERROR", message);
    }

    public static <T> ApiResponse<T> error(String message, ApiError error) {
        ApiResponse<T> response = new ApiResponse<>(false, "ERROR", message);
        response.setError(error);
        return response;
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(false, "BAD_REQUEST", message);
    }

    public static <T> ApiResponse<T> badRequest(String message, ApiError error) {
        ApiResponse<T> response = new ApiResponse<>(false, "BAD_REQUEST", message);
        response.setError(error);
        return response;
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, "NOT_FOUND", message);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, "FORBIDDEN", message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, "UNAUTHORIZED", message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(false, "CONFLICT", message);
    }

    public static <T> ApiResponse<T> conflict(String message, ApiError error) {
        ApiResponse<T> response = new ApiResponse<>(false, "CONFLICT", message);
        response.setError(error);
        return response;
    }

    public static <T> ApiResponse<T> validationError(String message, ApiError error) {
        ApiResponse<T> response = new ApiResponse<>(false, "VALIDATION_ERROR", message);
        response.setError(error);
        return response;
    }

    public static <T> ApiResponse<T> internalServerError(String message) {
        return new ApiResponse<>(false, "INTERNAL_SERVER_ERROR", message);
    }

    public static <T> ApiResponse<T> internalServerError(String message, ApiError error) {
        ApiResponse<T> response = new ApiResponse<>(false, "INTERNAL_SERVER_ERROR", message);
        response.setError(error);
        return response;
    }

    // Builder pattern support
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    // Getters and setters
//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
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

    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
//            "success=" + success +
            ", status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", data=" + data +
            ", error=" + error +
            ", timestamp=" + timestamp +
            ", path='" + path + '\'' +
            '}';
    }
}
