package co.com.pragma.crediya.usecase.user.validation;

import co.com.pragma.crediya.model.common.validation.ValidationOutcome;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ValidationUserOrchestratorTest {

    @Mock
    private EmailUniqueValidator emailValidator;

    @Mock
    private IdentificationNumberValidator idValidator;

    private ValidationUserOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new ValidationUserOrchestrator(emailValidator, idValidator);
    }

    @Test
    void validate_shouldReturnValidationOutcomes() {
        User user = new User(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990, 1, 1), "123456789", "john@example.com", "Addr", "555", BigDecimal.TEN, null, null);

        ValidationOutcome emailOutcome = ValidationOutcome.success(UserFieldNames.EMAIL);
        ValidationOutcome identificationNumberOutcome = ValidationOutcome.success(UserFieldNames.IDENTIFICATION_NUMBER);

        Mockito.when(emailValidator.validate(user.email())).thenReturn(Mono.just(emailOutcome));
        Mockito.when(idValidator.validate(user.identificationNumber())).thenReturn(Mono.just(identificationNumberOutcome));

        StepVerifier.create(orchestrator.validate(user))
                .expectNextMatches(list ->
                        list.contains(emailOutcome) && list.contains(identificationNumberOutcome)
                ).verifyComplete();
    }

}

