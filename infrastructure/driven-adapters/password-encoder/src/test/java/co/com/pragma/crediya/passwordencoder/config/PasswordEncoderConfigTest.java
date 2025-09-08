package co.com.pragma.crediya.passwordencoder.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderConfigTest {

    private final PasswordEncoderConfig config = new PasswordEncoderConfig();

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        String raw = "password";
        String encoded = encoder.encode(raw);

        assertTrue(encoder.matches(raw, encoded));
    }
}
