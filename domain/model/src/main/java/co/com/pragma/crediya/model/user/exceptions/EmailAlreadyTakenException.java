package co.com.pragma.crediya.model.user.exceptions;

public class EmailAlreadyTakenException extends RuntimeException {

    public EmailAlreadyTakenException() {
        super(ErrorMessages.EMAIL_ALREADY_TAKEN);
    }

}