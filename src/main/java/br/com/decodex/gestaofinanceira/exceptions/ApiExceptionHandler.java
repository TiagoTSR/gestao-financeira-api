package br.com.decodex.gestaofinanceira.exceptions;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            OffsetDateTime.now().toString()
        );
        return ResponseEntity.badRequest().body(error);
    }

    public static class ApiError {
        private int status;
        private String message;
        private String timestamp;

        public ApiError(int status, String message, String timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }
}