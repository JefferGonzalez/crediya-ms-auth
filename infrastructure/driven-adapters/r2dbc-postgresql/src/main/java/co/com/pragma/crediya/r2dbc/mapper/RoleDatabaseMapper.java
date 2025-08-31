package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleDatabaseMapper {

    Role toDomain(RoleEntity entity);

    RoleEntity toEntity(Role role);

}
