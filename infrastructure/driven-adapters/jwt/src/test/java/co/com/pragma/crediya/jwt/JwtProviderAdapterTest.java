package co.com.pragma.crediya.jwt;

import co.com.pragma.crediya.jwt.config.JwtProperties;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.jwt.Jwt;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtProviderAdapterTest {

    private JwtProviderAdapter adapter;

    private User user;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecretKey("q/MbiTiaKL9wCSeISqOlOQvDjg7s+xmYRtNhYbq7T3A=");
        properties.setExpiration(10000L);

        adapter = new JwtProviderAdapter(properties);

        Role role = new Role(UUID.randomUUID(), DomainConstants.ADMIN_ROLE, "A Admin role");

        user = new User(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990, 1, 1), "123456789", "john@example.com", null, null, BigDecimal.valueOf(5000000), role, null);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = adapter.generateToken(user);

        assertThat(token).isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(10);
    }

    @Test
    void parseToken_shouldReturnJwtObject() {
        String token = adapter.generateToken(user);

        Jwt jwt = adapter.parseToken(token);

        assertThat(jwt).isNotNull();

        assertThat(jwt.subject()).isEqualTo(user.email());

        assertEquals(jwt.roles(), List.of(user.role().name()));

        assertThat(jwt.identificationNumber()).isEqualTo(user.identificationNumber());

        assertThat(jwt.baseSalary()).isEqualTo(user.baseSalary());
    }

}
