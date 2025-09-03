package co.com.pragma.crediya.model.common.constants;

public class ValidationErrorMessages {

    private ValidationErrorMessages() {
    }

    public static final String NAME_REQUIRED = "Name is required.";

    public static final String NAME_MIN_LENGTH = "Name must have at least 3 characters.";

    public static final String NAME_MAX_LENGTH = "Name cannot exceed 100 characters.";

    public static final String NAME_INVALID_FORMAT = "Name can only contain letters.";

    public static final String LASTNAME_REQUIRED = "Last name is required.";

    public static final String LASTNAME_MIN_LENGTH = "Last name must have at least 3 characters.";

    public static final String LASTNAME_MAX_LENGTH = "Last name cannot exceed 100 characters.";

    public static final String LASTNAME_INVALID_FORMAT = "Last name can only contain letters.";

    public static final String INVALID_DATE_FORMAT = "Invalid date format. Use YYYY-MM-DD";

    public static final String IDENTIFICATION_NUMBER_REQUIRED = "Identification number is required.";

    public static final String IDENTIFICATION_NUMBER_LENGTH = "Identification number must be exactly 10 digits.";

    public static final String INVALID_IDENTIFICATION_NUMBER_FORMAT = "Identification number must contain only digits.";

    public static final String EMAIL_REQUIRED = "Email is required.";

    public static final String EMAIL_MAX_LENGTH = "Email cannot exceed 254 characters.";

    public static final String INVALID_EMAIL_FORMAT = "Invalid email format.";

    public static final String SALARY_REQUIRED = "Base salary is required.";

    public static final String PASSWORD_REQUIRED = "Password is required.";

    public static final String INVALID_PASSWORD_FORMAT = "Invalid password format. Password must be 8-16 characters long, and include at least one uppercase letter, one lowercase letter, one number, and one special character.";

    public static final String INVALID_DECIMAL_FORMAT = "Invalid decimal format.";

}
