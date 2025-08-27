package co.com.pragma.crediya.r2dbc.mapper;

import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;

import java.util.UUID;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserEntity user, RoleEntity role) {
        if (user == null) {
            return null;
        }

        Role r = role != null ? new Role(role.getId(), role.getName(), role.getDescription()) : null;

        return new User(user.getId(), user.getNames(), user.getLastName(), user.getBirthDate(), user.getEmail(), user.getAddress(), user.getPhoneNumber(), user.getBaseSalary(), r);
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UUID roleId = user.role() != null ? user.role().id() : null;

        return UserEntity.builder()
                .id(user.id())
                .names(user.names())
                .lastName(user.lastName())
                .birthDate(user.birthDate())
                .email(user.email())
                .address(user.address())
                .phoneNumber(user.phoneNumber())
                .baseSalary(user.baseSalary())
                .rolId(roleId)
                .build();
    }
}
