package co.com.pragma.crediya.usecase.user;

import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.transaction.gateways.TransactionalPort;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.DomainConstants;
import co.com.pragma.crediya.model.user.exceptions.EmailAlreadyTakenException;
import co.com.pragma.crediya.model.user.exceptions.ErrorMessages;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.model.user.exceptions.SalaryOutOfRangeException;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class UserUseCase {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final LoggerPort logger;

    private final TransactionalPort transactionalPort;

    public UserUseCase(UserRepository userRepository, RoleRepository roleRepository, LoggerPort logger, TransactionalPort transactionalPort) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.logger = logger;
        this.transactionalPort = transactionalPort;
    }

    public Mono<User> save(User user) {
        logger.info("Starting save operation for user with email: {}", user.email());

        BigDecimal baseSalary = user.baseSalary();
        if (baseSalary.compareTo(DomainConstants.MIN_SALARY) < 0 || baseSalary.compareTo(DomainConstants.MAX_SALARY) > 0) {
            SalaryOutOfRangeException ex = new SalaryOutOfRangeException();
            logger.error(ErrorMessages.SALARY_OUT_OF_RANGE, ex);

            return Mono.error(ex);
        }

        return roleRepository.findByName(user.role().name())
                .switchIfEmpty(Mono.defer(() -> {
                    RoleNotFoundException ex = new RoleNotFoundException();
                    logger.warn(ErrorMessages.ROLE_NOT_FOUND, ex);

                    return Mono.error(ex);
                }))
                .flatMap(role ->
                        userRepository.existsByEmail(user.email())
                                .flatMap(exists -> {
                                            if (Boolean.TRUE.equals(exists)) {
                                                EmailAlreadyTakenException ex = new EmailAlreadyTakenException();
                                                logger.error(ErrorMessages.EMAIL_ALREADY_TAKEN, ex);

                                                return Mono.error(ex);
                                            }

                                            User userToSave = new User(null, user.names(), user.lastName(), user.birthDate(), user.email(), user.address(), user.phoneNumber(), user.baseSalary(), role);

                                            logger.info("Saving user with email: {}", user.email());
                                            return userRepository.save(userToSave);
                                        }
                                )
                ).as(transactionalPort::transactional)
                .doOnSuccess(storedUser -> logger.info("User saved successfully with id: {}", storedUser.id()))
                .doOnError(e -> logger.error("Failed to save user with email: " + user.email(), e));
    }
}
