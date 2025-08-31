package co.com.pragma.crediya.model.user.exceptions;

import co.com.pragma.crediya.model.user.constants.UserErrorMessages;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException() {
        super(UserErrorMessages.ROLE_NOT_FOUND);
    }

}