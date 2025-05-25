package com.bu.getactivecore.service.security;

import com.bu.getactivecore.shared.ApiError;
import com.bu.getactivecore.shared.ApiErrorResponse;
import com.bu.getactivecore.shared.ErrorCode;
import com.bu.getactivecore.shared.exception.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler to standardize API error responses across the application.
 *
 * <p>Extends {@link ResponseEntityExceptionHandler} to customize handling of specific exceptions
 * and provide consistent error payloads wrapped in {@link ApiErrorResponse}.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Builds a {@link ResponseEntity} containing the given {@link ApiError}.
     *
     * @param apiError The error details to include in the response.
     * @return A {@link ResponseEntity} containing the error response.
     */
    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiErrorResponse(apiError), apiError.getStatus());
    }

    /**
     * Handles {@link HttpMessageNotReadableException} by returning a structured
     * error response with a predefined error code and message.
     *
     * @param ex      The exception that occurred.
     * @param headers The HTTP headers of the request.
     * @param status  The HTTP status code.
     * @param request The web request that caused the exception.
     * @return A {@link ResponseEntity} containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode errorCode = ErrorCode.DATA_STRUCTURE_INVALID;
        String errorMessage = ErrorCode.DATA_STRUCTURE_INVALID.getDetails();
        return buildResponseEntity(ApiError.from(errorCode, HttpStatus.BAD_REQUEST, errorMessage, ex));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> validationErrors = new LinkedHashMap<>();

        // Field-level validation errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors
                    .computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        // Global-level validation errors
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            validationErrors
                    .computeIfAbsent(error.getObjectName(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }
        return buildResponseEntity(ApiError.from(ErrorCode.DATA_STRUCTURE_INVALID, HttpStatus.BAD_REQUEST, ErrorCode.DATA_STRUCTURE_INVALID.getDetails(), validationErrors, ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode errorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE;
        String errorMessage = String.format("Expected content type(s) %s but received '%s'",
                ex.getSupportedMediaTypes(), ex.getContentType());
        return buildResponseEntity(ApiError.from(errorCode, HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorMessage, ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode errorCode = ErrorCode.UNSUPPORTED_OPERATION;
        String errorMessage = String.format("Expected HTTP method %s but received %s", ex.getMethod(), ex.getSupportedHttpMethods());
        return buildResponseEntity(ApiError.from(errorCode, HttpStatus.METHOD_NOT_ALLOWED, errorMessage, ex));
    }

    /**
     * Handles custom {@link ApiException} by returning a structured error response
     * with details provided by the exception.
     *
     * @param apiEx The custom exception that occurred.
     * @return A {@link ResponseEntity} containing the error response.
     */
    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException apiEx) {
        ApiError apiError = ApiError.from(apiEx.getErrorCode(), apiEx.getStatus(), apiEx.getErrorMessage(), apiEx);
        return buildResponseEntity(apiError);
    }
}
