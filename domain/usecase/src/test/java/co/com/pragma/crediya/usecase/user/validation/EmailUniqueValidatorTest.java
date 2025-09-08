package co.com.pragma.crediya.usecase.user.validation;

import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EmailUniqueValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggerPort logger;

    private EmailUniqueValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmailUniqueValidator(userRepository, logger);
    }

    @Test
    void validate_whenEmailIsUnique_shouldReturnSuccess() {
        String email = "unique@example.com";
        Mockito.when(userRepository.existsByEmail(email)).thenReturn(Mono.just(false));

        StepVerifier.create(validator.validate(email))
                .expectNextMatches(outcome ->
                        outcome.isValid() && outcome.field().equals(UserFieldNames.EMAIL)
                ).verifyComplete();
    }

    @Test
    void validate_whenEmailAlreadyTaken_shouldReturnError() {
        String email = "taken@example.com";
        Mockito.when(userRepository.existsByEmail(email)).thenReturn(Mono.just(true));

        StepVerifier.create(validator.validate(email))
                .expectNextMatches(outcome -> !outcome.isValid() &&
                        outcome.field().equals(UserFieldNames.EMAIL) &&
                        outcome.errorMessage().equals(UserErrorMessages.EMAIL_ALREADY_TAKEN))
                .verifyComplete();
    }

    @Test
    void validate_whenRepositoryThrows_shouldReturnError() {
        String email = "error@example.com";
        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(validator.validate(email))
                .expectNextMatches(outcome -> !outcome.isValid() &&
                        outcome.field().equals(UserFieldNames.EMAIL) &&
                        outcome.errorMessage().equals(UserErrorMessages.ERROR_VALIDATING_EMAIL))
                .verifyComplete();
    }

}
