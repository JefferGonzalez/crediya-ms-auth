package co.com.pragma.crediya.usecase.auth;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.InvalidCredentialsException;
import co.com.pragma.crediya.model.user.exceptions.UserDataInconsistencyException;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtProviderPort jwtProviderPort;

    @Mock
    private LoggerPort logger;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User user;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(UUID.randomUUID(), DomainConstants.CUSTOMER_ROLE, null);
        user = new User(UUID.randomUUID(), "John", "Doe", LocalDate.of(1980, 1, 1), "123456789", "johndoe@example.com", "Unknown", "123456789", new BigDecimal("15000000"), role, "hashed_password");
    }

    @Test
    @DisplayName("authenticate() should return JWT token when credentials are valid")
    void authenticate_WhenCredentialsAreValid_ShouldReturnToken() {
        String password = "@Client1234";
        String expectedToken = "jwt-token-123";

        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));

        when(passwordEncoderPort.matches(password, user.password())).thenReturn(true);

        when(roleRepository.findById(role.id())).thenReturn(Mono.just(role));

        when(jwtProviderPort.generateToken(any(User.class))).thenReturn(expectedToken);

        StepVerifier.create(authUseCase.authenticate(user.email(), password))
                .assertNext(token -> {
                    Assertions.assertEquals(expectedToken, token);
                    verify(passwordEncoderPort).matches(password, user.password());
                    verify(jwtProviderPort).generateToken(any(User.class));
                }).verifyComplete();
    }

    @Test
    @DisplayName("authenticate() should throw InvalidCredentialsException when user is not found")
    void authenticate_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.authenticate(user.email(), user.password()))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    @DisplayName("authenticate() should throw InvalidCredentialsException when password does not match")
    void authenticate_WhenPasswordDoesNotMatch_ShouldThrowException() {
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(authUseCase.authenticate(user.email(), user.password()))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    @DisplayName("authenticate() should throw UserDataInconsistencyException when role is not found")
    void authenticate_WhenRoleNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(user.password(), user.password())).thenReturn(true);
        when(roleRepository.findById(role.id())).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.authenticate(user.email(), user.password()))
                .expectError(UserDataInconsistencyException.class)
                .verify();
    }


}
