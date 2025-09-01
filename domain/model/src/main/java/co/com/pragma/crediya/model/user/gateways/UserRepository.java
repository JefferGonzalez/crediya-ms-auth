package co.com.pragma.crediya.model.user.gateways;

import co.com.pragma.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByIdentificationNumber(String identificationNumber);

    Mono<User> findByIdentificationNumber(String identificationNumber);

    Mono<User> save(User user);

}
