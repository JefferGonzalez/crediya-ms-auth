package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import co.com.pragma.crediya.r2dbc.mapper.UserDatabaseMapper;
import co.com.pragma.crediya.r2dbc.projection.UserProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository userReactiveRepository;

    @Mock
    private UserDatabaseMapper userMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User domainUser;

    private UserEntity userEntity;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(UUID.randomUUID(), DomainConstants.CUSTOMER_ROLE, "A customer role");

        userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .names("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .identificationNumber("123456789")
                .email("john@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .rolId(role.id())
                .build();

        domainUser = new User(userEntity.getId(), userEntity.getNames(), userEntity.getLastName(), userEntity.getBirthDate(), userEntity.getIdentificationNumber(), userEntity.getEmail(), userEntity.getAddress(), userEntity.getPhoneNumber(), userEntity.getBaseSalary(), role, null);
    }

    @Test
    @DisplayName("existsByEmail() should return true when email exists")
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        when(userReactiveRepository.existsByEmail(domainUser.email()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(domainUser.email()))
                .expectNext(true)
                .verifyComplete();

        verify(userReactiveRepository).existsByEmail(domainUser.email());
    }

    @Test
    @DisplayName("existsByEmail() should return false when email does not exist")
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        String email = "nonexistent@example.com";
        when(userReactiveRepository.existsByEmail(email))
                .thenReturn(Mono.just(false));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();

        verify(userReactiveRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("existsByEmail() should handle repository error")
    void existsByEmail_WhenRepositoryFails_ShouldPropagateError() {
        when(userReactiveRepository.existsByEmail(domainUser.email()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(domainUser.email()))
                .expectError(RuntimeException.class)
                .verify();

        verify(userReactiveRepository).existsByEmail(domainUser.email());
    }

    @Test
    @DisplayName("existsByIdentificationNumber() should return true when identification number exists")
    void existsByIdentificationNumber_WhenIdExists_ShouldReturnTrue() {
        when(userReactiveRepository.existsByIdentificationNumber(domainUser.identificationNumber()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userRepositoryAdapter.existsByIdentificationNumber(domainUser.identificationNumber()))
                .expectNext(true)
                .verifyComplete();

        verify(userReactiveRepository).existsByIdentificationNumber(domainUser.identificationNumber());
    }

    @Test
    @DisplayName("existsByIdentificationNumber() should return false when identification number does not exist")
    void existsByIdentificationNumber_WhenIdDoesNotExist_ShouldReturnFalse() {
        String identificationNumber = "999999999";
        when(userReactiveRepository.existsByIdentificationNumber(identificationNumber))
                .thenReturn(Mono.just(false));

        StepVerifier.create(userRepositoryAdapter.existsByIdentificationNumber(identificationNumber))
                .expectNext(false)
                .verifyComplete();

        verify(userReactiveRepository).existsByIdentificationNumber(identificationNumber);
    }

    @Test
    @DisplayName("existsByIdentificationNumber() should handle repository error")
    void existsByIdentificationNumber_WhenRepositoryFails_ShouldPropagateError() {
        when(userReactiveRepository.existsByIdentificationNumber(domainUser.identificationNumber()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.existsByIdentificationNumber(domainUser.identificationNumber()))
                .expectError(RuntimeException.class)
                .verify();

        verify(userReactiveRepository).existsByIdentificationNumber(domainUser.identificationNumber());
    }

    @Test
    @DisplayName("findByIdentificationNumber() should return user when found")
    void findByIdentificationNumber_WhenUserExists_ShouldReturnUser() {
        when(userReactiveRepository.findByIdentificationNumber(domainUser.identificationNumber()))
                .thenReturn(Mono.just(userEntity));

        when(userMapper.toDomain(userEntity)).thenReturn(domainUser);

        StepVerifier.create(userRepositoryAdapter.findByIdentificationNumber(domainUser.identificationNumber()))
                .expectNext(domainUser)
                .verifyComplete();

        verify(userReactiveRepository).findByIdentificationNumber(domainUser.identificationNumber());

        verify(userMapper).toDomain(userEntity);
    }

    @Test
    @DisplayName("findByIdentificationNumber() should return empty when user not found")
    void findByIdentificationNumber_WhenUserNotFound_ShouldReturnEmpty() {
        String identificationNumber = "999999999";
        when(userReactiveRepository.findByIdentificationNumber(identificationNumber))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.findByIdentificationNumber(identificationNumber))
                .verifyComplete();

        verify(userReactiveRepository).findByIdentificationNumber(identificationNumber);

        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("findByIdentificationNumber() should handle repository error")
    void findByIdentificationNumber_WhenRepositoryFails_ShouldPropagateError() {
        when(userReactiveRepository.findByIdentificationNumber(domainUser.identificationNumber()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.findByIdentificationNumber(domainUser.identificationNumber()))
                .expectError(RuntimeException.class)
                .verify();

        verify(userReactiveRepository).findByIdentificationNumber(domainUser.identificationNumber());

        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("findByEmail() should return user when found")
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        when(userReactiveRepository.findByEmail(domainUser.email())).thenReturn(Mono.just(userEntity));

        when(userMapper.toDomainForAuth(userEntity)).thenReturn(domainUser);

        StepVerifier.create(userRepositoryAdapter.findByEmail(domainUser.email()))
                .expectNext(domainUser)
                .verifyComplete();

        verify(userReactiveRepository).findByEmail(domainUser.email());

        verify(userMapper).toDomainForAuth(userEntity);
    }

    @Test
    @DisplayName("findByEmail() should return empty when user not found")
    void findByEmail_WhenUserNotFound_ShouldReturnEmpty() {
        String email = "nonexistent@example.com";
        when(userReactiveRepository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.findByEmail(email)).verifyComplete();

        verify(userReactiveRepository).findByEmail(email);

        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("findByEmail() should handle repository error")
    void findByEmail_WhenRepositoryFails_ShouldPropagateError() {
        when(userReactiveRepository.findByEmail(domainUser.email()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.findByEmail(domainUser.email()))
                .expectError(RuntimeException.class).verify();

        verify(userReactiveRepository).findByEmail(domainUser.email());

        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("findAllByEmailIn() should return users when found")
    void findAllByEmailIn_WhenUsersExist_ShouldReturnUsers() {
        User john = new User(null, null, null, null, null, "john@example.com", null, null, BigDecimal.valueOf(1223541), null, null);
        User jane = new User(null, null, null, null, null, "jane@example.com", null, null, BigDecimal.valueOf(4511554), null, null);

        UserProjection johnInfo = new UserProjection(john.email(), john.baseSalary());
        UserProjection janeInfo = new UserProjection(jane.email(), jane.baseSalary());

        List<String> emails = List.of(john.email(), jane.email());

        when(userReactiveRepository.findAllByEmailIn(emails)).thenReturn(Flux.just(johnInfo, janeInfo));

        when(userMapper.toDomain(johnInfo)).thenReturn(john);

        when(userMapper.toDomain(janeInfo)).thenReturn(jane);

        StepVerifier.create(userRepositoryAdapter.findAllByEmailIn(emails))
                .expectNext(john)
                .expectNext(jane)
                .verifyComplete();

        verify(userReactiveRepository).findAllByEmailIn(emails);
    }

    @Test
    @DisplayName("findAllByEmailIn() should return empty when no users found")
    void findAllByEmailIn_WhenNoUsersFound_ShouldReturnEmpty() {
        List<String> emails = List.of("nonexistent1@example.com", "nonexistent2@example.com");
        when(userReactiveRepository.findAllByEmailIn(emails)).thenReturn(Flux.empty());

        StepVerifier.create(userRepositoryAdapter.findAllByEmailIn(emails))
                .verifyComplete();

        verify(userReactiveRepository).findAllByEmailIn(emails);

        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("findAllByEmailIn() should handle repository error")
    void findAllByEmailIn_WhenRepositoryFails_ShouldPropagateError() {
        List<String> emails = List.of(domainUser.email());
        when(userReactiveRepository.findAllByEmailIn(emails))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.findAllByEmailIn(emails))
                .expectError(RuntimeException.class).verify();

        verify(userReactiveRepository).findAllByEmailIn(emails);

        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("save() should persist and return user")
    void save_WhenValidUser_ShouldPersistAndReturnUser() {
        when(userMapper.toEntity(domainUser)).thenReturn(userEntity);

        when(userReactiveRepository.save(userEntity)).thenReturn(Mono.just(userEntity));

        when(userMapper.toDomain(userEntity)).thenReturn(domainUser);

        StepVerifier.create(userRepositoryAdapter.save(domainUser))
                .expectNext(domainUser).verifyComplete();

        verify(userMapper).toEntity(domainUser);

        verify(userReactiveRepository).save(userEntity);

        verify(userMapper).toDomain(userEntity);
    }

    @Test
    @DisplayName("save() should handle repository error")
    void save_WhenRepositoryFails_ShouldPropagateError() {
        when(userMapper.toEntity(domainUser)).thenReturn(userEntity);

        when(userReactiveRepository.save(userEntity)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.save(domainUser)).expectError(RuntimeException.class).verify();

        verify(userMapper).toEntity(domainUser);

        verify(userReactiveRepository).save(userEntity);
    }

}