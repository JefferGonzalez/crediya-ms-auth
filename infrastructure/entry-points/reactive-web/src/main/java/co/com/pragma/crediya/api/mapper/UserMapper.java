package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.DomainConstants;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(SaveUserRequest user) {
        Role role = new Role(null, DomainConstants.DEFAULT_ROLE, null);
        return new User(null, user.getNames(), user.getLastName(), user.getBirthDate(), user.getEmail(), user.getAddress(), user.getPhoneNumber(), user.getBaseSalary(), role);
    }

    public static UserResponse toResponse(User user) {
        String roleName = user.role() != null ? user.role().name() : null;

        return UserResponse.builder()
                .id(user.id())
                .names(user.names())
                .lastName(user.lastName())
                .birthDate(user.birthDate())
                .email(user.email())
                .address(user.address())
                .phoneNumber(user.phoneNumber())
                .baseSalary(user.baseSalary())
                .rol(roleName)
                .build();
    }
}

