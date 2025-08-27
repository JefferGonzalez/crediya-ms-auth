package co.com.pragma.crediya.usecase.user;

import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.transaction.gateways.TransactionalPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.model.user.exceptions.SalaryOutOfRangeException;
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
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private LoggerPort logger;

    @Mock
    private TransactionalPort transactionalPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private Role role;

    private User user;

    @BeforeEach
    void setUp() {
        role = new Role(UUID.randomUUID(), "CUSTOMER", null);
        user = new User(UUID.randomUUID(), "John", "Doe", LocalDate.of(1980, 1, 1), "johndoe@example.com", "Unknown", "123456789", new BigDecimal("15000000"), role);
    }


    @Test
    @DisplayName("save() should persist user when data is valid and role is provided")
    void save_WhenDataIsValidAndRoleProvided_ShouldSaveUser() {
        when(roleRepository.findByName(role.name())).thenReturn(Mono.just(role));

        when(userRepository.existsByEmail("johndoe@example.com")).thenReturn(Mono.just(false));

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        when(transactionalPort.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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
    @DisplayName("save() should throw SalaryOutOfRangeException when salary is negative")
    void save_WhenSalaryIsNegative_ShouldThrowSalaryOutOfRangeException() {
        User userWithNegativeSalary = new User(user.id(), user.names(), user.lastName(), user.birthDate(), user.email(), user.address(), user.phoneNumber(), new BigDecimal("-1000"), role);

        StepVerifier.create(userUseCase.save(userWithNegativeSalary))
                .expectError(SalaryOutOfRangeException.class)
                .verify();

        verifyNoInteractions(roleRepository, userRepository);
    }

    @Test
    @DisplayName("save() should throw RoleNotFoundException when role does not exist")
    void save_WhenRoleNotFound_ShouldThrowRoleNotFoundException() {
        when(roleRepository.findByName(role.name())).thenReturn(Mono.empty());

        when(transactionalPort.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.save(user))
                .expectError(RoleNotFoundException.class)
                .verify();

        verify(roleRepository).findByName(role.name());
        verifyNoInteractions(userRepository);
    }

}