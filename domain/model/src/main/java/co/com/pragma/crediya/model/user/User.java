package co.com.pragma.crediya.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record User(
        UUID id,
        String names,
        String lastName,
        LocalDate birthDate,
        String email,
        String address,
        String phoneNumber,
        BigDecimal baseSalary,
        Role role) {
}