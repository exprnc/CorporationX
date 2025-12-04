package school.faang.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        var status = HttpStatus.BAD_REQUEST;
        var body = new ErrorResponse(status.value(), status.toString(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        var status = HttpStatus.BAD_REQUEST;
        var message = getErrorMessageOrDefault(ex, "Validation failed");
        var body = new ErrorResponse(status.value(), status.toString(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        var status = HttpStatus.BAD_REQUEST;
        var body = new ErrorResponse(status.value(), status.toString(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    private String getErrorMessageOrDefault(BindException ex, String defaultMessage) {
        return ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(defaultMessage);
    }
}
