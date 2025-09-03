package co.com.pragma.crediya.usecase.user;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.crediya.model.transaction.gateways.TransactionalPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.model.user.exceptions.SalaryOutOfRangeException;
import co.com.pragma.crediya.model.user.exceptions.UserNotFoundException;
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

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private LoggerPort logger;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TransactionalPort transactionalPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private Role role;

    private User user;

    @BeforeEach
    void setUp() {
        role = new Role(UUID.randomUUID(), DomainConstants.CUSTOMER_ROLE, null);
        user = new User(UUID.randomUUID(), "John", "Doe", LocalDate.of(1980, 1, 1), "123456789", "johndoe@example.com", "Unknown", "123456789", new BigDecimal("15000000"), role, "@Client1234");

        lenient().when(transactionalPort.transactional(any(Mono.class))).then(returnsFirstArg());
        lenient().when(passwordEncoderPort.encode(anyString())).thenReturn("hashed_password");
    }

    @Test
    @DisplayName("save() should persist user when data is valid and role is provided")
    void save_WhenDataIsValidAndRoleProvided_ShouldSaveUser() {
        when(userRepository.existsByEmail(user.email())).thenReturn(Mono.just(false));

        when(userRepository.existsByIdentificationNumber(user.identificationNumber())).thenReturn(Mono.just(false));

        when(roleRepository.findByName(role.name())).thenReturn(Mono.just(role));

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.save(user))
                .assertNext(storedUser -> {
                    Assertions.assertNotNull(storedUser.id());
                    Assertions.assertEquals(user.names(), storedUser.names());
                    Assertions.assertNotNull(storedUser.role());
                    Assertions.assertEquals(role.id(), storedUser.role().id());
                    Assertions.assertEquals(role.name(), storedUser.role().name());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("save() should encode password before persisting user")
    void save_ShouldEncodePasswordBeforePersistingUser() {
        when(userRepository.existsByEmail(user.email())).thenReturn(Mono.just(false));

        when(userRepository.existsByIdentificationNumber(user.identificationNumber())).thenReturn(Mono.just(false));

        when(roleRepository.findByName(role.name())).thenReturn(Mono.just(role));

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.save(user))
                .assertNext(storedUser -> {
                    Assertions.assertNotNull(storedUser);
                    verify(passwordEncoderPort).encode(user.password());
                    Assertions.assertEquals(user.email(), storedUser.email());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("save() should throw SalaryOutOfRangeException when salary is negative")
    void save_WhenSalaryIsNegative_ShouldThrowSalaryOutOfRangeException() {
        User userWithNegativeSalary = new User(user.id(), user.names(), user.lastName(), user.birthDate(), user.identificationNumber(), user.email(), user.address(), user.phoneNumber(), new BigDecimal("-1000"), role, null);

        StepVerifier.create(userUseCase.save(userWithNegativeSalary))
                .expectError(SalaryOutOfRangeException.class)
                .verify();

        verifyNoInteractions(roleRepository, userRepository);
    }

    @Test
    @DisplayName("save() should throw RoleNotFoundException when role does not exist")
    void save_WhenRoleNotFound_ShouldThrowRoleNotFoundException() {
        when(userRepository.existsByEmail(user.email())).thenReturn(Mono.just(false));

        when(userRepository.existsByIdentificationNumber(user.identificationNumber())).thenReturn(Mono.just(false));

        when(roleRepository.findByName(role.name())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.save(user))
                .expectError(RoleNotFoundException.class)
                .verify();

        verify(roleRepository).findByName(role.name());
    }

    @Test
    @DisplayName("findByIdentificationNumber() should return user when exists")
    void findByIdentificationNumber_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByIdentificationNumber(user.identificationNumber()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByIdentificationNumber(user.identificationNumber()))
                .assertNext(foundUser -> {
                    Assertions.assertNotNull(foundUser);
                    Assertions.assertEquals(user.identificationNumber(), foundUser.identificationNumber());
                    Assertions.assertEquals(user.email(), foundUser.email());
                })
                .verifyComplete();

        verify(userRepository).findByIdentificationNumber(user.identificationNumber());
    }

    @Test
    @DisplayName("findByIdentificationNumber() should throw UserNotFoundException when user does not exist")
    void findByIdentificationNumber_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.findByIdentificationNumber(user.identificationNumber()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.findByIdentificationNumber(user.identificationNumber()))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findByIdentificationNumber(user.identificationNumber());
    }

}