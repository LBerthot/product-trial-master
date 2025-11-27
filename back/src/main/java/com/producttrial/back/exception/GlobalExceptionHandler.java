package com.producttrial.back.exception;

import com.producttrial.back.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400 pour @Valid on @RequestBody (ex: @NotBlank fail)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String message = "Validation failed for " + fields.size() + " field(s)";
        log.warn("Validation failed: {}", fields);

        ErrorResponse body = new ErrorResponse(Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                req.getRequestURI());
        body.setFields(fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 400 pour @Validated @PathVariable/@RequestParam violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        log.warn("Constraint violation: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 403 pour AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Access denied: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest req) {
        return buildNotFoundResponse(ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest req) {
        return buildNotFoundResponse(ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(WishlistItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWishlistItemNotFound(WishlistItemNotFoundException ex, HttpServletRequest req) {
        return buildNotFoundResponse(ex.getMessage(), req.getRequestURI());
    }

    // 404 pour NotFoundException
    private ResponseEntity<ErrorResponse> buildNotFoundResponse(String message, String path) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                404,
                "Not Found",
                message,
                path
        );
        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        Throwable root = ex.getRootCause();
        String detail;
        if (root != null && root.getMessage() != null) {
            detail = root.getMessage();
        } else {
            detail = ex.getMessage() != null ? ex.getMessage() : "Data integrity violation";
        }

        return buildConflictResponse(detail, req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Illegal argument: {}", ex.getMessage(), ex);
        Throwable root = ex.getCause();
        String detail;
        if (root != null && root.getMessage() != null) {
            detail = root.getMessage();
        } else {
            detail = ex.getMessage() != null ? ex.getMessage() : "Invalid argument";
        }

        return buildConflictResponse(detail, req.getRequestURI());
    }

    // 409 pour conflict exception
    private ResponseEntity<ErrorResponse> buildConflictResponse(String message, String path) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // handler pour ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req) {
        int rawStatus = ex.getStatusCode().value();
        log.warn("ResponseStatusException: status={} message={} path={}", rawStatus, ex.getReason(), req.getRequestURI());

        HttpStatus status = HttpStatus.resolve(rawStatus);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }


    // Catch-all -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception for {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse body = new ErrorResponse(Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unexpected error",
                req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
