package co.com.pragma.crediya.api.config;

import co.com.pragma.crediya.api.RouterRest;
import co.com.pragma.crediya.api.UserHandler;
import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.api.exceptions.GlobalExceptionHandler;
import co.com.pragma.crediya.api.mapper.UserRestMapper;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class, GlobalExceptionHandler.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReactiveValidator reactiveValidator;

    @MockitoBean
    private UserRestMapper userRestMapper;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private UserUseCase userUseCase;

    private SaveUserRequest request;

    private User user;

    private UserResponse userResponse;

    private final UUID userId = UUID.fromString("7171da0f-12e1-4e04-a2d6-a7dc43d6d411");


    @BeforeEach
    void setup() {
        request = SaveUserRequest.builder()
                .names("John")
                .lastName("Doe")
                .identificationNumber("123456789")
                .email("johndoe@example.com")
                .baseSalary("15000000")
                .build();

        user = new User(userId, "John", "Doe", null, "123456789", "johndoe@example.com", null, null, new BigDecimal("15000000"), null);

        userResponse = UserResponse.builder()
                .id(userId)
                .names("John")
                .lastName("Doe")
                .identificationNumber("123456789")
                .email("johndoe@example.com")
                .baseSalary(new BigDecimal("15000000"))
                .rol(DomainConstants.DEFAULT_ROLE)
                .build();

        when(reactiveValidator.validate(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0))
        );

        when(userRestMapper.toDomain(any(SaveUserRequest.class))).thenReturn(user);

    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(userUseCase.save(any())).thenReturn(Mono.just(user));

        when(userRestMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}