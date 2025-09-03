package co.com.pragma.crediya.api.config;

import co.com.pragma.crediya.api.RouterRest;
import co.com.pragma.crediya.api.UserHandler;
import co.com.pragma.crediya.api.config.security.JwtAuthenticationManager;
import co.com.pragma.crediya.api.config.security.SecurityConfig;
import co.com.pragma.crediya.api.config.security.SecurityContextRepository;
import co.com.pragma.crediya.api.config.security.SecurityHeadersConfig;
import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.dto.LoginRequest;
import co.com.pragma.crediya.api.exceptions.handler.CustomAccessDeniedHandler;
import co.com.pragma.crediya.api.exceptions.handler.GlobalExceptionHandler;
import co.com.pragma.crediya.api.mapper.UserRestMapper;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.crediya.usecase.auth.AuthUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@WebFluxTest
@Import({
        CorsConfig.class,
        SecurityHeadersConfig.class,
        GlobalExceptionHandler.class,
        CustomAccessDeniedHandler.class,
        SecurityConfig.class
})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtProviderPort jwtProviderPort;

    @MockitoBean
    private JwtAuthenticationManager jwtAuthenticationManager;

    @MockitoBean
    private SecurityContextRepository securityContextRepository;

    @MockitoBean
    private ReactiveValidator reactiveValidator;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private UserRestMapper userRestMapper;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private AuthUseCase authUseCase;

    private LoginRequest request;

    @BeforeEach
    void setup() {
        request = new LoginRequest("johndoe@example.com", "@Client1234");

        when(reactiveValidator.validate(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0))
        );
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(securityContextRepository.load(any()))
                .thenReturn(Mono.empty());

        when(authUseCase.authenticate(request.getEmail(), request.getPassword()))
                .thenReturn(Mono.just("fake-token"));

        webTestClient.post()
                .uri(ApiConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
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