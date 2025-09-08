package co.com.pragma.crediya.usecase.user.validation;

import co.com.pragma.crediya.model.common.validation.ValidationOutcome;
import co.com.pragma.crediya.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public record ValidationUserOrchestrator(
        EmailUniqueValidator emailValidator,
        IdentificationNumberValidator idValidator) {

    public Mono<List<ValidationOutcome>> validate(User user) {
        return Mono.zip(
                emailValidator.validate(user.email()),
                idValidator.validate(user.identificationNumber())
        ).map(tuple -> List.of(tuple.getT1(), tuple.getT2()));
    }

}
