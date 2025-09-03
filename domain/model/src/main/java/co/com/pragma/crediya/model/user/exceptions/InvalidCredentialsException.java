package co.com.pragma.crediya.model.user.exceptions;

import co.com.pragma.crediya.model.user.constants.UserErrorMessages;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super(UserErrorMessages.INVALID_CREDENTIALS);
    }

}
