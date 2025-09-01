package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserDatabaseMapper {

    @Mapping(source = "userEntity.id", target = "id")
    User toDomain(UserEntity userEntity);

    @Mapping(source = "role.id", target = "rolId")
    UserEntity toEntity(User user);

}
