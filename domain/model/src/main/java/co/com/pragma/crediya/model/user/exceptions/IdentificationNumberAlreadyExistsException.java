package co.com.pragma.crediya.model.user.exceptions;

import co.com.pragma.crediya.model.user.constants.UserErrorMessages;

public class IdentificationNumberAlreadyExistsException extends RuntimeException {

    public IdentificationNumberAlreadyExistsException() {
        super(UserErrorMessages.IDENTIFICATION_NUMBER_ALREADY_TAKEN);
    }

}