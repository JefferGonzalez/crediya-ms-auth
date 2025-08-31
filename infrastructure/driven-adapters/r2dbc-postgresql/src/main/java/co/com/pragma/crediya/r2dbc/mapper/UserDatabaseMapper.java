package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleDatabaseMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserDatabaseMapper {

    @Mapping(source = "userEntity.id", target = "id")
    @Mapping(source = "roleEntity", target = "role")
    User toDomain(UserEntity userEntity, RoleEntity roleEntity);

    @Mapping(source = "role.id", target = "rolId")
    UserEntity toEntity(User user);

}
