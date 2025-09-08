package co.com.pragma.crediya.usecase.user.validation;

import co.com.pragma.crediya.model.common.validation.ValidationOutcome;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import co.com.pragma.crediya.model.user.exceptions.EmailAlreadyTakenException;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public record EmailUniqueValidator(UserRepository userRepository, LoggerPort logger) {

    public Mono<ValidationOutcome> validate(String email) {
        return userRepository.existsByEmail(email)
                .map(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        EmailAlreadyTakenException ex = new EmailAlreadyTakenException();
                        logger.error(UserErrorMessages.EMAIL_ALREADY_TAKEN, ex);

                        return ValidationOutcome.error(UserFieldNames.EMAIL, UserErrorMessages.EMAIL_ALREADY_TAKEN);
                    }

                    logger.info("Email is unique: {}", email);
                    return ValidationOutcome.success(UserFieldNames.EMAIL);
                })
                .onErrorResume(throwable -> {
                    logger.error("Error validating email uniqueness", throwable);

                    return Mono.just(ValidationOutcome.error(UserFieldNames.EMAIL, UserErrorMessages.ERROR_VALIDATING_EMAIL));
                });
    }

}
