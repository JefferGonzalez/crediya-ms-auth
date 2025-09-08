package co.com.pragma.crediya.usecase.user.validation;

import co.com.pragma.crediya.model.common.validation.ValidationOutcome;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import co.com.pragma.crediya.model.user.exceptions.IdentificationNumberAlreadyExistsException;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public record IdentificationNumberValidator(UserRepository userRepository, LoggerPort logger) {

    public Mono<ValidationOutcome> validate(String identificationNumber) {
        return userRepository.existsByIdentificationNumber(identificationNumber)
                .map(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        IdentificationNumberAlreadyExistsException ex = new IdentificationNumberAlreadyExistsException();
                        logger.error(UserErrorMessages.IDENTIFICATION_NUMBER_ALREADY_TAKEN, ex);

                        return ValidationOutcome.error(UserFieldNames.IDENTIFICATION_NUMBER, UserErrorMessages.IDENTIFICATION_NUMBER_ALREADY_TAKEN);
                    }

                    logger.info("Identification number is unique: {}", identificationNumber);
                    return ValidationOutcome.success(UserFieldNames.IDENTIFICATION_NUMBER);
                }).onErrorResume(throwable -> {
                    logger.error("Error validating identification number uniqueness", throwable);

                    return Mono.just(ValidationOutcome.error(UserFieldNames.IDENTIFICATION_NUMBER, UserErrorMessages.ERROR_VALIDATING_IDENTIFICATION_NUMBER));
                });
    }

}
