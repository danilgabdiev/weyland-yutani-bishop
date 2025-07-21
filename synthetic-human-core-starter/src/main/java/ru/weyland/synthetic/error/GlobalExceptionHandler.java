package ru.weyland.synthetic.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCommand(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "invalid",
                        (existing, replacement) -> existing // при дублировании поля берем первое
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(CommandQueueOverflowException.class)
    public ResponseEntity<Object> handleFullQueue(CommandQueueOverflowException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("error", "Queue is full");
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("error", "Internal Server Error");
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, String> body = new HashMap<>();

        // Если причина — ошибка Enum
        if (ex.getCause() instanceof InvalidFormatException invalidFormatEx) {
            var targetType = invalidFormatEx.getTargetType();
            if (targetType.isEnum()) {
                String fieldName = invalidFormatEx.getPath().stream()
                        .map(ref -> ref.getFieldName())
                        .findFirst()
                        .orElse("unknown");

                String allowedValues = String.join(", ",
                        Arrays.stream(targetType.getEnumConstants())
                                .map(Object::toString)
                                .toList()
                );

                body.put(fieldName, fieldName + " must be one of: " + allowedValues);
                return ResponseEntity.badRequest().body(body);
            }
        }

        body.put("error", "Malformed JSON or unsupported data format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}