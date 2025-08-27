package co.com.pragma.crediya.api.dto;

import co.com.pragma.crediya.model.user.constants.DomainConstants;
import co.com.pragma.crediya.model.user.exceptions.ErrorMessages;
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

    @NotBlank(message = ErrorMessages.NAME_REQUIRED)
    @Size(min = DomainConstants.MIN_LENGTH_NAME, message = ErrorMessages.NAME_MIN_LENGTH)
    @Pattern(regexp = RegexPatterns.NAME_REGEX, message = ErrorMessages.NAME_INVALID_FORMAT)
    @Size(max = DomainConstants.MAX_LENGTH_NAME, message = ErrorMessages.NAME_MAX_LENGTH)
    @Schema(description = "User's first names", example = "Carlos Andrés")
    private String names;

    @NotBlank(message = ErrorMessages.LASTNAME_REQUIRED)
    @Size(min = DomainConstants.MIN_LENGTH_LAST_NAME, message = ErrorMessages.LASTNAME_MIN_LENGTH)
    @Pattern(regexp = RegexPatterns.NAME_REGEX, message = ErrorMessages.LASTNAME_INVALID_FORMAT)
    @Size(max = DomainConstants.MAX_LENGTH_LAST_NAME, message = ErrorMessages.LASTNAME_MAX_LENGTH)
    @Schema(description = "User's last name", example = "Gómez Ramírez")
    private String lastName;

    @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
    @Size(max = DomainConstants.MAX_LENGTH_EMAIL, message = ErrorMessages.EMAIL_MAX_LENGTH)
    @Email(message = ErrorMessages.INVALID_EMAIL_FORMAT)
    @Schema(description = "User's email address", example = "carlos.gomez@example.com")
    private String email;

    @NotNull(message = ErrorMessages.SALARY_REQUIRED)
    @Pattern(regexp = RegexPatterns.DECIMAL_REGEX, message = ErrorMessages.INVALID_DECIMAL_FORMAT)
    @Schema(description = "Base salary as a decimal value (string for validation purposes)", example = "2500.75")
    private String baseSalary;

    @Pattern(regexp = RegexPatterns.OPTIONAL_LOCAL_DATE_REGEX, message = ErrorMessages.INVALID_DATE_FORMAT)
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
