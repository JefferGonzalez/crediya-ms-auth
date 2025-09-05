package co.com.pragma.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "EmailsRequest", description = "Request body for search user by emails")
public class EmailsRequest {

    @NotNull(message = "emails is required")
    @Size(min = 1, message = "emails must not be empty")
    private List<@Email(message = "invalid email format") String> emails;

}


