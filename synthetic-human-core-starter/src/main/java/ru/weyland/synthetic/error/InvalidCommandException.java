package ru.weyland.synthetic.error;

import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

public class InvalidCommandException extends RuntimeException {

    private final Set<ConstraintViolation<?>> violations;


    public InvalidCommandException(String message, Set<? extends ConstraintViolation<?>> violations) {
        super(message);
        this.violations = violations.stream()
                .map(v -> (ConstraintViolation<?>) v)
                .collect(Collectors.toSet());
    }

    public Set<ConstraintViolation<?>> getViolations() {
        return violations;
    }
}
