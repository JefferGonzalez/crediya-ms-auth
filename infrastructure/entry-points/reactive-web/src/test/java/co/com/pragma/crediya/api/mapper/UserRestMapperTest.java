package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserRestMapperImpl.class})
class UserRestMapperTest {

    @Autowired
    private UserRestMapper mapper;

    @Test
    void toDomain_fromSaveUserRequest() {
        SaveUserRequest request = SaveUserRequest.builder()
                .names("Carlos")
                .lastName("Gomez")
                .identificationNumber("1234567890")
                .email("carlos.gomez@example.com")
                .baseSalary("2500")
                .birthDate("1990-08-15")
                .address("Cra 45 #123-45")
                .phoneNumber("3001234567")
                .password("P@ssw0rd")
                .role(DomainConstants.CUSTOMER_ROLE)
                .build();

        User user = mapper.toDomain(request);

        assertThat(user).isNotNull();
        assertThat(user.id()).isNull();
        assertThat(user.role()).isNotNull();
        assertThat(user.role().name()).isEqualTo(request.getRole());
    }

    @Test
    void toResponse_fromUser() {
        User user = new User(UUID.randomUUID(), "Ana", "Lopez", LocalDate.of(1995, 5, 20), "987654321", "ana@example.com", "Street 12", "3109876543", BigDecimal.valueOf(3500), new Role(UUID.randomUUID(), DomainConstants.ADMIN_ROLE, "A admin role"), null);

        UserResponse response = mapper.toResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getRol()).isEqualTo(user.role().name());
        assertThat(response.getEmail()).isEqualTo(user.email());
    }

}

