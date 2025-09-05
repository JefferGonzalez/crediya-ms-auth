package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.config.security.JwtAuthenticationManager;
import co.com.pragma.crediya.api.config.security.SecurityConfig;
import co.com.pragma.crediya.api.config.security.SecurityContextRepository;
import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.dto.LoginRequest;
import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.TokenResponse;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.api.exceptions.handler.CustomAccessDeniedHandler;
import co.com.pragma.crediya.api.exceptions.handler.GlobalExceptionHandler;
import co.com.pragma.crediya.api.mapper.UserRestMapper;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.InvalidCredentialsException;
import co.com.pragma.crediya.usecase.auth.AuthUseCase;
import co.com.pragma.crediya.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        RouterRest.class,
        UserHandler.class,
        GlobalExceptionHandler.class,
        CustomAccessDeniedHandler.class,
        SecurityConfig.class
})
@WebFluxTest
class RouterRestTest {

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
    private UserRestMapper userRestMapper;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private AuthUseCase authUseCase;

    private SaveUserRequest request;

    private LoginRequest loginRequest;

    private User user;

    private UserResponse userResponse;

    private final UUID userId = UUID.fromString("7171da0f-12e1-4e04-a2d6-a7dc43d6d411");

    private static final String FAKE_TOKEN = "fake-jwt-token";

    @BeforeEach
    void setup() {
        request = SaveUserRequest.builder()
                .names("John")
                .lastName("Doe")
                .identificationNumber("123456789")
                .email("johndoe@example.com")
                .password("@Client1234")
                .baseSalary("15000000")
                .build();

        user = new User(userId, "John", "Doe", null, "123456789", "johndoe@example.com", null, null, new BigDecimal("15000000"), null, "@Client1234");

        userResponse = UserResponse.builder()
                .id(userId)
                .names("John")
                .lastName("Doe")
                .identificationNumber("123456789")
                .email("johndoe@example.com")
                .baseSalary(new BigDecimal("15000000"))
                .rol(DomainConstants.CUSTOMER_ROLE)
                .build();

        loginRequest = new LoginRequest("johndoe@example.com", "@Client1234");

        when(securityContextRepository.load(any()))
                .thenReturn(Mono.empty());

        when(reactiveValidator.validate(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0))
        );

        when(userRestMapper.toDomain(any(SaveUserRequest.class))).thenReturn(user);
    }

    @Test
    void saveUser_shouldReturnCreated_whenValidRequest() {
        when(securityContextRepository.load(any()))
                .thenReturn(Mono.just(new SecurityContextImpl(
                        new UsernamePasswordAuthenticationToken(FAKE_TOKEN, FAKE_TOKEN, List.of(new SimpleGrantedAuthority(DomainConstants.ADMIN_ROLE)))
                )));

        when(userUseCase.save(any(User.class))).thenReturn(Mono.just(user));

        when(userRestMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.post()
                .uri(ApiConstants.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(response -> {
                    Assertions.assertThat(response).isInstanceOf(UserResponse.class);
                    Assertions.assertThat(response.getId()).isNotNull();
                    Assertions.assertThat(response.getId()).isEqualTo(userId);
                    Assertions.assertThat(response.getRol()).isEqualTo(DomainConstants.CUSTOMER_ROLE);
                });
    }

    @Test
    void getUserByIdentificationNumber_shouldReturnUser_whenFound() {
        when(securityContextRepository.load(any()))
                .thenReturn(Mono.just(new SecurityContextImpl(
                        new UsernamePasswordAuthenticationToken(FAKE_TOKEN, FAKE_TOKEN, List.of(new SimpleGrantedAuthority(DomainConstants.ADMIN_ROLE)))
                )));

        when(userUseCase.findByIdentificationNumber(user.identificationNumber()))
                .thenReturn(Mono.just(user));

        UserResponse response = UserResponse.builder()
                .identificationNumber(user.identificationNumber())
                .email(user.email())
                .build();

        when(userRestMapper.toResponse(user)).thenReturn(response);

        webTestClient.get()
                .uri(ApiConstants.USER_BY_IDENTIFICATION_NUMBER_PATH, user.identificationNumber())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.identificationNumber").isEqualTo(response.getIdentificationNumber())
                .jsonPath("$.email").isEqualTo(response.getEmail());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        String expectedToken = "jwt-token-123";

        when(authUseCase.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(Mono.just(expectedToken));

        webTestClient.post()
                .uri(ApiConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .value(response -> {
                    Assertions.assertThat(response).isNotNull();
                    Assertions.assertThat(response.getToken()).isEqualTo(expectedToken);
                });
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() {
        when(authUseCase.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(Mono.error(new InvalidCredentialsException()));

        webTestClient.post()
                .uri(ApiConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
