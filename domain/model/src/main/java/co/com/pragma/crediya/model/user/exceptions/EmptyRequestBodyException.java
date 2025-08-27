package co.com.pragma.crediya.model.user.exceptions;

public class EmptyRequestBodyException extends RuntimeException {

    public EmptyRequestBodyException() {
        super(ErrorMessages.REQUEST_BODY_REQUIRED);
    }

}