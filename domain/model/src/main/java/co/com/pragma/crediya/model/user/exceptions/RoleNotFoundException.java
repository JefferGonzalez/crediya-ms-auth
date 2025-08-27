package co.com.pragma.crediya.model.user.exceptions;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException() {
        super(ErrorMessages.ROLE_NOT_FOUND);
    }

}