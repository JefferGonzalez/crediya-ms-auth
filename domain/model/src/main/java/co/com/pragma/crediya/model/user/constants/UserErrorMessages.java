package co.com.pragma.crediya.model.user.constants;

public final class UserErrorMessages {

    private UserErrorMessages() {
    }

    public static final String USER_NOT_FOUND = "User not found";

    public static final String ROLE_NOT_FOUND = "We couldn’t find the specified role.";

    public static final String SALARY_OUT_OF_RANGE = "Base salary must be between 0 and 15,000,000.";

    public static final String IDENTIFICATION_NUMBER_ALREADY_TAKEN = "This identification number is already registered.";

    public static final String EMAIL_ALREADY_TAKEN = "This email address is already registered.";

    public static final String INVALID_CREDENTIALS = "Invalid credentials provided. Please check your email and password.";

    public static final String USER_DATA_INCONSISTENCY = "Authentication failed due to invalid user data";

}
