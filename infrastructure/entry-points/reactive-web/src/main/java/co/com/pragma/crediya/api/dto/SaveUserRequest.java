package co.com.pragma.crediya.api.dto;

import co.com.pragma.crediya.model.common.constants.ValidationConstants;
import co.com.pragma.crediya.model.common.constants.ValidationErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "SaveUserRequest", description = "Request body for creating a new user")
public class SaveUserRequest {

    @NotBlank(message = ValidationErrorMessages.NAME_REQUIRED)
    @Size(min = ValidationConstants.MIN_LENGTH_NAME, message = ValidationErrorMessages.NAME_MIN_LENGTH)
    @Pattern(regexp = RegexPatterns.NAME_REGEX, message = ValidationErrorMessages.NAME_INVALID_FORMAT)
    @Size(max = ValidationConstants.MAX_LENGTH_NAME, message = ValidationErrorMessages.NAME_MAX_LENGTH)
    @Schema(description = "User's first names", example = "Carlos Andrés")
    private String names;

    @NotBlank(message = ValidationErrorMessages.LASTNAME_REQUIRED)
    @Size(min = ValidationConstants.MIN_LENGTH_LAST_NAME, message = ValidationErrorMessages.LASTNAME_MIN_LENGTH)
    @Pattern(regexp = RegexPatterns.NAME_REGEX, message = ValidationErrorMessages.LASTNAME_INVALID_FORMAT)
    @Size(max = ValidationConstants.MAX_LENGTH_LAST_NAME, message = ValidationErrorMessages.LASTNAME_MAX_LENGTH)
    @Schema(description = "User's last name", example = "Gómez Ramírez")
    private String lastName;

    @NotBlank(message = ValidationErrorMessages.IDENTIFICATION_NUMBER_REQUIRED)
    @Size(min = ValidationConstants.IDENTIFICATION_NUMBER_LENGTH, max = ValidationConstants.IDENTIFICATION_NUMBER_LENGTH, message = ValidationErrorMessages.IDENTIFICATION_NUMBER_LENGTH)
    @Pattern(regexp = RegexPatterns.IDENTIFICATION_NUMBER_REGEX, message = ValidationErrorMessages.INVALID_IDENTIFICATION_NUMBER_FORMAT)
    @Schema(description = "User's identification number", example = "1234567890")
    private String identificationNumber;

    @NotBlank(message = ValidationErrorMessages.EMAIL_REQUIRED)
    @Size(max = ValidationConstants.MAX_LENGTH_EMAIL, message = ValidationErrorMessages.EMAIL_MAX_LENGTH)
    @Email(message = ValidationErrorMessages.INVALID_EMAIL_FORMAT)
    @Schema(description = "User's email address", example = "carlos.gomez@example.com")
    private String email;

    @NotNull(message = ValidationErrorMessages.SALARY_REQUIRED)
    @Pattern(regexp = RegexPatterns.DECIMAL_REGEX, message = ValidationErrorMessages.INVALID_DECIMAL_FORMAT)
    @Schema(description = "Base salary as a decimal value (string for validation purposes)", example = "2500.75")
    private String baseSalary;

    @Pattern(regexp = RegexPatterns.OPTIONAL_LOCAL_DATE_REGEX, message = ValidationErrorMessages.INVALID_DATE_FORMAT)
    @Schema(description = "User's birth date in ISO format (optional)", example = "1990-08-15")
    private String birthDate;

    @Schema(description = "User's address", example = "Cra 45 #123-45, Bogotá")
    private String address;

    @Schema(description = "User's phone number", example = "+57 3201234567")
    private String phoneNumber;

    public LocalDate getBirthDate() {
        return (birthDate == null || birthDate.isEmpty()) ? null : LocalDate.parse(birthDate);
    }

    public BigDecimal getBaseSalary() {
        return new BigDecimal(baseSalary);
    }

}
