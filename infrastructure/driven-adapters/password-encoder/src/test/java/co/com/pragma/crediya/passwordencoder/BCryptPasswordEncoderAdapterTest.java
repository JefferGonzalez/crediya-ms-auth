package co.com.pragma.crediya.passwordencoder;

import co.com.pragma.crediya.model.password.gateways.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordEncoderPort adapter;

    @BeforeEach
    void setUp() {
        adapter = new BCryptPasswordEncoderAdapter(passwordEncoder);
    }

    @Test
    void encode_shouldReturnEncodedPassword() {
        String raw = "password";
        String encoded = "encodedPassword";

        when(passwordEncoder.encode(raw)).thenReturn(encoded);

        String result = adapter.encode(raw);

        assertEquals(encoded, result);
    }

    @Test
    void matches_shouldReturnTrueWhenPasswordsMatch() {
        String raw = "password";
        String encoded = "encodedPassword";

        when(passwordEncoder.matches(raw, encoded)).thenReturn(true);

        boolean result = adapter.matches(raw, encoded);

        assertTrue(result);
    }

}
