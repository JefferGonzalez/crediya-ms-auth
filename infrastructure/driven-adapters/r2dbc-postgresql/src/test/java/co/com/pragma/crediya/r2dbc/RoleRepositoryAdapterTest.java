package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import co.com.pragma.crediya.r2dbc.mapper.RoleDatabaseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

    @Mock
    private RoleReactiveRepository repository;

    @Mock
    private RoleDatabaseMapper mapper;

    @InjectMocks
    private RoleRepositoryAdapter roleRepositoryAdapter;

    private Role role;

    private RoleEntity roleEntity;

    private UUID roleId;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();

        roleEntity = RoleEntity.builder()
                .id(roleId)
                .name(DomainConstants.CUSTOMER_ROLE)
                .description("A customer role")
                .build();

        role = new Role(roleEntity.getId(), roleEntity.getName(), roleEntity.getDescription());
    }

    @Test
    @DisplayName("findById() should return role when found")
    void findById_WhenRoleExists_ShouldReturnRole() {
        when(repository.findById(roleId)).thenReturn(Mono.just(roleEntity));

        when(mapper.toDomain(roleEntity)).thenReturn(role);

        StepVerifier.create(roleRepositoryAdapter.findById(roleId))
                .expectNext(role)
                .verifyComplete();

        verify(repository).findById(roleId);

        verify(mapper).toDomain(roleEntity);
    }

    @Test
    @DisplayName("findById() should return empty when role not found")
    void findById_WhenRoleNotFound_ShouldReturnEmpty() {
        when(repository.findById(roleId)).thenReturn(Mono.empty());

        StepVerifier.create(roleRepositoryAdapter.findById(roleId))
                .verifyComplete();

        verify(repository).findById(roleId);

        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findById() should handle repository error")
    void findById_WhenRepositoryFails_ShouldPropagateError() {
        when(repository.findById(roleId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(roleRepositoryAdapter.findById(roleId))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findById(roleId);

        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findByName() should return role when found")
    void findByName_WhenRoleExists_ShouldReturnRole() {
        when(repository.findByName(DomainConstants.CUSTOMER_ROLE)).thenReturn(Mono.just(roleEntity));

        when(mapper.toDomain(roleEntity)).thenReturn(role);

        StepVerifier.create(roleRepositoryAdapter.findByName(DomainConstants.CUSTOMER_ROLE))
                .expectNext(role)
                .verifyComplete();

        verify(repository).findByName(DomainConstants.CUSTOMER_ROLE);

        verify(mapper).toDomain(roleEntity);
    }

    @Test
    @DisplayName("findByName() should return empty when role not found")
    void findByName_WhenRoleNotFound_ShouldReturnEmpty() {
        String roleName = "NONEXISTENT";
        when(repository.findByName(roleName)).thenReturn(Mono.empty());

        StepVerifier.create(roleRepositoryAdapter.findByName(roleName))
                .verifyComplete();

        verify(repository).findByName(roleName);

        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findByName() should handle repository error")
    void findByName_WhenRepositoryFails_ShouldPropagateError() {
        when(repository.findByName(DomainConstants.CUSTOMER_ROLE))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(roleRepositoryAdapter.findByName(DomainConstants.CUSTOMER_ROLE))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByName(DomainConstants.CUSTOMER_ROLE);

        verifyNoInteractions(mapper);
    }

}