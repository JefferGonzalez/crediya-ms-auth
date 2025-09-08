package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import co.com.pragma.crediya.r2dbc.projection.UserProjection;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserDatabaseMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(source = "userEntity.id", target = "id")
    User toDomain(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "names", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "identificationNumber", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toDomain(UserProjection userProjection);

    @Mapping(source = "rolId", target = "role.id")
    @Mapping(target = "role.name", ignore = true)
    @Mapping(target = "role.description", ignore = true)
    @Mapping(source = "userEntity.id", target = "id")
    @Mapping(source = "userEntity.password", target = "password")
    User toDomainForAuth(UserEntity userEntity);

    @Mapping(source = "role.id", target = "rolId")
    UserEntity toEntity(User user);

}
