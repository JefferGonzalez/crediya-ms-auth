package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.UserEmailResponse;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role.name", source = "role")
    User toDomain(SaveUserRequest dto);

    @Mapping(source = "role.name", target = "rol")
    UserResponse toResponse(User user);

    UserEmailResponse toEmailResponse(User user);

}
