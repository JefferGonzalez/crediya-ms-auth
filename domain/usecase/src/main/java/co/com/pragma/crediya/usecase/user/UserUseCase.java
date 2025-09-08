package co.com.pragma.crediya.usecase.user;

import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.common.validation.ValidationFailuresException;
import co.com.pragma.crediya.model.common.validation.ValidationOutcome;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.crediya.model.transaction.gateways.TransactionalPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.UserConstants;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.model.user.exceptions.SalaryOutOfRangeException;
import co.com.pragma.crediya.model.user.exceptions.UserNotFoundException;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import co.com.pragma.crediya.usecase.user.validation.ValidationUserOrchestrator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public record UserUseCase(UserRepository userRepository,
                          RoleRepository roleRepository,
                          ValidationUserOrchestrator validationUserOrchestrator,
                          LoggerPort logger,
                          PasswordEncoderPort passwordEncoderPort,
                          TransactionalPort transactionalPort) {

    public Mono<User> save(User user) {
        logger.info("Starting save operation for user with email: {}", user.email());

        Role defaultRole = new Role(null, DomainConstants.CUSTOMER_ROLE, null);
        User finalUser = User.withDefaultRol(user, defaultRole);

        return validateBaseSalary(finalUser.baseSalary())
                .then(validationUserOrchestrator.validate(finalUser))
                .flatMap(outcomes -> {
                    List<ValidationOutcome> errors = outcomes.stream()
                            .filter(outcome -> !outcome.isValid())
                            .toList();

                    if (!errors.isEmpty()) {
                        return Mono.error(new ValidationFailuresException(errors));
                    }

                    return validateRoleExists(finalUser.role().name());
                })
                .flatMap(role ->
                        {
                            User userToSave = new User(null, finalUser.names(), finalUser.lastName(), finalUser.birthDate(), finalUser.identificationNumber(), finalUser.email(), finalUser.address(), finalUser.phoneNumber(), finalUser.baseSalary(), role, passwordEncoderPort.encode(finalUser.password()));

                            logger.info("Saving user with email: {}", finalUser.email());
                            return userRepository.save(userToSave)
                                    .map(storedUser -> new User(storedUser.id(), storedUser.names(), storedUser.lastName(), storedUser.birthDate(), storedUser.identificationNumber(), storedUser.email(), storedUser.address(), storedUser.phoneNumber(), storedUser.baseSalary(), role, null));
                        }
                ).as(transactionalPort::transactional)
                .doOnSuccess(storedUser -> logger.info("User saved successfully with id: {}", storedUser.id()))
                .doOnError(e -> logger.error("Failed to save user with email: {}", finalUser.email(), e));
    }

    public Mono<User> findByIdentificationNumber(String identificationNumber) {
        logger.info("Finding user with identification number: {}", identificationNumber);

        return userRepository.findByIdentificationNumber(identificationNumber)
                .switchIfEmpty(Mono.defer(() -> {
                    UserNotFoundException ex = new UserNotFoundException();
                    logger.error(UserErrorMessages.USER_NOT_FOUND, ex);

                    return Mono.error(ex);
                }))
                .doOnNext(user -> logger.info("User found: {}", user.identificationNumber()));
    }

    public Flux<User> findAllByEmails(List<String> emails) {
        logger.info("Finding users by {} emails", emails.size());

        return userRepository.findAllByEmailIn(emails);
    }

    private Mono<Void> validateBaseSalary(BigDecimal baseSalary) {
        if (baseSalary.compareTo(UserConstants.MIN_SALARY) < 0 || baseSalary.compareTo(UserConstants.MAX_SALARY) > 0) {
            SalaryOutOfRangeException ex = new SalaryOutOfRangeException();
            logger.error(UserErrorMessages.SALARY_OUT_OF_RANGE, ex);

            return Mono.error(ex);
        }

        return Mono.empty();
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

}
