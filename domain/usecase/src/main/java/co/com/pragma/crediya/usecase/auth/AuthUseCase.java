package co.com.pragma.crediya.usecase.auth;

import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.UserErrorMessages;
import co.com.pragma.crediya.model.user.exceptions.InvalidCredentialsException;
import co.com.pragma.crediya.model.user.exceptions.UserDataInconsistencyException;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public record AuthUseCase(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoderPort passwordEncoderPort,
                          JwtProviderPort jwtProviderPort,
                          LoggerPort logger) {

    public Mono<String> authenticate(String email, String password) {
        logger.info("Starting authentication flow for email: {}", email);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> {
                    InvalidCredentialsException ex = new InvalidCredentialsException();
                    logger.error(UserErrorMessages.INVALID_CREDENTIALS, ex);

                    return Mono.error(ex);
                }))
                .flatMap(user -> {
                    if (!passwordEncoderPort.matches(password, user.password())) {
                        InvalidCredentialsException ex = new InvalidCredentialsException();
                        logger.error("Invalid password for email: {}", email, ex);

                        return Mono.error(ex);
                    }

                    return roleRepository.findById(user.role().id())
                            .switchIfEmpty(Mono.defer(() -> {
                                UserDataInconsistencyException ex = new UserDataInconsistencyException();
                                logger.error("Authentication denied - invalid role reference for user: {}", user.id(), ex);

                                return Mono.error(ex);
                            }))
                            .map(role -> {
                                User fullUser = new User(user.id(), user.names(), user.lastName(), user.birthDate(), user.identificationNumber(), user.email(), user.address(), user.phoneNumber(), user.baseSalary(), role, null);

                                return jwtProviderPort.generateToken(fullUser);
                            });
                })
                .doOnSuccess(token -> logger.info("User authenticated successfully with email: {}", email))
                .doOnError(e -> logger.error("Failed to authenticate user with email: {}", email, e));
    }

}

