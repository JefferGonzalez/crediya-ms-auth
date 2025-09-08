package co.com.pragma.crediya.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record User(
        UUID id,
        String names,
        String lastName,
        LocalDate birthDate,
        String identificationNumber,
        String email,
        String address,
        String phoneNumber,
        BigDecimal baseSalary,
        Role role,
        String password) {

    public static User withDefaultRol(User user, Role role) {
        if (user.role() == null || user.role().name() == null || user.role().name().isEmpty()) {
            return new User(
                    user.id(),
                    user.names(),
                    user.lastName(),
                    user.birthDate(),
                    user.identificationNumber(),
                    user.email(),
                    user.address(),
                    user.phoneNumber(),
                    user.baseSalary(),
                    role,
                    user.password()
            );
        }
        return user;
    }

}