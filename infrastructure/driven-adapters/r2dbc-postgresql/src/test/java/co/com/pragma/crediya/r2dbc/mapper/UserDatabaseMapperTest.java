package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import co.com.pragma.crediya.r2dbc.projection.UserProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserDatabaseMapperImpl.class})
class UserDatabaseMapperTest {

    @Autowired
    private UserDatabaseMapper mapper;

    @Test
    void toDomain_fromUserEntity() {
        UserEntity entity = UserEntity.builder().id(UUID.randomUUID()).names("John").lastName("Doe").birthDate(LocalDate.of(1990, 1, 1)).identificationNumber("123").email("john@example.com").password("secret").address("Street 123").phoneNumber("555").baseSalary(BigDecimal.TEN).rolId(UUID.randomUUID()).build();

        User user = mapper.toDomain(entity);

        assertThat(user).isNotNull();
        assertThat(user.id()).isEqualTo(entity.getId());
        assertThat(user.password()).isNull();
        assertThat(user.role()).isNull();
    }

    @Test
    void toDomain_fromProjection() {
        UserProjection projection = new UserProjection("jane@example.com", BigDecimal.valueOf(2000));

        User user = mapper.toDomain(projection);

        assertThat(user).isNotNull();
        assertThat(user.email()).isEqualTo("jane@example.com");
        assertThat(user.baseSalary()).isEqualTo(BigDecimal.valueOf(2000));
        assertThat(user.id()).isNull();
    }

    @Test
    void toDomainForAuth_fromUserEntity() {
        UUID roleId = UUID.randomUUID();
        UserEntity entity = UserEntity.builder().id(UUID.randomUUID()).email("auth@example.com").password("pass").rolId(roleId).build();

        User user = mapper.toDomainForAuth(entity);

        assertThat(user).isNotNull();
        assertThat(user.id()).isEqualTo(entity.getId());
        assertThat(user.password()).isEqualTo(entity.getPassword());
        assertThat(user.role()).isNotNull();
        assertThat(user.role().id()).isEqualTo(roleId);
    }

    @Test
    void toEntity_fromUser() {
        Role role = new Role(UUID.randomUUID(), DomainConstants.ADMIN_ROLE, "A admin role");
        User user = new User(UUID.randomUUID(), "Ann", "Smith", LocalDate.now(), "999", "ann@example.com", "Addr", "777", BigDecimal.valueOf(3000), role, "pwd");

        UserEntity entity = mapper.toEntity(user);

        assertThat(entity).isNotNull();
        assertThat(entity.getRolId()).isEqualTo(role.id());
        assertThat(entity.getId()).isEqualTo(user.id());
    }

}
