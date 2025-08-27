package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
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

	private String email;

	private String address;

	private String phoneNumber;

	private BigDecimal baseSalary;

    private UUID rolId;

}
