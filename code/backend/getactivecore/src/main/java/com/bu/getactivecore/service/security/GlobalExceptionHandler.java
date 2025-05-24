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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * @param apiError
     * @return
     */
    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiErrorResponse(apiError), apiError.getStatus());
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_DATA_STRUCTURE;
        String errorMessage = ErrorCode.INVALID_DATA_STRUCTURE.getDetails();
        return buildResponseEntity(new ApiError(errorCode, HttpStatus.BAD_REQUEST, errorMessage, ex));
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException apiEx) {
        ApiError apiError = new ApiError(apiEx.getErrorCode(), apiEx.getStatus(), apiEx.getErrorMessage(), apiEx);
        return buildResponseEntity(apiError);
    }

        /**
    @ai-generated,
    Tool: Google Gemini,
    Prompt: "org.springframework.web.bind.MethodArgumentNotValidException",
    Generated on: 2025-05-22,
    Modified by: Jin Hao Li,
    Verified: ✅ Unit tested, reviewed
*/


    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), null, errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }*/
}
