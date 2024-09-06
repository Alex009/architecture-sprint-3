package ru.yandex.practicum.telemetry.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        HttpStatus status = getStatus(ex);
        logError(ex, status);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus getStatus(Exception ex) {
        if (ex.getClass().isAnnotationPresent(ResponseStatus.class)) {
            return ex.getClass().getAnnotation(ResponseStatus.class).value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // Default status if none is specified
    }

    private void logError(Exception ex, HttpStatus status) {
        // Логирование ошибки с уровнем WARN для исключений с кодом 4xx, и ERROR для 5xx
        if (status.is4xxClientError()) {
            logger.warn("Client error occurred: Status Code - {}, Message - {}", status.value(), ex.getMessage(), ex);
        } else {
            logger.error("Server error occurred: Status Code - {}, Message - {}", status.value(), ex.getMessage(), ex);
        }
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
