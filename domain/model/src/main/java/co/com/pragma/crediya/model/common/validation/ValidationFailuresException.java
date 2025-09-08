package co.com.pragma.crediya.model.common.validation;

import java.util.List;

public class ValidationFailuresException extends RuntimeException {

    private final transient List<ValidationOutcome> errors;

    public ValidationFailuresException(List<ValidationOutcome> errors) {
        super("Multiple validation errors occurred");

        this.errors = errors;
    }

    public List<ValidationOutcome> getErrors() {
        return errors;
    }

}