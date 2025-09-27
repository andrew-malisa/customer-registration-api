package com.vodacom.customerregistration.api.web.rest.errors;

import com.vodacom.customerregistration.api.web.rest.util.ApiError;
import com.vodacom.customerregistration.api.web.rest.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that provides standardized error responses using ApiResponse format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ApiError.FieldError(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage(),
                error.getCode()
            ))
            .collect(Collectors.toList());

        ApiError apiError = ApiError.validationError("Validation failed", fieldErrors)
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .validationErrorResponse("Request validation failed", apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Constraint violation: {}", ex.getMessage());
        
        List<ApiError.FieldError> fieldErrors = ex.getConstraintViolations()
            .stream()
            .map(violation -> new ApiError.FieldError(
                violation.getPropertyPath().toString(),
                violation.getInvalidValue(),
                violation.getMessage(),
                violation.getMessageTemplate()
            ))
            .collect(Collectors.toList());

        ApiError apiError = ApiError.validationError("Constraint validation failed", fieldErrors)
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .validationErrorResponse("Constraint validation failed", apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle bad request alert exceptions
     */
    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestAlert(
            BadRequestAlertException ex, HttpServletRequest request) {
        
        log.warn("Bad request: {}", ex.getMessage());
        
        ApiError apiError = ApiError.badRequest(ex.getMessage())
            .withDetails("Entity: " + ex.getEntityName() + ", Error: " + ex.getErrorKey())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .badRequestResponse(ex.getMessage())
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ApiError apiError = ApiError.unauthorized("Authentication failed")
            .withDetails(ex.getMessage())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .unauthorizedResponse("Authentication required")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle authorization exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ApiError apiError = ApiError.forbidden("Access denied")
            .withDetails(ex.getMessage())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .forbiddenResponse("Access denied")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        log.warn("No handler found: {}", ex.getMessage());
        
        ApiError apiError = ApiError.notFound("Resource not found")
            .withDetails("No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .notFoundResponse("Resource not found")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle method not allowed exceptions
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        log.warn("Method not allowed: {}", ex.getMessage());
        
        ApiError apiError = ApiError.of("METHOD_NOT_ALLOWED", "HTTP method not supported", ex.getMessage())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .failure()
            .status("METHOD_NOT_ALLOWED")
            .message("HTTP method not supported")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Handle unsupported media type exceptions
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        
        log.warn("Unsupported media type: {}", ex.getMessage());
        
        ApiError apiError = ApiError.of("UNSUPPORTED_MEDIA_TYPE", "Media type not supported", ex.getMessage())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .failure()
            .status("UNSUPPORTED_MEDIA_TYPE")
            .message("Media type not supported")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * Handle malformed JSON exceptions
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.warn("Malformed JSON: {}", ex.getMessage());
        
        ApiError apiError = ApiError.badRequest("Malformed JSON request")
            .withDetails(ex.getMessage())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .badRequestResponse("Invalid JSON format")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle missing request parameter exceptions
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        log.warn("Missing request parameter: {}", ex.getMessage());
        
        ApiError apiError = ApiError.badRequest("Required request parameter is missing")
            .withDetails("Parameter '" + ex.getParameterName() + "' of type '" + ex.getParameterType() + "' is missing")
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .badRequestResponse("Missing required parameter")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle method argument type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("Method argument type mismatch: {}", ex.getMessage());
        
        ApiError apiError = ApiError.badRequest("Invalid parameter type")
            .withDetails("Parameter '" + ex.getName() + "' should be of type '" + ex.getRequiredType().getSimpleName() + "'")
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .badRequestResponse("Invalid parameter type")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle data integrity violation exceptions
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        log.warn("Data integrity violation: {}", ex.getMessage());
        
        ApiError apiError = ApiError.conflict("Data integrity constraint violated")
            .withDetails(ex.getMessage())
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .conflictResponse("Data integrity constraint violated")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error occurred: ", ex);
        
        ApiError apiError = ApiError.internalServerError("An unexpected error occurred")
            .withDetails("Please contact support if the problem persists")
            .withPath(request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
            .internalServerErrorResponse("Internal server error")
            .error(apiError)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getCurrentRequestPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest().getRequestURI();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}