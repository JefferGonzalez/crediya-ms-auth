package co.com.pragma.crediya.usecase.user;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.common.validation.ValidationFailuresException;
import co.com.pragma.crediya.model.common.validation.ValidationOutcome;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.transaction.gateways.TransactionalPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.UserConstants;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import co.com.pragma.crediya.model.user.exceptions.EmailAlreadyTakenException;
import co.com.pragma.crediya.model.user.exceptions.IdentificationNumberAlreadyExistsException;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.model.user.exceptions.SalaryOutOfRangeException;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record UserUseCase(UserRepository userRepository,
                          RoleRepository roleRepository,
                          LoggerPort logger,
                          TransactionalPort transactionalPort) {

    public Mono<User> save(User user) {
        logger.info("Starting save operation for user with email: {}", user.email());

        BigDecimal baseSalary = user.baseSalary();
        if (baseSalary.compareTo(UserConstants.MIN_SALARY) < 0 || baseSalary.compareTo(UserConstants.MAX_SALARY) > 0) {
            SalaryOutOfRangeException ex = new SalaryOutOfRangeException();
            logger.error(UserErrorMessages.SALARY_OUT_OF_RANGE, ex);

            return Mono.error(ex);
        }

        if (user.role() == null) {
            Role defaultRole = new Role(null, DomainConstants.DEFAULT_ROLE, null);
            user = new User(null, user.names(), user.lastName(), user.birthDate(), user.identificationNumber(), user.email(), user.address(), user.phoneNumber(), user.baseSalary(), defaultRole);
        }

        User finalUser = user;
        return Mono.zip(
                        validateEmailUnique(finalUser.email()),
                        validateIdentificationNumberUnique(finalUser.identificationNumber())
                ).flatMap(tuple -> {
                    ValidationOutcome emailValidation = tuple.getT1();
                    ValidationOutcome identificationNumberValidation = tuple.getT2();

                    List<ValidationOutcome> errors = new ArrayList<>();

                    if (!emailValidation.isValid()) {
                        errors.add(emailValidation);
                    }

                    if (!identificationNumberValidation.isValid()) {
                        errors.add(identificationNumberValidation);
                    }

                    if (!errors.isEmpty()) {
                        return Mono.error(new ValidationFailuresException(errors));
                    }

                    return validateRoleExists(finalUser.role().name());
                })
                .flatMap(role ->
                        {
                            User userToSave = new User(null, finalUser.names(), finalUser.lastName(), finalUser.birthDate(), finalUser.identificationNumber(), finalUser.email(), finalUser.address(), finalUser.phoneNumber(), finalUser.baseSalary(), role);

                            logger.info("Saving user with email: {}", finalUser.email());
                            return userRepository.save(userToSave);
                        }
                ).as(transactionalPort::transactional)
                .doOnSuccess(storedUser -> logger.info("User saved successfully with id: {}", storedUser.id()))
                .doOnError(e -> logger.error("Failed to save user with email: " + finalUser.email(), e));
    }

    private Mono<Role> validateRoleExists(String roleName) {
        return roleRepository.findByName(roleName)
                .switchIfEmpty(Mono.defer(() -> {
                    RoleNotFoundException ex = new RoleNotFoundException();
                    logger.warn(UserErrorMessages.ROLE_NOT_FOUND, ex);

                    return Mono.error(ex);
                }))
                .doOnNext(role -> logger.info("Role found: {}", role.name()));
    }

    private Mono<ValidationOutcome> validateEmailUnique(String email) {
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

                    return Mono.just(ValidationOutcome.error(UserFieldNames.EMAIL, "Error validating email"));
                });
    }

    private Mono<ValidationOutcome> validateIdentificationNumberUnique(String identificationNumber) {
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

                    return Mono.just(ValidationOutcome.error(UserFieldNames.IDENTIFICATION_NUMBER, "Error validating identification"));
                });
    }

}
