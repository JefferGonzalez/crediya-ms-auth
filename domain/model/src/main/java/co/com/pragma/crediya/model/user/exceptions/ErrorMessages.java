package co.com.pragma.crediya.model.user.exceptions;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String ROLE_NOT_FOUND = "We couldn’t find the specified role.";

    public static final String SALARY_OUT_OF_RANGE = "Base salary must be between 0 and 15,000,000.";

    public static final String EMAIL_ALREADY_TAKEN = "This email address is already registered.";

    public static final String REQUEST_BODY_REQUIRED = "Request body is required";

    public static final String NAME_REQUIRED = "Name cannot be empty.";

    public static final String NAME_MIN_LENGTH = "Name must have at least 3 characters.";

    public static final String NAME_MAX_LENGTH = "Name cannot exceed 100 characters.";

    public static final String NAME_INVALID_FORMAT = "Name can only contain letters.";

    public static final String LASTNAME_REQUIRED = "Last name cannot be empty.";

    public static final String LASTNAME_MIN_LENGTH = "Last name must have at least 3 characters.";

    public static final String LASTNAME_MAX_LENGTH = "Last name cannot exceed 100 characters.";

    public static final String LASTNAME_INVALID_FORMAT = "Last name can only contain letters.";

    public static final String INVALID_DATE_FORMAT = "Invalid date format. Use YYYY-MM-DD";

    public static final String EMAIL_REQUIRED = "Email cannot be empty.";

    public static final String EMAIL_MAX_LENGTH = "Email cannot exceed 254 characters.";

    public static final String INVALID_EMAIL_FORMAT = "Invalid email format.";

    public static final String SALARY_REQUIRED = "Base salary cannot be null.";

    public static final String INVALID_DECIMAL_FORMAT = "Invalid decimal format.";

}
