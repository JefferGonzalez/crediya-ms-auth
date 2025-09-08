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
class IdentificationNumberValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggerPort logger;

    private IdentificationNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IdentificationNumberValidator(userRepository, logger);
    }

    @Test
    void validate_whenIdIsUnique_shouldReturnSuccess() {
        String identificationNumber = "123456789";
        Mockito.when(userRepository.existsByIdentificationNumber(identificationNumber)).thenReturn(Mono.just(false));

        StepVerifier.create(validator.validate(identificationNumber))
                .expectNextMatches(outcome ->
                        outcome.isValid() && outcome.field().equals(UserFieldNames.IDENTIFICATION_NUMBER)
                ).verifyComplete();
    }

    @Test
    void validate_whenIdAlreadyExists_shouldReturnError() {
        String identificationNumber = "987654321";
        Mockito.when(userRepository.existsByIdentificationNumber(identificationNumber)).thenReturn(Mono.just(true));

        StepVerifier.create(validator.validate(identificationNumber))
                .expectNextMatches(outcome -> !outcome.isValid() &&
                        outcome.field().equals(UserFieldNames.IDENTIFICATION_NUMBER) &&
                        outcome.errorMessage().contains(UserErrorMessages.IDENTIFICATION_NUMBER_ALREADY_TAKEN))
                .verifyComplete();
    }

    @Test
    void validate_whenRepositoryThrows_shouldReturnError() {
        String identificationNumber = "000111222";
        Mockito.when(userRepository.existsByIdentificationNumber(identificationNumber))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(validator.validate(identificationNumber))
                .expectNextMatches(outcome -> !outcome.isValid() &&
                        outcome.field().equals(UserFieldNames.IDENTIFICATION_NUMBER) &&
                        outcome.errorMessage().contains(UserErrorMessages.ERROR_VALIDATING_IDENTIFICATION_NUMBER))
                .verifyComplete();
    }
}

