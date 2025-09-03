package co.com.pragma.crediya.api.dto;

import co.com.pragma.crediya.model.common.constants.ValidationConstants;
import co.com.pragma.crediya.model.common.constants.ValidationErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginRequest", description = "Request body for login")
public class LoginRequest {

    @NotBlank(message = ValidationErrorMessages.EMAIL_REQUIRED)
    @Size(max = ValidationConstants.MAX_LENGTH_EMAIL, message = ValidationErrorMessages.EMAIL_MAX_LENGTH)
    @Email(message = ValidationErrorMessages.INVALID_EMAIL_FORMAT)
    @Schema(description = "User's email address", example = "carlos.gomez@example.com")
    private String email;

    @NotBlank(message = ValidationErrorMessages.PASSWORD_REQUIRED)
    @Schema(description = "User's password.", example = "P@ssw0rd")
    private String password;

}
