package ru.yandex.practicum.telemetry.collector.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return String.format("Field '%s': %s", fieldError.getField(), error.getDefaultMessage());
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        String errorMessage = "Validation failed for request";
        log.warn("Validation errors for {} {}: {}", request.getMethod(), request.getRequestURI(), errors);

        ApiError apiError = ApiError.create(
                errorMessage,
                "Invalid request parameters",
                HttpStatus.BAD_REQUEST.name(),
                errors
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex,
                                                              HttpServletRequest request) {
        log.warn("Validation exception for {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.create(
                ex.getMessage(),
                "Business validation failed",
                HttpStatus.BAD_REQUEST.name(),
                ex.getErrors()
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(EventProcessingException.class)
    public ResponseEntity<ApiError> handleEventProcessingException(EventProcessingException ex,
                                                                   HttpServletRequest request) {
        log.error("Event processing error for {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ApiError apiError = ApiError.create(
                "Error processing event",
                "Internal event processing error",
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                   HttpServletRequest request) {
        log.warn("Illegal argument for {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.create(
                ex.getMessage(),
                "Invalid argument provided",
                HttpStatus.BAD_REQUEST.name()
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error for {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ApiError apiError = ApiError.create(
                "Internal server error",
                "Unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
