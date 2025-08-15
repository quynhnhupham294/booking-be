package org.example.bookingql.enums;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingql.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private void logError(Exception e, HttpServletRequest request) {
        log.error("Failed to call API " + request.getRequestURI() + " : " + e);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException e, HttpServletRequest request) {
        logError(e, request);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus()) // <-- HTTP status theo enum
                .body(ApiResponse.builder()
                        .status(errorCode.getStatus())
                        .message(e.getMessage())
                        .build()
                );
    }
}
