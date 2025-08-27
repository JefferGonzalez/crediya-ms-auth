package co.com.pragma.crediya.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID id;

    private String names;

    private String lastName;

    private LocalDate birthDate;

    private String email;

    private String address;

    private String phoneNumber;

    private BigDecimal baseSalary;

    private String rol;

}
