package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.dto.*;
import co.com.pragma.crediya.api.exceptions.ProblemDetails;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = ApiConstants.LOGIN_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = LoginRequest.class)),
                                    required = true,
                                    description = "Request body for login"
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login successfully",
                                            content = @Content(schema = @Schema(implementation = TokenResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Request body is required",
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = UserErrorMessages.INVALID_CREDENTIALS,
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = ApiConstants.USER_BY_IDENTIFICATION_NUMBER_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getUserByIdentificationNumber",
                    operation = @Operation(
                            operationId = "getUserByIdentificationNumber",
                            parameters = @Parameter(
                                    in = ParameterIn.PATH,
                                    name = UserFieldNames.IDENTIFICATION_NUMBER,
                                    description = "User's identification number",
                                    required = true,
                                    example = "1234567890"
                            ),
                            security = @SecurityRequirement(name = "bearerAuth"),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found successfully",
                                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User with provided identification number not found",
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = ApiConstants.USERS_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "createUser",
                    operation = @Operation(
                            operationId = "saveUser",
                            summary = "Create a new user",
                            description = "Registers a new user in the system with the provided information.",
                            requestBody = @RequestBody(
                                    content = @Content(
                                            schema = @Schema(implementation = SaveUserRequest.class)
                                    ),
                                    required = true,
                                    description = "User data required to create a new account"
                            ),
                            security = @SecurityRequirement(name = "bearerAuth"),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User successfully created",
                                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid request. The provided data is missing or has an invalid format.",
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Conflict. A user with the same email exists.",
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error. An unexpected error occurred while processing the request.",
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(UserHandler handler) {
        return RouterFunctions.route()
                .POST(ApiConstants.LOGIN_PATH, handler::login)
                .GET(ApiConstants.USER_BY_IDENTIFICATION_NUMBER_PATH, handler::getUserByIdentificationNumber)
                .POST(ApiConstants.USERS_PATH, handler::createUser)
                .POST(ApiConstants.USERS_SEARCH_PATH, handler::search)
                .build();
    }


}
