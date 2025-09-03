package co.com.pragma.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserEntity {

    @Id
    private UUID id;

    private String names;

    private String lastName;

    private LocalDate birthDate;

    private String identificationNumber;

    private String email;

    private String password;

    private String address;

    private String phoneNumber;

    private BigDecimal baseSalary;

    private UUID rolId;

}
