package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RoleDatabaseMapperImpl.class})
class RoleDatabaseMapperTest {

    @Autowired
    private RoleDatabaseMapper mapper;

    @Test
    void toDomain_fromEntity() {
        RoleEntity entity = RoleEntity.builder()
                .id(UUID.randomUUID())
                .name(DomainConstants.ADMIN_ROLE)
                .description("A admin role")
                .build();

        Role role = mapper.toDomain(entity);

        assertThat(role).isNotNull();
        assertThat(role.id()).isEqualTo(entity.getId());
        assertThat(role.name()).isEqualTo(entity.getName());
        assertThat(role.description()).isEqualTo(entity.getDescription());
    }
}
